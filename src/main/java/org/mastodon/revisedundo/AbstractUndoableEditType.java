package org.mastodon.revisedundo;

/**
 * Abstract base class for the UndoableEdit types to be recorded in {@link UndoRedoStack}.
 *
 * @param <T>
 *            the {@link AbstractUndoableEdit} type.
 */
public abstract class AbstractUndoableEditType
{
	private final UndoRedoStack undoRedoStack;

	private final int typeIndex;

	public AbstractUndoableEditType( final UndoRedoStack undoRedoStack )
	{
		this.undoRedoStack = undoRedoStack;
		this.typeIndex = undoRedoStack.addEditType( this );
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

	protected void recordType()
	{
		undoRedoStack.record( this );
	}

	public abstract void undo();

	public abstract void redo();
}
