/*-
 * #%L
 * Mastodon Graphs
 * %%
 * Copyright (C) 2015 - 2025 Tobias Pietzsch, Jean-Yves Tinevez
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
package org.mastodon.graph.algorithm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mastodon.graph.TestSimpleEdge;
import org.mastodon.graph.TestSimpleVertex;
import org.mastodon.graph.algorithm.traversal.GraphSearch.SearchDirection;
import org.mastodon.graph.algorithm.traversal.GraphsForTests;
import org.mastodon.graph.algorithm.traversal.GraphsForTests.GraphTestBundle;

public class ShortestPathTest
{

	private GraphTestBundle< TestSimpleVertex, TestSimpleEdge > bundle;

	@Before
	public void setUp() throws Exception
	{
		bundle = GraphsForTests.wpExamplePoolObjects();
	}

	@Test
	public void testUndirected()
	{
		final ShortestPath< TestSimpleVertex, TestSimpleEdge > sp = new ShortestPath<>( bundle.graph, SearchDirection.UNDIRECTED );
		final TestSimpleVertex D = bundle.vertices.get( 3 );
		final TestSimpleVertex G = bundle.vertices.get( 6 );

		final Iterator< TestSimpleVertex > path = sp.findPath( D, G ).iterator();
		assertNotNull( "Path not found, though it exists.", path );

		final List< TestSimpleVertex > expectedOrder = new ArrayList<>( 5 );
		// Reverse order
		expectedOrder.add( bundle.vertices.get( 6 ) );
		expectedOrder.add( bundle.vertices.get( 2 ) );
		expectedOrder.add( bundle.vertices.get( 0 ) );
		expectedOrder.add( bundle.vertices.get( 1 ) );
		expectedOrder.add( bundle.vertices.get( 3 ) );
		final Iterator< TestSimpleVertex > eit = expectedOrder.iterator();
		while ( eit.hasNext() )
		{
			assertEquals( "Path found does not follow expected order.", eit.next(), path.next() );
		}
		assertFalse( "Path is longer than expected.", path.hasNext() );
	}

	@Test
	public void testDirected()
	{
		final TestSimpleVertex A = bundle.vertices.get( 0 );
		final TestSimpleVertex E = bundle.vertices.get( 4 );
		// Change the direction of eAE
		bundle.graph.remove( bundle.edges.get( 2 ) );
		bundle.graph.addEdge( E, A );

		final ShortestPath< TestSimpleVertex, TestSimpleEdge > spUndirected = new ShortestPath<>( bundle.graph, SearchDirection.UNDIRECTED );
		final Iterator< TestSimpleVertex > pathUndirected = spUndirected.findPath( A, E ).iterator();
		assertNotNull( "Path not found, though it exists.", pathUndirected );

		// Reverse order
		final List< TestSimpleVertex > expectedOrderUD = new ArrayList<>( 2 );
		expectedOrderUD.add( E );
		expectedOrderUD.add( A );
		final Iterator< TestSimpleVertex > eitUD = expectedOrderUD.iterator();
		while ( eitUD.hasNext() )
		{
			assertEquals( "Path found does not follow expected order.", eitUD.next(), pathUndirected.next() );
		}
		assertFalse( "Path is longer than expected.", pathUndirected.hasNext() );

		// Redo it, as directed search.
		final ShortestPath< TestSimpleVertex, TestSimpleEdge > spDirected = new ShortestPath<>( bundle.graph, SearchDirection.DIRECTED );
		final Iterator< TestSimpleVertex > pathDirected = spDirected.findPath( A, E ).iterator();
		assertNotNull( "Path not found, though it exists.", pathDirected );

		final List< TestSimpleVertex > expectedOrderD = new ArrayList<>( 5 );
		// Reverse order
		expectedOrderD.add( E );
		expectedOrderD.add( bundle.vertices.get( 5 ) );
		expectedOrderD.add( bundle.vertices.get( 1 ) );
		expectedOrderD.add( A );
		final Iterator< TestSimpleVertex > eitD = expectedOrderD.iterator();
		while ( eitD.hasNext() )
		{
			final TestSimpleVertex v = pathDirected.next();
			assertEquals( "Path found does not follow expected order.", eitD.next(), v );
		}
		assertFalse( "Path is longer than expected.", pathDirected.hasNext() );
	}

	@Test
	public void testNonExistingPath()
	{
		// TODO
	}

}
