package org.mastodon.revisedundo.edits;

import org.mastodon.revisedundo.AbstractUndoableEditType;
import org.mastodon.revisedundo.Recorder;
import org.mastodon.revisedundo.UndoRedoStack;
import org.mastodon.revisedundo.UndoableEdit;
import org.mastodon.revisedundo.UndoableEditUndoRedoStack;

public class GenericUndoableEditType< E extends UndoableEdit > extends AbstractUndoableEditType implements Recorder< E >
{
	private final UndoableEditUndoRedoStack edits;

	public GenericUndoableEditType( final UndoRedoStack undoRedoStack )
	{
		super( undoRedoStack );
		edits = new UndoableEditUndoRedoStack();
	}

	@Override
	public void record( final E edit )
	{
		recordType();
		edits.record( edit );
	}

	@Override
	public void undo()
	{
		edits.undo();
	}

	@Override
	public void redo()
	{
		edits.redo();
	}
}
