package org.mastodon.graph.algorithm;

import gnu.trove.set.hash.TIntHashSet;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.mastodon.collection.RefCollection;
import org.mastodon.collection.RefCollections;
import org.mastodon.collection.RefList;
import org.mastodon.collection.RefSet;
import org.mastodon.graph.TestSimpleEdge;
import org.mastodon.graph.TestSimpleGraph;
import org.mastodon.graph.TestSimpleVertex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class StronglyConnectedComponentsTest
{
	@Test
	public void testBehavior()
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
		graph.addEdge( v6, v7 );
		graph.addEdge( v7, v5 );
		graph.addEdge( v7, v6 );
		graph.addEdge( v7, v7 );


		final Set< Set< TestSimpleVertex > > expected = new HashSet<>();
		expected.add( new HashSet<>( Arrays.asList( v0, v1, v2 ) ) );
		expected.add( new HashSet<>( Arrays.asList( v3, v5 ) ) );
		expected.add( new HashSet<>( Arrays.asList( v3, v5 ) ) );
		expected.add( new HashSet<>( Arrays.asList( v4 ) ) );
		expected.add( new HashSet<>( Arrays.asList( v6, v7 ) ) );

		final Set< RefSet< TestSimpleVertex > > actual = StronglyConnectedComponents.stronglyConnectedComponents( graph );

		assertEquals( expected, comparableCopy( actual ) );
	}

	private static < T > Set< Set< T > > comparableCopy( Set< RefSet< T > > setOfSets )
	{
		final Set< Set< T > > copy = new HashSet<>();
		for ( RefSet< T > set : setOfSets )
			copy.add( comparableCopy( set ) );
		return copy;
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
