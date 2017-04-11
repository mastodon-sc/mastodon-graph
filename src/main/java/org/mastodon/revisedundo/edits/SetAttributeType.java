package org.mastodon.revisedundo.edits;

import static org.mastodon.pool.ByteUtils.INT_SIZE;

import org.mastodon.revisedundo.AbstractUndoableEditType;
import org.mastodon.revisedundo.ByteArrayUndoRedoStack;
import org.mastodon.revisedundo.ByteArrayUndoRedoStack.ByteArrayRef;
import org.mastodon.revisedundo.Recorder;
import org.mastodon.revisedundo.UndoIdBimap;
import org.mastodon.revisedundo.UndoRedoStack;
import org.mastodon.revisedundo.attributes.AttributeSerializer;

public class SetAttributeType< O > extends AbstractUndoableEditType implements Recorder< O >
{
	private final AttributeSerializer< O > serializer;

	private final UndoIdBimap< O > undoIdBimap;

	private final ByteArrayUndoRedoStack dataStack;

	private final int size;

	private final ByteArrayRef ref;

	private final byte[] data;

	private final byte[] swapdata;

	private final static int OBJ_ID_OFFSET = 0;
	private final static int DATA_OFFSET = OBJ_ID_OFFSET + INT_SIZE;

	public SetAttributeType(
			final AttributeSerializer< O > serializer,
			final UndoIdBimap< O > undoIdBimap,
			final ByteArrayUndoRedoStack dataStack,
			final UndoRedoStack undoRedoStack )
	{
		super( undoRedoStack );
		this.serializer = serializer;
		this.undoIdBimap = undoIdBimap;
		this.dataStack = dataStack;
		size = DATA_OFFSET + serializer.getNumBytes();
		ref = dataStack.createRef();
		data = new byte[ size ];
		swapdata = new byte[ size ];
	}

	@Override
	public void record( final O obj )
	{
		final ByteArrayRef buffer = dataStack.record( size, ref );
		final int oi = undoIdBimap.getId( obj );
		buffer.putInt( OBJ_ID_OFFSET, oi );
		serializer.getBytes( obj, data );
		buffer.putBytes( DATA_OFFSET, data );
	}

	@Override
	public void undo()
	{
		swap( dataStack.undo( size, ref ) );
	}

	@Override
	public void redo()
	{
		swap( dataStack.redo( size, ref ) );
	}

	private void swap( final ByteArrayRef buffer )
	{
		final O oref = undoIdBimap.createRef();
		final int oi = buffer.getInt( OBJ_ID_OFFSET );
		final O obj = undoIdBimap.getObject( oi, oref );

		buffer.getBytes( DATA_OFFSET, swapdata );
		serializer.getBytes( obj, data );
		serializer.setBytes( obj, swapdata );
		buffer.putBytes( DATA_OFFSET, data );

		serializer.notifySet( obj );
		undoIdBimap.releaseRef( oref );
	}
}