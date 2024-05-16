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
package org.mastodon.graph;

import org.mastodon.graph.ref.AbstractEdge;
import org.mastodon.graph.ref.AbstractSimpleEdgePool;
import org.mastodon.graph.ref.AbstractVertex;
import org.mastodon.graph.ref.AbstractVertexPool;
import org.mastodon.graph.ref.GraphImp;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.ByteMappedElementArray;
import org.mastodon.pool.SingleArrayMemPool;

public class MinimalGraphExample
{
	static class MyVertex extends AbstractVertex< MyVertex, MyEdge, MyVertexPool, ByteMappedElement >
	{
		protected MyVertex( final MyVertexPool pool )
		{
			super( pool );
		}
	}

	static class MyEdge extends AbstractEdge< MyEdge, MyVertex, MyEdgePool, ByteMappedElement >
	{
		protected MyEdge( final MyEdgePool pool )
		{
			super( pool );
		}
	}

	static class MyVertexPool extends AbstractVertexPool< MyVertex, MyEdge, ByteMappedElement >
	{
		static AbstractVertexLayout layout = new AbstractVertexLayout();

		public MyVertexPool( final int initialCapacity )
		{
			super(
					initialCapacity,
					layout,
					MyVertex.class,
					SingleArrayMemPool.factory( ByteMappedElementArray.factory ) );
		}

		@Override
		protected MyVertex createEmptyRef()
		{
			return new MyVertex( this );
		}
	}

	static class MyEdgePool extends AbstractSimpleEdgePool< MyEdge, MyVertex, ByteMappedElement >
	{
		static AbstractEdgeLayout layout = new AbstractEdgeLayout();

		public MyEdgePool( final int initialCapacity, final MyVertexPool vertexPool )
		{
			super(
					initialCapacity,
					layout,
					MyEdge.class,
					SingleArrayMemPool.factory( ByteMappedElementArray.factory ),
					vertexPool );
		}

		@Override
		protected MyEdge createEmptyRef()
		{
			return new MyEdge( this );
		}
	}

	public static void main( final String[] args )
	{
		final int initialCapacity = 1000;
		final MyVertexPool vertexPool = new MyVertexPool( initialCapacity );
		final MyEdgePool edgePool = new MyEdgePool( initialCapacity, vertexPool );
		new GraphImp<>( edgePool );
	}
}
