/*-
 * #%L
 * Mastodon Graphs
 * %%
 * Copyright (C) 2015 - 2021 Tobias Pietzsch, Jean-Yves Tinevez
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

import java.util.Iterator;

import org.mastodon.graph.Edges;

public class OutgoingEdgesToTarget< E extends AbstractEdge< E, ?, ?, ? > > implements Edges< E >
{
	private final AbstractVertex< ?, ?, ?, ? > vertex;
	private final AbstractEdgePool< E, ?, ? > edgePool;

	private OutgoingEdgesToTargetIterator iterator;

	private int targetInternalPoolIndex;

	public OutgoingEdgesToTarget(
			final AbstractVertex< ?, ?, ?, ? > vertex,
			final AbstractEdgePool< E, ?, ? > edgePool )
	{
		this.vertex = vertex;
		this.edgePool = edgePool;

		iterator = null;
	}

	public void setTarget( final AbstractVertex< ?, ?, ?, ? > target )
	{
		targetInternalPoolIndex = target.getInternalPoolIndex();
	}

	@Override
	public int size()
	{
		int numEdges = 0;
		int edgeIndex = vertex.getFirstOutEdgeIndex();
		if ( edgeIndex >= 0 )
		{
			final E edge = edgePool.createRef();
			while ( edgeIndex >= 0 )
			{
				edgePool.getObject( edgeIndex, edge );
				if ( edge.getTargetVertexInternalPoolIndex() == targetInternalPoolIndex )
					++numEdges;
				edgeIndex = edge.getNextSourceEdgeIndex();
			}
			edgePool.releaseRef( edge );
		}
		return numEdges;
	}

	@Override
	public boolean isEmpty()
	{
		return vertex.getFirstOutEdgeIndex() < 0 || size() == 0;
	}

	@Override
	public E get( final int i )
	{
		return get( i, edgePool.createRef() );
	}

	// garbage-free version
	@Override
	public E get( int i, final E edge )
	{
		int edgeIndex = vertex.getFirstOutEdgeIndex();
		while( i-- >= 0 )
		{
			edgePool.getObject( edgeIndex, edge );
			while ( edge.getTargetVertexInternalPoolIndex() != targetInternalPoolIndex )
			{
				edgeIndex = edge.getNextSourceEdgeIndex();
				edgePool.getObject( edgeIndex, edge );
			}
			edgeIndex = edge.getNextSourceEdgeIndex();
		}
		return edge;
	}

	@Override
	public OutgoingEdgesToTargetIterator iterator()
	{
		if ( iterator == null )
			iterator = new OutgoingEdgesToTargetIterator();
		else
			iterator.reset();
		return iterator;
	}

	@Override
	public OutgoingEdgesToTargetIterator safe_iterator()
	{
		return new OutgoingEdgesToTargetIterator();
	}

	public class OutgoingEdgesToTargetIterator implements Iterator< E >
	{
		private int edgeIndex;

		private final E edge;

		public OutgoingEdgesToTargetIterator()
		{
			this.edge = edgePool.createRef();
			reset();
		}

		public void reset()
		{
			edgeIndex = vertex.getFirstOutEdgeIndex();
			while ( edgeIndex >= 0 )
			{
				edgePool.getObject( edgeIndex, edge );
				if ( edge.getTargetVertexInternalPoolIndex() == targetInternalPoolIndex )
					break;
				edgeIndex = edge.getNextSourceEdgeIndex();
			}
		}

		private void prefetch()
		{
			edgePool.getObject( edgeIndex, edge );
			edgeIndex = edge.getNextSourceEdgeIndex();
			while ( edgeIndex >= 0 )
			{
				edgePool.getObject( edgeIndex, edge );
				if ( edge.getTargetVertexInternalPoolIndex() == targetInternalPoolIndex )
					break;
				edgeIndex = edge.getNextSourceEdgeIndex();
			}
		}

		@Override
		public boolean hasNext()
		{
			return edgeIndex >= 0;
		}

		@Override
		public E next()
		{
			final int i = edgeIndex;
			prefetch();
			edgePool.getObject( i, edge );
			return edge;
		}

		@Override
		public void remove()
		{
			edgePool.delete( edge );
		}
	}
}
