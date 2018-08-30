package org.mastodon.graph.algorithm;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.junit.Test;
import org.mastodon.collection.RefCollections;
import org.mastodon.collection.RefSet;
import org.mastodon.graph.TestSimpleGraph;
import org.mastodon.graph.TestSimpleVertex;

import static org.junit.Assert.assertEquals;

public class AncestorFinderTest
{
	@Test
	public void testBehavior1()
	{
		final TestSimpleGraph graph = new TestSimpleGraph();

		final TestSimpleVertex v0 = graph.addVertex().init( 0 );
		final TestSimpleVertex v1 = graph.addVertex().init( 1 );
		final TestSimpleVertex v2 = graph.addVertex().init( 2 );
		final TestSimpleVertex v3 = graph.addVertex().init( 3 );
		final TestSimpleVertex v4 = graph.addVertex().init( 4 );
		final TestSimpleVertex v5 = graph.addVertex().init( 5 );
		final TestSimpleVertex v6 = graph.addVertex().init( 6 );
		final TestSimpleVertex v7 = graph.addVertex().init( 7 );

		graph.addEdge( v0, v1 );
		graph.addEdge( v1, v2 );
		graph.addEdge( v2, v0 );
		graph.addEdge( v3, v1 );
		graph.addEdge( v3, v2 );
		graph.addEdge( v3, v5 );
		graph.addEdge( v4, v2 );
		graph.addEdge( v5, v3 );
		graph.addEdge( v5, v4 );
		graph.addEdge( v6, v4 );
		graph.addEdge( v7, v5 );
		graph.addEdge( v7, v6 );
		graph.addEdge( v7, v7 );

		Set< TestSimpleVertex > initial;
		RefSet< TestSimpleVertex > actual;
		Set< TestSimpleVertex > expected;

		initial = new HashSet<>( Arrays.asList( v4 ) );
		expected = new HashSet<>( Arrays.asList( v3, v4, v5, v6, v7 ) );
		actual = AncestorFinder.ancestors( graph, initial );
		assertEquals( expected, comparableCopy( actual ) );

		initial = new HashSet<>( Arrays.asList( v4, v7 ) );
		expected = new HashSet<>( Arrays.asList( v3, v4, v5, v6, v7 ) );
		actual = AncestorFinder.ancestors( graph, initial );
		assertEquals( expected, comparableCopy( actual ) );

		initial = new HashSet<>( Arrays.asList( v3 ) );
		expected = new HashSet<>( Arrays.asList( v3, v5, v7 ) );
		actual = AncestorFinder.ancestors( graph, initial );
		assertEquals( expected, comparableCopy( actual ) );
	}

	@Test
	public void testBehavior2()
	{
		final TestSimpleGraph graph = new TestSimpleGraph();

		final TestSimpleVertex v0 = graph.addVertex().init( 0 );
		final TestSimpleVertex v1 = graph.addVertex().init( 1 );
		final TestSimpleVertex v2 = graph.addVertex().init( 2 );
		final TestSimpleVertex v3 = graph.addVertex().init( 3 );
		final TestSimpleVertex v4 = graph.addVertex().init( 4 );
		final TestSimpleVertex v5 = graph.addVertex().init( 5 );
		final TestSimpleVertex v6 = graph.addVertex().init( 6 );

		graph.addEdge( v0, v1 );
		graph.addEdge( v0, v5 );
		graph.addEdge( v1, v2 );
		graph.addEdge( v1, v4 );
		graph.addEdge( v2, v3 );
		graph.addEdge( v4, v3 );
		graph.addEdge( v5, v6 );

		Set< TestSimpleVertex > initial;
		RefSet< TestSimpleVertex > actual;
		Set< TestSimpleVertex > expected;

		initial = new HashSet<>( Arrays.asList( v4 ) );
		expected = new HashSet<>( Arrays.asList( v0, v1, v4 ) );
		actual = AncestorFinder.ancestors( graph, initial );
		assertEquals( expected, comparableCopy( actual ) );
	}

	private static < T > Set< T > comparableCopy( RefSet< T > set )
	{
		final Set< T > copy = new HashSet<>();
		final Iterator< T > it = RefCollections.safeIterator( set.iterator(), set );
		while ( it.hasNext() )
			copy.add( it.next() );
		return copy;
	}
}
