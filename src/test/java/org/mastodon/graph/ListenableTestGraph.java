package org.mastodon.graph;

import org.mastodon.graph.ref.ListenableGraphImp;
import org.mastodon.pool.ByteMappedElement;

public class ListenableTestGraph extends ListenableGraphImp< ListenableTestVertexPool, ListenableTestEdgePool, ListenableTestVertex, ListenableTestEdge, ByteMappedElement >
{
	public ListenableTestGraph( final int initialCapacity )
	{
		super( new ListenableTestEdgePool( initialCapacity, new ListenableTestVertexPool( initialCapacity ) ) );
	}

	public ListenableTestGraph()
	{
		this( 10 );
	}

	public ListenableTestVertexPool getVertexPool()
	{
		return vertexPool;
	}

	public ListenableTestEdgePool getEdgePool()
	{
		return edgePool;
	}

	@Override
	public String toString()
	{
		final StringBuffer sb = new StringBuffer( "ListenableGraphTest {\n" );
		sb.append( "  vertices = {\n" );
		for ( final ListenableTestVertex v : vertexPool )
			sb.append( "    " + v + "\n" );
		sb.append( "  },\n" );

		sb.append( "  edges = {\n" );
		for ( final ListenableTestEdge e : edgePool )
			sb.append( "    " + e + "\n" );
		sb.append( "  }\n" );
		sb.append( "}" );
		return sb.toString();
	}
}
