package org.mastodon.graph;

import org.mastodon.graph.ref.GraphImp;
import org.mastodon.pool.ByteMappedElement;

public class TestGraph extends GraphImp< TestVertexPool, TestEdgePool, TestVertex, TestEdge, ByteMappedElement >
{
	public TestGraph( final int initialCapacity )
	{
		super( new TestEdgePool( initialCapacity, new TestVertexPool( initialCapacity ) ) );
	}

	public TestGraph()
	{
		this( 10 );
	}

	public TestVertexPool getVertexPool()
	{
		return vertexPool;
	}

	public TestEdgePool getEdgePool()
	{
		return edgePool;
	}

	@Override
	public String toString()
	{
		final StringBuffer sb = new StringBuffer( "TestNonSimpleGraph {\n" );
		sb.append( "  vertices = {\n" );
		for ( final TestVertex v : vertexPool )
			sb.append( "    " + v + "\n" );
		sb.append( "  },\n" );

		sb.append( "  edges = {\n" );
		for ( final TestEdge e : edgePool )
			sb.append( "    " + e + "\n" );
		sb.append( "  }\n" );
		sb.append( "}" );
		return sb.toString();
	}
}
