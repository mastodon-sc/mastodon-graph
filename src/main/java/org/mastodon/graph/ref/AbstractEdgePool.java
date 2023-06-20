/*-
 * #%L
 * Mastodon Graphs
 * %%
 * Copyright (C) 2015 - 2023 Tobias Pietzsch, Jean-Yves Tinevez
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
import org.mastodon.pool.Pool;
import org.mastodon.pool.PoolObjectLayout;

/**
 * Mother class for edge pools of <b>directed</b> graphs.
 * <p>
 * Graphs based on this edge pool do not have a limitation on the number of
 * edges between a source and target vertices.
 *
 * @param <E>
 *            the edge type.
 * @param <V>
 *            the vertex type.
 * @param <T>
 *            the MappedElement type of the pool.
 */
public abstract class AbstractEdgePool<
			E extends AbstractEdge< E, V, ?, T >,
			V extends AbstractVertex< V, ?, ?, ? >,
			T extends MappedElement >
		extends Pool< E, T >
{
	final AbstractVertexPool< V, ?, ? > vertexPool;

	public static class AbstractEdgeLayout extends PoolObjectLayout
	{
		final IndexField source = indexField();
		final IndexField target = indexField();
		final IndexField nextSourceEdge = indexField();
		final IndexField nextTargetEdge = indexField();
	}

	public static AbstractEdgeLayout layout = new AbstractEdgeLayout();

	public AbstractEdgePool(
			final int initialCapacity,
			final AbstractEdgeLayout layout,
			final Class< E > edgeClass,
			final MemPool.Factory< T > memPoolFactory,
			final AbstractVertexPool< V, ?, ? > vertexPool )
	{
		super( initialCapacity, layout, edgeClass, memPoolFactory );
		this.vertexPool = vertexPool;
	}

	/**
	 * Adds an edge between the specified source and target.
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
	public E addEdge( final AbstractVertex< ?, ?, ?, ? > source, final AbstractVertex< ?, ?, ?, ? > target, final E edge )
	{
		create( edge );
		edge.setSourceVertexInternalPoolIndex( source.getInternalPoolIndex() );
		edge.setTargetVertexInternalPoolIndex( target.getInternalPoolIndex() );

		final E tmp = createRef();

		final int sourceOutIndex = source.getFirstOutEdgeIndex();
		if ( sourceOutIndex < 0 )
		{
			// source has no outgoing edge yet. Set this one as the first.
			source.setFirstOutEdgeIndex( edge.getInternalPoolIndex() );
		}
		else
		{
			// source has outgoing edges. Append this one to the end of the linked list.
			getObject( sourceOutIndex, tmp );
			int nextSourceEdgeIndex = tmp.getNextSourceEdgeIndex();
			while ( nextSourceEdgeIndex >= 0 )
			{
				getObject( nextSourceEdgeIndex, tmp );
				nextSourceEdgeIndex = tmp.getNextSourceEdgeIndex();
			}
			tmp.setNextSourceEdgeIndex( edge.getInternalPoolIndex() );
		}

		final int targetInIndex = target.getFirstInEdgeIndex();
		if ( targetInIndex < 0 )
		{
			// target has no incoming edge yet. Set this one as the first.
			target.setFirstInEdgeIndex( edge.getInternalPoolIndex() );
		}
		else
		{
			// target has incoming edges. Append this one to the end of the linked list.
			getObject( targetInIndex, tmp );
			int nextTargetEdgeIndex = tmp.getNextTargetEdgeIndex();
			while ( nextTargetEdgeIndex >= 0 )
			{
				getObject( nextTargetEdgeIndex, tmp );
				nextTargetEdgeIndex = tmp.getNextTargetEdgeIndex();
			}
			tmp.setNextTargetEdgeIndex( edge.getInternalPoolIndex() );
		}

		releaseRef( tmp );
		return edge;
	}

	/**
	 * Inserts an edge between the specified source and target, at the specified
	 * positions in the edge lists of the source and target vertices.
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
	public E insertEdge( final AbstractVertex< ?, ?, ?, ? > source, final int sourceOutInsertAt, final AbstractVertex< ?, ?, ?, ? > target, final int targetInInsertAt, final E edge )
	{
		create( edge );
		edge.setSourceVertexInternalPoolIndex( source.getInternalPoolIndex() );
		edge.setTargetVertexInternalPoolIndex( target.getInternalPoolIndex() );

		final E tmp = createRef();

		int nextSourceEdgeIndex = source.getFirstOutEdgeIndex();
		int insertIndex = 0;
		while ( nextSourceEdgeIndex >= 0 && insertIndex < sourceOutInsertAt )
		{
			getObject( nextSourceEdgeIndex, tmp );
			nextSourceEdgeIndex = tmp.getNextSourceEdgeIndex();
			++insertIndex;
		}
		edge.setNextSourceEdgeIndex( nextSourceEdgeIndex );
		if ( insertIndex == 0 )
			source.setFirstOutEdgeIndex( edge.getInternalPoolIndex() );
		else
			tmp.setNextSourceEdgeIndex( edge.getInternalPoolIndex() );

		int nextTargetEdgeIndex = target.getFirstInEdgeIndex();
		insertIndex = 0;
		while ( nextTargetEdgeIndex >= 0 && insertIndex < targetInInsertAt )
		{
			getObject( nextTargetEdgeIndex, tmp );
			nextTargetEdgeIndex = tmp.getNextTargetEdgeIndex();
			++insertIndex;
		}
		edge.setNextTargetEdgeIndex( nextTargetEdgeIndex );
		if ( insertIndex == 0 )
			target.setFirstInEdgeIndex( edge.getInternalPoolIndex() );
		else
			tmp.setNextTargetEdgeIndex( edge.getInternalPoolIndex() );

		releaseRef( tmp );
		return edge;
	}

	public E getEdge( final AbstractVertex< ?, ?, ?, ? > source, final AbstractVertex< ?, ?, ?, ? > target, final E edge )
	{
		int nextSourceEdgeIndex = source.getFirstOutEdgeIndex();
		if ( nextSourceEdgeIndex < 0 )
			return null;
		do
		{
			getObject( nextSourceEdgeIndex, edge );
			if ( edge.getTargetVertexInternalPoolIndex() == target.getInternalPoolIndex() )
				return edge;
			nextSourceEdgeIndex = edge.getNextSourceEdgeIndex();
		}
		while ( nextSourceEdgeIndex >= 0 );
		return null;
	}

	public void deleteAllLinkedEdges( final AbstractVertex< ?, ?, ?, ? > vertex )
	{
		final V tmpVertex = vertexPool.createRef();
		final E edge = createRef();
		final E tmpEdge = createRef();

		// release all outgoing edges
		int index = vertex.getFirstOutEdgeIndex();
		vertex.setFirstOutEdgeIndex( -1 );
		while ( index >= 0 )
		{
			getObject( index, edge );
			unlinkFromTarget( edge, tmpEdge, tmpVertex );
			index = edge.getNextSourceEdgeIndex();
			super.delete( edge );
		}

		// release all incoming edges
		index = vertex.getFirstInEdgeIndex();
		vertex.setFirstInEdgeIndex( -1 );
		while ( index >= 0 )
		{
			getObject( index, edge );
			unlinkFromSource( edge, tmpEdge, tmpVertex );
			index = edge.getNextTargetEdgeIndex();
			super.delete( edge );
		}

		vertexPool.releaseRef( tmpVertex );
		releaseRef( edge );
		releaseRef( tmpEdge );
	}

	@Override
	public void delete( final E edge )
	{
		final V tmpVertex = vertexPool.createRef();
		final E tmp = createRef();

		unlinkFromSource( edge, tmp, tmpVertex );
		unlinkFromTarget( edge, tmp, tmpVertex );
		super.delete( edge );

		vertexPool.releaseRef( tmpVertex );
		releaseRef( tmp );
	}

	/*
	 * Internal stuff.
	 *
	 * If it should be necessary for performance reasons, these
	 * can be made protected or public
	 */

	private void unlinkFromSource( final E edge, final E tmpEdge, final V tmpVertex )
	{
		vertexPool.getObject( edge.getSourceVertexInternalPoolIndex(), tmpVertex );
		final int sourceOutIndex = tmpVertex.getFirstOutEdgeIndex();
		if ( sourceOutIndex == edge.getInternalPoolIndex() )
		{
			// this edge is the first in the sources list of outgoing edges
			tmpVertex.setFirstOutEdgeIndex( edge.getNextSourceEdgeIndex() );
		}
		else
		{
			// find this edge in the sources list of outgoing edges and remove it
			getObject( sourceOutIndex, tmpEdge );
			int nextSourceEdgeIndex = tmpEdge.getNextSourceEdgeIndex();
			while ( nextSourceEdgeIndex != edge.getInternalPoolIndex() )
			{
				getObject( nextSourceEdgeIndex, tmpEdge );
				nextSourceEdgeIndex = tmpEdge.getNextSourceEdgeIndex();
			}
			tmpEdge.setNextSourceEdgeIndex( edge.getNextSourceEdgeIndex() );
		}
	}

	private void unlinkFromTarget( final E edge, final E tmpEdge, final V tmpVertex )
	{
		vertexPool.getObject( edge.getTargetVertexInternalPoolIndex(), tmpVertex );
		final int targetInIndex = tmpVertex.getFirstInEdgeIndex();
		if ( targetInIndex == edge.getInternalPoolIndex() )
		{
			// this edge is the first in the targets list of incoming edges
			tmpVertex.setFirstInEdgeIndex( edge.getNextTargetEdgeIndex() );
		}
		else
		{
			// find this edge in the targets list of incoming edges and remove it
			getObject( targetInIndex, tmpEdge );
			int nextTargetEdgeIndex = tmpEdge.getNextTargetEdgeIndex();
			while ( nextTargetEdgeIndex != edge.getInternalPoolIndex() )
			{
				getObject( nextTargetEdgeIndex, tmpEdge );
				nextTargetEdgeIndex = tmpEdge.getNextTargetEdgeIndex();
			}
			tmpEdge.setNextTargetEdgeIndex( edge.getNextTargetEdgeIndex() );
		}
	}
}
