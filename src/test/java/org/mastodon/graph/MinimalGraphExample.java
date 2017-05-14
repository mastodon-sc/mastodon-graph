package org.mastodon.graph;

import org.mastodon.graph.ref.AbstractSimpleEdge;
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

	static class MyEdge extends AbstractSimpleEdge< MyEdge, MyVertex, MyEdgePool, ByteMappedElement >
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
