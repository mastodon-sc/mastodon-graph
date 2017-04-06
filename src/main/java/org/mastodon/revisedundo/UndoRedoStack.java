package org.mastodon.revisedundo;

/**
 * An expandable array of elements with a {@code top} pointer. (Not really a
 * stack because elements above the {@code top} may be retained.)
 *
 * @param <T>
 *            the type of elements stored on the stack.
 *
 * @author Tobias Pietzsch
 */
public interface UndoRedoStack< T >
{
	/**
	 * Put {@code element} at the top of the stack, expanding the stack if
	 * necessary. Then increment top. Then, optionally, clear any elements at
	 * top and beyond.
	 *
	 * @param element
	 *            the element to push
	 */
	public void record( final T element );

	/**
	 * Decrement top. Then return the element at top.
	 */
	public T undo();

	/**
	 * Return the element at top. Then increment top.
	 */
	public T redo();
}
