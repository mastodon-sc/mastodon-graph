package org.mastodon.revisedundo;

import java.util.ArrayList;

/**
 * A Undo/Redo stack of {@link UndoableEdit}s. This is used to record
 * {@link UndoableEdit}s that are <em>not represented by pool objects</em>.
 * TODO: clarify
 * TODO: clarify
 * TODO: clarify
 * <p>
 * In principle it is an expandable array of elements with a {@code top}
 * pointer/index. This is not really a stack: {@code top} might point to
 * somewhere in the middle of the array, i.e., elements above the top may be
 * retained. Basically, {@code top} points to where the next action would be
 * recorded, and/or where the next (previously recorded and undone) action for
 * redo is stored.
 * </p>
 *
 * @author Tobias Pietzsch
 */
public class UndoableEditUndoRedoStack
{
	private final ArrayList< UndoableEdit > stack;

	private int top;

	private int end;

	public UndoableEditUndoRedoStack()
	{
		stack = new ArrayList<>();
		top = 0;
		end = 0;
	}

	/**
	 * Put {@code edit} at the top of the stack, expanding the stack if
	 * necessary. Increment top. Clear any elements at top and beyond.
	 *
	 * @param element
	 *            the element to push
	 */
	public void record( final UndoableEdit edit )
	{
		if ( top < stack.size() )
			stack.set( top, edit );
		else
			stack.add( edit );
		end = ++top;
	}

	/**
	 * Decrement top. Then call {@link UndoableEdit#undo() undo()} for the edit
	 * stored there.
	 */
	public void undo()
	{
		if ( top > 0 )
			stack.get( --top ).undo();
	}

	/**
	 * Call {@link UndoableEdit#redo() redo()} for the edit
	 * stored at {@code top}. Then increment {@code top}.
	 */
	public void redo()
	{
		if ( top < end )
			stack.get( top++ ).redo();
	}

	/**
	 * Truncate entries starting from {@code end}.
	 */
	public void trim()
	{
		while ( stack.size() > end )
			stack.remove( end );
		stack.trimToSize();
	}
}
