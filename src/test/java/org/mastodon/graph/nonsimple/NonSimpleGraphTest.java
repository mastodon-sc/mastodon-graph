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
package org.mastodon.graph.nonsimple;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;
import org.mastodon.collection.RefCollections;
import org.mastodon.collection.RefList;
import org.mastodon.graph.Edges;
import org.mastodon.graph.TestEdge;
import org.mastodon.graph.TestGraph;
import org.mastodon.graph.TestVertex;

public class NonSimpleGraphTest
{

	private TestGraph graph;

	@Before
	public void setUp() throws Exception
	{
		this.graph = new TestGraph();
	}

	@Test
	public void testSingleBranch()
	{
		final RefList< TestVertex > vlist = RefCollections.createRefList( graph.vertices() );
		for ( int i = 0; i < 5; i++ )
			vlist.add( graph.addVertex().init( i ) );
		final RefList< TestEdge > elist = RefCollections.createRefList( graph.edges() );
		final TestVertex ref1 = graph.vertexRef();
		final TestVertex ref2 = graph.vertexRef();
		final TestEdge eref = graph.edgeRef();
		for ( int i = 0; i < vlist.size() - 1; i++ )
		{
			final TestVertex source = vlist.get( i, ref1 );
			final TestVertex target = vlist.get( i + 1, ref2 );
			final TestEdge edge = graph.addEdge( source, target, eref );
			elist.add( edge );
		}
	}

	@Test
	public void testBranchY()
	{
		// Make a long branch.
		final RefList< TestVertex > vlist = RefCollections.createRefList( graph.vertices() );
		for ( int i = 0; i < 7; i++ )
			vlist.add( graph.addVertex().init( i ) );
		final RefList< TestEdge > elist = RefCollections.createRefList( graph.edges() );
		final TestVertex ref1 = graph.vertexRef();
		final TestVertex ref2 = graph.vertexRef();
		final TestEdge eref = graph.edgeRef();
		for ( int i = 0; i < vlist.size() - 1; i++ )
		{
			final TestVertex source = vlist.get( i, ref1 );
			final TestVertex target = vlist.get( i + 1, ref2 );
			final TestEdge edge = graph.addEdge( source, target, eref );
			elist.add( edge );
		}
		final TestVertex middle = vlist.get( vlist.size() / 2 );

		// Branch from its middle vertex in Y shape (merge event).
		TestVertex source = graph.addVertex().init( vlist.size() );
		TestVertex target = null;
		vlist.add( source );
		for ( int i = 1; i < 4; i++ )
		{
			target = graph.addVertex().init( vlist.size() );
			vlist.add( target );
			final TestEdge e = graph.addEdge( source, target );
			elist.add( e );
			source = target;
		}
		graph.addEdge( target, middle );
	}

	@Test
	public void testBranchingLambda()
	{
		// Make a long branch.
		final RefList< TestVertex > vlist = RefCollections.createRefList( graph.vertices() );
		for ( int i = 0; i < 7; i++ )
			vlist.add( graph.addVertex().init( i ) );
		final RefList< TestEdge > elist = RefCollections.createRefList( graph.edges() );
		final TestVertex ref1 = graph.vertexRef();
		final TestVertex ref2 = graph.vertexRef();
		final TestEdge eref = graph.edgeRef();
		for ( int i = 0; i < vlist.size() - 1; i++ )
		{
			final TestVertex source = vlist.get( i, ref1 );
			final TestVertex target = vlist.get( i + 1, ref2 );
			final TestEdge edge = graph.addEdge( source, target, eref );
			elist.add( edge );
		}

		// Branch from its middle vertex in lambda shape (split event).
		final TestVertex middle = vlist.get( vlist.size() / 2 );
		TestVertex source = middle;
		for ( int i = 0; i < vlist.size() / 2; i++ )
		{
			final TestVertex target = graph.addVertex().init( vlist.size() + i );
			vlist.add( target );
			final TestEdge e = graph.addEdge( source, target );
			elist.add( e );
			source = target;
		}
	}

	@Test
	public void testMultipleEdges()
	{
		final TestVertex s = graph.addVertex().init( 0 );
		final TestVertex t = graph.addVertex().init( 1 );

		// Add 10 edges betwen the same 2 vertices.
		final RefList< TestEdge > elist = RefCollections.createRefList( graph.edges() );
		for ( int i = 0; i < 10; i++ )
		{
			final TestEdge e = graph.addEdge( s, t );
			elist.add( e );
		}

		// Remove them one by one.
		for ( int i = 0; i < elist.size(); i++ )
		{
			final TestEdge e = graph.getEdge( s, t );
			assertNotNull( "There still should be at least one edge between source and target.", e );
			graph.remove( e );
		}
		assertNull( "There should be no edge left between source and target.", graph.getEdge( s, t ) );
	}

	@Test
	public void testMultipleEdgeIterator()
	{
		final TestVertex s = graph.addVertex().init( 0 );
		final TestVertex t = graph.addVertex().init( 1 );
		final TestVertex t2 = graph.addVertex().init( 2 );

		// Add 10 edges betwen the same 2 vertices.
		final RefList< TestEdge > elist = RefCollections.createRefList( graph.edges() );
		for ( int i = 0; i < 10; i++ )
		{
			final TestEdge e = graph.addEdge( s, t );
			elist.add( e );
		}
		// And an edge to another target.
		graph.addEdge( s, t2 );

		final TestVertex ref = graph.vertexRef();
		final Edges< TestEdge > edges = graph.getEdges( s, t, ref );
		final Iterator< TestEdge > it = edges.iterator();
		int nedges = 0;
		TestEdge previous = null;
		while ( it.hasNext() )
		{
			nedges++;
			final TestEdge e = it.next();

			// Test non equality with previous edge.
			if ( null != previous )
			{
				assertNotEquals( "Iterated edges should be all difference", previous, e );
			}
			else
			{
				previous = graph.edgeRef();
			}
			previous.refTo( e );

			// Check that edge iterated was indeed added to the graph.
			assertTrue( "Iterated edge is unexpected.", elist.contains( e ) );

			// Are source and target ok?
			assertEquals( "Edge source is unexpected.", s, e.getSource() );
			assertEquals( "Edge target is unexpected.", t, e.getTarget() );
			elist.remove( e );
		}
		assertEquals( "Did not iterate over the expected number of edges.", 10, nedges );
		assertTrue( "Not all edges have been iterated.", elist.isEmpty() );

		/*
		 * Test reuse of iterator ref.
		 */
		final Edges< TestEdge > edges2 = graph.getEdges( s, t, ref );
		assertEquals( "Did not reuse iterator reference.", edges, edges2 );

		/*
		 * Test iterator with removal.
		 */

		final Iterator< TestEdge > it2 = edges2.iterator();
		while ( it2.hasNext() )
		{
			it2.next();
			it2.remove();
		}

		assertNull( "All edges between source and target should have been removed.", graph.getEdge( s, t ) );

		/*
		 * Test reuse of iterator ref.
		 */

		final Edges< TestEdge > edges3 = graph.getEdges( s, t2, ref );
		assertEquals( "Did not reuse iterator reference.", edges, edges3 );

		/*
		 * Test single edge.
		 */

		final Iterator< TestEdge > it3 = edges3.iterator();
		while ( it3.hasNext() )
		{
			final TestEdge e = it3.next();
			assertEquals( "Unexpected edge.", graph.getEdge( s, t2 ), e );
		}

	}

}
