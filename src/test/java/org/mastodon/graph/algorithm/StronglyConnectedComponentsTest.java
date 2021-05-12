/*-
 * #%L
 * Mastodon Graphs
 * %%
 * Copyright (C) 2015 - 2021 Tobias Pietzsch, Jean-Yves Tinevez
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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.Test;
import org.mastodon.collection.RefCollections;
import org.mastodon.collection.RefSet;
import org.mastodon.graph.TestSimpleGraph;
import org.mastodon.graph.TestSimpleVertex;

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
		graph.addEdge( v7, v5 );
		graph.addEdge( v7, v6 );
		graph.addEdge( v7, v7 );


		final Set< Set< TestSimpleVertex > > expected = new HashSet<>();
		expected.add( new HashSet<>( Arrays.asList( v0, v1, v2 ) ) );
		expected.add( new HashSet<>( Arrays.asList( v3, v5 ) ) );
		expected.add( new HashSet<>( Arrays.asList( v3, v5 ) ) );
		expected.add( new HashSet<>( Arrays.asList( v4 ) ) );
		expected.add( new HashSet<>( Arrays.asList( v6 ) ) );
		expected.add( new HashSet<>( Arrays.asList( v7 ) ) );

		final Set< RefSet< TestSimpleVertex > > actual = StronglyConnectedComponents.stronglyConnectedComponents( graph );

		assertEquals( expected, comparableCopy( actual ) );
	}

	private static < T > Set< Set< T > > comparableCopy( final Set< RefSet< T > > setOfSets )
	{
		final Set< Set< T > > copy = new HashSet<>();
		for ( final RefSet< T > set : setOfSets )
			copy.add( comparableCopy( set ) );
		return copy;
	}

	private static < T > Set< T > comparableCopy( final RefSet< T > set )
	{
		final Set< T > copy = new HashSet<>();
		final Iterator< T > it = RefCollections.safeIterator( set.iterator(), set );
		while ( it.hasNext() )
			copy.add( it.next() );
		return copy;
	}
}
