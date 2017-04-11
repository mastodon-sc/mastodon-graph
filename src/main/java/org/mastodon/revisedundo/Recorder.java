package org.mastodon.revisedundo;

/**
 * Functor to record an undoable edit concerning one object of type {@code O}.
 *
 * @param <O>
 *            the type of object about which to record an undoable action.
 *
 * @author Tobias Pietzsch
 */
public interface Recorder< O >
{
	public void record( final O obj );
}
