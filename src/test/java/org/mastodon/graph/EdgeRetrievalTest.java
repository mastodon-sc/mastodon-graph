/*-
 * #%L
 * Mastodon Graphs
 * %%
 * Copyright (C) 2015 - 2020 Tobias Pietzsch, Jean-Yves Tinevez
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
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
