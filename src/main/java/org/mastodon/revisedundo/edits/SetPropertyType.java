package org.mastodon.revisedundo.edits;

import static org.mastodon.pool.ByteUtils.INT_SIZE;

import org.mastodon.properties.undo.PropertyUndoRedoStack;
import org.mastodon.revisedundo.AbstractUndoableEditType;
import org.mastodon.revisedundo.ByteArrayUndoRedoStack;
import org.mastodon.revisedundo.ByteArrayUndoRedoStack.ByteArrayRef;
import org.mastodon.revisedundo.Recorder;
import org.mastodon.revisedundo.UndoIdBimap;
import org.mastodon.revisedundo.UndoRedoStack;
import org.mastodon.revisedundo.UndoRedoStack.Element;

public class SetPropertyType< O > extends AbstractUndoableEditType implements Recorder< O >
{
	private final PropertyUndoRedoStack< O > propertyUndoRedoStack;

	private final UndoIdBimap< O > undoIdBimap;

	private final ByteArrayUndoRedoStack dataStack;

	private final ByteArrayRef ref;

	private final Element elmtRef;

	private final static int OBJ_ID_OFFSET = 0;
	private final static int SIZE = OBJ_ID_OFFSET + INT_SIZE;

	public SetPropertyType(
			final PropertyUndoRedoStack< O > propertyUndoRedoStack,
			final UndoIdBimap< O > undoIdBimap,
			final ByteArrayUndoRedoStack dataStack,
			final UndoRedoStack undoRedoStack )
	{
		super( undoRedoStack );
		this.propertyUndoRedoStack = propertyUndoRedoStack;
		this.undoIdBimap = undoIdBimap;
		this.dataStack = dataStack;
		ref = dataStack.createRef();
		elmtRef = undoRedoStack.createRef();
	}

	@Override
	public void record( final O obj )
	{
		final int oi = undoIdBimap.getId( obj );

		final Element peek = undoRedoStack.peek( elmtRef );
		if ( peek != null && peek.isUndoPoint() == false && peek.getType() == this )
		{
			final ByteArrayRef buffer = dataStack.peek( SIZE, ref );
			if ( buffer != null && buffer.getInt( OBJ_ID_OFFSET ) == oi )
				return; // fuse with previous edit (of same type and object)
		}

		recordType();
		final ByteArrayRef buffer = dataStack.record( SIZE, ref );
		buffer.putInt( OBJ_ID_OFFSET, oi );
		propertyUndoRedoStack.record( obj );
	}

	@Override
	public void undo()
	{
		final O oref = undoIdBimap.createRef();
		final ByteArrayRef buffer = dataStack.undo( SIZE, ref );
		final int oi = buffer.getInt( OBJ_ID_OFFSET );
		final O obj = undoIdBimap.getObject( oi, oref );
		propertyUndoRedoStack.undo( obj );
		undoIdBimap.releaseRef( oref );
	}

	@Override
	public void redo()
	{
		final O oref = undoIdBimap.createRef();
		final ByteArrayRef buffer = dataStack.redo( SIZE, ref );
		final int oi = buffer.getInt( OBJ_ID_OFFSET );
		final O obj = undoIdBimap.getObject( oi, oref );
		propertyUndoRedoStack.redo( obj );
		undoIdBimap.releaseRef( oref );
	}
}