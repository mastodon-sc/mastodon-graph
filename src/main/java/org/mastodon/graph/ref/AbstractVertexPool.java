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
package org.mastodon.graph.ref;

import org.mastodon.pool.MappedElement;
import org.mastodon.pool.MemPool;
import org.mastodon.pool.Pool;
import org.mastodon.pool.PoolObjectLayout;

public abstract class AbstractVertexPool<
			V extends AbstractVertex< V, E, ?, T >,
			E extends AbstractEdge< E, ?, ?, ? >,
			T extends MappedElement >
		extends Pool< V, T >
{
	// TODO make it private again when we do not need this anymore.
	protected AbstractEdgePool< E, ?, ? > edgePool;

	public static class AbstractVertexLayout extends PoolObjectLayout
	{
		final IndexField firstInEdge = indexField();
		final IndexField firstOutEdge = indexField();
	}

	public static AbstractVertexLayout layout = new AbstractVertexLayout();

	public AbstractVertexPool(
			final int initialCapacity,
			final AbstractVertexLayout layout,
			final Class< V > vertexClass,
			final MemPool.Factory< T > memPoolFactory )
	{
		super( initialCapacity, layout, vertexClass, memPoolFactory );
	}

	public void linkEdgePool( final AbstractEdgePool< E, ?, ? > edgePool )
	{
		this.edgePool = edgePool;
	}

	@Override
	public V createRef()
	{
		final V vertex = super.createRef();
		if ( edgePool != null )
			vertex.linkEdgePool( edgePool );
		return vertex;
	}

	@Override
	public V create( final V vertex )
	{
		return super.create( vertex );
	}

	@Override
	public void delete( final V vertex )
	{
		if ( edgePool != null )
			edgePool.deleteAllLinkedEdges( vertex );
		super.delete( vertex );
	}
}
