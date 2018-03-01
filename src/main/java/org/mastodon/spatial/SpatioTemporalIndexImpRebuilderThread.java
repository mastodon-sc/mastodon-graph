package org.mastodon.spatial;

import java.lang.ref.WeakReference;

/**
 * A spatio-temporal index rebuilder thread. In its {@code run()} method, the
 * thread periodically tries to rebuild spatial indices comprising a
 * {@link SpatioTemporalIndexImp}, one by one.
 *
 * @author Tobias Pietzsch
 */
public class SpatioTemporalIndexImpRebuilderThread extends Thread
{
	private final WeakReference< SpatioTemporalIndexImp< ?, ? > > index;

	private final int modCountThreshold;

	private final long timeout;

	private final boolean rebuildAll;

	/**
	 * Create a new spatio-temporal index rebuilder thread. In its {@code run()}
	 * method, the thread periodically tries to rebuild spatial indices one by
	 * one. The thread only keeps a {@link WeakReference} to the index and will
	 * terminate if the index is garbage-collected.
	 *
	 * @param name
	 *            the name of the new thread
	 * @param index
	 *            the spatio-temporal index that the new thread will take care
	 *            of rebuilding.
	 * @param modCountThreshold
	 *            how many modifications should have happened (at least) to a
	 *            {@code SpatialIndexData} to make it eligible for rebuilding.
	 * @param timeout
	 *            how many milliseconds the new thread should wait before
	 *            attempting to rebuild indices.
	 * @param rebuildAll
	 *            if {@code true} then during a rebuild, all eligible indices
	 *            are rebuild. if {@code false} then during a rebuild, only one
	 *            eligible index is rebuild.
	 */
	public SpatioTemporalIndexImpRebuilderThread(
			final String name,
			final SpatioTemporalIndexImp< ?, ? > index,
			final int modCountThreshold,
			final long timeout,
			final boolean rebuildAll )
	{
		super( name );
		this.index = new WeakReference<>( index );
		this.modCountThreshold = modCountThreshold;
		this.timeout = timeout;
		this.rebuildAll = rebuildAll;
	}

	@Override
	public void run()
	{
		while ( !isInterrupted() )
		{
			final SpatioTemporalIndexImp< ?, ? > i = index.get();
			if ( i == null )
				break;

			while ( i.rebuildAny( modCountThreshold ) && rebuildAll )
			{}

			synchronized ( this )
			{
				try
				{
					wait( timeout );
				}
				catch ( final InterruptedException e )
				{
					break;
				}
			}
		}
	}

}
