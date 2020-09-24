/*-
 * #%L
 * Mastodon Graphs
 * %%
 * Copyright (C) 2015 - 2020 Tobias Pietzsch, Jean-Yves Tinevez
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

import org.mastodon.graph.Edge;
import org.mastodon.pool.MappedElement;
import org.mastodon.pool.PoolObject;

/**
 * Abstract mother class for edges in a Mastodon graph.
 *
 * @param <E>
 *            the concrete type of this edge.
 * @param <V>
 *            the type of vertex in the graph.
 * @param <EP>
 *            the type of the pool on which edges are built
 * @param <T>
 *            the type of mapped element on which the edge pool is built.
 *
 * @author Tobias Pietzsch
 */
public class AbstractEdge<
			E extends AbstractEdge< E, V, EP, T >,
			V extends AbstractVertex< V, ?, ?, ? >,
			EP extends AbstractEdgePool< E, V, T >,
			T extends MappedElement >
		extends PoolObject< E, EP, T >
		implements Edge< V >
{
	protected static final int SOURCE_INDEX_OFFSET = AbstractEdgePool.layout.source.getOffset();
	protected static final int TARGET_INDEX_OFFSET = AbstractEdgePool.layout.target.getOffset();
	protected static final int NEXT_SOURCE_EDGE_INDEX_OFFSET = AbstractEdgePool.layout.nextSourceEdge.getOffset();
	protected static final int NEXT_TARGET_EDGE_INDEX_OFFSET = AbstractEdgePool.layout.nextTargetEdge.getOffset();

	protected final AbstractVertexPool< V, ?, ? > vertexPool;

	protected AbstractEdge( final EP pool )
	{
		super( pool );
		this.vertexPool = pool.vertexPool;
	}

	protected int getSourceVertexInternalPoolIndex()
	{
		return access.getIndex( SOURCE_INDEX_OFFSET );
	}

	protected void setSourceVertexInternalPoolIndex( final int index )
	{
		access.putIndex( index, SOURCE_INDEX_OFFSET );
	}

	protected int getTargetVertexInternalPoolIndex()
	{
		return access.getIndex( TARGET_INDEX_OFFSET );
	}

	protected void setTargetVertexInternalPoolIndex( final int index )
	{
		access.putIndex( index, TARGET_INDEX_OFFSET );
	}

	protected int getNextSourceEdgeIndex()
	{
		return access.getIndex( NEXT_SOURCE_EDGE_INDEX_OFFSET );
	}

	protected void setNextSourceEdgeIndex( final int index )
	{
		access.putIndex( index, NEXT_SOURCE_EDGE_INDEX_OFFSET );
	}

	protected int getNextTargetEdgeIndex()
	{
		return access.getIndex( NEXT_TARGET_EDGE_INDEX_OFFSET );
	}

	protected void setNextTargetEdgeIndex( final int index )
	{
		access.putIndex( index, NEXT_TARGET_EDGE_INDEX_OFFSET );
	}

	@Override
	protected void setToUninitializedState()
	{
		setNextSourceEdgeIndex( -1 );
		setNextTargetEdgeIndex( -1 );
	}

	@Override
	public V getSource()
	{
		return getSource( vertexPool.createRef() );
	}

	@Override
	public V getSource( final V vertex )
	{
		vertexPool.getObject( getSourceVertexInternalPoolIndex(), vertex );
		return vertex;
	}

	@Override
	public int getSourceOutIndex()
	{
		final V ref = vertexPool.createRef();
		final V source = getSource( ref );
		int outIndex = 0;
		for ( final Object e : source.outgoingEdges() )
		{
			if ( e.equals( this ) )
				break;
			++outIndex;
		}
		vertexPool.releaseRef( ref );
		return outIndex;
	}

	@Override
	public V getTarget()
	{
		return getTarget( vertexPool.createRef() );
	}

	@Override
	public V getTarget( final V vertex )
	{
		vertexPool.getObject( getTargetVertexInternalPoolIndex(), vertex );
		return vertex;
	}

	@Override
	public int getTargetInIndex()
	{
		final V ref = vertexPool.createRef();
		final V target = getTarget( ref );
		int inIndex = 0;
		for ( final Object e : target.incomingEdges() )
		{
			if ( e.equals( this ) )
				break;
			++inIndex;
		}
		vertexPool.releaseRef( ref );
		return inIndex;
	}
}
