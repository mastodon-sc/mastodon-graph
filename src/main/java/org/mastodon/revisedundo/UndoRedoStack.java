/**
 *
 */
package org.mastodon.revisedundo;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.mastodon.undo.UndoPointMarker;

import gnu.trove.list.TByteList;
import gnu.trove.list.array.TByteArrayList;

/**
 * A undo/redo stack to record undoable edits. Basically, this is an expandable
 * array of elements with a {@code top} pointer. (Not really a stack because
 * elements above the {@code top} may be retained.)
 * <p>
 * {@link UndoRedoStack} simply records the type of each new edit, and
 * whether it is an <em>undo point</em> The data required to undo/redo the new
 * edit is stored in separate undo/redo stacks that are members of the specific
 * {@link AbstractUndoableEditType} instances.
 * </p>
 *
 * @author Tobias Pietzsch
 */
public class UndoRedoStack implements UndoPointMarker
{
	private final ArrayList< AbstractUndoableEditType > editTypes = new ArrayList<>();

	private final Stack stack;

	private final Access ref;

	private int top;

	private int end;

	public UndoRedoStack( final int initialCapacity )
	{
		stack = new ByteStack( initialCapacity );
		ref = stack.createRef();
		top = 0;
		end = 0;
	}

	/**
	 * Put a new element of the specified {@code type} at the top of the stack,
	 * expanding the stack if necessary. Then increment top.
	 *
	 * @param type
	 *            type of the new element to push
	 */
	public void record( final AbstractUndoableEditType type )
	{
//		final Access ref = stack.createRef();
		if ( top >= stack.size() )
			stack.addElement();
		ref.setIndex( top++ );
		ref.setValue( type.typeIndex(), false );
		end = top;
//		stack.releaseRef( ref );
	}

	@Override
	public void setUndoPoint()
	{
//		final Access ref = stack.createRef();
		if ( top > 0 )
		{
			ref.setIndex( top - 1 );
			ref.setUndoPoint( true );
		}
//		stack.releaseRef( ref );
	}

	/**
	 * Undo until the next undo-point.
	 * <p>
	 * Decrement top. Then undo the element at top. Repeat while the element
	 * below top is not marked as an undo-point.
	 * </p>
	 */
	public void undo()
	{
//		final Access ref = stack.createRef();
		boolean first = true;
		for ( int i = top - 1; i >= 0; --i )
		{
			ref.setIndex( i );
			if ( ref.isUndoPoint() && !first )
				break;
			ref.getType().undo();
			--top;
			first = false;
		}
//		stack.releaseRef( ref );
	}

	/**
	 * Redo until the next undo-point.
	 * <p>
	 * Redo the element at top. Then increment top. Repeat while the element at
	 * the top is not marked as an undo-point.
	 * </p>
	 */
	public void redo()
	{
//		final Access ref = stack.createRef();
		for ( int i = top; i < end; ++i )
		{
			ref.setIndex( i );
			ref.getType().redo();
			++top;
			if ( ref.isUndoPoint() )
				break;
		}
//		stack.releaseRef( ref );
	}

	/**
	 * Truncate entries starting from {@code end}.
	 */
	public void trim()
	{
		stack.remove( end, stack.size() - end );
		stack.trimToSize();
	}

//	/**
//	 * Return the element at {@code top - 1}, i.e., the last recorded element.
//	 * @param ref
//	 * @return
//	 */
//	protected Access peek( final Access ref )
//	{
//		if ( top <= 0 || top >= end )
//			return null;
//		ref.setIndex( top );
//		return ref;
//	}
//
//	/**
//	 * Decrement top. Then return the element at top.
//	 */
//	protected Access undo( final Access ref )
//	{
//		if ( top <= 0 )
//			return null;
//		ref.setIndex( --top );
//		return ref;
//	}
//
//	/**
//	 * Return the element at top. Then increment top.
//	 */
//	protected Access redo( final Access ref )
//	{
//		if ( top >= end )
//			return null;
//		ref.setIndex( top++ );
//		return ref;
//	}

	/**
	 * This is called automatically when instantiating derived {@link AbstractUndoableEditType}s.
	 */
	int addEditType( final AbstractUndoableEditType editType )
	{
		int index;
		synchronized ( editTypes )
		{
			index = editTypes.size();
			editTypes.add( editType );
		}
		if ( index > stack.maxEditTypeIndex() )
			throw new IllegalStateException( "Too many edit types. Time to implement ShortAccess..." );
		return index;
	}

	static interface Access
	{
		/**
		 * Point this {@link Access} to the specified index of the {@link Stack}.
		 * Do not confuse this with {@link #setTypeIndex(int)}!
		 */
		void setIndex( int index );

		void setValue( final int typeIndex, final boolean isUndoPoint );

		AbstractUndoableEditType getType();

		int getTypeIndex();

		void setTypeIndex( final int typeIndex );

		boolean isUndoPoint();

		void setUndoPoint( final boolean isUndoPoint );
	}

	static interface Stack
	{
		Access createRef();

		void releaseRef( Access ref );

		int size();

		void addElement();

		void remove( int offset, int length );

		void trimToSize();

		int maxEditTypeIndex();
	}

	class ByteAccess implements Access
	{
		private int index;

		private final TByteList buf;

		ByteAccess( final TByteList buf )
		{
			this.buf = buf;
		}

		@Override
		public void setIndex( final int index )
		{
			this.index = index;
		}

		@Override
		public void setValue( final int typeIndex, final boolean isUndoPoint )
		{
			buf.set( index, ( byte ) ( isUndoPoint ? ( typeIndex | 0x80 ) : typeIndex ) );
		}

		@Override
		public AbstractUndoableEditType getType()
		{
			return editTypes.get( getTypeIndex() );
		}

		@Override
		public int getTypeIndex()
		{
			return buf.get( index ) & 0x7F;
		}

		@Override
		public void setTypeIndex( final int typeIndex )
		{
			setValue( typeIndex, isUndoPoint() );
		}

		@Override
		public boolean isUndoPoint()
		{
			return buf.get( index ) < 0;
		}

		@Override
		public void setUndoPoint( final boolean isUndoPoint )
		{
			setValue( getTypeIndex(), isUndoPoint );
		}
	}

	class ByteStack implements Stack
	{
		private final TByteArrayList buf;

		private final ConcurrentLinkedQueue< Access > tmpObjRefs;

		ByteStack( final int initialCapacity )
		{
			buf = new TByteArrayList( initialCapacity );
			tmpObjRefs = new ConcurrentLinkedQueue<>();
		}

		@Override
		public Access createRef()
		{
			final Access access = tmpObjRefs.poll();
			return access == null ? new ByteAccess( buf ) : access;
		}

		@Override
		public void releaseRef( final Access access )
		{
			tmpObjRefs.add( access );
		}

		@Override
		public int size()
		{
			return buf.size();
		}

		@Override
		public void addElement()
		{
			buf.add( ( byte ) 0 );
		}

		@Override
		public void trimToSize()
		{
			buf.trimToSize();
		}

		@Override
		public void remove( final int offset, final int length )
		{
			buf.remove( offset, length );
		}

		@Override
		public int maxEditTypeIndex()
		{
			return 127;
		}
	}

//class ShortAccess implements Access
//{
//	TODO
//}

//class ShortStack implements Stack
//{
//	TODO
//}
}
