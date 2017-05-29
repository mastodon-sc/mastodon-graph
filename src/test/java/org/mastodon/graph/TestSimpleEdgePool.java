package org.mastodon.graph;

import org.mastodon.graph.ref.AbstractEdgePool;
import org.mastodon.graph.ref.AbstractSimpleEdgePool;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.ByteMappedElementArray;
import org.mastodon.pool.SingleArrayMemPool;

public class TestSimpleEdgePool extends AbstractSimpleEdgePool< TestSimpleEdge, TestSimpleVertex, ByteMappedElement >
{
	public TestSimpleEdgePool( final int initialCapacity, final TestSimpleVertexPool vertexPool )
	{
		super(
				initialCapacity,
				AbstractEdgePool.layout,
				TestSimpleEdge.class,
				SingleArrayMemPool.factory( ByteMappedElementArray.factory ),
				vertexPool );
	}

	@Override
	protected TestSimpleEdge createEmptyRef()
	{
		return new TestSimpleEdge( this );
	}
}
