package org.mastodon.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class EdgeRetrievalTest
{

	@Test
	public void testTwoOutgoingLinks()
	{
		// Make a graph in V.
		final TestSimpleGraph graph = new TestSimpleGraph();
		final TestSimpleVertex v1 = graph.addVertex().init( 1 );
		final TestSimpleVertex v2 = graph.addVertex().init( 2 );
		final TestSimpleVertex v3 = graph.addVertex().init( 3 );
		final TestSimpleVertex v4 = graph.addVertex().init( 4 );
		final TestSimpleEdge e12 = graph.addEdge( v1, v2 );
		final TestSimpleEdge e13 = graph.addEdge( v1, v3 );
		final TestSimpleEdge e14 = graph.addEdge( v1, v4 );

		// Getting first edge.
		final TestSimpleEdge l12 = graph.getEdge( v1, v2 );
		assertNotNull( "The first retrieved edge should not be null, but " + e12 + ".", l12 );
		assertEquals( "Did not retrieve the expected edge.", e12, l12 );

		// Getting second edge.
		final TestSimpleEdge l13 = graph.getEdge( v1, v3 );
		assertNotNull( "The second retrieved edge should not be null, but " + e13 + ".", l13 );
		assertEquals( "Did not retrieve the expected edge.", e13, l13 );

		// Getting third edge.
		final TestSimpleEdge l14 = graph.getEdge( v1, v4 );
		assertNotNull( "The third retrieved edge should not be null, but " + e14 + ".", l14 );
		assertEquals( "Did not retrieve the expected edge.", e14, l14 );
	}

	@Test
	public void testTwoIncomingLinks()
	{
		// Make a graph in V.
		final TestSimpleGraph graph = new TestSimpleGraph();
		final TestSimpleVertex v1 = graph.addVertex().init( 1 );
		final TestSimpleVertex v2 = graph.addVertex().init( 2 );
		final TestSimpleVertex v3 = graph.addVertex().init( 3 );
		final TestSimpleVertex v4 = graph.addVertex().init( 4 );
		final TestSimpleEdge e21 = graph.addEdge( v2, v1 );
		final TestSimpleEdge e31 = graph.addEdge( v3, v1 );
		final TestSimpleEdge e41 = graph.addEdge( v4, v1 );

		// Getting first edge.
		final TestSimpleEdge l21 = graph.getEdge( v2, v1 );
		assertNotNull( "The first retrieved edge should not be null, but " + e21 + ".", l21 );
		assertEquals( "Did not retrieve the expected edge.", e21, l21 );

		// Getting second edge.
		final TestSimpleEdge l31 = graph.getEdge( v3, v1 );
		assertNotNull( "The second retrieved edge should not be null, but " + e31 + ".", l31 );
		assertEquals( "Did not retrieve the expected edge.", e31, l31 );

		// Getting third edge.
		final TestSimpleEdge l41 = graph.getEdge( v4, v1 );
		assertNotNull( "The third retrieved edge should not be null, but " + e41 + ".", l41 );
		assertEquals( "Did not retrieve the expected edge.", e41, l41 );
	}

	@Test
	public void testOneIncomingLink()
	{
		// Make a graph in V.
		final TestSimpleGraph graph = new TestSimpleGraph();
		final TestSimpleVertex v1 = graph.addVertex().init( 1 );
		final TestSimpleVertex v2 = graph.addVertex().init( 2 );
		final TestSimpleEdge e21 = graph.addEdge( v2, v1 );

		// Getting first edge.
		final TestSimpleEdge l21 = graph.getEdge( v2, v1 );
		assertNotNull( "The retrieved edge should not be null, but " + e21 + ".", l21 );
		assertEquals( "Did not retrieve the expected edge.", e21, l21 );
	}

	@Test
	public void testOneOutgoingLink()
	{
		// Make a graph in V.
		final TestSimpleGraph graph = new TestSimpleGraph();
		final TestSimpleVertex v1 = graph.addVertex().init( 1 );
		final TestSimpleVertex v2 = graph.addVertex().init( 2 );
		final TestSimpleEdge e12 = graph.addEdge( v1, v2 );

		// Getting first edge.
		final TestSimpleEdge l12 = graph.getEdge( v1, v2 );
		assertNotNull( "The retrieved edge should not be null, but " + e12 + ".", l12 );
		assertEquals( "Did not retrieve the expected edge.", e12, l12 );
	}

}
