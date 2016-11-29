package org.mastodon.graph;

import org.mastodon.graph.ref.GraphImp;
import org.mastodon.pool.ByteMappedElement;

public class TestNonSimpleGraph extends GraphImp< TestNonSimpleVertexPool, TestNonSimpleEdgePool, TestNonSimpleVertex, TestNonSimpleEdge, ByteMappedElement >
{
	public TestNonSimpleGraph( final int initialCapacity )
	{
		super( new TestNonSimpleEdgePool( initialCapacity, new TestNonSimpleVertexPool( initialCapacity ) ) );
	}

	public TestNonSimpleGraph()
	{
		this( 10 );
	}

	public TestNonSimpleVertexPool getVertexPool()
	{
		return vertexPool;
	}

	public TestNonSimpleEdgePool getEdgePool()
	{
		return edgePool;
	}

	@Override
	public String toString()
	{
		final StringBuffer sb = new StringBuffer( "TestNonSimpleGraph {\n" );
		sb.append( "  vertices = {\n" );
		for ( final TestNonSimpleVertex v : vertexPool )
			sb.append( "    " + v + "\n" );
		sb.append( "  },\n" );

		sb.append( "  edges = {\n" );
		for ( final TestNonSimpleEdge e : edgePool )
			sb.append( "    " + e + "\n" );
		sb.append( "  }\n" );
		sb.append( "}" );
		return sb.toString();
	}
}
