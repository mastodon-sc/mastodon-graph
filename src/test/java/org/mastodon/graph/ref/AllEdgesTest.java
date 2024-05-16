/*-
 * #%L
 * Mastodon Graphs
 * %%
 * Copyright (C) 2015 - 2024 Tobias Pietzsch, Jean-Yves Tinevez
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
package org.mastodon.graph.ref;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.mastodon.graph.TestEdge;
import org.mastodon.graph.TestGraph;
import org.mastodon.graph.TestVertex;

/**
 * Tests {@link AllEdges}.
 * <p>
 * The test takes advantage of the fact that {@link TestVertex#edges()} returns
 * an {@link AllEdges} object.
 */
public class AllEdgesTest
{
	/**
	 * A graph with the following structure:
	 * <pre>
	 *   A    E
	 *   |
	 *   B
	 *  / \
	 * C   D
	 * </pre>
	 */
	private final TestGraph graph = new TestGraph();
	private final TestVertex vertexA = graph.addVertex().init( 0 );
	private final TestVertex vertexB = graph.addVertex().init( 1 );
	private final TestVertex vertexC = graph.addVertex().init( 2 );
	private final TestVertex vertexD = graph.addVertex().init( 3 );
	private final TestVertex vertexE = graph.addVertex().init( 4 );
	private final TestEdge edgeAB = graph.addEdge( vertexA, vertexB );
	private final TestEdge edgeBC = graph.addEdge( vertexB, vertexC );
	private final TestEdge edgeBD = graph.addEdge( vertexB, vertexD );

	@Test
	public void testGet()
	{
		// vertex A has only one outgoing edge
		assertEquals( edgeAB, vertexA.edges().get( 0 ) );
		// vertex B has incoming and outgoing edges
		assertEquals( edgeAB, vertexB.edges().get( 0 ) );
		assertEquals( edgeBC, vertexB.edges().get( 1 ) );
		assertEquals( edgeBD, vertexB.edges().get( 2 ) );
		// vertex C has only incoming edges
		assertEquals( edgeBC, vertexC.edges().get( 0 ) );
	}

	@Test
	public void testSize()
	{
		assertEquals( 1, vertexA.edges().size() );
		assertEquals( 3, vertexB.edges().size() );
		assertEquals( 1, vertexC.edges().size() );
		assertEquals( 0, vertexE.edges().size() );
	}

	@Test
	public void testIterator()
	{
		AllEdges< TestEdge >.EdgesIterator iterator = vertexB.edges().iterator();
		assertTrue( iterator.hasNext() );
		assertEquals( edgeAB, iterator.next() );
		assertTrue( iterator.hasNext() );
		assertEquals( edgeBC, iterator.next() );
		assertTrue( iterator.hasNext() );
		assertEquals( edgeBD, iterator.next() );
		assertFalse( iterator.hasNext() );
		iterator.reset();
		assertTrue( iterator.hasNext() );
		assertEquals( edgeAB, iterator.next() );
		assertTrue( iterator.hasNext() );
		assertEquals( edgeBC, iterator.next() );
		assertTrue( iterator.hasNext() );
		assertEquals( edgeBD, iterator.next() );
		assertFalse( iterator.hasNext() );
	}
}
