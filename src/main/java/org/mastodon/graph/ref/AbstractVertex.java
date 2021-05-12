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

import org.mastodon.graph.Vertex;
import org.mastodon.pool.MappedElement;
import org.mastodon.pool.PoolObject;

/**
 * Abstract class for vertices in a Mastodon graph.
 *
 * @param <V>
 *            the concrete type of this vertex.
 * @param <E>
 *            the type of edges in the graph..
 * @param <VP>
 *            the type of the pool on which vertices are built
 * @param <T>
 *            the type of mapped element on which the vertex pool is built.
 * 
 * @author Tobias Pietzsch
 */
public class AbstractVertex<
			V extends AbstractVertex< V, E, VP, T >,
			E extends AbstractEdge< E, ?, ?, ? >,
			VP extends AbstractVertexPool< V, ?, T >,
			T extends MappedElement >
		extends PoolObject< V, VP, T >
		implements Vertex< E >
{
	protected static final int FIRST_IN_EDGE_INDEX_OFFSET = AbstractVertexPool.layout.firstInEdge.getOffset();
	protected static final int FIRST_OUT_EDGE_INDEX_OFFSET = AbstractVertexPool.layout.firstOutEdge.getOffset();

	protected AbstractVertex( final VP pool )
	{
		super( pool );
	}

	protected int getFirstInEdgeIndex()
	{
		return access.getIndex( FIRST_IN_EDGE_INDEX_OFFSET );
	}

	protected void setFirstInEdgeIndex( final int index )
	{
		access.putIndex( index, FIRST_IN_EDGE_INDEX_OFFSET );
	}

	protected int getFirstOutEdgeIndex()
	{
		return access.getIndex( FIRST_OUT_EDGE_INDEX_OFFSET );
	}

	protected void setFirstOutEdgeIndex( final int index )
	{
		access.putIndex( index, FIRST_OUT_EDGE_INDEX_OFFSET );
	}

	@Override
	protected void setToUninitializedState()
	{
		setFirstInEdgeIndex( -1 );
		setFirstOutEdgeIndex( -1 );
	}

	private AbstractEdgePool< E, ?, ? > edgePool;

	private IncomingEdges< E > incomingEdges;

	private OutgoingEdges< E > outgoingEdges;

	private AllEdges< E > edges;

	OutgoingEdgesToTarget< E > outgoingEdgesToTarget;

	@Override
	public IncomingEdges< E > incomingEdges()
	{
		return incomingEdges;
	}

	@Override
	public OutgoingEdges< E > outgoingEdges()
	{
		return outgoingEdges;
	}

	@Override
	public AllEdges< E > edges()
	{
		return edges;
	}

	void linkEdgePool( final AbstractEdgePool< E, ?, ? > edgePool )
	{
		if ( this.edgePool != edgePool )
		{
			this.edgePool = edgePool;
			incomingEdges = new IncomingEdges<>( this, edgePool );
			outgoingEdges = new OutgoingEdges<>( this, edgePool );
			edges = new AllEdges<>( this, edgePool );
			outgoingEdgesToTarget = new OutgoingEdgesToTarget<>( this, edgePool );
		}
	}
}
