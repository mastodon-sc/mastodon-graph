package org.mastodon.graph.traversal;

import static org.junit.Assert.assertEquals;
import static org.mastodon.graph.algorithm.traversal.GraphSearch.EdgeClass.CROSS;
import static org.mastodon.graph.algorithm.traversal.GraphSearch.EdgeClass.TREE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.mastodon.collection.RefCollections;
import org.mastodon.collection.RefIntMap;
import org.mastodon.collection.RefList;
import org.mastodon.graph.TestEdge;
import org.mastodon.graph.TestVertex;
import org.mastodon.graph.algorithm.traversal.BreadthFirstCrossComponentSearch;
import org.mastodon.graph.algorithm.traversal.GraphSearch.EdgeClass;
import org.mastodon.graph.algorithm.traversal.GraphSearch.SearchDirection;
import org.mastodon.graph.algorithm.traversal.SearchListener;
import org.mastodon.graph.object.ObjectEdge;
import org.mastodon.graph.object.ObjectVertex;
import org.mastodon.graph.traversal.GraphsForTests.GraphTestBundle;
import org.mastodon.graph.traversal.GraphsForTests.TraversalTester;

/**
 * We assume that for unsorted search, child vertices are returned in the order
 * they are added to the graph. If they are not, this test will fail, but it
 * does not necessary means it is incorrect
 */
public class BreadthFirstCrossComponentSearchUndirectedTest
{

	@Test
	public void testmultipleComponentsPoolObjects()
	{
		final GraphTestBundle< TestVertex, TestEdge > bundle = GraphsForTests.multipleComponentsPoolObjects();


		final List< EdgeClass > edgeClass = new ArrayList<>();
		for ( int i = 0; i < 4; i++ )
			edgeClass.addAll( Arrays.asList( new EdgeClass[] { TREE, TREE, TREE, CROSS } ) );

		// Expected iteration order.
		final RefList< TestVertex > expected = RefCollections.createRefList( bundle.graph.vertices() );
		expected.add( bundle.vertices.get( 7 ) );
		expected.add( bundle.vertices.get( 5 ) );
		expected.add( bundle.vertices.get( 6 ) );
		expected.add( bundle.vertices.get( 4 ) );

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

		final RefList< TestEdge > edges = RefCollections.createRefList( bundle.graph.edges() );
		edges.add( bundle.edges.get( 6 ) );
		edges.add( bundle.edges.get( 7 ) );
		edges.add( bundle.edges.get( 4 ) );
		edges.add( bundle.edges.get( 5 ) );

		edges.add( bundle.edges.get( 10 ) );
		edges.add( bundle.edges.get( 11 ) );
		edges.add( bundle.edges.get( 8 ) );
		edges.add( bundle.edges.get( 9 ) );

		edges.add( bundle.edges.get( 2 ) );
		edges.add( bundle.edges.get( 3 ) );
		edges.add( bundle.edges.get( 0 ) );
		edges.add( bundle.edges.get( 1 ) );

		edges.add( bundle.edges.get( 14 ) );
		edges.add( bundle.edges.get( 15 ) );
		edges.add( bundle.edges.get( 12 ) );
		edges.add( bundle.edges.get( 13 ) );

		// Specifies leaf order.
		final RefList< TestVertex > leaves = RefCollections.createRefList( bundle.graph.vertices(), 4 );
		leaves.add( bundle.vertices.get( 11 ) );
		leaves.add( bundle.vertices.get( 3 ) );
		leaves.add( bundle.vertices.get( 15 ) );
		leaves.add( bundle.vertices.get( 7 ) );
		final TestVertex first = bundle.vertices.get( 7 );
		final BreadthFirstCrossComponentSearch< TestVertex, TestEdge > bfs =
				new BreadthFirstCrossComponentSearch<>( bundle.graph, SearchDirection.UNDIRECTED, leaves );

		final TraversalTester< TestVertex, TestEdge, BreadthFirstCrossComponentSearch< TestVertex, TestEdge > > traversalTester =
				new TraversalTester<>(
						expected.iterator(),
						expected.iterator(),
						edges.iterator(),
						edgeClass.iterator() );

		bfs.setTraversalListener( traversalTester );
		bfs.start( first );
		traversalTester.searchDone();
	}

	@Test
	public void testmultipleComponentsStdObjects()
	{
		final GraphTestBundle< ObjectVertex< Integer >, ObjectEdge< Integer > > bundle = GraphsForTests.multipleComponentsStdObjects();

		final List< EdgeClass > edgeClass = new ArrayList<>();
		for ( int i = 0; i < 4; i++ )
			edgeClass.addAll( Arrays.asList( new EdgeClass[] { TREE, TREE, TREE, CROSS } ) );

		// Expected iteration order.
		final RefList< ObjectVertex< Integer > > expected = RefCollections.createRefList( bundle.graph.vertices() );
		expected.add( bundle.vertices.get( 7 ) );
		expected.add( bundle.vertices.get( 5 ) );
		expected.add( bundle.vertices.get( 6 ) );
		expected.add( bundle.vertices.get( 4 ) );

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

		final RefList< ObjectEdge< Integer > > edges = RefCollections.createRefList( bundle.graph.edges() );
		edges.add( bundle.edges.get( 6 ) );
		edges.add( bundle.edges.get( 7 ) );
		edges.add( bundle.edges.get( 4 ) );
		edges.add( bundle.edges.get( 5 ) );

		edges.add( bundle.edges.get( 10 ) );
		edges.add( bundle.edges.get( 11 ) );
		edges.add( bundle.edges.get( 8 ) );
		edges.add( bundle.edges.get( 9 ) );

		edges.add( bundle.edges.get( 2 ) );
		edges.add( bundle.edges.get( 3 ) );
		edges.add( bundle.edges.get( 0 ) );
		edges.add( bundle.edges.get( 1 ) );

		edges.add( bundle.edges.get( 14 ) );
		edges.add( bundle.edges.get( 15 ) );
		edges.add( bundle.edges.get( 12 ) );
		edges.add( bundle.edges.get( 13 ) );

		// Specifies leaf order.
		final RefList< ObjectVertex< Integer > > leaves = RefCollections.createRefList( bundle.graph.vertices(), 4 );
		leaves.add( bundle.vertices.get( 11 ) );
		leaves.add( bundle.vertices.get( 3 ) );
		leaves.add( bundle.vertices.get( 15 ) );
		leaves.add( bundle.vertices.get( 7 ) );
		final ObjectVertex< Integer > first = bundle.vertices.get( 7 );
		final BreadthFirstCrossComponentSearch< ObjectVertex< Integer >, ObjectEdge< Integer > > bfs =
				new BreadthFirstCrossComponentSearch<>( bundle.graph, SearchDirection.UNDIRECTED, leaves );

		final TraversalTester< ObjectVertex< Integer >, ObjectEdge< Integer >, BreadthFirstCrossComponentSearch< ObjectVertex< Integer >, ObjectEdge< Integer > > > traversalTester =
				new TraversalTester<>(
						expected.iterator(),
						expected.iterator(),
						edges.iterator(),
						edgeClass.iterator() );

		bfs.setTraversalListener( traversalTester );
		bfs.start( first );
		traversalTester.searchDone();
	}

	@Test
	public void testTwoComponentsPoolObjects()
	{
		final GraphTestBundle< TestVertex, TestEdge > bundle = GraphsForTests.twoComponentsPoolObjects();

		final TestVertex first = bundle.vertices.get( 0 );
		final BreadthFirstCrossComponentSearch< TestVertex, TestEdge > bfs =
				new BreadthFirstCrossComponentSearch<>( bundle.graph, SearchDirection.UNDIRECTED,
						Arrays.asList( // whatever
								bundle.vertices.get( 0 ),
								bundle.vertices.get( 7 ) ) );

		final RefList< TestVertex > expectedVertices = RefCollections.createRefList( bundle.graph.vertices() );
		expectedVertices.add( bundle.vertices.get( 0 ) );
		expectedVertices.add( bundle.vertices.get( 1 ) );
		expectedVertices.add( bundle.vertices.get( 2 ) );
		expectedVertices.add( bundle.vertices.get( 4 ) );
		expectedVertices.add( bundle.vertices.get( 3 ) );
		expectedVertices.add( bundle.vertices.get( 5 ) );
		expectedVertices.add( bundle.vertices.get( 6 ) );

		expectedVertices.add( bundle.vertices.get( 7 ) );
		expectedVertices.add( bundle.vertices.get( 8 ) );
		expectedVertices.add( bundle.vertices.get( 9 ) );
		expectedVertices.add( bundle.vertices.get( 10 ) );
		expectedVertices.add( bundle.vertices.get( 11 ) );
		expectedVertices.add( bundle.vertices.get( 12 ) );
		expectedVertices.add( bundle.vertices.get( 13 ) );

		final List< TestVertex > processedVertices = expectedVertices;
		final List< TestEdge > expectedEdges = Arrays.asList( new TestEdge[] {
				bundle.edges.get( 0 ),
				bundle.edges.get( 1 ),
				bundle.edges.get( 2 ),
				bundle.edges.get( 3 ),
				bundle.edges.get( 4 ),
				bundle.edges.get( 6 ),
				bundle.edges.get( 5 ),

				bundle.edges.get( 7 ),
				bundle.edges.get( 8 ),
				bundle.edges.get( 9 ),
				bundle.edges.get( 10 ),
				bundle.edges.get( 11 ),
				bundle.edges.get( 12 )
		} );
		final List< EdgeClass > edgeClass = Arrays.asList( new EdgeClass[] {
				TREE, TREE,
				TREE, TREE, TREE,
				TREE, CROSS,
				TREE, TREE, TREE, TREE, TREE, TREE } );

		final TraversalTester< TestVertex, TestEdge, BreadthFirstCrossComponentSearch< TestVertex, TestEdge > > traversalTester =
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
		final GraphTestBundle< ObjectVertex< Integer >, ObjectEdge< Integer > > bundle = GraphsForTests.twoComponentsStdObjects();

		final ObjectVertex< Integer > first = bundle.vertices.get( 0 );
		final BreadthFirstCrossComponentSearch< ObjectVertex< Integer >, ObjectEdge< Integer > > bfs =
				new BreadthFirstCrossComponentSearch<>( bundle.graph, SearchDirection.UNDIRECTED,
						Arrays.asList( // whatever
								bundle.vertices.get( 0 ),
								bundle.vertices.get( 7 ) 
								) );

		final RefList< ObjectVertex< Integer > > expectedVertices = RefCollections.createRefList( bundle.graph.vertices() );
		expectedVertices.add( bundle.vertices.get( 0 ) );
		expectedVertices.add( bundle.vertices.get( 1 ) );
		expectedVertices.add( bundle.vertices.get( 2 ) );
		expectedVertices.add( bundle.vertices.get( 4 ) );
		expectedVertices.add( bundle.vertices.get( 3 ) );
		expectedVertices.add( bundle.vertices.get( 5 ) );
		expectedVertices.add( bundle.vertices.get( 6 ) );

		expectedVertices.add( bundle.vertices.get( 7 ) );
		expectedVertices.add( bundle.vertices.get( 8 ) );
		expectedVertices.add( bundle.vertices.get( 9 ) );
		expectedVertices.add( bundle.vertices.get( 10 ) );
		expectedVertices.add( bundle.vertices.get( 11 ) );
		expectedVertices.add( bundle.vertices.get( 12 ) );
		expectedVertices.add( bundle.vertices.get( 13 ) );

		final List< ObjectVertex< Integer > > processedVertices = expectedVertices;
		@SuppressWarnings( "unchecked" )
		final List< ObjectEdge< Integer > > expectedEdges = Arrays.asList( new ObjectEdge[] {
				bundle.edges.get( 0 ),
				bundle.edges.get( 1 ),
				bundle.edges.get( 2 ),
				bundle.edges.get( 3 ),
				bundle.edges.get( 4 ),
				bundle.edges.get( 6 ),
				bundle.edges.get( 5 ),

				bundle.edges.get( 7 ),
				bundle.edges.get( 8 ),
				bundle.edges.get( 9 ),
				bundle.edges.get( 10 ),
				bundle.edges.get( 11 ),
				bundle.edges.get( 12 )
		} );
		final List< EdgeClass > edgeClass = Arrays.asList( new EdgeClass[] {
				TREE, TREE, TREE, TREE, TREE, TREE, CROSS,
				TREE, TREE, TREE, TREE, TREE, TREE } );

		final TraversalTester< ObjectVertex< Integer >, ObjectEdge< Integer >, BreadthFirstCrossComponentSearch< ObjectVertex< Integer >, ObjectEdge< Integer > > > traversalTester =
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
	public void testIterateWholeGraph()
	{
		// Test that we iterate the whole graph.
		final GraphTestBundle< TestVertex, TestEdge > bundle = GraphsForTests.twoComponentsPoolObjects();
		final RefIntMap< TestVertex > map = RefCollections.createRefIntMap( bundle.graph.vertices(), -1 );
		for ( final TestVertex v : bundle.graph.vertices() )
			map.put( v, 0 );

		final BreadthFirstCrossComponentSearch< TestVertex, TestEdge > bfs =
				new BreadthFirstCrossComponentSearch<>( bundle.graph, SearchDirection.UNDIRECTED );

		final AtomicInteger iter = new AtomicInteger( 0 );
		bfs.setTraversalListener( new SearchListener< TestVertex, TestEdge, BreadthFirstCrossComponentSearch< TestVertex, TestEdge > >()
		{

			@Override
			public void processVertexLate( final TestVertex vertex, final BreadthFirstCrossComponentSearch< TestVertex, TestEdge > search )
			{}

			@Override
			public void processVertexEarly( final TestVertex vertex, final BreadthFirstCrossComponentSearch< TestVertex, TestEdge > search )
			{
				map.adjustValue( vertex, 1 );
				iter.incrementAndGet();
			}

			@Override
			public void processEdge( final TestEdge edge, final TestVertex from, final TestVertex to, final BreadthFirstCrossComponentSearch< TestVertex, TestEdge > search )
			{}

			@Override
			public void crossComponent( final TestVertex from, final TestVertex to, final BreadthFirstCrossComponentSearch< TestVertex, TestEdge > search )
			{}
		} );
		bfs.start( bundle.graph.vertices().iterator().next() );

		assertEquals( "Did not iterate over all vertices.", bundle.graph.vertices().size(), iter.get() );
		for ( final TestVertex v : map.keySet() )
			assertEquals( "Vertex was not iterated exactly once.", 1, map.get( v ) );
	}
}
