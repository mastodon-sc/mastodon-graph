package org.mastodon.graph.algorithm.traversal;

import java.util.Iterator;
import org.junit.Before;
import org.junit.Test;
import org.mastodon.collection.RefCollections;
import org.mastodon.collection.RefList;
import org.mastodon.graph.Graph;
import org.mastodon.graph.TestSimpleEdge;
import org.mastodon.graph.TestSimpleVertex;
import org.mastodon.graph.algorithm.traversal.GraphsForTests.GraphTestBundle;
import org.mastodon.graph.object.ObjectEdge;
import org.mastodon.graph.object.ObjectVertex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UndirectedDepthFirstIteratorTest
{

	@Before
	public void setUp() throws Exception
	{}

	@Test
	public void testStraightLinePoolObjects()
	{
		final GraphTestBundle< TestSimpleVertex, TestSimpleEdge > bundle = GraphsForTests.straightLinePoolObjects();

		final TestSimpleVertex first = bundle.vertices.get( 5 );
		final UndirectedDepthFirstIterator< TestSimpleVertex, TestSimpleEdge > it = new UndirectedDepthFirstIterator<>( first, bundle.graph );

		final RefList< TestSimpleVertex > expected = RefCollections.createRefList( bundle.graph.vertices() );
		expected.add( bundle.vertices.get( 5 ) );
		expected.add( bundle.vertices.get( 6 ) );
		expected.add( bundle.vertices.get( 4 ) );
		expected.add( bundle.vertices.get( 3 ) );
		expected.add( bundle.vertices.get( 2 ) );
		expected.add( bundle.vertices.get( 1 ) );
		expected.add( bundle.vertices.get( 0 ) );
		final Iterator< TestSimpleVertex > eit = expected.iterator();

		while ( eit.hasNext() )
		{
			assertTrue( "Iterator should not be finished, but is.", it.hasNext() );
			assertEquals( "Unexpected vertex met during iteration.", eit.next(), it.next() );
		}

		assertFalse( "Iteration should be finished, but is not.", it.hasNext() );
	}

	@Test
	public void testTwoComponentsPoolObjects()
	{
		final GraphTestBundle< TestSimpleVertex, TestSimpleEdge > bundle = GraphsForTests.twoComponentsPoolObjects();

		final TestSimpleVertex first = bundle.vertices.get( 4 );
		final UndirectedDepthFirstIterator< TestSimpleVertex, TestSimpleEdge > it = new UndirectedDepthFirstIterator<>( first, bundle.graph );

		final RefList< TestSimpleVertex > expected = RefCollections.createRefList( bundle.graph.vertices() );
		expected.add( bundle.vertices.get( 4 ) );
		expected.add( bundle.vertices.get( 5 ) );
		expected.add( bundle.vertices.get( 1 ) );
		expected.add( bundle.vertices.get( 3 ) );
		expected.add( bundle.vertices.get( 0 ) );
		expected.add( bundle.vertices.get( 2 ) );
		expected.add( bundle.vertices.get( 6 ) );
		final Iterator< TestSimpleVertex > eit = expected.iterator();

		while ( eit.hasNext() )
		{
			assertTrue( "Iterator should not be finished, but is.", it.hasNext() );
			assertEquals( "Unexpected vertex met during iteration.", eit.next(), it.next() );
		}

		assertFalse( "Iteration should be finished, but is not.", it.hasNext() );
	}
}
