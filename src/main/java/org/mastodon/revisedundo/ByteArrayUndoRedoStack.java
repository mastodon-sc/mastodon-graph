package org.mastodon.revisedundo;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A undo/redo stack for byte arrays of variable size. This is used to record
 * graph and attribute changes.
 *
 * @author Tobias Pietzsch
 */
public class ByteArrayUndoRedoStack
{
	private static final int DEFAULT_CAPACITY = 1024 * 1024 * 8;

	private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 2;

	/**
	 * The current data storage. This is changed when the array is
	 * {@link #resize(int) resized}.
	 */
	byte[] buf;

	public static class ByteArrayRef
	{
		final ByteArrayUndoRedoStack pool;

		int offset;

		ByteArrayRef( final ByteArrayUndoRedoStack pool )
		{
			this.pool = pool;
		}

		public void putInt( final int index, final int value )
		{
			// TODO
			ByteBuffer.wrap( pool.buf ).putInt( offset + index, value );
		}

		public int getInt( final int index )
		{
			// TODO
			return ByteBuffer.wrap( pool.buf ).getInt( offset + index );
		}

		public void putShort( final int index, final short value )
		{
			// TODO
			ByteBuffer.wrap( pool.buf ).putShort( offset + index, value );
		}

		public short getShort( final int index )
		{
			// TODO
			return ByteBuffer.wrap( pool.buf ).getShort( offset + index );
		}

		public void putBytes( final int index, final byte[] array )
		{
			putBytes( index, array, 0, array.length );
		}

		public void getBytes( final int index, final byte[] array )
		{
			getBytes( index, array, 0, array.length );
		}

		public void putBytes( final int index, final byte[] array, final int offset, final int length )
		{
			// TODO
			final ByteBuffer bb = ByteBuffer.wrap( pool.buf );
			bb.position( offset + index );
			bb.put( array, offset, length );
		}

		public void getBytes( final int index, final byte[] array, final int offset, final int length )
		{
			// TODO
			final ByteBuffer bb = ByteBuffer.wrap( pool.buf );
			bb.position( offset + index );
			bb.get( array, offset, length );
		}
	}

	private final ConcurrentLinkedQueue< ByteArrayRef > tmpObjRefs;

	private int top;

	public ByteArrayUndoRedoStack()
	{
		this( DEFAULT_CAPACITY );
	}

	public ByteArrayUndoRedoStack( final int capacity )
	{
		buf = new byte[ capacity ];
		tmpObjRefs = new ConcurrentLinkedQueue<>();
		top = 0;
	}

	//  stack[top++] := e
	public ByteArrayRef record( final int size, final ByteArrayRef ref )
	{
		ref.offset = top;
		top += size;
		ensureCapacity( top );
		return ref;
	}

	// return stack[--top]
	public ByteArrayRef undo( final int size, final ByteArrayRef ref )
	{
		if ( top - size < 0 )
			throw new IllegalStateException();

		top -= size;
		ref.offset = top;
		return ref;
	}

	// return stack[top++]
	public ByteArrayRef redo( final int size, final ByteArrayRef ref )
	{
		if ( top + size > buf.length )
			throw new IllegalStateException();

		ref.offset = top;
		top += size;
		return ref;
	}

	public ByteArrayRef createRef()
	{
		final ByteArrayRef obj = tmpObjRefs.poll();
		return obj == null ? new ByteArrayRef( this ) : obj;
	}

	public void releaseRef( final ByteArrayRef obj )
	{
		if ( obj.pool == this )
			tmpObjRefs.add( obj );
		else
			obj.pool.releaseRef( obj );
	}

	private void ensureCapacity( final int minCapacity )
	{
		if ( minCapacity < 0 )
			throw new OutOfMemoryError( "array size too big: " + ( minCapacity & 0xffffffffL ) );
		if ( buf.length < minCapacity )
		{
			final long capacity = Math.min(
					MAX_ARRAY_SIZE,
					Math.max( buf.length << 1, minCapacity ) );
			if ( capacity < minCapacity )
				throw new OutOfMemoryError( "array size too big: " + minCapacity );
			buf = Arrays.copyOf( buf, ( int ) capacity );
		}
	}
}
