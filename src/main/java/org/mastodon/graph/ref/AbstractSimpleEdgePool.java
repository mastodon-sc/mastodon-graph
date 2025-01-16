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

import org.mastodon.pool.MappedElement;
import org.mastodon.pool.MemPool;

/**
 * Mother class for edge pools of <b>simple directed</b> graphs.
 * <p>
 * Simple, directed graphs are graphs for which there is at most one directed
 * edge between a source and a target vertices (0 or 1, but there might another
 * edge in the opposite direction). This is enforced here by preventing adding
 * an edge between a source and a target already linked by an edge.
 *
 * @param <E>
 *            the edge type.
 * @param <V>
 *            the vertex type.
 * @param <T>
 *            the MappedElement type of the pool.
 */
public abstract class AbstractSimpleEdgePool<
			E extends AbstractEdge< E, V, ?, T >,
			V extends AbstractVertex< V, ?, ?, ? >,
			T extends MappedElement >
		extends AbstractEdgePool< E, V, T >
{
	public AbstractSimpleEdgePool(
			final int initialCapacity,
			final AbstractEdgeLayout layout,
			final Class< E > edgeClass,
			final MemPool.Factory< T > memPoolFactory,
			final AbstractVertexPool< V, ?, ? > vertexPool )
	{
		super( initialCapacity, layout, edgeClass, memPoolFactory, vertexPool );
	}

	/**
	 * Adds an edge between the specified source and target.
	 * <p>
	 * If an edge already exists between this source and target (with this
	 * direction), the edge is not added and this method returns
	 * <code>null</code>.
	 *
	 * @param source
	 *            the source vertex.
	 * @param target
	 *            the target vertex.
	 * @param edge
	 *            a reference object used for operation.
	 * @return the added edge, or <code>null</code> if an edge already exists
	 *         between source and target.
	 */
	@Override
	public E addEdge( final AbstractVertex< ?, ?, ?, ? > source, final AbstractVertex< ?, ?, ?, ? > target, final E edge )
	{
		if ( getEdge( source, target, edge ) != null )
			return null;

		return super.addEdge( source, target, edge );
	}

	/**
	 * Inserts an edge between the specified source and target, at the specified
	 * positions in the edge lists of the source and target vertices.
	 * <p>
	 * If an edge already exists between this source and target (with this
	 * direction), the edge is not added and this method returns
	 * <code>null</code>.
	 *
	 * @param source
	 *            the source vertex.
	 * @param sourceOutInsertAt
	 *            the position the created edge is to be inserted in the source
	 *            vertex outgoing edge list.
	 * @param target
	 *            the target vertex.
	 * @param targetInInsertAt
	 *            the position the created edge is to be inserted in the target
	 *            vertex incoming edge list.
	 * @param edge
	 *            a reference object used for operation.
	 * @return the added edge, or <code>null</code> if an edge already exists
	 *         between source and target.
	 */
	@Override
	public E insertEdge( final AbstractVertex< ?, ?, ?, ? > source, final int sourceOutInsertAt, final AbstractVertex< ?, ?, ?, ? > target, final int targetInInsertAt, final E edge )
	{
		if ( getEdge( source, target, edge ) != null )
			return null;

		return super.insertEdge( source, sourceOutInsertAt, target, targetInInsertAt, edge );
	}
}
