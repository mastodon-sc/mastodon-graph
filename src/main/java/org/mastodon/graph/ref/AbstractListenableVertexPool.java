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
import org.mastodon.pool.Properties;

public abstract class AbstractListenableVertexPool<
			V extends AbstractListenableVertex< V, E, ?, T >,
			E extends AbstractEdge< E, ?, ?, ? >,
			T extends MappedElement >
		extends AbstractVertexPool< V, E, T >
{
	public AbstractListenableVertexPool(
			final int initialCapacity,
			final AbstractVertexLayout layout,
			final Class< V > vertexClass,
			final MemPool.Factory< T > memPoolFactory )
	{
		super( initialCapacity, layout, vertexClass, memPoolFactory );
	}

	NotifyPostInit< V, ? > notifyPostInit;

	public void linkNotify( final NotifyPostInit< V, ? > notifyPostInit )
	{
		this.notifyPostInit = notifyPostInit;
	}

	@Override
	protected Properties< V > getProperties()
	{
		return super.getProperties();
	}

	/*
	 * Debug helper. Uncomment to do additional verifyInitialized() whenever a
	 * Ref is pointed to a vertex.
	 */
//	@Override
//	public V getObject( final int index, final V obj )
//	{
//		final V v = super.getObject( index, obj );
//		v.verifyInitialized();
//		return v;
//	}
}
