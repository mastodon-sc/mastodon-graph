package org.mastodon.graph;

import org.mastodon.graph.ref.ListenableGraphImp;
import org.mastodon.pool.ByteMappedElement;

public class TestSimpleSpatialGraph extends ListenableGraphImp< TestSimpleSpatialVertexPool, TestSimpleSpatialEdgePool, TestSimpleSpatialVertex, TestSimpleSpatialEdge, ByteMappedElement >
{

	public TestSimpleSpatialGraph( final int initialCapacity )
	{
		super( new TestSimpleSpatialEdgePool( initialCapacity, new TestSimpleSpatialVertexPool( initialCapacity ) ) );
	}

	public TestSimpleSpatialGraph()
	{
		this( 10 );
	}

	public TestSimpleSpatialVertexPool getVertexPool()
	{
		return vertexPool;
	}

	public TestSimpleSpatialEdgePool getEdgePool()
	{
		return edgePool;
	}

	@Override
	public String toString()
	{
		final StringBuffer sb = new StringBuffer( "TestNonSimpleGraph {\n" );
		sb.append( "  vertices = {\n" );
		for ( final TestSimpleSpatialVertex v : vertexPool )
			sb.append( "    " + v + "\n" );
		sb.append( "  },\n" );

		sb.append( "  edges = {\n" );
		for ( final TestSimpleSpatialEdge e : edgePool )
			sb.append( "    " + e + "\n" );
		sb.append( "  }\n" );
		sb.append( "}" );
		return sb.toString();
	}
}
