package org.mastodon.graph.branch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.mastodon.graph.GraphIdBimap;
import org.mastodon.graph.ListenableTestEdge;
import org.mastodon.graph.ListenableTestGraph;
import org.mastodon.graph.ListenableTestVertex;
import org.mastodon.pool.PoolCollectionWrapper;

public class BranchGraphTest
{

	private ListenableTestGraph graph;

	private BranchGraph< ListenableTestVertex, ListenableTestEdge > bg;

	@Before
	public void setUp() throws Exception
	{
		this.graph = new ListenableTestGraph();
		this.bg = new BranchGraph<>( graph, new GraphIdBimap<>( graph.getVertexPool(), graph.getEdgePool() ) );

	}

	@Test
	public void testAddOneVertex()
	{
		final ListenableTestVertex v0 = graph.addVertex().init( 0 );

		final PoolCollectionWrapper< BranchVertex > vertices = bg.vertices();
		final int size = vertices.size();
		assertEquals( "Expected the branch graph to have 1 vertex.", 1, size );

		final BranchVertex bv = vertices.iterator().next();
		assertEquals( "Branch vertex does not link to expected linked vertex.",
				v0, bg.getLinkedVertex( bv, graph.vertexRef() ) );

		final BranchVertex bv0 = bg.getBranchVertex( v0, bg.vertexRef() );
		assertEquals( "Linked vertex does not link to expected branch vertex.",
				bv, bv0 );
	}

	@Test
	public void testAddTwoVertices()
	{
		final ListenableTestVertex v0 = graph.addVertex().init( 0 );
		final ListenableTestVertex v1 = graph.addVertex().init( 1 );

		final PoolCollectionWrapper< BranchVertex > vertices = bg.vertices();
		final int size = vertices.size();
		assertEquals( "Expected the branch graph to have 2 vertices.", 2, size );

		final BranchVertex bv0 = bg.getBranchVertex( v0, bg.vertexRef() );
		assertEquals( "Linked vertex does not link to expected branch vertex.",
				v0.getInternalPoolIndex(), bv0.getLinkedVertexId() );
		assertEquals( "Branch vertex does not link to expected linked vertex.",
				v0, bg.getLinkedVertex( bv0, graph.vertexRef() ) );

		final BranchVertex bv1 = bg.getBranchVertex( v1, bg.vertexRef() );
		assertEquals( "Linked vertex does not link to expected branch vertex.",
				v1.getInternalPoolIndex(), bv1.getLinkedVertexId() );
		assertEquals( "Branch vertex does not link to expected linked vertex.",
				v1, bg.getLinkedVertex( bv1, graph.vertexRef() ) );
	}

	@Test
	public void testLinkTwoVertices()
	{
		final ListenableTestVertex v0 = graph.addVertex().init( 0 );
		final ListenableTestVertex v1 = graph.addVertex().init( 1 );
		final ListenableTestEdge e0 = graph.addEdge( v0, v1 ).init();

		final PoolCollectionWrapper< BranchVertex > vertices = bg.vertices();
		final int vSize = vertices.size();
		assertEquals( "Expected the branch graph to have 2 vertices.", 2, vSize );

		final PoolCollectionWrapper< BranchEdge > edges = bg.edges();
		final int eSize = edges.size();
		assertEquals( "Expected the branch graph to have 1 edge.", 1, eSize );

		final BranchVertex bv0 = bg.getBranchVertex( v0, bg.vertexRef() );
		assertEquals( "Linked vertex does not link to expected branch vertex.",
				v0.getInternalPoolIndex(), bv0.getLinkedVertexId() );
		assertEquals( "Branch vertex does not link to expected linked vertex.",
				v0, bg.getLinkedVertex( bv0, graph.vertexRef() ) );

		final BranchVertex bv1 = bg.getBranchVertex( v1, bg.vertexRef() );
		assertEquals( "Linked vertex does not link to expected branch vertex.",
				v1.getInternalPoolIndex(), bv1.getLinkedVertexId() );
		assertEquals( "Branch vertex does not link to expected linked vertex.",
				v1, bg.getLinkedVertex( bv1, graph.vertexRef() ) );

		final BranchEdge be0 = bg.getBranchEdge( e0, bg.edgeRef() );
		assertEquals( "Linked edge does not link to expected branch edge.",
				e0.getInternalPoolIndex(), be0.getLinkedEdgeId() );
		assertEquals( "Branch edge does not link to expected linked vertex.",
				e0, bg.getLinkedEdge( be0, graph.edgeRef() ) );
	}

	@Test
	public void testLinkSeveralVertices()
	{
		// This order first: 2 vertices - 1 edge - 1 vertex - 1 edge.
		final ListenableTestVertex v0 = graph.addVertex().init( 0 );
		final ListenableTestVertex v1 = graph.addVertex().init( 1 );
		final ListenableTestEdge e0 = graph.addEdge( v0, v1 ).init();
		final ListenableTestVertex v2 = graph.addVertex().init( 2 );
		System.out.println(); // DEBUG
		final ListenableTestEdge e1 = graph.addEdge( v1, v2 ).init();

		final PoolCollectionWrapper< BranchVertex > vertices = bg.vertices();
		final int vSize = vertices.size();
		assertEquals( "Expected the branch graph to have 2 vertices.", 2, vSize );

		final PoolCollectionWrapper< BranchEdge > edges = bg.edges();
		final int eSize = edges.size();
		assertEquals( "Expected the branch graph to have 1 edge.", 1, eSize );

		final BranchVertex bv0 = bg.getBranchVertex( v0, bg.vertexRef() );
		assertEquals( "Linked vertex does not link to expected branch vertex.",
				v0.getInternalPoolIndex(), bv0.getLinkedVertexId() );
		assertEquals( "Branch vertex does not link to expected linked vertex.",
				v0, bg.getLinkedVertex( bv0, graph.vertexRef() ) );

		final BranchVertex bv1 = bg.getBranchVertex( v1, bg.vertexRef() );
		assertNull( "Branch vertex linked to a non-joint vertex should be null.", bv1 );

		final BranchVertex bv2 = bg.getBranchVertex( v2, bg.vertexRef() );
		assertEquals( "Linked vertex does not link to expected branch vertex.",
				v2.getInternalPoolIndex(), bv2.getLinkedVertexId() );
		assertEquals( "Branch vertex does not link to expected linked vertex.",
				v2, bg.getLinkedVertex( bv2, graph.vertexRef() ) );

		final BranchEdge be0 = bg.getBranchEdge( e0, bg.edgeRef() );
		assertEquals( "Linked edge does not link to expected branch edge.",
				e0.getInternalPoolIndex(), be0.getLinkedEdgeId() );
		assertEquals( "Branch edge does not link to expected linked vertex.",
				e0, bg.getLinkedEdge( be0, graph.edgeRef() ) );

		final BranchEdge be1 = bg.getBranchEdge( e1, bg.edgeRef() );
		assertEquals( "Non-join edge should link to the same branch edge.", be0, be1 );
	}
}
