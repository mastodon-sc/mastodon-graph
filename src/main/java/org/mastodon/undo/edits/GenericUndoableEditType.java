package org.mastodon.undo.edits;

import org.mastodon.undo.AbstractUndoableEditType;
import org.mastodon.undo.Recorder;
import org.mastodon.undo.UndoRedoStack;
import org.mastodon.undo.UndoableEdit;
import org.mastodon.undo.UndoableEditUndoRedoStack;

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
