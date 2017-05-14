package org.mastodon.graph;

import org.mastodon.graph.ref.AbstractSimpleEdgePool;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.ByteMappedElementArray;
import org.mastodon.pool.SingleArrayMemPool;

public class TestEdgePool extends AbstractSimpleEdgePool< TestEdge, TestVertex, ByteMappedElement >
{
	public TestEdgePool( final int initialCapacity, final TestVertexPool vertexPool )
	{
		super(
				initialCapacity,
				AbstractSimpleEdgePool.layout,
				TestEdge.class,
				SingleArrayMemPool.factory( ByteMappedElementArray.factory ),
				vertexPool );
	}

	@Override
	protected TestEdge createEmptyRef()
	{
		return new TestEdge( this );
	}
}
