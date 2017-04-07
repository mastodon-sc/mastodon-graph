package org.mastodon.revisedundo;

/**
 * Abstract base class for the UndoableEdit types to be recorded in {@link UndoRedoStack}.
 *
 * @param <T>
 *            the {@link AbstractUndoableEdit} type.
 */
public abstract class AbstractUndoableEditType
{
	int typeIndex;

	public AbstractUndoableEditType( final UndoRedoStack undoRedoStack )
	{
		undoRedoStack.addEditType( this );
	}

	/**
	 * Get the unique index of this edit type.
	 *
	 * @return the unique index associated to T.
	 */
	protected final int typeIndex()
	{
		return typeIndex;
	}

	public abstract void undo();

	public abstract void redo();
}
