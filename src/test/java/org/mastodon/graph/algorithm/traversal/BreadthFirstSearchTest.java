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
package org.mastodon.graph.algorithm.traversal;

import static org.mastodon.graph.algorithm.traversal.GraphSearch.EdgeClass.BACK;
import static org.mastodon.graph.algorithm.traversal.GraphSearch.EdgeClass.CROSS;
import static org.mastodon.graph.algorithm.traversal.GraphSearch.EdgeClass.TREE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.mastodon.graph.TestSimpleEdge;
import org.mastodon.graph.TestSimpleVertex;
import org.mastodon.graph.algorithm.traversal.GraphSearch.EdgeClass;
import org.mastodon.graph.algorithm.traversal.GraphSearch.SearchDirection;
import org.mastodon.graph.algorithm.traversal.GraphsForTests.GraphTestBundle;
import org.mastodon.graph.algorithm.traversal.GraphsForTests.TraversalTester;
import org.mastodon.graph.object.ObjectEdge;
import org.mastodon.graph.object.ObjectVertex;

/**
 * We assume that for unsorted search, child vertices are returned in the order
 * they are added to the graph. If they are not, this test will fail, but it
 * does not necessary means it is incorrect
 */
public class BreadthFirstSearchTest
{

	@Test
	public void testForkPoolObjects()
	{
		final GraphTestBundle< TestSimpleVertex, TestSimpleEdge > bundle = GraphsForTests.forkPoolObjects();

		final TestSimpleVertex first = bundle.vertices.get( 0 );
		final BreadthFirstSearch< TestSimpleVertex, TestSimpleEdge > bfs = new BreadthFirstSearch<>( bundle.graph, SearchDirection.DIRECTED );

		final List< EdgeClass > edgeClass = Arrays.asList( new EdgeClass[] { TREE, TREE } );

		final TraversalTester< TestSimpleVertex, TestSimpleEdge, BreadthFirstSearch< TestSimpleVertex, TestSimpleEdge > > traversalTester =
				new TraversalTester<>(
				bundle.vertices.iterator(),
				bundle.vertices.iterator(),
				bundle.edges.iterator(),
				edgeClass.iterator() );

		bfs.setTraversalListener( traversalTester );
		bfs.start( first );
		traversalTester.searchDone();
	}

	@Test
	public void testForkStdObjects()
	{
		final GraphTestBundle< ObjectVertex< Integer >, ObjectEdge< Integer >> bundle = GraphsForTests.forkStdObjects();

		final ObjectVertex< Integer > first = bundle.vertices.get( 0 );
		final BreadthFirstSearch< ObjectVertex< Integer >, ObjectEdge< Integer > > bfs = new BreadthFirstSearch<>( bundle.graph, SearchDirection.DIRECTED );

		final List< EdgeClass > edgeClass = Arrays.asList( new EdgeClass[] { TREE, TREE } );

		final TraversalTester< ObjectVertex< Integer >, ObjectEdge< Integer >, BreadthFirstSearch< ObjectVertex< Integer >, ObjectEdge< Integer > >> traversalTester =
				new TraversalTester<>(
				bundle.vertices.iterator(),
				bundle.vertices.iterator(),
				bundle.edges.iterator(),
				edgeClass.iterator() );

		bfs.setTraversalListener( traversalTester );
		bfs.start( first );
		traversalTester.searchDone();
	}

	@Test
	public void testLoopPoolObjects()
	{
		final GraphTestBundle< TestSimpleVertex, TestSimpleEdge > bundle = GraphsForTests.loopPoolObjects();

		final TestSimpleVertex first = bundle.vertices.get( 0 );
		final BreadthFirstSearch< TestSimpleVertex, TestSimpleEdge > bfs = new BreadthFirstSearch<>( bundle.graph, SearchDirection.DIRECTED );

		final List< EdgeClass > edgeClass = Arrays.asList( new EdgeClass[] { TREE, TREE, TREE, TREE, TREE, TREE, BACK } );

		final TraversalTester< TestSimpleVertex, TestSimpleEdge, BreadthFirstSearch< TestSimpleVertex, TestSimpleEdge >> traversalTester =
				new TraversalTester<>(
				bundle.vertices.iterator(),
				bundle.vertices.iterator(),
				bundle.edges.iterator(),
				edgeClass.iterator() );

		bfs.setTraversalListener( traversalTester );
		bfs.start( first );
		traversalTester.searchDone();
	}

	@Test
	public void testLoopStdObjects()
	{
		final GraphTestBundle< ObjectVertex< Integer >, ObjectEdge< Integer >> bundle = GraphsForTests.loopStdObjects();

		final ObjectVertex< Integer > first = bundle.vertices.get( 0 );
		final BreadthFirstSearch< ObjectVertex< Integer >, ObjectEdge< Integer > > bfs = new BreadthFirstSearch<>( bundle.graph, SearchDirection.DIRECTED );

		final List< EdgeClass > edgeClass = Arrays.asList( new EdgeClass[] { TREE, TREE, TREE, TREE, TREE, TREE, BACK } );

		final TraversalTester< ObjectVertex< Integer >, ObjectEdge< Integer >, BreadthFirstSearch< ObjectVertex< Integer >, ObjectEdge< Integer > >> traversalTester =
				new TraversalTester<>(
				bundle.vertices.iterator(),
				bundle.vertices.iterator(),
				bundle.edges.iterator(),
				edgeClass.iterator() );

		bfs.setTraversalListener( traversalTester );
		bfs.start( first );
		traversalTester.searchDone();
	}

	@Test
	public void testExamplePoolObjects()
	{
		final GraphTestBundle< TestSimpleVertex, TestSimpleEdge > bundle = GraphsForTests.wpExamplePoolObjects();

		final TestSimpleVertex first = bundle.vertices.get( 0 );
		final BreadthFirstSearch< TestSimpleVertex, TestSimpleEdge > bfs = new BreadthFirstSearch<>( bundle.graph, SearchDirection.DIRECTED );

		final List< TestSimpleVertex > expectedVertices = Arrays.asList( new TestSimpleVertex[] {
				bundle.vertices.get( 0 ),
				bundle.vertices.get( 1 ),
				bundle.vertices.get( 2 ),
				bundle.vertices.get( 4 ),
				bundle.vertices.get( 3 ),
				bundle.vertices.get( 5 ),
				bundle.vertices.get( 6 )
		} );
		final List< TestSimpleVertex > processedVertices = expectedVertices;
		final List< TestSimpleEdge > expectedEdges = Arrays.asList( new TestSimpleEdge[] {
				bundle.edges.get( 0 ),
				bundle.edges.get( 1 ),
				bundle.edges.get( 2 ),
				bundle.edges.get( 3 ),
				bundle.edges.get( 4 ),
				bundle.edges.get( 6 ),
				bundle.edges.get( 5 )
		} );
		final List< EdgeClass > edgeClass = Arrays.asList( new EdgeClass[] { TREE, TREE, TREE, TREE, TREE, TREE, CROSS } );

		final TraversalTester< TestSimpleVertex, TestSimpleEdge, BreadthFirstSearch< TestSimpleVertex, TestSimpleEdge >> traversalTester =
				new TraversalTester<>(
				expectedVertices.iterator(),
				processedVertices.iterator(),
				expectedEdges.iterator(),
				edgeClass.iterator() );

		bfs.setTraversalListener( traversalTester );
		bfs.start( first );
		traversalTester.searchDone();
	}

	@Test
	public void testExampleStdObjects()
	{
		final GraphTestBundle< ObjectVertex< Integer >, ObjectEdge< Integer >> bundle = GraphsForTests.wpExampleStdObjects();

		final ObjectVertex< Integer > first = bundle.vertices.get( 0 );
		final BreadthFirstSearch< ObjectVertex< Integer >, ObjectEdge< Integer > > bfs = new BreadthFirstSearch<>( bundle.graph, SearchDirection.DIRECTED );

		final List< ObjectVertex< Integer > > expectedVertices = new ArrayList<>( 7 );
		expectedVertices.add( bundle.vertices.get( 0 ) );
		expectedVertices.add( bundle.vertices.get( 1 ) );
		expectedVertices.add( bundle.vertices.get( 2 ) );
		expectedVertices.add( bundle.vertices.get( 4 ) );
		expectedVertices.add( bundle.vertices.get( 3 ) );
		expectedVertices.add( bundle.vertices.get( 5 ) );
		expectedVertices.add( bundle.vertices.get( 6 ) );

		final List< ObjectVertex< Integer > > processedVertices = expectedVertices;

		final List< ObjectEdge< Integer > > expectedEdges = new ArrayList<>( 7 );
		expectedEdges.add( bundle.edges.get( 0 ) );
		expectedEdges.add( bundle.edges.get( 1 ) );
		expectedEdges.add( bundle.edges.get( 2 ) );
		expectedEdges.add( bundle.edges.get( 3 ) );
		expectedEdges.add( bundle.edges.get( 4 ) );
		expectedEdges.add( bundle.edges.get( 6 ) );
		expectedEdges.add( bundle.edges.get( 5 ) );

		final List< EdgeClass > edgeClass = Arrays.asList( new EdgeClass[] { TREE, TREE, TREE, TREE, TREE, TREE, CROSS } );

		final TraversalTester< ObjectVertex< Integer >, ObjectEdge< Integer >, BreadthFirstSearch< ObjectVertex< Integer >, ObjectEdge< Integer > >> traversalTester =
				new TraversalTester<>(
				expectedVertices.iterator(),
				processedVertices.iterator(),
				expectedEdges.iterator(),
				edgeClass.iterator() );

		bfs.setTraversalListener( traversalTester );
		bfs.start( first );
		traversalTester.searchDone();
	}

	@Test
	public void testSingleEdgePoolObjects()
	{
		final GraphTestBundle< TestSimpleVertex, TestSimpleEdge > bundle = GraphsForTests.singleEdgePoolObjects();

		final TestSimpleVertex first = bundle.vertices.get( 0 );
		final BreadthFirstSearch< TestSimpleVertex, TestSimpleEdge > bfs = new BreadthFirstSearch<>( bundle.graph, SearchDirection.DIRECTED );

		final List< TestSimpleVertex > expectedVertices = Arrays.asList( new TestSimpleVertex[] {
				bundle.vertices.get( 0 ),
				bundle.vertices.get( 1 )
		} );
		final List< TestSimpleVertex > processedVertices = Arrays.asList( new TestSimpleVertex[] {
				bundle.vertices.get( 0 ),
				bundle.vertices.get( 1 )
		} );
		final List< TestSimpleEdge > expectedEdges = Arrays.asList( new TestSimpleEdge[] {
				bundle.edges.get( 0 )
		} );
		final List< EdgeClass > edgeClass = Arrays.asList( new EdgeClass[] { TREE } );

		final TraversalTester< TestSimpleVertex, TestSimpleEdge, BreadthFirstSearch< TestSimpleVertex, TestSimpleEdge > > traversalTester =
				new TraversalTester<>(
				expectedVertices.iterator(),
				processedVertices.iterator(),
				expectedEdges.iterator(),
				edgeClass.iterator() );

		bfs.setTraversalListener( traversalTester );
		bfs.start( first );
		traversalTester.searchDone();
	}

	@Test
	public void testSingleEdgeStdObjects()
	{
		final GraphTestBundle< ObjectVertex< Integer >, ObjectEdge< Integer >> bundle = GraphsForTests.singleEdgeStdObjects();

		final ObjectVertex< Integer > first = bundle.vertices.get( 0 );
		final BreadthFirstSearch< ObjectVertex< Integer >, ObjectEdge< Integer > > bfs = new BreadthFirstSearch<>( bundle.graph, SearchDirection.DIRECTED );

		final List< ObjectVertex< Integer > > expectedVertices = new ArrayList<>( 2 );
		expectedVertices.add( bundle.vertices.get( 0 ) );
		expectedVertices.add( bundle.vertices.get( 1 ) );

		final List< ObjectVertex< Integer > > processedVertices = new ArrayList<>( 2 );
		processedVertices.add( bundle.vertices.get( 0 ) );
		processedVertices.add( bundle.vertices.get( 1 ) );

		final List< ObjectEdge< Integer > > expectedEdges = new ArrayList<>( 1 );
		expectedEdges.add( bundle.edges.get( 0 ) );

		final List< EdgeClass > edgeClass = Arrays.asList( new EdgeClass[] { TREE } );

		final TraversalTester< ObjectVertex< Integer >, ObjectEdge< Integer >, BreadthFirstSearch< ObjectVertex< Integer >, ObjectEdge< Integer > >> traversalTester =
				new TraversalTester<>(
				expectedVertices.iterator(),
				processedVertices.iterator(),
				expectedEdges.iterator(),
				edgeClass.iterator() );

		bfs.setTraversalListener( traversalTester );
		bfs.start( first );
		traversalTester.searchDone();
	}

	@Test
	public void testStraightLinePoolObjects()
	{
		final GraphTestBundle< TestSimpleVertex, TestSimpleEdge > bundle = GraphsForTests.straightLinePoolObjects();

		final TestSimpleVertex first = bundle.vertices.get( 0 );
		final BreadthFirstSearch< TestSimpleVertex, TestSimpleEdge > bfs = new BreadthFirstSearch<>( bundle.graph, SearchDirection.DIRECTED );

		final List< TestSimpleVertex > expectedVertices = bundle.vertices;
		final List< TestSimpleVertex > processedVertices = bundle.vertices;
		final List< TestSimpleEdge > expectedEdges = bundle.edges;

		final List< EdgeClass > edgeClass = Arrays.asList( new EdgeClass[] { TREE, TREE, TREE, TREE, TREE, TREE } );

		final TraversalTester< TestSimpleVertex, TestSimpleEdge, BreadthFirstSearch< TestSimpleVertex, TestSimpleEdge >> traversalTester =
				new TraversalTester<>(
				expectedVertices.iterator(),
				processedVertices.iterator(),
				expectedEdges.iterator(),
				edgeClass.iterator() );

		bfs.setTraversalListener( traversalTester );
		bfs.start( first );
		traversalTester.searchDone();
	}

	@Test
	public void testStraightLineStdObjects()
	{
		final GraphTestBundle< ObjectVertex< Integer >, ObjectEdge< Integer >> bundle = GraphsForTests.straightLineStdObjects();

		final ObjectVertex< Integer > first = bundle.vertices.get( 0 );
		final BreadthFirstSearch< ObjectVertex< Integer >, ObjectEdge< Integer > > bfs = new BreadthFirstSearch<>( bundle.graph, SearchDirection.DIRECTED );

		final List< ObjectVertex< Integer > > expectedVertices = bundle.vertices;

		final List< ObjectVertex< Integer > > processedVertices = bundle.vertices;

		final List< ObjectEdge< Integer > > expectedEdges = bundle.edges;

		final List< EdgeClass > edgeClass = Arrays.asList( new EdgeClass[] { TREE, TREE, TREE, TREE, TREE, TREE } );

		final TraversalTester< ObjectVertex< Integer >, ObjectEdge< Integer >, BreadthFirstSearch< ObjectVertex< Integer >, ObjectEdge< Integer > >> traversalTester =
				new TraversalTester<>(
				expectedVertices.iterator(),
				processedVertices.iterator(),
				expectedEdges.iterator(),
				edgeClass.iterator() );

		bfs.setTraversalListener( traversalTester );
		bfs.start( first );
		traversalTester.searchDone();
	}

	@Test
	public void testTwoComponentsPoolObjects()
	{
		final GraphTestBundle< TestSimpleVertex, TestSimpleEdge > bundle = GraphsForTests.twoComponentsPoolObjects();

		final TestSimpleVertex first = bundle.vertices.get( 0 );
		final BreadthFirstSearch< TestSimpleVertex, TestSimpleEdge > bfs = new BreadthFirstSearch<>( bundle.graph, SearchDirection.DIRECTED );

		final List< TestSimpleVertex > expectedVertices = Arrays.asList( new TestSimpleVertex[] {
				bundle.vertices.get( 0 ),
				bundle.vertices.get( 1 ),
				bundle.vertices.get( 2 ),
				bundle.vertices.get( 4 ),
				bundle.vertices.get( 3 ),
				bundle.vertices.get( 5 ),
				bundle.vertices.get( 6 )
		} );
		final List< TestSimpleVertex > processedVertices = expectedVertices;
		final List< TestSimpleEdge > expectedEdges = Arrays.asList( new TestSimpleEdge[] {
				bundle.edges.get( 0 ),
				bundle.edges.get( 1 ),
				bundle.edges.get( 2 ),
				bundle.edges.get( 3 ),
				bundle.edges.get( 4 ),
				bundle.edges.get( 6 ),
				bundle.edges.get( 5 )
		} );
		final List< EdgeClass > edgeClass = Arrays.asList( new EdgeClass[] { TREE, TREE, TREE, TREE, TREE, TREE, CROSS } );

		final TraversalTester< TestSimpleVertex, TestSimpleEdge, BreadthFirstSearch< TestSimpleVertex, TestSimpleEdge >> traversalTester =
				new TraversalTester<>(
				expectedVertices.iterator(),
				processedVertices.iterator(),
				expectedEdges.iterator(),
				edgeClass.iterator() );

		bfs.setTraversalListener( traversalTester );
		bfs.start( first );
		traversalTester.searchDone();
	}

	@Test
	public void testTwoComponentsStdObjects()
	{
		final GraphTestBundle< ObjectVertex< Integer >, ObjectEdge< Integer >> bundle = GraphsForTests.twoComponentsStdObjects();

		final ObjectVertex< Integer > first = bundle.vertices.get( 0 );
		final BreadthFirstSearch< ObjectVertex< Integer >, ObjectEdge< Integer > > bfs = new BreadthFirstSearch<>( bundle.graph, SearchDirection.DIRECTED );

		final List< ObjectVertex< Integer > > expectedVertices = new ArrayList<>( 7 );
		expectedVertices.add( bundle.vertices.get( 0 ) );
		expectedVertices.add( bundle.vertices.get( 1 ) );
		expectedVertices.add( bundle.vertices.get( 2 ) );
		expectedVertices.add( bundle.vertices.get( 4 ) );
		expectedVertices.add( bundle.vertices.get( 3 ) );
		expectedVertices.add( bundle.vertices.get( 5 ) );
		expectedVertices.add( bundle.vertices.get( 6 ) );

		final List< ObjectVertex< Integer > > processedVertices = expectedVertices;

		final List< ObjectEdge< Integer > > expectedEdges = new ArrayList<>( 7 );
		expectedEdges.add( bundle.edges.get( 0 ) );
		expectedEdges.add( bundle.edges.get( 1 ) );
		expectedEdges.add( bundle.edges.get( 2 ) );
		expectedEdges.add( bundle.edges.get( 3 ) );
		expectedEdges.add( bundle.edges.get( 4 ) );
		expectedEdges.add( bundle.edges.get( 6 ) );
		expectedEdges.add( bundle.edges.get( 5 ) );

		final List< EdgeClass > edgeClass = Arrays.asList( new EdgeClass[] { TREE, TREE, TREE, TREE, TREE, TREE, CROSS } );

		final TraversalTester< ObjectVertex< Integer >, ObjectEdge< Integer >, BreadthFirstSearch< ObjectVertex< Integer >, ObjectEdge< Integer > >> traversalTester =
				new TraversalTester<>(
				expectedVertices.iterator(),
				processedVertices.iterator(),
				expectedEdges.iterator(),
				edgeClass.iterator() );

		bfs.setTraversalListener( traversalTester );
		bfs.start( first );
		traversalTester.searchDone();
	}

	@Test
	public void testSingleVertexPoolObjects()
	{
		final GraphTestBundle< TestSimpleVertex, TestSimpleEdge > bundle = GraphsForTests.singleVertexPoolObjects();

		final TestSimpleVertex first = bundle.vertices.get( 0 );
		final BreadthFirstSearch< TestSimpleVertex, TestSimpleEdge > bfs = new BreadthFirstSearch<>( bundle.graph, SearchDirection.DIRECTED );

		final List< TestSimpleVertex > expectedVertices = Arrays.asList( new TestSimpleVertex[] {
				bundle.vertices.get( 0 )
		} );

		final List< TestSimpleVertex > processedVertices = Arrays.asList( new TestSimpleVertex[] {
				bundle.vertices.get( 0 )
		} );

		final List< TestSimpleEdge > expectedEdges = Collections.emptyList();

		final List< EdgeClass > edgeClass = Collections.emptyList();

		final TraversalTester< TestSimpleVertex, TestSimpleEdge, BreadthFirstSearch< TestSimpleVertex, TestSimpleEdge >> traversalTester =
				new TraversalTester<>(
				expectedVertices.iterator(),
				processedVertices.iterator(),
				expectedEdges.iterator(),
				edgeClass.iterator() );

		bfs.setTraversalListener( traversalTester );
		bfs.start( first );
		traversalTester.searchDone();
	}

	@Test
	public void testSingleVertexStdObjects()
	{
		final GraphTestBundle< ObjectVertex< Integer >, ObjectEdge< Integer >> bundle = GraphsForTests.singleVertexStdObjects();

		final ObjectVertex< Integer > first = bundle.vertices.get( 0 );
		final BreadthFirstSearch< ObjectVertex< Integer >, ObjectEdge< Integer > > bfs = new BreadthFirstSearch<>( bundle.graph, SearchDirection.DIRECTED );

		final List< ObjectVertex< Integer > > expectedVertices = new ArrayList<>( 2 );
		expectedVertices.add( bundle.vertices.get( 0 ) );

		final List< ObjectVertex< Integer > > processedVertices = new ArrayList<>( 2 );
		processedVertices.add( bundle.vertices.get( 0 ) );

		final List< ObjectEdge< Integer > > expectedEdges = Collections.emptyList();

		final List< EdgeClass > edgeClass = Collections.emptyList();

		final TraversalTester< ObjectVertex< Integer >, ObjectEdge< Integer >, BreadthFirstSearch< ObjectVertex< Integer >, ObjectEdge< Integer > >> traversalTester =
				new TraversalTester<>(
				expectedVertices.iterator(),
				processedVertices.iterator(),
				expectedEdges.iterator(),
				edgeClass.iterator() );

		bfs.setTraversalListener( traversalTester );
		bfs.start( first );
		traversalTester.searchDone();
	}
}
