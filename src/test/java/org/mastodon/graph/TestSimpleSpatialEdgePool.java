package org.mastodon.graph;

import org.mastodon.graph.ref.AbstractEdgePool;
import org.mastodon.graph.ref.AbstractListenableEdgePool;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.ByteMappedElementArray;
import org.mastodon.pool.SingleArrayMemPool;

public class TestSimpleSpatialEdgePool extends AbstractListenableEdgePool< TestSimpleSpatialEdge, TestSimpleSpatialVertex, ByteMappedElement >
{

	public TestSimpleSpatialEdgePool( final int initialCapacity, final TestSimpleSpatialVertexPool vertexPool )
	{
		super(
				initialCapacity,
				AbstractEdgePool.layout,
				TestSimpleSpatialEdge.class,
				SingleArrayMemPool.factory( ByteMappedElementArray.factory ),
				vertexPool );
	}

	@Override
	protected TestSimpleSpatialEdge createEmptyRef()
	{
		return new TestSimpleSpatialEdge( this );
	}
}
