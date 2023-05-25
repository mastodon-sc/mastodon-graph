/*-
 * #%L
 * Mastodon Graphs
 * %%
 * Copyright (C) 2015 - 2022 Tobias Pietzsch, Jean-Yves Tinevez
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
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
	private final WeakReference< SpatioTemporalIndexImp< ?, ? > > weakReference;

	private final int modCountThreshold;

	private final long timeout;

	private final boolean rebuildAll;

	private SpatioTemporalIndexImp< ?, ? > index;

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
		setDaemon( true );
		this.index = index;
		this.weakReference = new WeakReference<>( index );
		this.modCountThreshold = modCountThreshold;
		this.timeout = timeout;
		this.rebuildAll = rebuildAll;
	}

	@Override
	public void run()
	{
		while ( index != null && !isInterrupted() )
		{
			while ( index.rebuildAny( modCountThreshold ) && rebuildAll )
			{}

			// NB: The strong reference to the index must be cleared before waiting.
			// Otherwise, the index would never be garbage-collected.
			index = null;

			try
			{
				Thread.sleep( timeout );
			}
			catch ( final InterruptedException e )
			{
				break;
			}

			// Restore the strong reference to the index. It will still be null if
			// the spatio-temporal index was garbage-collected during waiting.
			index = weakReference.get();
		}
	}

}
