/*-
 * #%L
 * Mastodon Graphs
 * %%
 * Copyright (C) 2015 - 2022 Tobias Pietzsch, Jean-Yves Tinevez
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

import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mastodon.collection.RefCollections;
import org.mastodon.collection.RefList;
import org.mastodon.collection.RefSet;
import org.mastodon.graph.TestSimpleEdge;
import org.mastodon.graph.TestSimpleGraph;
import org.mastodon.graph.TestSimpleVertex;

import gnu.trove.set.hash.TIntHashSet;

public class ConnectedComponentsTest
{

	private TestSimpleGraph graph;

	private int idLoop;

	private int idTree;

	private TestSimpleVertex R00;

	private TestSimpleVertex E;

	@Before
	public void setUp() throws Exception
	{
		graph = new TestSimpleGraph();

		/*
		 * 1. Linear branch.
		 */

		final int idLinearBranch = 0;
		final TestSimpleVertex A = graph.addVertex().init( idLinearBranch );
		final TestSimpleVertex B = graph.addVertex().init( idLinearBranch );
		final TestSimpleVertex C = graph.addVertex().init( idLinearBranch );
		final TestSimpleVertex D = graph.addVertex().init( idLinearBranch );
		graph.addEdge( A, B );
		graph.addEdge( B, C );
		graph.addEdge( C, D );

		/*
		 * 2. Loop.
		 */

		idLoop = 1;
		E = graph.addVertex().init( idLoop );
		final TestSimpleVertex F = graph.addVertex().init( idLoop );
		final TestSimpleVertex G = graph.addVertex().init( idLoop );
		final TestSimpleVertex H = graph.addVertex().init( idLoop );
		graph.addEdge( E, F );
		graph.addEdge( F, G );
		graph.addEdge( G, H );
		graph.addEdge( H, E );

		/*
		 * 3. Tree.
		 */

		idTree = 2;
		final TestSimpleVertex R0 = graph.addVertex().init( idTree );
		R00 = graph.addVertex().init( idTree );
		final TestSimpleVertex R01 = graph.addVertex().init( idTree );
		final TestSimpleVertex R000 = graph.addVertex().init( idTree );
		final TestSimpleVertex R001 = graph.addVertex().init( idTree );
		final TestSimpleVertex R010 = graph.addVertex().init( idTree );
		final TestSimpleVertex R011 = graph.addVertex().init( idTree );
		graph.addEdge( R0, R00 );
		graph.addEdge( R0, R01 );
		graph.addEdge( R00, R000 );
		graph.addEdge( R00, R001 );
		graph.addEdge( R01, R010 );
		graph.addEdge( R01, R011 );

		/*
		 * 4. Single.
		 */

		final int idSingle = 3;
		graph.addVertex().init( idSingle );

		/*
		 * 5. Double.
		 */

		final int idDouble = 4;
		final TestSimpleVertex I1 = graph.addVertex().init( idDouble );
		final TestSimpleVertex I2 = graph.addVertex().init( idDouble );
		graph.addEdge( I1, I2 );

		/*
		 * 6. Random.
		 */

		final Random ran = new Random( 1l );
		final int nv = 50 + ran.nextInt( 100 );
		final int nExtraEdges = 20 + ran.nextInt( 50 );
		final RefList< TestSimpleVertex > vList = RefCollections.createRefList( graph.vertices(), nv );
		final int idRandom = 5;

		final TestSimpleVertex previous = graph.addVertex().init( idRandom );
		vList.add( previous );
		for ( int i = 1; i < nv; i++ )
		{
			final TestSimpleVertex current = graph.addVertex().init( idRandom );
			vList.add( current );
			// At least a linear branch to ensure they are all connected.
			graph.addEdge( previous, current );
		}

		for ( int i = 0; i < nExtraEdges; i++ )
		{
			final int iSource = ran.nextInt( vList.size() );
			final int iTarget = ran.nextInt( vList.size() );
			if ( iSource == iTarget )
			{
				continue;
			}

			final TestSimpleVertex source = vList.get( iSource );
			final TestSimpleVertex target = vList.get( iTarget );
			graph.addEdge( source, target );
		}
	}

	@Test
	public void testBehavior()
	{
		final ConnectedComponents< TestSimpleVertex, TestSimpleEdge > cc = new ConnectedComponents< >( graph );
		final Set< RefSet< TestSimpleVertex >> components = cc.get();
		final TIntHashSet componentIds = new TIntHashSet( components.size() );

		int counter = 0;
		for ( final RefSet< TestSimpleVertex > refSet : components )
		{
			final Iterator< TestSimpleVertex > it = refSet.iterator();
			final TestSimpleVertex previous = it.next();
			counter++;
			final int currentId = previous.getId();

			assertFalse( "The component with ID = " + currentId + " is disjoint: it belongs to two different sets.", componentIds.contains( currentId ) );

			componentIds.add( currentId );
			while ( it.hasNext() )
			{
				counter++;
				final TestSimpleVertex current = it.next();
				final int id = current.getId();
				assertEquals( "Found an undesired vertex in a connected components.", currentId, id );
			}
		}

		/*
		 * Check that we did not forget any vertex.
		 */
		assertEquals( "Connected components do not span the whole graph.", graph.vertices().size(), counter );

		/*
		 * Link two components and reuse the same algo instance.
		 */

		graph.addEdge( E, R00 );
		final Set< RefSet< TestSimpleVertex >> nCC = cc.get();

		counter = 0;
		componentIds.clear();
		for ( final RefSet< TestSimpleVertex > refSet : nCC )
		{
			final Iterator< TestSimpleVertex > it = refSet.iterator();
			final TestSimpleVertex previous = it.next();
			counter++;
			final int currentId = previous.getId();
			assertFalse( "The component with ID = " + currentId + " is disjoint: it belongs to two different sets.", componentIds.contains( currentId ) );

			componentIds.add( currentId );
			while ( it.hasNext() )
			{
				counter++;
				final TestSimpleVertex current = it.next();
				final int id = current.getId();
				componentIds.add( id );

				if ( id != idLoop && id != idTree )
				{
					assertEquals( "Found an undesired vertex in a connected components.", currentId, id );
				}
			}
		}
		assertEquals( "Connected components do not span the whole graph.", graph.vertices().size(), counter );

	}

}
