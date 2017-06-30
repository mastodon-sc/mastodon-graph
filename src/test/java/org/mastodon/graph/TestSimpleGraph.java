package org.mastodon.graph;

import org.mastodon.graph.ref.GraphImp;
import org.mastodon.pool.ByteMappedElement;

public class TestSimpleGraph extends GraphImp< TestSimpleVertexPool, TestSimpleEdgePool, TestSimpleVertex, TestSimpleEdge, ByteMappedElement >
{
	public TestSimpleGraph( final int initialCapacity )
	{
		super( new TestSimpleEdgePool( initialCapacity, new TestSimpleVertexPool( initialCapacity ) ) );
	}

	public TestSimpleGraph()
	{
		this( 10 );
	}

	public TestSimpleVertexPool getVertexPool()
	{
		return vertexPool;
	}

	public TestSimpleEdgePool getEdgePool()
	{
		return edgePool;
	}
}
