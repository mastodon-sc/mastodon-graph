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

import org.mastodon.pool.MappedElement;
import org.mastodon.pool.MemPool;
import org.mastodon.pool.Properties;

/**
 * Mother class for edge pools of <b>directed, listenable</b> graphs.
 * <p>
 * Graphs based on this edge pool do not have a limitation on the number of
 * edges between a source and target vertices.
 * 
 * @param <E>
 *            the concrete type of edges in this pool.
 * @param <V>
 *            the type of vertex in the graph.
 * @param <T>
 *            the type of mapped element on which this edge pool is built.
 */
public abstract class AbstractListenableEdgePool<
			E extends AbstractListenableEdge< E, V, ?, T >,
			V extends AbstractVertex< V, ?, ?, ? >,
			T extends MappedElement >
		extends AbstractEdgePool< E, V, T >
{
	public AbstractListenableEdgePool(
			final int initialCapacity,
			final AbstractEdgeLayout layout,
			final Class< E > edgeClass,
			final MemPool.Factory< T > memPoolFactory,
			final AbstractVertexPool< V, ?, ? > vertexPool )
	{
		super( initialCapacity, layout, edgeClass, memPoolFactory, vertexPool );
	}

	NotifyPostInit< ?, E > notifyPostInit;

	public void linkNotify( final NotifyPostInit< ?, E > notifyPostInit )
	{
		this.notifyPostInit = notifyPostInit;
	}

	@Override
	protected Properties< E > getProperties()
	{
		return super.getProperties();
	}

	/*
	 * Debug helper. Uncomment to do additional verifyInitialized() whenever a
	 * Ref is pointed to an edge.
	 */
//	@Override
//	public E getObject( final int index, final E obj )
//	{
//		final E e = super.getObject( index, obj );
//		e.verifyInitialized();
//		return e;
//	}
}
