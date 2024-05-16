/*-
 * #%L
 * Mastodon Graphs
 * %%
 * Copyright (C) 2015 - 2024 Tobias Pietzsch, Jean-Yves Tinevez
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
package org.mastodon.graph.ref;

import java.util.ArrayList;

import org.mastodon.graph.GraphChangeListener;
import org.mastodon.graph.GraphListener;
import org.mastodon.graph.ListenableGraph;
import org.mastodon.pool.MappedElement;

public class ListenableGraphImp<
		VP extends AbstractListenableVertexPool< V, E, T >,
		EP extends AbstractListenableEdgePool< E, V, T >,
		V extends AbstractListenableVertex< V, E, VP, T >,
		E extends AbstractListenableEdge< E, V, EP, T >,
		T extends MappedElement >
	extends GraphImp< VP, EP, V, E, T >
	implements ListenableGraph< V, E >
{
	protected final ArrayList< GraphListener< V, E > > listeners;

	protected final ArrayList< GraphChangeListener > changeListeners;

	protected boolean emitEvents;

	public ListenableGraphImp( final VP vertexPool, final EP edgePool )
	{
		super( vertexPool, edgePool );
		vertexPool.linkNotify( notifyPostInit );
		edgePool.linkNotify( notifyPostInit );
		listeners = new ArrayList<>();
		changeListeners = new ArrayList<>();
		emitEvents = true;
	}

	public ListenableGraphImp( final EP edgePool )
	{
		super( edgePool );
		vertexPool.linkNotify( notifyPostInit );
		edgePool.linkNotify( notifyPostInit );
		listeners = new ArrayList<>();
		changeListeners = new ArrayList<>();
		emitEvents = true;
	}

	private final NotifyPostInit< V, E > notifyPostInit = new NotifyPostInit< V, E >()
	{
		@Override
		public void notifyVertexAdded( final V vertex )
		{
			ListenableGraphImp.this.notifyVertexAdded( vertex );
		}

		@Override
		public void notifyEdgeAdded( final E edge )
		{
			ListenableGraphImp.this.notifyEdgeAdded( edge );
		}
	};

	@Override
	public void remove( final V vertex )
	{
		if ( emitEvents )
		{
			for ( final E edge : vertex.edges() )
				for ( final GraphListener< V, E > listener : listeners )
					listener.edgeRemoved( edge );
			for ( final GraphListener< V, E > listener : listeners )
				listener.vertexRemoved( vertex );
		}
		vertexPool.delete( vertex );
	}

	@Override
	public void remove( final E edge )
	{
		if ( emitEvents )
			for ( final GraphListener< V, E > listener : listeners )
				listener.edgeRemoved( edge );
		edgePool.delete( edge );
	}

	@Override
	public synchronized boolean addGraphListener( final GraphListener< V, E > listener )
	{
		if ( ! listeners.contains( listener ) )
		{
			listeners.add( listener );
			return true;
		}
		return false;
	}

	@Override
	public synchronized boolean removeGraphListener( final GraphListener< V, E > listener )
	{
		return listeners.remove( listener );
	}

	@Override
	public synchronized boolean addGraphChangeListener( final GraphChangeListener listener )
	{
		if ( ! changeListeners.contains( listener ) )
		{
			changeListeners.add( listener );
			return true;
		}
		return false;
	}

	@Override
	public synchronized boolean removeGraphChangeListener( final GraphChangeListener listener )
	{
		return changeListeners.remove( listener );
	}

	protected void notifyVertexAdded( final V vertex )
	{
		if ( emitEvents )
			for ( final GraphListener< V, E > listener : listeners )
				listener.vertexAdded( vertex );
	}

	protected void notifyEdgeAdded( final E edge )
	{
		if ( emitEvents )
			for ( final GraphListener< V, E > listener : listeners )
				listener.edgeAdded( edge );
	}

	/**
	 * Pause sending events to {@link GraphListener}s. This is called before
	 * large modifications to the graph are made, for example when the graph is
	 * loaded from a file.
	 * <p>
	 * Note that pausing and resuming listeners basically means that undo
	 * history is lost and restarted.
	 */
	protected void pauseListeners()
	{
		emitEvents = false;
		vertexPool.getProperties().pauseListeners();
		edgePool.getProperties().pauseListeners();
	}

	/**
	 * Resume sending events to {@link GraphListener}s, and send
	 * {@link GraphListener#graphRebuilt()} to all registered listeners. This is
	 * called after large modifications to the graph are made, for example when
	 * the graph is loaded from a file.
	 * <p>
	 * Note that pausing and resuming listeners basically means that undo
	 * history is lost and restarted.
	 */
	protected void resumeListeners()
	{
		emitEvents = true;
		vertexPool.getProperties().resumeListeners();
		edgePool.getProperties().resumeListeners();
		for ( final GraphListener< V, E > listener : listeners )
			listener.graphRebuilt();
	}

	/**
	 * Send {@link GraphChangeListener#graphChanged() graphChanged} event to all
	 * {@link GraphChangeListener} (if sending events is not currently
	 * {@link #pauseListeners() paused}).
	 */
	protected void notifyGraphChanged()
	{
		if ( emitEvents )
			for ( final GraphChangeListener listener : changeListeners )
				listener.graphChanged();
	}
}
