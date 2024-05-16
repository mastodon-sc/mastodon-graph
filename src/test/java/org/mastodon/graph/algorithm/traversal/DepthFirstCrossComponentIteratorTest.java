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
package org.mastodon.graph.algorithm.traversal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;
import org.mastodon.collection.RefCollections;
import org.mastodon.collection.RefList;
import org.mastodon.graph.Graph;
import org.mastodon.graph.TestSimpleEdge;
import org.mastodon.graph.TestSimpleVertex;
import org.mastodon.graph.object.ObjectEdge;
import org.mastodon.graph.object.ObjectVertex;
import org.mastodon.graph.algorithm.traversal.GraphsForTests.GraphTestBundle;

public class DepthFirstCrossComponentIteratorTest
{

	@Before
	public void setUp() throws Exception
	{}

	@Test
	public void testTwoComponentsStartOnRoot()
	{
		final GraphTestBundle< ObjectVertex< Integer >, ObjectEdge< Integer > > bundle = GraphsForTests.twoComponentsStdObjects();
		final Graph< ObjectVertex< Integer >, ObjectEdge< Integer > > graph = bundle.graph;
		// Start with a root.
		final ObjectVertex< Integer > start = bundle.vertices.get( 0 );

		final RefList< ObjectVertex< Integer > > expected = RefCollections.createRefList( bundle.graph.vertices() );
		expected.add( bundle.vertices.get( 0 ) );
		expected.add( bundle.vertices.get( 4 ) );
		expected.add( bundle.vertices.get( 2 ) );
		expected.add( bundle.vertices.get( 6 ) );
		expected.add( bundle.vertices.get( 1 ) );
		expected.add( bundle.vertices.get( 5 ) );
		expected.add( bundle.vertices.get( 3 ) );
		expected.add( bundle.vertices.get( 7 ) );
		expected.add( bundle.vertices.get( 8 ) );
		expected.add( bundle.vertices.get( 9 ) );
		expected.add( bundle.vertices.get( 10 ) );
		expected.add( bundle.vertices.get( 11 ) );
		expected.add( bundle.vertices.get( 12 ) );
		expected.add( bundle.vertices.get( 13 ) );

		final Iterator< ObjectVertex< Integer > > eit = expected.iterator();
		final Iterator< ObjectVertex< Integer > > it =
				new DepthFirstCrossComponentIterator<>( start, graph );

		while ( eit.hasNext() )
		{
			assertTrue( "Iterator should not be finished, but is.", it.hasNext() );
			final ObjectVertex< Integer > a = it.next();
			final ObjectVertex< Integer > e = eit.next();
			assertEquals( "Unexpected vertex met during iteration.", e, a );
		}

		assertFalse( "Iteration should be finished, but is not.", it.hasNext() );
	}

	@Test
	public void testTwoComponentsComeBackOnFirstPool()
	{
		final GraphTestBundle< TestSimpleVertex, TestSimpleEdge > bundle = GraphsForTests.twoComponentsPoolObjects();
		final Graph< TestSimpleVertex, TestSimpleEdge > graph = bundle.graph;
		// Start with B.
		final TestSimpleVertex start = bundle.vertices.get( 1 );

		final RefList< TestSimpleVertex > expected = RefCollections.createRefList( bundle.graph.vertices() );
		expected.add( bundle.vertices.get( 1 ) );
		expected.add( bundle.vertices.get( 5 ) );
		expected.add( bundle.vertices.get( 4 ) );
		expected.add( bundle.vertices.get( 3 ) );
		expected.add( bundle.vertices.get( 7 ) ); // Jump
		expected.add( bundle.vertices.get( 8 ) );
		expected.add( bundle.vertices.get( 9 ) );
		expected.add( bundle.vertices.get( 10 ) );
		expected.add( bundle.vertices.get( 11 ) );
		expected.add( bundle.vertices.get( 12 ) );
		expected.add( bundle.vertices.get( 13 ) ); // Jump
		expected.add( bundle.vertices.get( 0 ) );
		expected.add( bundle.vertices.get( 2 ) );
		expected.add( bundle.vertices.get( 6 ) );

		final Iterator< TestSimpleVertex > eit = expected.iterator();
		final Iterator< TestSimpleVertex > it =
				new DepthFirstCrossComponentIterator<>( start, graph );

		while ( eit.hasNext() )
		{
			assertTrue( "Iterator should not be finished, but is.", it.hasNext() );
			final TestSimpleVertex a = it.next();
			final TestSimpleVertex e = eit.next();
			assertEquals( "Unexpected vertex met during iteration.", e, a );
		}

		assertFalse( "Iteration should be finished, but is not.", it.hasNext() );
	}

	@Test
	public void testTwoComponentsStartOnRootPool()
	{
		final GraphTestBundle< TestSimpleVertex, TestSimpleEdge > bundle = GraphsForTests.twoComponentsPoolObjects();
		final Graph< TestSimpleVertex, TestSimpleEdge > graph = bundle.graph;
		// Start with a root.
		final TestSimpleVertex start = bundle.vertices.get( 0 );

		final RefList< TestSimpleVertex > expected = RefCollections.createRefList( bundle.graph.vertices() );
		expected.add( bundle.vertices.get( 0 ) );
		expected.add( bundle.vertices.get( 4 ) );
		expected.add( bundle.vertices.get( 2 ) );
		expected.add( bundle.vertices.get( 6 ) );
		expected.add( bundle.vertices.get( 1 ) );
		expected.add( bundle.vertices.get( 5 ) );
		expected.add( bundle.vertices.get( 3 ) );
		expected.add( bundle.vertices.get( 7 ) );
		expected.add( bundle.vertices.get( 8 ) );
		expected.add( bundle.vertices.get( 9 ) );
		expected.add( bundle.vertices.get( 10 ) );
		expected.add( bundle.vertices.get( 11 ) );
		expected.add( bundle.vertices.get( 12 ) );
		expected.add( bundle.vertices.get( 13 ) );

		final Iterator< TestSimpleVertex > eit = expected.iterator();
		final Iterator< TestSimpleVertex > it =
				new DepthFirstCrossComponentIterator<>( start, graph );

		while ( eit.hasNext() )
		{
			assertTrue( "Iterator should not be finished, but is.", it.hasNext() );
			final TestSimpleVertex a = it.next();
			final TestSimpleVertex e = eit.next();
			assertEquals( "Unexpected vertex met during iteration.", e, a );
		}

		assertFalse( "Iteration should be finished, but is not.", it.hasNext() );
	}

	@Test
	public void testOneComponent()
	{
		final GraphTestBundle< ObjectVertex< Integer >, ObjectEdge< Integer > > bundle = GraphsForTests.wpExampleStdObjects();
		final Graph< ObjectVertex< Integer >, ObjectEdge< Integer > > graph = bundle.graph;
		// Start with B.
		final ObjectVertex< Integer > start = bundle.vertices.get( 1 );

		final RefList< ObjectVertex< Integer > > expected = RefCollections.createRefList( bundle.graph.vertices() );
		expected.add( bundle.vertices.get( 1 ) );
		expected.add( bundle.vertices.get( 5 ) );
		expected.add( bundle.vertices.get( 4 ) );
		expected.add( bundle.vertices.get( 3 ) ); // Jump here.
		expected.add( bundle.vertices.get( 0 ) ); // Come back on first
		expected.add( bundle.vertices.get( 2 ) );
		expected.add( bundle.vertices.get( 6 ) );

		final Iterator< ObjectVertex< Integer > > eit = expected.iterator();
		final Iterator< ObjectVertex< Integer > > it =
				new DepthFirstCrossComponentIterator<>( start, graph );

		while ( eit.hasNext() )
		{
			assertTrue( "Iterator should not be finished, but is.", it.hasNext() );
			final ObjectVertex< Integer > a = it.next();
			final ObjectVertex< Integer > e = eit.next();
			assertEquals( "Unexpected vertex met during iteration.", e, a );
		}

		assertFalse( "Iteration should be finished, but is not.", it.hasNext() );
	}

	@Test
	public void testOneComponentPool()
	{
		final GraphTestBundle< TestSimpleVertex, TestSimpleEdge > bundle = GraphsForTests.wpExamplePoolObjects();
		final Graph< TestSimpleVertex, TestSimpleEdge > graph = bundle.graph;
		// Start with B.
		final TestSimpleVertex start = bundle.vertices.get( 1 );

		final RefList< TestSimpleVertex > expected = RefCollections.createRefList( bundle.graph.vertices() );
		expected.add( bundle.vertices.get( 1 ) );
		expected.add( bundle.vertices.get( 5 ) );
		expected.add( bundle.vertices.get( 4 ) );
		expected.add( bundle.vertices.get( 3 ) ); // End, jump to
		expected.add( bundle.vertices.get( 0 ) ); // Here
		expected.add( bundle.vertices.get( 2 ) );
		expected.add( bundle.vertices.get( 6 ) );

		final Iterator< TestSimpleVertex > eit = expected.iterator();
		final Iterator< TestSimpleVertex > it =
				new DepthFirstCrossComponentIterator<>( start, graph );

		while ( eit.hasNext() )
		{
			assertTrue( "Iterator should not be finished, but is.", it.hasNext() );
			final TestSimpleVertex a = it.next();
			final TestSimpleVertex e = eit.next();
			assertEquals( "Unexpected vertex met during iteration.", e, a );
		}

		assertFalse( "Iteration should be finished, but is not.", it.hasNext() );
	}

	@Test
	public void testOneEdge()
	{
		final GraphTestBundle< ObjectVertex< Integer >, ObjectEdge< Integer > > bundle = GraphsForTests.singleEdgeStdObjects();
		final Graph< ObjectVertex< Integer >, ObjectEdge< Integer > > graph = bundle.graph;
		// Start with B.
		final ObjectVertex< Integer > start = bundle.vertices.get( 1 );

		final RefList< ObjectVertex< Integer > > expected = RefCollections.createRefList( bundle.graph.vertices() );
		expected.add( bundle.vertices.get( 1 ) ); // Jump
		expected.add( bundle.vertices.get( 0 ) ); // Come back on first

		final Iterator< ObjectVertex< Integer > > eit = expected.iterator();
		final Iterator< ObjectVertex< Integer > > it =
				new DepthFirstCrossComponentIterator<>( start, graph );

		while ( eit.hasNext() )
		{
			assertTrue( "Iterator should not be finished, but is.", it.hasNext() );
			final ObjectVertex< Integer > a = it.next();
			final ObjectVertex< Integer > e = eit.next();
			assertEquals( "Unexpected vertex met during iteration.", e, a );
		}

		assertFalse( "Iteration should be finished, but is not.", it.hasNext() );
	}

	@Test
	public void testOneEdgePool()
	{
		final GraphTestBundle< TestSimpleVertex, TestSimpleEdge > bundle = GraphsForTests.singleEdgePoolObjects();
		final Graph< TestSimpleVertex, TestSimpleEdge > graph = bundle.graph;
		// Start with B.
		final TestSimpleVertex start = bundle.vertices.get( 1 );

		final RefList< TestSimpleVertex > expected = RefCollections.createRefList( bundle.graph.vertices() );
		expected.add( bundle.vertices.get( 1 ) ); // Jump here.
		expected.add( bundle.vertices.get( 0 ) ); // Come back on first

		final Iterator< TestSimpleVertex > eit = expected.iterator();
		final Iterator< TestSimpleVertex > it =
				new DepthFirstCrossComponentIterator<>( start, graph );

		while ( eit.hasNext() )
		{
			assertTrue( "Iterator should not be finished, but is.", it.hasNext() );
			final TestSimpleVertex a = it.next();
			final TestSimpleVertex e = eit.next();
			assertEquals( "Unexpected vertex met during iteration.", e, a );
		}

		assertFalse( "Iteration should be finished, but is not.", it.hasNext() );
	}

	@Test
	public void testOneVertex()
	{
		final GraphTestBundle< ObjectVertex< Integer >, ObjectEdge< Integer > > bundle = GraphsForTests.singleVertexStdObjects();
		final Graph< ObjectVertex< Integer >, ObjectEdge< Integer > > graph = bundle.graph;
		// Start with B.
		final ObjectVertex< Integer > start = bundle.vertices.get( 0 );

		final RefList< ObjectVertex< Integer > > expected = RefCollections.createRefList( bundle.graph.vertices() );
		expected.add( bundle.vertices.get( 0 ) );

		final Iterator< ObjectVertex< Integer > > eit = expected.iterator();
		final Iterator< ObjectVertex< Integer > > it =
				new DepthFirstCrossComponentIterator<>( start, graph );

		while ( eit.hasNext() )
		{
			assertTrue( "Iterator should not be finished, but is.", it.hasNext() );
			final ObjectVertex< Integer > a = it.next();
			final ObjectVertex< Integer > e = eit.next();
			assertEquals( "Unexpected vertex met during iteration.", e, a );
		}

		assertFalse( "Iteration should be finished, but is not.", it.hasNext() );
	}

	@Test
	public void testOneVertexPool()
	{
		final GraphTestBundle< TestSimpleVertex, TestSimpleEdge > bundle = GraphsForTests.singleVertexPoolObjects();
		final Graph< TestSimpleVertex, TestSimpleEdge > graph = bundle.graph;
		// Start with B.
		final TestSimpleVertex start = bundle.vertices.get( 0 );

		final RefList< TestSimpleVertex > expected = RefCollections.createRefList( bundle.graph.vertices() );
		expected.add( bundle.vertices.get( 0 ) );

		final Iterator< TestSimpleVertex > eit = expected.iterator();
		final Iterator< TestSimpleVertex > it =
				new DepthFirstCrossComponentIterator<>( start, graph );

		while ( eit.hasNext() )
		{
			assertTrue( "Iterator should not be finished, but is.", it.hasNext() );
			final TestSimpleVertex a = it.next();
			final TestSimpleVertex e = eit.next();
			assertEquals( "Unexpected vertex met during iteration.", e, a );
		}

		assertFalse( "Iteration should be finished, but is not.", it.hasNext() );
	}

	@Test
	public void testCrossComponentOrder()
	{
		final GraphTestBundle< ObjectVertex< Integer >, ObjectEdge< Integer > > bundle = GraphsForTests.multipleComponentsStdObjects();
		final Graph< ObjectVertex< Integer >, ObjectEdge< Integer > > graph = bundle.graph;

		// Specifies root order.
		final RefList< ObjectVertex< Integer > > roots = RefCollections.createRefList( graph.vertices(), 4 );
		roots.add( bundle.vertices.get( 8 ) );
		roots.add( bundle.vertices.get( 0 ) );
		roots.add( bundle.vertices.get( 12 ) );
		roots.add( bundle.vertices.get( 4 ) );

		// Expected iteration order.
		final RefList< ObjectVertex< Integer > > expected = RefCollections.createRefList( bundle.graph.vertices() );
		expected.add( bundle.vertices.get( 8 ) );
		expected.add( bundle.vertices.get( 10 ) );
		expected.add( bundle.vertices.get( 11 ) );
		expected.add( bundle.vertices.get( 9 ) );

		expected.add( bundle.vertices.get( 0 ) );
		expected.add( bundle.vertices.get( 2 ) );
		expected.add( bundle.vertices.get( 3 ) );
		expected.add( bundle.vertices.get( 1 ) );

		expected.add( bundle.vertices.get( 12 ) );
		expected.add( bundle.vertices.get( 14 ) );
		expected.add( bundle.vertices.get( 15 ) );
		expected.add( bundle.vertices.get( 13 ) );

		expected.add( bundle.vertices.get( 4 ) );
		expected.add( bundle.vertices.get( 6 ) );
		expected.add( bundle.vertices.get( 7 ) );
		expected.add( bundle.vertices.get( 5 ) );

		final Iterator< ObjectVertex< Integer > > eit = expected.iterator();
		// Use constructor with specified roots.
		final Iterator< ObjectVertex< Integer > > it =
				new DepthFirstCrossComponentIterator<>( bundle.vertices.get( 8 ), graph, roots );

		while ( eit.hasNext() )
		{
			assertTrue( "Iterator should not be finished, but is.", it.hasNext() );
			final ObjectVertex< Integer > a = it.next();
			final ObjectVertex< Integer > e = eit.next();
			assertEquals( "Unexpected vertex met during iteration.", e, a );
		}

		assertFalse( "Iteration should be finished, but is not.", it.hasNext() );
	}

	@Test
	public void testCrossComponentOrderPool()
	{
		final GraphTestBundle< TestSimpleVertex, TestSimpleEdge > bundle = GraphsForTests.multipleComponentsPoolObjects();
		final Graph< TestSimpleVertex, TestSimpleEdge > graph = bundle.graph;

		// Specifies root order.
		final RefList< TestSimpleVertex > roots = RefCollections.createRefList( graph.vertices(), 4 );
		roots.add( bundle.vertices.get( 8 ) );
		roots.add( bundle.vertices.get( 0 ) );
		roots.add( bundle.vertices.get( 12 ) );
		roots.add( bundle.vertices.get( 4 ) );

		// Expected iteration order.
		final RefList< TestSimpleVertex > expected = RefCollections.createRefList( bundle.graph.vertices() );
		expected.add( bundle.vertices.get( 8 ) );
		expected.add( bundle.vertices.get( 10 ) );
		expected.add( bundle.vertices.get( 11 ) );
		expected.add( bundle.vertices.get( 9 ) );

		expected.add( bundle.vertices.get( 0 ) );
		expected.add( bundle.vertices.get( 2 ) );
		expected.add( bundle.vertices.get( 3 ) );
		expected.add( bundle.vertices.get( 1 ) );

		expected.add( bundle.vertices.get( 12 ) );
		expected.add( bundle.vertices.get( 14 ) );
		expected.add( bundle.vertices.get( 15 ) );
		expected.add( bundle.vertices.get( 13 ) );

		expected.add( bundle.vertices.get( 4 ) );
		expected.add( bundle.vertices.get( 6 ) );
		expected.add( bundle.vertices.get( 7 ) );
		expected.add( bundle.vertices.get( 5 ) );

		final Iterator< TestSimpleVertex > eit = expected.iterator();
		// Use constructor with specified roots.
		final Iterator< TestSimpleVertex > it =
				new DepthFirstCrossComponentIterator<>( bundle.vertices.get( 8 ), graph, roots );

		while ( eit.hasNext() )
		{
			assertTrue( "Iterator should not be finished, but is.", it.hasNext() );
			final TestSimpleVertex a = it.next();
			final TestSimpleVertex e = eit.next();
			assertEquals( "Unexpected vertex met during iteration.", e, a );
		}

		assertFalse( "Iteration should be finished, but is not.", it.hasNext() );
	}
}
