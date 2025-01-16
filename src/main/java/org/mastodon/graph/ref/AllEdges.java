/*-
 * #%L
 * Mastodon Graphs
 * %%
 * Copyright (C) 2015 - 2025 Tobias Pietzsch, Jean-Yves Tinevez
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

public class AllEdges< E extends AbstractEdge< E, ?, ?, ? > > implements Edges< E >
{
	private final AbstractVertex< ?, ?, ?, ? > vertex;
	private final AbstractEdgePool< E, ?, ? > edgePool;

	private EdgesIterator iterator;

	public AllEdges(
			final AbstractVertex< ?, ?, ?, ? > vertex,
			final AbstractEdgePool< E, ?, ? > edgePool )
	{
		this.vertex = vertex;
		this.edgePool = edgePool;

		iterator = null;
	}

	@Override
	public int size()
	{
		int numEdges = 0;
		int inEdgeIndex = vertex.getFirstInEdgeIndex();
		int outEdgeIndex = vertex.getFirstOutEdgeIndex();
		if ( inEdgeIndex >= 0 || outEdgeIndex >= 0 )
		{
			final E edge = edgePool.createRef();
			if ( inEdgeIndex >= 0 )
			{
				while ( inEdgeIndex >= 0 )
				{
					++numEdges;
					edgePool.getObject( inEdgeIndex, edge );
					inEdgeIndex = edge.getNextTargetEdgeIndex();
				}
			}
			if ( outEdgeIndex >= 0 )
			{
				while ( outEdgeIndex >= 0 )
				{
					++numEdges;
					edgePool.getObject( outEdgeIndex, edge );
					outEdgeIndex = edge.getNextSourceEdgeIndex();
				}
			}
			edgePool.releaseRef( edge );
		}
		return numEdges;
	}

	@Override
	public boolean isEmpty()
	{
		return vertex.getFirstInEdgeIndex() < 0 && vertex.getFirstOutEdgeIndex() < 0;
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
		boolean in = true;
		int edgeIndex = vertex.getFirstInEdgeIndex();
		if ( edgeIndex < 0 )
		{
			in = false;
			edgeIndex = vertex.getFirstOutEdgeIndex();
		}
		edgePool.getObject( edgeIndex, edge );
		while( i-- > 0 )
		{
			if ( in )
			{
				edgeIndex = edge.getNextTargetEdgeIndex();
				if ( edgeIndex < 0 )
				{
					in = false;
					edgeIndex = vertex.getFirstOutEdgeIndex();
				}
			}
			else
				edgeIndex = edge.getNextSourceEdgeIndex();
			edgePool.getObject( edgeIndex, edge );
		}
		return edge;
	}

	@Override
	public EdgesIterator iterator()
	{
		if ( iterator == null )
			iterator = new EdgesIterator();
		else
			iterator.reset();
		return iterator;
	}

	@Override
	public EdgesIterator safe_iterator()
	{
		return new EdgesIterator();
	}

	public class EdgesIterator implements Iterator< E >
	{
		private int edgeIndex;

		private boolean in;

		private final E edge;

		public EdgesIterator()
		{
			this.edge = edgePool.createRef();
			reset();
		}

		public void reset()
		{
			edgeIndex = vertex.getFirstInEdgeIndex();
			if ( edgeIndex < 0 )
			{
				in = false;
				edgeIndex = vertex.getFirstOutEdgeIndex();
			}
			else
				in = true;
		}

		@Override
		public boolean hasNext()
		{
			return edgeIndex >= 0;
		}

		@Override
		public E next()
		{
			edgePool.getObject( edgeIndex, edge );
			if ( in )
			{
				edgeIndex = edge.getNextTargetEdgeIndex();
				if ( edgeIndex < 0 )
				{
					in = false;
					edgeIndex = vertex.getFirstOutEdgeIndex();
				}
			}
			else
				edgeIndex = edge.getNextSourceEdgeIndex();
			return edge;
		}

		@Override
		public void remove()
		{
			edgePool.delete( edge );
		}
	}

	@Override
	public String toString()
	{

		final Iterator< E > i = iterator();
		if ( !i.hasNext() )
			return "[]";

		final StringBuilder sb = new StringBuilder();
		sb.append( '[' );
		for ( ;; )
		{
			final E e = i.next();
			sb.append( e );
			if ( !i.hasNext() )
				return sb.append( ']' ).toString();
			sb.append( ", " );
		}
	}
}
