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
package org.mastodon.graph.algorithm.traversal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;
import org.mastodon.collection.RefCollections;
import org.mastodon.collection.RefList;
import org.mastodon.graph.TestSimpleEdge;
import org.mastodon.graph.TestSimpleVertex;
import org.mastodon.graph.algorithm.traversal.GraphsForTests.GraphTestBundle;

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
