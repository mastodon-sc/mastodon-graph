package org.mastodon.graph;

import org.mastodon.graph.ref.AbstractEdgePool;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.ByteMappedElementArray;
import org.mastodon.pool.SingleArrayMemPool;

public class TestNonSimpleEdgePool extends AbstractEdgePool< TestNonSimpleEdge, TestNonSimpleVertex, ByteMappedElement >
{
	public TestNonSimpleEdgePool( final int initialCapacity, final TestNonSimpleVertexPool vertexPool )
	{
		super(
				initialCapacity,
				AbstractEdgePool.layout,
				TestNonSimpleEdge.class,
				SingleArrayMemPool.factory( ByteMappedElementArray.factory ),
				vertexPool );
	}

	@Override
	protected TestNonSimpleEdge createEmptyRef()
	{
		return new TestNonSimpleEdge( this );
	}
}
