package org.mastodon.graph.branch;

import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.mastodon.graph.GraphIdBimap;
import org.mastodon.graph.ListenableTestEdge;
import org.mastodon.graph.ListenableTestGraph;
import org.mastodon.graph.ListenableTestVertex;

public class BranchGraphTest
{

	private ListenableTestGraph graph;

	private BranchGraph< ListenableTestVertex, ListenableTestEdge > bg;

	@Before
	public void setUp() throws Exception
	{
		this.graph = new ListenableTestGraph();
		this.bg = new BranchGraph<>( graph, new GraphIdBimap<>( graph.getVertexPool(), graph.getEdgePool() ) );

	}

	@Test
	public void testAddOneVertex()
	{
		final ListenableTestVertex v0 = graph.addVertex().init( 0 );

		System.out.println( graph ); // DEBUG
		System.out.println(); // DEBUG
		System.out.println( bg ); // DEBUG

		fail( "Not yet implemented" );
	}

}
