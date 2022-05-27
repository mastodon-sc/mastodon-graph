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
package org.mastodon.graph.algorithm.traversal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.mastodon.graph.Edge;
import org.mastodon.graph.Graph;
import org.mastodon.graph.TestSimpleEdge;
import org.mastodon.graph.TestSimpleGraph;
import org.mastodon.graph.TestSimpleVertex;
import org.mastodon.graph.Vertex;
import org.mastodon.graph.algorithm.traversal.GraphSearch.EdgeClass;
import org.mastodon.graph.object.ObjectEdge;
import org.mastodon.graph.object.ObjectGraph;
import org.mastodon.graph.object.ObjectVertex;

public class GraphsForTests
{
	public static class TraversalTester< V extends Vertex< E >, E extends Edge< V >, T extends GraphSearch< T, V, E > > implements SearchListener< V, E, T >
	{

		private final Iterator< V > expectedDiscoveredVertexIterator;

		private final Iterator< EdgeClass > expectedEdgeClassIterator;

		final Iterator< V > expectedProcessedVertexIterator;

		private final Iterator< E > expectedEdgeIterator;

		public TraversalTester( final Iterator< V > expectedDiscoveredVertexIterator, final Iterator< V > expectedProcessedVertexIterator, final Iterator< E > expectedEdgeIterator, final Iterator< EdgeClass > expectedEdgeClassIterator )
		{
			this.expectedDiscoveredVertexIterator = expectedDiscoveredVertexIterator;
			this.expectedProcessedVertexIterator = expectedProcessedVertexIterator;
			this.expectedEdgeIterator = expectedEdgeIterator;
			this.expectedEdgeClassIterator = expectedEdgeClassIterator;
		}

		@Override
		public void processVertexLate( final V vertex, final T search )
		{
			assertEquals( "Did not finish processing vertex in expected order during search.", expectedProcessedVertexIterator.next(), vertex );
		}

		@Override
		public void processVertexEarly( final V vertex, final T search )
		{
			assertEquals( "Did not discover the expected vertex sequence during search.", expectedDiscoveredVertexIterator.next(), vertex );
		}

		@Override
		public void processEdge( final E edge, final V from, final V to, final T search )
		{
			assertEquals( "Did not cross the expected edge sequence during search.", expectedEdgeIterator.next(), edge );

			final EdgeClass eclass = search.edgeClass( from, to );
			assertEquals( "The edge " + edge + " traversed  from " + from + " to " + to + " has an unexpected class in the search.", expectedEdgeClassIterator.next(), eclass );
		}

		public void searchDone()
		{
			assertFalse( "Did not discover all the expected vertices.", expectedDiscoveredVertexIterator.hasNext() );
			assertFalse( "Did not finish processing all the expected vertices.", expectedProcessedVertexIterator.hasNext() );
			assertFalse( "Did not cross all the expected edges.", expectedEdgeIterator.hasNext() );
			assertFalse( "Did not assess all edge classes.", expectedEdgeClassIterator.hasNext() );
		}

		@Override
		public void crossComponent( final V from, final V to, final T search )
		{}
	}

	public static final class VerboseTraversalTester< V extends Vertex< E >, E extends Edge< V >, T extends GraphSearch< T, V, E > > extends TraversalTester< V, E, T >
	{

		public VerboseTraversalTester( final Iterator< V > expectedDiscoveredVertexIterator, final Iterator< V > expectedProcessedVertexIterator, final Iterator< E > expectedEdgeIterator, final Iterator< EdgeClass > expectedEdgeClassIterator )
		{
			super( expectedDiscoveredVertexIterator, expectedProcessedVertexIterator, expectedEdgeIterator, expectedEdgeClassIterator );
		}

		@Override
		public void processEdge( final E edge, final V from, final V to, final T search )
		{
			System.out.println( " - Process edge " + from + " -> " + to + ", with class " + search.edgeClass( from, to ) );
			super.processEdge( edge, from, to, search );
		}

		@SuppressWarnings( { "unchecked", "rawtypes" } )
		@Override
		public void processVertexEarly( final V vertex, final T search )
		{
			System.out.print( " - Discovered vertex " + vertex );
			if ( search instanceof AbstractBreadthFirstSearch )
			{
				final AbstractBreadthFirstSearch adfs = ( AbstractBreadthFirstSearch ) search;
				System.out.println( ", depth = " + adfs.depthOf( vertex ) );
			}
			else
			{
				System.out.println();
			}
			super.processVertexEarly( vertex, search );
		}

		@Override
		public void processVertexLate( final V vertex, final T search )
		{
			System.out.println( " - Finished dealing with vertex " + vertex );
			super.processVertexLate( vertex, search );
		}

		@Override
		public void crossComponent( final V from, final V to, final T search )
		{
			System.out.println( " - Jumping to another component from " + from + " -> " + to );
			super.crossComponent( from, to, search );
		}

		@Override
		public void searchDone()
		{
			System.out.println( " - Search complete." );
			super.searchDone();
		}
	}

	public static final < V extends Vertex< E >, E extends Edge< V >, T extends GraphSearch< T, V, E > > SearchListener< V, E, T > traversalPrinter( final Graph< V, E > graph )
	{
		return new SearchListener< V, E, T >()
		{
			@Override
			public void processVertexLate( final V vertex, final T search )
			{
				System.out.println( " - Finished processing " + vertex );
			}

			@SuppressWarnings( { "rawtypes", "unchecked" } )
			@Override
			public void processVertexEarly( final V vertex, final T search )
			{
				System.out.print( " - Discovered " + vertex );
				if ( search instanceof AbstractBreadthFirstSearch )
				{
					final AbstractBreadthFirstSearch adfs = ( AbstractBreadthFirstSearch ) search;
					System.out.println( ", depth = " + adfs.depthOf( vertex ) );
				}
				else
				{
					System.out.println();
				}
			}

			@Override
			public void processEdge( final E edge, final V from, final V to, final T search )
			{
				System.out.println( " - Crossing " + edge + " from " + from + " to " + to + ". Edge class = " + search.edgeClass( from, to ) );
			}

			@Override
			public void crossComponent( final V from, final V to, final T search )
			{
				System.out.println( " - Jumping to another component from " + from + " -> " + to );
			}
		};
	}

	public static final GraphTestBundle< TestSimpleVertex, TestSimpleEdge > straightLinePoolObjects()
	{
		final GraphTestBundle< TestSimpleVertex, TestSimpleEdge > bundle = new GraphTestBundle<>();

		final TestSimpleGraph graph = new TestSimpleGraph();
		bundle.graph = graph;

		final TestSimpleVertex A = graph.addVertex().init( 1 );
		final TestSimpleVertex B = graph.addVertex().init( 2 );
		final TestSimpleVertex C = graph.addVertex().init( 3 );
		final TestSimpleVertex D = graph.addVertex().init( 4 );
		final TestSimpleVertex E = graph.addVertex().init( 5 );
		final TestSimpleVertex F = graph.addVertex().init( 6 );
		final TestSimpleVertex G = graph.addVertex().init( 7 );
		bundle.vertices = new ArrayList<>( 7 );
		bundle.vertices.add( A );
		bundle.vertices.add( B );
		bundle.vertices.add( C );
		bundle.vertices.add( D );
		bundle.vertices.add( E );
		bundle.vertices.add( F );
		bundle.vertices.add( G );

		final TestSimpleEdge eAB = graph.addEdge( A, B );
		final TestSimpleEdge eBC = graph.addEdge( B, C );
		final TestSimpleEdge eCD = graph.addEdge( C, D );
		final TestSimpleEdge eDE = graph.addEdge( D, E );
		final TestSimpleEdge eEF = graph.addEdge( E, F );
		final TestSimpleEdge eFG = graph.addEdge( F, G );
		bundle.edges = new ArrayList<>( 6 );
		bundle.edges.add( eAB );
		bundle.edges.add( eBC );
		bundle.edges.add( eCD );
		bundle.edges.add( eDE );
		bundle.edges.add( eEF );
		bundle.edges.add( eFG );

		bundle.name = "Straight line pool objects";
		return bundle;
	}

	public static final GraphTestBundle< ObjectVertex< Integer >, ObjectEdge< Integer > > straightLineStdObjects()
	{
		final GraphTestBundle< ObjectVertex< Integer >, ObjectEdge< Integer > > bundle = new GraphTestBundle<>();

		final ObjectGraph< Integer > graph = new ObjectGraph<>();
		bundle.graph = graph;

		final ObjectVertex< Integer > A = graph.addVertex().init( 1 );
		final ObjectVertex< Integer > B = graph.addVertex().init( 2 );
		final ObjectVertex< Integer > C = graph.addVertex().init( 3 );
		final ObjectVertex< Integer > D = graph.addVertex().init( 4 );
		final ObjectVertex< Integer > E = graph.addVertex().init( 5 );
		final ObjectVertex< Integer > F = graph.addVertex().init( 6 );
		final ObjectVertex< Integer > G = graph.addVertex().init( 7 );
		bundle.vertices = new ArrayList<>( 7 );
		bundle.vertices.add( A );
		bundle.vertices.add( B );
		bundle.vertices.add( C );
		bundle.vertices.add( D );
		bundle.vertices.add( E );
		bundle.vertices.add( F );
		bundle.vertices.add( G );

		final ObjectEdge< Integer > eAB = graph.addEdge( A, B );
		final ObjectEdge< Integer > eBC = graph.addEdge( B, C );
		final ObjectEdge< Integer > eCD = graph.addEdge( C, D );
		final ObjectEdge< Integer > eDE = graph.addEdge( D, E );
		final ObjectEdge< Integer > eEF = graph.addEdge( E, F );
		final ObjectEdge< Integer > eFG = graph.addEdge( F, G );
		bundle.edges = new ArrayList<>( 6 );
		bundle.edges.add( eAB );
		bundle.edges.add( eBC );
		bundle.edges.add( eCD );
		bundle.edges.add( eDE );
		bundle.edges.add( eEF );
		bundle.edges.add( eFG );

		bundle.name = "Straight line standard objects";
		return bundle;
	}

	public static final GraphTestBundle< TestSimpleVertex, TestSimpleEdge > loopPoolObjects()
	{
		final GraphTestBundle< TestSimpleVertex, TestSimpleEdge > bundle = straightLinePoolObjects();
		final TestSimpleEdge edge = bundle.graph.addEdge( bundle.vertices.get( 6 ), bundle.vertices.get( 0 ) );
		bundle.edges.add( edge );

		bundle.name = "Loop pool objects";
		return bundle;
	}

	public static final GraphTestBundle< ObjectVertex< Integer >, ObjectEdge< Integer > > loopStdObjects()
	{
		final GraphTestBundle< ObjectVertex< Integer >, ObjectEdge< Integer > > bundle = straightLineStdObjects();
		bundle.edges.add( bundle.graph.addEdge( bundle.vertices.get( 6 ), bundle.vertices.get( 0 ) ) );

		bundle.name = "Loop standard objects";
		return bundle;
	}

	public static final GraphTestBundle< TestSimpleVertex, TestSimpleEdge > wpExamplePoolObjects()
	{
		final GraphTestBundle< TestSimpleVertex, TestSimpleEdge > bundle = new GraphTestBundle<>();

		final TestSimpleGraph graph = new TestSimpleGraph();
		bundle.graph = graph;

		final TestSimpleVertex A = graph.addVertex().init( 1 );
		final TestSimpleVertex B = graph.addVertex().init( 2 );
		final TestSimpleVertex C = graph.addVertex().init( 3 );
		final TestSimpleVertex D = graph.addVertex().init( 4 );
		final TestSimpleVertex E = graph.addVertex().init( 5 );
		final TestSimpleVertex F = graph.addVertex().init( 6 );
		final TestSimpleVertex G = graph.addVertex().init( 7 );
		bundle.vertices = new ArrayList<>( 7 );
		bundle.vertices.add( A );
		bundle.vertices.add( B );
		bundle.vertices.add( C );
		bundle.vertices.add( D );
		bundle.vertices.add( E );
		bundle.vertices.add( F );
		bundle.vertices.add( G );

		final TestSimpleEdge eAB = graph.addEdge( A, B ); // 0
		final TestSimpleEdge eAC = graph.addEdge( A, C ); // 1
		final TestSimpleEdge eAE = graph.addEdge( A, E ); // 2
		final TestSimpleEdge eBD = graph.addEdge( B, D ); // 3
		final TestSimpleEdge eBF = graph.addEdge( B, F ); // 4
		final TestSimpleEdge eFE = graph.addEdge( F, E ); // 5
		final TestSimpleEdge eCG = graph.addEdge( C, G ); // 6
		bundle.edges = new ArrayList<>( 7 );
		bundle.edges.add( eAB ); // 0
		bundle.edges.add( eAC ); // 1
		bundle.edges.add( eAE ); // 2
		bundle.edges.add( eBD ); // 3
		bundle.edges.add( eBF ); // 4
		bundle.edges.add( eFE ); // 5
		bundle.edges.add( eCG ); // 6

		bundle.name = "General example pool objects";
		return bundle;
	}

	public static final GraphTestBundle< ObjectVertex< Integer >, ObjectEdge< Integer > > wpExampleStdObjects()
	{
		final GraphTestBundle< ObjectVertex< Integer >, ObjectEdge< Integer > > bundle = new GraphTestBundle<>();

		final ObjectGraph< Integer > graph = new ObjectGraph<>();
		bundle.graph = graph;

		final ObjectVertex< Integer > A = graph.addVertex().init( 1 );
		final ObjectVertex< Integer > B = graph.addVertex().init( 2 );
		final ObjectVertex< Integer > C = graph.addVertex().init( 3 );
		final ObjectVertex< Integer > D = graph.addVertex().init( 4 );
		final ObjectVertex< Integer > E = graph.addVertex().init( 5 );
		final ObjectVertex< Integer > F = graph.addVertex().init( 6 );
		final ObjectVertex< Integer > G = graph.addVertex().init( 7 );
		bundle.vertices = new ArrayList<>( 7 );
		bundle.vertices.add( A );
		bundle.vertices.add( B );
		bundle.vertices.add( C );
		bundle.vertices.add( D );
		bundle.vertices.add( E );
		bundle.vertices.add( F );
		bundle.vertices.add( G );

		final ObjectEdge< Integer > eAB = graph.addEdge( A, B );
		final ObjectEdge< Integer > eAC = graph.addEdge( A, C );
		final ObjectEdge< Integer > eAE = graph.addEdge( A, E );
		final ObjectEdge< Integer > eBD = graph.addEdge( B, D );
		final ObjectEdge< Integer > eBF = graph.addEdge( B, F );
		final ObjectEdge< Integer > eFE = graph.addEdge( F, E );
		final ObjectEdge< Integer > eCG = graph.addEdge( C, G );
		bundle.edges = new ArrayList<>( 7 );
		bundle.edges.add( eAB );
		bundle.edges.add( eAC );
		bundle.edges.add( eAE );
		bundle.edges.add( eBD );
		bundle.edges.add( eBF );
		bundle.edges.add( eFE );
		bundle.edges.add( eCG );

		bundle.name = "General example standard objects";
		return bundle;
	}

	public static final GraphTestBundle< TestSimpleVertex, TestSimpleEdge > singleVertexPoolObjects()
	{
		final GraphTestBundle< TestSimpleVertex, TestSimpleEdge > bundle = new GraphTestBundle<>();

		final TestSimpleGraph graph = new TestSimpleGraph();
		bundle.graph = graph;

		final TestSimpleVertex A = graph.addVertex().init( 1 );
		bundle.vertices = Arrays.asList( new TestSimpleVertex[] { A } );

		bundle.edges = Collections.emptyList();

		bundle.name = "Single vertex pool objects";
		return bundle;
	}

	public static final GraphTestBundle< ObjectVertex< Integer >, ObjectEdge< Integer >> singleVertexStdObjects()
	{
		final GraphTestBundle< ObjectVertex< Integer >, ObjectEdge< Integer > > bundle = new GraphTestBundle<>();

		final ObjectGraph< Integer > graph = new ObjectGraph<>();
		bundle.graph = graph;

		final ObjectVertex< Integer > A = graph.addVertex().init( 1 );
		bundle.vertices = new ArrayList<>( 1 );
		bundle.vertices.add( A );

		bundle.edges = Collections.emptyList();
		bundle.name = "Single vertex standard objects";
		return bundle;
	}

	public static final GraphTestBundle< TestSimpleVertex, TestSimpleEdge > singleEdgePoolObjects()
	{
		final GraphTestBundle< TestSimpleVertex, TestSimpleEdge > bundle = new GraphTestBundle<>();

		final TestSimpleGraph graph = new TestSimpleGraph();
		bundle.graph = graph;

		final TestSimpleVertex A = graph.addVertex().init( 1 );
		final TestSimpleVertex B = graph.addVertex().init( 2 );
		bundle.vertices = Arrays.asList( A, B );

		final TestSimpleEdge eAB = graph.addEdge( A, B );
		bundle.edges = Arrays.asList( eAB );

		bundle.name = "Single edge pool objects";
		return bundle;
	}

	public static final GraphTestBundle< ObjectVertex< Integer >, ObjectEdge< Integer >> singleEdgeStdObjects()
	{
		final GraphTestBundle< ObjectVertex< Integer >, ObjectEdge< Integer > > bundle = new GraphTestBundle<>();

		final ObjectGraph< Integer > graph = new ObjectGraph<>();
		bundle.graph = graph;

		final ObjectVertex< Integer > A = graph.addVertex().init( 1 );
		final ObjectVertex< Integer > B = graph.addVertex().init( 2 );
		bundle.vertices = new ArrayList<>( 2 );
		bundle.vertices.add( A );
		bundle.vertices.add( B );

		final ObjectEdge< Integer > eAB = graph.addEdge( A, B );
		bundle.edges = new ArrayList<>( 1 );
		bundle.edges.add( eAB );

		bundle.name = "Single edge standard objects";
		return bundle;
	}

	public static final GraphTestBundle< TestSimpleVertex, TestSimpleEdge > forkPoolObjects()
	{
		final GraphTestBundle< TestSimpleVertex, TestSimpleEdge > bundle = new GraphTestBundle<>();

		final TestSimpleGraph graph = new TestSimpleGraph();
		bundle.graph = graph;

		final TestSimpleVertex A = graph.addVertex().init( 1 );
		final TestSimpleVertex B = graph.addVertex().init( 2 );
		final TestSimpleVertex C = graph.addVertex().init( 3 );
		bundle.vertices = Arrays.asList( A, B, C );

		final TestSimpleEdge eAB = graph.addEdge( A, B );
		final TestSimpleEdge eAC = graph.addEdge( A, C );
		bundle.edges = Arrays.asList( eAB, eAC );

		bundle.name = "Fork pool objects";
		return bundle;
	}

	public static final GraphTestBundle< TestSimpleVertex, TestSimpleEdge > diamondPoolObjects()
	{
		final GraphTestBundle< TestSimpleVertex, TestSimpleEdge > bundle = new GraphTestBundle<>();

		final TestSimpleGraph graph = new TestSimpleGraph();
		bundle.graph = graph;

		final TestSimpleVertex A = graph.addVertex().init( 1 );
		final TestSimpleVertex B = graph.addVertex().init( 2 );
		final TestSimpleVertex C = graph.addVertex().init( 3 );
		final TestSimpleVertex D = graph.addVertex().init( 4 );
		bundle.vertices = Arrays.asList( A, B, C, D );

		final TestSimpleEdge eAB = graph.addEdge( A, B );
		final TestSimpleEdge eAC = graph.addEdge( A, C );
		final TestSimpleEdge eBD = graph.addEdge( B, D );
		final TestSimpleEdge eCD = graph.addEdge( C, D );
		bundle.edges = Arrays.asList( eAB, eAC, eBD, eCD );

		bundle.name = "Diamond pool objects";
		return bundle;
	}

	public static final GraphTestBundle< ObjectVertex< Integer >, ObjectEdge< Integer >> diamondStdObjects()
	{
		final GraphTestBundle< ObjectVertex< Integer >, ObjectEdge< Integer > > bundle = new GraphTestBundle<>();

		final ObjectGraph< Integer > graph = new ObjectGraph<>();
		bundle.graph = graph;

		final ObjectVertex< Integer > A = graph.addVertex().init( 1 );
		final ObjectVertex< Integer > B = graph.addVertex().init( 2 );
		final ObjectVertex< Integer > C = graph.addVertex().init( 3 );
		final ObjectVertex< Integer > D = graph.addVertex().init( 4 );
		bundle.vertices = Arrays.asList( A, B, C, D );

		final ObjectEdge< Integer > eAB = graph.addEdge( A, B );
		final ObjectEdge< Integer > eAC = graph.addEdge( A, C );
		final ObjectEdge< Integer > eBD = graph.addEdge( B, D );
		final ObjectEdge< Integer > eCD = graph.addEdge( C, D );
		bundle.edges = Arrays.asList( eAB, eAC, eBD, eCD );

		bundle.name = "Diamond std objects";
		return bundle;
	}

	public static final GraphTestBundle< ObjectVertex< Integer >, ObjectEdge< Integer >> forkStdObjects()
	{
		final GraphTestBundle< ObjectVertex< Integer >, ObjectEdge< Integer > > bundle = new GraphTestBundle<>();

		final ObjectGraph< Integer > graph = new ObjectGraph<>();
		bundle.graph = graph;

		final ObjectVertex< Integer > A = graph.addVertex().init( 1 );
		final ObjectVertex< Integer > B = graph.addVertex().init( 2 );
		final ObjectVertex< Integer > C = graph.addVertex().init( 3 );
		bundle.vertices = new ArrayList<>( 3 );
		bundle.vertices.add( A );
		bundle.vertices.add( B );
		bundle.vertices.add( C );

		final ObjectEdge< Integer > eAB = graph.addEdge( A, B );
		final ObjectEdge< Integer > eAC = graph.addEdge( A, C );
		bundle.edges = new ArrayList<>( 2 );
		bundle.edges.add( eAB );
		bundle.edges.add( eAC );

		bundle.name = "Fork standard objects";
		return bundle;
	}

	public static final GraphTestBundle< TestSimpleVertex, TestSimpleEdge > twoComponentsPoolObjects()
	{
		final GraphTestBundle< TestSimpleVertex, TestSimpleEdge > bundle = wpExamplePoolObjects();

		final TestSimpleVertex A = bundle.graph.addVertex().init( 11 );
		final TestSimpleVertex B = bundle.graph.addVertex().init( 12 );
		final TestSimpleVertex C = bundle.graph.addVertex().init( 13 );
		final TestSimpleVertex D = bundle.graph.addVertex().init( 14 );
		final TestSimpleVertex E = bundle.graph.addVertex().init( 15 );
		final TestSimpleVertex F = bundle.graph.addVertex().init( 16 );
		final TestSimpleVertex G = bundle.graph.addVertex().init( 17 );
		bundle.vertices.addAll( Arrays.asList( A, B, C, D, E, F, G ) );

		final TestSimpleEdge eAB = bundle.graph.addEdge( A, B );
		final TestSimpleEdge eBC = bundle.graph.addEdge( B, C );
		final TestSimpleEdge eCD = bundle.graph.addEdge( C, D );
		final TestSimpleEdge eDE = bundle.graph.addEdge( D, E );
		final TestSimpleEdge eEF = bundle.graph.addEdge( E, F );
		final TestSimpleEdge eFG = bundle.graph.addEdge( F, G );
		bundle.edges.addAll( Arrays.asList( eAB, eBC, eCD, eDE, eEF, eFG ) );

		bundle.name = "Two components pool objects";
		return bundle;
	}

	public static final GraphTestBundle< ObjectVertex< Integer >, ObjectEdge< Integer >> twoComponentsStdObjects()
	{
		final GraphTestBundle< ObjectVertex< Integer >, ObjectEdge< Integer >> bundle = wpExampleStdObjects();

		final ObjectVertex< Integer > A = bundle.graph.addVertex().init( 11 );
		final ObjectVertex< Integer > B = bundle.graph.addVertex().init( 12 );
		final ObjectVertex< Integer > C = bundle.graph.addVertex().init( 13 );
		final ObjectVertex< Integer > D = bundle.graph.addVertex().init( 14 );
		final ObjectVertex< Integer > E = bundle.graph.addVertex().init( 15 );
		final ObjectVertex< Integer > F = bundle.graph.addVertex().init( 16 );
		final ObjectVertex< Integer > G = bundle.graph.addVertex().init( 17 );
		bundle.vertices.add( A );
		bundle.vertices.add( B );
		bundle.vertices.add( C );
		bundle.vertices.add( D );
		bundle.vertices.add( E );
		bundle.vertices.add( F );
		bundle.vertices.add( G );

		final ObjectEdge< Integer > eAB = bundle.graph.addEdge( A, B );
		final ObjectEdge< Integer > eBC = bundle.graph.addEdge( B, C );
		final ObjectEdge< Integer > eCD = bundle.graph.addEdge( C, D );
		final ObjectEdge< Integer > eDE = bundle.graph.addEdge( D, E );
		final ObjectEdge< Integer > eEF = bundle.graph.addEdge( E, F );
		final ObjectEdge< Integer > eFG = bundle.graph.addEdge( F, G );
		bundle.edges.add( eAB );
		bundle.edges.add( eBC );
		bundle.edges.add( eCD );
		bundle.edges.add( eDE );
		bundle.edges.add( eEF );
		bundle.edges.add( eFG );

		bundle.name = "Two components standard objects";
		return bundle;
	}

	public static final GraphTestBundle< ObjectVertex< Integer >, ObjectEdge< Integer > > multipleComponentsStdObjects()
	{
		final GraphTestBundle< ObjectVertex< Integer >, ObjectEdge< Integer > > bundle = new GraphTestBundle<>();

		final ObjectGraph< Integer > graph = new ObjectGraph<>();
		bundle.graph = graph;
		bundle.vertices = new ArrayList<>( 0 );
		bundle.edges = new ArrayList<>( 0 );

		// Create 4 diamonds.
		for ( int i = 0; i < 4; i++ )
		{
			final ObjectVertex< Integer > A = graph.addVertex().init( 0 + 4 * i );
			final ObjectVertex< Integer > B = graph.addVertex().init( 1 + 4 * i );
			final ObjectVertex< Integer > C = graph.addVertex().init( 2 + 4 * i );
			final ObjectVertex< Integer > D = graph.addVertex().init( 3 + 4 * i );
			bundle.vertices.add( A );
			bundle.vertices.add( B );
			bundle.vertices.add( C );
			bundle.vertices.add( D );

			final ObjectEdge< Integer > eAB = graph.addEdge( A, B );
			final ObjectEdge< Integer > eAC = graph.addEdge( A, C );
			final ObjectEdge< Integer > eBD = graph.addEdge( B, D );
			final ObjectEdge< Integer > eCD = graph.addEdge( C, D );
			bundle.edges.add( eAB );
			bundle.edges.add( eAC );
			bundle.edges.add( eBD );
			bundle.edges.add( eCD );
		}

		bundle.name = "Multiple components std objects";
		return bundle;
	}

	public static final GraphTestBundle< TestSimpleVertex, TestSimpleEdge > multipleComponentsPoolObjects()
	{
		final GraphTestBundle< TestSimpleVertex, TestSimpleEdge > bundle = new GraphTestBundle<>();

		final TestSimpleGraph graph = new TestSimpleGraph();
		bundle.graph = graph;
		bundle.vertices = new ArrayList<>( 0 );
		bundle.edges = new ArrayList<>( 0 );

		// Create 4 diamonds.
		for ( int i = 0; i < 4; i++ )
		{
			final TestSimpleVertex A = graph.addVertex().init( 0 + 4 * i );
			final TestSimpleVertex B = graph.addVertex().init( 1 + 4 * i );
			final TestSimpleVertex C = graph.addVertex().init( 2 + 4 * i );
			final TestSimpleVertex D = graph.addVertex().init( 3 + 4 * i );
			bundle.vertices.add( A );
			bundle.vertices.add( B );
			bundle.vertices.add( C );
			bundle.vertices.add( D );

			final TestSimpleEdge eAB = graph.addEdge( A, B );
			final TestSimpleEdge eAC = graph.addEdge( A, C );
			final TestSimpleEdge eBD = graph.addEdge( B, D );
			final TestSimpleEdge eCD = graph.addEdge( C, D );
			bundle.edges.add( eAB );
			bundle.edges.add( eAC );
			bundle.edges.add( eBD );
			bundle.edges.add( eCD );
		}

		bundle.name = "Multiple components pool objects";
		return bundle;
	}

	public static class GraphTestBundle< V extends Vertex< E >, E extends Edge< V > >
	{
		public Graph< V, E > graph;

		public List< V > vertices;

		public List< E > edges;

		public String name;
	}

	private GraphsForTests()
	{}
}
