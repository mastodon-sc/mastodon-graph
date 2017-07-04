package org.mastodon.graph.traversal;

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
import org.mastodon.graph.algorithm.traversal.InverseBreadthFirstCrossComponentIterator;
import org.mastodon.graph.object.ObjectEdge;
import org.mastodon.graph.object.ObjectVertex;
import org.mastodon.graph.traversal.GraphsForTests.GraphTestBundle;

public class InverseBreadthFirstCrossComponentIteratorTest
{

	@Before
	public void setUp() throws Exception
	{}

	@Test
	public void testTwoComponentsComeBackOnFirstPool()
	{
		final GraphTestBundle< TestSimpleVertex, TestSimpleEdge > bundle = GraphsForTests.twoComponentsPoolObjects();
		final Graph< TestSimpleVertex, TestSimpleEdge > graph = bundle.graph;
		// Start with B.
		final TestSimpleVertex start = bundle.vertices.get( 1 );

		final RefList< TestSimpleVertex > expected = RefCollections.createRefList( bundle.graph.vertices() );
		expected.add( bundle.vertices.get( 1 ) );
		expected.add( bundle.vertices.get( 0 ) ); // Jump
		expected.add( bundle.vertices.get( 13 ) );
		expected.add( bundle.vertices.get( 12 ) );
		expected.add( bundle.vertices.get( 11 ) );
		expected.add( bundle.vertices.get( 10 ) );
		expected.add( bundle.vertices.get( 9 ) );
		expected.add( bundle.vertices.get( 8 ) );
		expected.add( bundle.vertices.get( 7 ) ); // Jump
		expected.add( bundle.vertices.get( 6 ) );
		expected.add( bundle.vertices.get( 2 ) );
		expected.add( bundle.vertices.get( 4 ) );
		expected.add( bundle.vertices.get( 5 ) );
		expected.add( bundle.vertices.get( 3 ) );

		final Iterator< TestSimpleVertex > eit = expected.iterator();
		final Iterator< TestSimpleVertex > it =
				new InverseBreadthFirstCrossComponentIterator<>( start, graph );

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
	public void testTwoComponentsStartOnLeafPool()
	{
		final GraphTestBundle< TestSimpleVertex, TestSimpleEdge > bundle = GraphsForTests.twoComponentsPoolObjects();
		final Graph< TestSimpleVertex, TestSimpleEdge > graph = bundle.graph;
		// Start with a leaf.
		final TestSimpleVertex start = bundle.vertices.get( 4 );

		final RefList< TestSimpleVertex > expected = RefCollections.createRefList( bundle.graph.vertices() );
		expected.add( bundle.vertices.get( 4 ) );
		expected.add( bundle.vertices.get( 0 ) ); // Finish, jump..
		expected.add( bundle.vertices.get( 5 ) );
		expected.add( bundle.vertices.get( 1 ) );
		expected.add( bundle.vertices.get( 13 ) ); // Here
		expected.add( bundle.vertices.get( 12 ) );
		expected.add( bundle.vertices.get( 11 ) );
		expected.add( bundle.vertices.get( 10 ) );
		expected.add( bundle.vertices.get( 9 ) );
		expected.add( bundle.vertices.get( 8 ) );
		expected.add( bundle.vertices.get( 7 ) ); // Finish, jump back
		expected.add( bundle.vertices.get( 6 ) );
		expected.add( bundle.vertices.get( 2 ) );
		expected.add( bundle.vertices.get( 3 ) );

		final Iterator< TestSimpleVertex > eit = expected.iterator();
		final Iterator< TestSimpleVertex > it =
				new InverseBreadthFirstCrossComponentIterator<>( start, graph );

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
	public void testOneComponentPool()
	{
		final GraphTestBundle< TestSimpleVertex, TestSimpleEdge > bundle = GraphsForTests.wpExamplePoolObjects();
		final Graph< TestSimpleVertex, TestSimpleEdge > graph = bundle.graph;
		// Start with B.
		final TestSimpleVertex start = bundle.vertices.get( 1 );

		final RefList< TestSimpleVertex > expected = RefCollections.createRefList( bundle.graph.vertices() );
		expected.add( bundle.vertices.get( 1 ) );
		expected.add( bundle.vertices.get( 0 ) ); // Finish, jump...
		expected.add( bundle.vertices.get( 6 ) ); // Here
		expected.add( bundle.vertices.get( 2 ) ); // Finish
		expected.add( bundle.vertices.get( 4 ) );
		expected.add( bundle.vertices.get( 5 ) );
		expected.add( bundle.vertices.get( 3 ) );

		final Iterator< TestSimpleVertex > eit = expected.iterator();
		final Iterator< TestSimpleVertex > it =
				new InverseBreadthFirstCrossComponentIterator<>( start, graph );

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
				new InverseBreadthFirstCrossComponentIterator<>( start, graph );

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
				new InverseBreadthFirstCrossComponentIterator<>( start, graph );

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
				new InverseBreadthFirstCrossComponentIterator<>( start, graph );

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
				new InverseBreadthFirstCrossComponentIterator<>( start, graph );

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

		// Specifies leaf order.
		final RefList< ObjectVertex< Integer > > leaves = RefCollections.createRefList( graph.vertices(), 4 );
		leaves.add( bundle.vertices.get( 11 ) );
		leaves.add( bundle.vertices.get( 3 ) );
		leaves.add( bundle.vertices.get( 15 ) );
		leaves.add( bundle.vertices.get( 7 ) );

		// Expected iteration order.
		final RefList< ObjectVertex< Integer > > expected = RefCollections.createRefList( bundle.graph.vertices() );
		expected.add( bundle.vertices.get( 11 ) );
		expected.add( bundle.vertices.get( 9 ) );
		expected.add( bundle.vertices.get( 10 ) );
		expected.add( bundle.vertices.get( 8 ) );

		expected.add( bundle.vertices.get( 3 ) );
		expected.add( bundle.vertices.get( 1 ) );
		expected.add( bundle.vertices.get( 2 ) );
		expected.add( bundle.vertices.get( 0 ) );

		expected.add( bundle.vertices.get( 15 ) );
		expected.add( bundle.vertices.get( 13 ) );
		expected.add( bundle.vertices.get( 14 ) );
		expected.add( bundle.vertices.get( 12 ) );

		expected.add( bundle.vertices.get( 7 ) );
		expected.add( bundle.vertices.get( 5 ) );
		expected.add( bundle.vertices.get( 6 ) );
		expected.add( bundle.vertices.get( 4 ) );

		final Iterator< ObjectVertex< Integer > > eit = expected.iterator();
		// Use constructor with specified roots.
		final Iterator< ObjectVertex< Integer > > it =
				new InverseBreadthFirstCrossComponentIterator<>( bundle.vertices.get( 11 ), graph, leaves );

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

		// Specifies leaf order.
		final RefList< TestSimpleVertex > leaves = RefCollections.createRefList( graph.vertices(), 4 );
		leaves.add( bundle.vertices.get( 11 ) );
		leaves.add( bundle.vertices.get( 3 ) );
		leaves.add( bundle.vertices.get( 15 ) );
		leaves.add( bundle.vertices.get( 7 ) );

		// Expected iteration order.
		final RefList< TestSimpleVertex > expected = RefCollections.createRefList( bundle.graph.vertices() );
		expected.add( bundle.vertices.get( 11 ) );
		expected.add( bundle.vertices.get( 9 ) );
		expected.add( bundle.vertices.get( 10 ) );
		expected.add( bundle.vertices.get( 8 ) );

		expected.add( bundle.vertices.get( 3 ) );
		expected.add( bundle.vertices.get( 1 ) );
		expected.add( bundle.vertices.get( 2 ) );
		expected.add( bundle.vertices.get( 0 ) );

		expected.add( bundle.vertices.get( 15 ) );
		expected.add( bundle.vertices.get( 13 ) );
		expected.add( bundle.vertices.get( 14 ) );
		expected.add( bundle.vertices.get( 12 ) );

		expected.add( bundle.vertices.get( 7 ) );
		expected.add( bundle.vertices.get( 5 ) );
		expected.add( bundle.vertices.get( 6 ) );
		expected.add( bundle.vertices.get( 4 ) );

		final Iterator< TestSimpleVertex > eit = expected.iterator();
		// Use constructor with specified roots.
		final Iterator< TestSimpleVertex > it =
				new InverseBreadthFirstCrossComponentIterator<>( bundle.vertices.get( 11 ), graph, leaves );

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
