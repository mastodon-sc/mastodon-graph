package org.mastodon.graph.branch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.mastodon.collection.RefCollections;
import org.mastodon.collection.RefList;
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

	@Test
	public void testLinkManyVertices()
	{
		// First all vertices then all edges.
		final RefList< ListenableTestVertex > vlist = RefCollections.createRefList( graph.vertices() );
		for ( int i = 0; i < 5; i++ )
			vlist.add( graph.addVertex().init( i ) );
		final RefList< ListenableTestEdge > elist = RefCollections.createRefList( graph.edges() );
		final ListenableTestVertex ref1 = graph.vertexRef();
		final ListenableTestVertex ref2 = graph.vertexRef();
		final ListenableTestEdge eref = graph.edgeRef();
		for ( int i = 0; i < vlist.size() - 1; i++ )
		{
			final ListenableTestVertex source = vlist.get( i, ref1 );
			final ListenableTestVertex target = vlist.get( i + 1, ref2 );
			final ListenableTestEdge edge = graph.addEdge( source, target, eref ).init();
			elist.add( edge );
		}

		final PoolCollectionWrapper< BranchVertex > vertices = bg.vertices();
		final int vSize = vertices.size();
		assertEquals( "Expected the branch graph to have 2 vertices.", 2, vSize );

		final PoolCollectionWrapper< BranchEdge > edges = bg.edges();
		final int eSize = edges.size();
		assertEquals( "Expected the branch graph to have 1 edge.", 1, eSize );
		
		final ListenableTestVertex v0 = vlist.get( 0 );
		final BranchVertex bv0 = bg.getBranchVertex( v0, bg.vertexRef() );
		assertNotNull( "First linked vertex should link to a branch vertex.", bv0 );
		assertEquals( "First branch vertex should link to first linked vertex in the branch.",
				v0, bg.getLinkedVertex( bv0, graph.vertexRef() ) );

		final ListenableTestVertex vlast = vlist.get( vlist.size() - 1 );
		final BranchVertex bvlast = bg.getBranchVertex( vlast, bg.vertexRef() );
		assertNotNull( "Last linked vertex should link to a branch vertex.", bvlast );
		assertEquals( "Last branch vertex should link to last linked vertex in the branch.",
				vlast, bg.getLinkedVertex( bvlast, graph.vertexRef() ) );

		final BranchEdge be = bg.edges().iterator().next();
		final ListenableTestEdge e0 = elist.get( 0 );
		assertEquals( "Branch edge should link to first linked edge in the branch.",
				e0, bg.getLinkedEdge( be, graph.edgeRef() ) );

		
		final BranchEdge beref = bg.edgeRef();
		for ( int i = 1; i < vlist.size() - 1; i++ )
		{
			final ListenableTestVertex vi = vlist.get( i, ref1 );
			assertNull( "Middle vertex should not link to a branch vertex.", bg.getBranchVertex( vi, bg.vertexRef() ) );
			final BranchEdge bei = bg.getBranchEdge( vi, beref );
			assertNotNull( "Middle vertex should link to a branch edge.", bei );
			assertEquals( "Middle vertex shoud link to the branch edge.", be, bei );
		}

		for ( final ListenableTestEdge ei : elist )
			assertEquals( "Linked edge should link to the branch edge.", be, bg.getBranchEdge( ei, beref ) );

		graph.releaseRef( ref1 );
		graph.releaseRef( ref2 );
		graph.releaseRef( eref );
		bg.releaseRef( beref );
	}

	@Test
	public void testRemoveOneEdge()
	{
		final RefList< ListenableTestVertex > vlist = RefCollections.createRefList( graph.vertices() );
		for ( int i = 0; i < 6; i++ )
			vlist.add( graph.addVertex().init( i ) );
		final RefList< ListenableTestEdge > elist = RefCollections.createRefList( graph.edges() );
		final ListenableTestVertex ref1 = graph.vertexRef();
		final ListenableTestVertex ref2 = graph.vertexRef();
		final ListenableTestEdge eref = graph.edgeRef();
		for ( int i = 0; i < vlist.size() - 1; i++ )
		{
			final ListenableTestVertex source = vlist.get( i, ref1 );
			final ListenableTestVertex target = vlist.get( i + 1, ref2 );
			final ListenableTestEdge edge = graph.addEdge( source, target, eref ).init();
			elist.add( edge );
		}

		// Remove middle edge.
		final ListenableTestEdge removedEdge = elist.get( elist.size() / 2 );
		final ListenableTestVertex oldSource = removedEdge.getSource();
		final ListenableTestVertex oldTarget = removedEdge.getTarget();
		graph.remove( removedEdge );

		final PoolCollectionWrapper< BranchVertex > vertices = bg.vertices();
		final int vSize = vertices.size();
		assertEquals( "Expected the branch graph to have 4 vertices.", 4, vSize );

		final PoolCollectionWrapper< BranchEdge > edges = bg.edges();
		final int eSize = edges.size();
		assertEquals( "Expected the branch graph to have 2 edges.", 2, eSize );

		// Does stuff link to new branch vertex?
		final BranchVertex newBVsource = bg.getBranchVertex( oldSource, bg.vertexRef() );
		assertNotNull( "Old source linked vertex should link to a branch vertex.", newBVsource );
		assertEquals( "New branch vertex should link to old source linked vertex.", oldSource, bg.getLinkedVertex( newBVsource, ref1 ) );

		final BranchVertex newBVtarget = bg.getBranchVertex( oldTarget, bg.vertexRef() );
		assertNotNull( "Old target linked vertex should link to a branch vertex.", newBVtarget );
		assertEquals( "New branch vertex should link to old target linked vertex.", oldTarget, bg.getLinkedVertex( newBVtarget, ref2 ) );

		// Does stuff link to branch edge?
		final BranchEdge newIncomingBE = newBVsource.incomingEdges().get( 0 );
		assertEquals( "New incoming branch edge should link to first linked edge in the branch.",
				elist.get( 0 ), bg.getLinkedEdge( newIncomingBE, graph.edgeRef() ) );
		for ( int i = 0; i < elist.size() / 2; i++ )
			assertEquals( "Linked edge in the new first branch should link to new incoming branch edge.",
					newIncomingBE, bg.getBranchEdge( elist.get( i ), bg.edgeRef() ) );
		for ( int i = 1; i < vlist.size() / 2 - 1; i++ )
			assertEquals( "Middle vertex in the new first branch should link to new incoming branch edge.",
					newIncomingBE, bg.getBranchEdge( vlist.get( i ), bg.edgeRef() ) );

		final BranchEdge newOutgoingBE = newBVtarget.outgoingEdges().get( 0 );
		assertEquals( "New outgoing branch edge should link to first linked edge in the second branch.",
				elist.get( elist.size() / 2 + 1 ), bg.getLinkedEdge( newOutgoingBE, graph.edgeRef() ) );
		for ( int i = elist.size() / 2 + 1; i < elist.size(); i++ )
			assertEquals( "Linked edge in the new second branch should link to new ougoing branch edge.",
					newOutgoingBE, bg.getBranchEdge( elist.get( i ), bg.edgeRef() ) );
		for ( int i = vlist.size() / 2 + 1; i < vlist.size() - 1; i++ )
			assertEquals( "Middle vertex in the new second branch should link to new outgoing branch edge.",
					newOutgoingBE, bg.getBranchEdge( vlist.get( i ), bg.edgeRef() ) );

		graph.releaseRef( ref1 );
		graph.releaseRef( ref2 );
		graph.releaseRef( eref );
	}

	@Test
	public void testRemoveStartVertex()
	{
		final RefList< ListenableTestVertex > vlist = RefCollections.createRefList( graph.vertices() );
		for ( int i = 0; i < 6; i++ )
			vlist.add( graph.addVertex().init( i ) );
		final RefList< ListenableTestEdge > elist = RefCollections.createRefList( graph.edges() );
		final ListenableTestVertex ref1 = graph.vertexRef();
		final ListenableTestVertex ref2 = graph.vertexRef();
		final ListenableTestEdge eref = graph.edgeRef();
		for ( int i = 0; i < vlist.size() - 1; i++ )
		{
			final ListenableTestVertex source = vlist.get( i, ref1 );
			final ListenableTestVertex target = vlist.get( i + 1, ref2 );
			final ListenableTestEdge edge = graph.addEdge( source, target, eref ).init();
			elist.add( edge );
		}

		// Remove first vertex.
		graph.remove( vlist.get( 0 ) );

		final PoolCollectionWrapper< BranchVertex > vertices = bg.vertices();
		final int vSize = vertices.size();
		assertEquals( "Expected the branch graph to have 2 vertices.", 2, vSize );

		final PoolCollectionWrapper< BranchEdge > edges = bg.edges();
		final int eSize = edges.size();
		assertEquals( "Expected the branch graph to have 1 edge.", 1, eSize );

		final ListenableTestVertex v1 = vlist.get( 1 );
		final BranchVertex bv0 = bg.getBranchVertex( v1, bg.vertexRef() );
		assertNotNull( "First linked vertex should link to a branch vertex.", bv0 );
		assertEquals( "First branch vertex should link to first linked vertex in the branch.",
				v1, bg.getLinkedVertex( bv0, graph.vertexRef() ) );

		final ListenableTestVertex vlast = vlist.get( vlist.size() - 1 );
		final BranchVertex bvlast = bg.getBranchVertex( vlast, bg.vertexRef() );
		assertNotNull( "Last linked vertex should link to a branch vertex.", bvlast );
		assertEquals( "Last branch vertex should link to last linked vertex in the branch.",
				vlast, bg.getLinkedVertex( bvlast, graph.vertexRef() ) );

		final BranchEdge be = bg.edges().iterator().next();
		final ListenableTestEdge e1 = elist.get( 1 );
		assertEquals( "Branch edge should link to first linked edge (second before removal) in the branch.",
				e1, bg.getLinkedEdge( be, graph.edgeRef() ) );

		final BranchEdge beref = bg.edgeRef();
		for ( int i = 2; i < vlist.size() - 1; i++ )
		{
			final ListenableTestVertex vi = vlist.get( i, ref1 );
			assertNull( "Middle vertex should not link to a branch vertex.", bg.getBranchVertex( vi, bg.vertexRef() ) );
			final BranchEdge bei = bg.getBranchEdge( vi, beref );
			assertNotNull( "Middle vertex should link to a branch edge.", bei );
			assertEquals( "Middle vertex shoud link to the branch edge.", be, bei );
		}

		for ( int i = 1; i < elist.size(); i++ )
			assertEquals( "Linked edge should link to the branch edge.", be, bg.getBranchEdge( elist.get( i ), beref ) );

		graph.releaseRef( ref1 );
		graph.releaseRef( ref2 );
		graph.releaseRef( eref );
	}

	@Test
	public void testRemoveMiddleVertex()
	{
		final RefList< ListenableTestVertex > vlist = RefCollections.createRefList( graph.vertices() );
		for ( int i = 0; i < 6; i++ )
			vlist.add( graph.addVertex().init( i ) );
		final RefList< ListenableTestEdge > elist = RefCollections.createRefList( graph.edges() );
		final ListenableTestVertex ref1 = graph.vertexRef();
		final ListenableTestVertex ref2 = graph.vertexRef();
		final ListenableTestEdge eref = graph.edgeRef();
		for ( int i = 0; i < vlist.size() - 1; i++ )
		{
			final ListenableTestVertex source = vlist.get( i, ref1 );
			final ListenableTestVertex target = vlist.get( i + 1, ref2 );
			final ListenableTestEdge edge = graph.addEdge( source, target, eref ).init();
			elist.add( edge );
		}

		// Remove middle vertex.
		final ListenableTestVertex toRemove = vlist.get( vlist.size() / 2 );
		graph.remove( toRemove );
		final ListenableTestVertex newTarget = vlist.get( vlist.size() / 2 - 1 );
		final ListenableTestVertex newSource = vlist.get( vlist.size() / 2 + 1 );

		final PoolCollectionWrapper< BranchVertex > vertices = bg.vertices();
		final int vSize = vertices.size();
		assertEquals( "Expected the branch graph to have 4 vertices.", 4, vSize );

		final PoolCollectionWrapper< BranchEdge > edges = bg.edges();
		final int eSize = edges.size();
		assertEquals( "Expected the branch graph to have 2 edges.", 2, eSize );

		// First branch.
		final ListenableTestVertex v0 = vlist.get( 0 );
		final BranchVertex bv0 = bg.getBranchVertex( v0, bg.vertexRef() );
		assertNotNull( "First linked vertex should link to a branch vertex.", bv0 );
		assertEquals( "First branch vertex should link to first linked vertex in the branch.",
				v0, bg.getLinkedVertex( bv0, graph.vertexRef() ) );

		final BranchVertex bvNewTarget = bg.getBranchVertex( newTarget, bg.vertexRef() );
		assertNotNull( "Last linked vertex in the new branch should link to a branch vertex.", bvNewTarget );
		assertEquals( "Last branch vertex should link to last linked vertex in the first branch.",
				newTarget, bg.getLinkedVertex( bvNewTarget, graph.vertexRef() ) );

		final BranchEdge be1 = bv0.outgoingEdges().get( 0 );
		final ListenableTestEdge e0 = elist.get( 0 );
		assertEquals( "Branch edge should link to first linked edge in the branch.",
				e0, bg.getLinkedEdge( be1, graph.edgeRef() ) );

		final BranchEdge beref = bg.edgeRef();
		for ( int i = 2; i < vlist.size() / 2 - 1; i++ )
		{
			final ListenableTestVertex vi = vlist.get( i, ref1 );
			assertNull( "Middle vertex should not link to a branch vertex.", bg.getBranchVertex( vi, bg.vertexRef() ) );
			final BranchEdge bei = bg.getBranchEdge( vi, beref );
			assertNotNull( "Middle vertex should link to a branch edge.", bei );
			assertEquals( "Middle vertex shoud link to the branch edge.", be1, bei );
		}

		for ( int i = 1; i < elist.size() / 2; i++ )
			assertEquals( "Linked edge should link to the branch edge.",
					be1, bg.getBranchEdge( elist.get( i ), beref ) );

		// Second branch
		final BranchVertex bvNewSource = bg.getBranchVertex( newSource, bg.vertexRef() );
		assertNotNull( "First linked vertex in the new branch should link to a branch vertex.", bvNewSource );
		assertEquals( "First branch vertex should link to first linked vertex in the second branch.",
				newSource, bg.getLinkedVertex( bvNewSource, graph.vertexRef() ) );

		final ListenableTestVertex vlast = vlist.get( vlist.size() - 1 );
		final BranchVertex bvlast = bg.getBranchVertex( vlast, bg.vertexRef() );
		assertNotNull( "Last linked vertex should link to a branch vertex.", bvlast );
		assertEquals( "Last branch vertex should link to last linked vertex in the branch.",
				vlast, bg.getLinkedVertex( bvlast, graph.vertexRef() ) );

		final BranchEdge be2 = bvNewSource.outgoingEdges().get( 0 );
		final ListenableTestEdge ens = newSource.outgoingEdges().get( 0 );
		assertEquals( "Branch edge should link to first linked edge in the second branch.",
				ens, bg.getLinkedEdge( be2, graph.edgeRef() ) );

		for ( int i = vlist.size() / 2 + 2; i < vlist.size() - 1; i++ )
		{
			final ListenableTestVertex vi = vlist.get( i, ref1 );
			assertNull( "Middle vertex should not link to a branch vertex.",
					bg.getBranchVertex( vi, bg.vertexRef() ) );
			final BranchEdge bei = bg.getBranchEdge( vi, beref );
			assertNotNull( "Middle vertex should link to a branch edge.", bei );
			assertEquals( "Middle vertex shoud link to the branch edge.", be2, bei );
		}

		for ( int i = elist.size() / 2 + 2; i < elist.size(); i++ )
			assertEquals( "Linked edge should link to the branch edge.",
					be2, bg.getBranchEdge( elist.get( i ), beref ) );

		graph.releaseRef( ref1 );
		graph.releaseRef( ref2 );
		graph.releaseRef( eref );
	}

	@Test
	public void testRemoveLastVertex()
	{
		final RefList< ListenableTestVertex > vlist = RefCollections.createRefList( graph.vertices() );
		for ( int i = 0; i < 6; i++ )
			vlist.add( graph.addVertex().init( i ) );
		final RefList< ListenableTestEdge > elist = RefCollections.createRefList( graph.edges() );
		final ListenableTestVertex ref1 = graph.vertexRef();
		final ListenableTestVertex ref2 = graph.vertexRef();
		final ListenableTestEdge eref = graph.edgeRef();
		for ( int i = 0; i < vlist.size() - 1; i++ )
		{
			final ListenableTestVertex source = vlist.get( i, ref1 );
			final ListenableTestVertex target = vlist.get( i + 1, ref2 );
			final ListenableTestEdge edge = graph.addEdge( source, target, eref ).init();
			elist.add( edge );
		}

		// Remove last vertex.
		graph.remove( vlist.get( vlist.size() - 1 ) );

		final PoolCollectionWrapper< BranchVertex > vertices = bg.vertices();
		final int vSize = vertices.size();
		assertEquals( "Expected the branch graph to have 2 vertices.", 2, vSize );

		final PoolCollectionWrapper< BranchEdge > edges = bg.edges();
		final int eSize = edges.size();
		assertEquals( "Expected the branch graph to have 1 edge.", 1, eSize );

		final ListenableTestVertex v0 = vlist.get( 0 );
		final BranchVertex bv0 = bg.getBranchVertex( v0, bg.vertexRef() );
		assertNotNull( "First linked vertex should link to a branch vertex.", bv0 );
		assertEquals( "First branch vertex should link to first linked vertex in the branch.",
				v0, bg.getLinkedVertex( bv0, graph.vertexRef() ) );

		final ListenableTestVertex vforetolast = vlist.get( vlist.size() - 2 );
		final BranchVertex bvlast = bg.getBranchVertex( vforetolast, bg.vertexRef() );
		assertNotNull( "Last linked vertex should link to a branch vertex.", bvlast );
		assertEquals( "Last branch vertex should link to last linked vertex in the branch.",
				vforetolast, bg.getLinkedVertex( bvlast, graph.vertexRef() ) );

		final BranchEdge be = bg.edges().iterator().next();
		final ListenableTestEdge e0 = elist.get( 0 );
		assertEquals( "Branch edge should link to first linked edge in the branch.",
				e0, bg.getLinkedEdge( be, graph.edgeRef() ) );

		final BranchEdge beref = bg.edgeRef();
		for ( int i = 1; i < vlist.size() - 2; i++ )
		{
			final ListenableTestVertex vi = vlist.get( i, ref1 );
			assertNull( "Middle vertex should not link to a branch vertex.", bg.getBranchVertex( vi, bg.vertexRef() ) );
			final BranchEdge bei = bg.getBranchEdge( vi, beref );
			assertNotNull( "Middle vertex should link to a branch edge.", bei );
			assertEquals( "Middle vertex shoud link to the branch edge.", be, bei );
		}

		for ( int i = 0; i < elist.size() - 1; i++ )
			assertEquals( "Linked edge should link to the branch edge.", be, bg.getBranchEdge( elist.get( i ), beref ) );

		graph.releaseRef( ref1 );
		graph.releaseRef( ref2 );
		graph.releaseRef( eref );
	}

	@Test
	public void testBranchingLambda()
	{
		// Make a long branch.
		final RefList< ListenableTestVertex > vlist = RefCollections.createRefList( graph.vertices() );
		for ( int i = 0; i < 7; i++ )
			vlist.add( graph.addVertex().init( i ) );
		final RefList< ListenableTestEdge > elist = RefCollections.createRefList( graph.edges() );
		final ListenableTestVertex ref1 = graph.vertexRef();
		final ListenableTestVertex ref2 = graph.vertexRef();
		final ListenableTestEdge eref = graph.edgeRef();
		for ( int i = 0; i < vlist.size() - 1; i++ )
		{
			final ListenableTestVertex source = vlist.get( i, ref1 );
			final ListenableTestVertex target = vlist.get( i + 1, ref2 );
			final ListenableTestEdge edge = graph.addEdge( source, target, eref ).init();
			elist.add( edge );
		}

		// Branch from its middle vertex in lambda shape (split event).
		final ListenableTestVertex middle = vlist.get( vlist.size() / 2 );
		ListenableTestVertex source = middle;
		for ( int i = 0; i < vlist.size() / 2; i++ )
		{
			final ListenableTestVertex target = graph.addVertex().init( vlist.size() + i );
			vlist.add( target );
			final ListenableTestEdge e = graph.addEdge( source, target ).init();
			elist.add( e );
			source = target;
		}

		// Basic test on N vertices and N edges.
		final PoolCollectionWrapper< BranchVertex > vertices = bg.vertices();
		final int vSize = vertices.size();
		assertEquals( "Expected the branch graph to have 4 vertices.", 4, vSize );

		final PoolCollectionWrapper< BranchEdge > edges = bg.edges();
		final int eSize = edges.size();
		assertEquals( "Expected the branch graph to have 3 edges.", 3, eSize );

		// Test that we branched correctly.
		final BranchVertex middleBV = bg.getBranchVertex( middle, bg.vertexRef() );
		assertNotNull( "Middle linked vertex should link to a branch vertex.", middleBV );
		assertEquals( "Middle branch vertex should link to middle linked vertex in the branch.",
				middle, bg.getLinkedVertex( middleBV, graph.vertexRef() ) );

		final ListenableTestVertex first = vlist.get( 0 );
		final BranchVertex firstBV = bg.getBranchVertex( first, bg.vertexRef() );
		final ListenableTestEdge efirst = first.outgoingEdges().get( 0 );
		final BranchEdge efirstBE = bg.getBranchEdge( efirst, bg.edgeRef() );
		assertEquals( "Outgoing edge of a BV should link to the first edge of the linked branch.",
				firstBV.outgoingEdges().get( 0 ), efirstBE );

		// First branch.
		for ( int i = 1; i < 3; i++ )
		{
			final ListenableTestVertex v = vlist.get( i );
			assertNull( "Vertex in the middle of a branch should not link to a branch vertex.", bg.getBranchVertex( v, bg.vertexRef() ) );
			assertEquals( "Vertex  in the middle of a branch should link to a branch edge.", efirstBE, bg.getBranchEdge( v, bg.edgeRef() ) );
		}
	}
	
	@Test
	public void testBranchingY()
	{
		// Make a long branch.
		final RefList< ListenableTestVertex > vlist = RefCollections.createRefList( graph.vertices() );
		for ( int i = 0; i < 7; i++ )
			vlist.add( graph.addVertex().init( i ) );
		final RefList< ListenableTestEdge > elist = RefCollections.createRefList( graph.edges() );
		final ListenableTestVertex ref1 = graph.vertexRef();
		final ListenableTestVertex ref2 = graph.vertexRef();
		final ListenableTestEdge eref = graph.edgeRef();
		for ( int i = 0; i < vlist.size() - 1; i++ )
		{
			final ListenableTestVertex source = vlist.get( i, ref1 );
			final ListenableTestVertex target = vlist.get( i + 1, ref2 );
			final ListenableTestEdge edge = graph.addEdge( source, target, eref ).init();
			elist.add( edge );
		}
		final ListenableTestVertex middle = vlist.get( vlist.size() / 2 );

		// Branch from its middle vertex in Y shape (merge event).
		ListenableTestVertex source = graph.addVertex().init( vlist.size() );
		ListenableTestVertex target = null;
		vlist.add( source );
		for ( int i = 1; i < 4; i++ )
		{
			target = graph.addVertex().init( vlist.size() );
			vlist.add( target );
			final ListenableTestEdge e = graph.addEdge( source, target ).init();
			elist.add( e );
			source = target;
		}
		graph.addEdge( target, middle ).init();

		// Basic test on N vertices and N edges.
		final PoolCollectionWrapper< BranchVertex > vertices = bg.vertices();
		final int vSize = vertices.size();
		assertEquals( "Expected the branch graph to have 4 vertices.", 4, vSize );

		final PoolCollectionWrapper< BranchEdge > edges = bg.edges();
		final int eSize = edges.size();
		assertEquals( "Expected the branch graph to have 3 edges.", 3, eSize );

		// First branch.
		final ListenableTestVertex first = vlist.get( 0 );
		final BranchVertex firstBV = bg.getBranchVertex( first, bg.vertexRef() );
		final ListenableTestEdge efirst = first.outgoingEdges().get( 0 );
		final BranchEdge efirstBE = bg.getBranchEdge( efirst, bg.edgeRef() );
		assertEquals( "Outgoing edge of a BV should link to the first edge of the linked branch.",
				firstBV.outgoingEdges().get( 0 ), efirstBE );

		for ( int i = 1; i < 3; i++ )
		{
			final ListenableTestVertex v = vlist.get( i );
			assertNull( "Vertex in the middle of a branch should not link to a branch vertex.", bg.getBranchVertex( v, bg.vertexRef() ) );
			assertEquals( "Vertex  in the middle of a branch should link to a branch edge.", efirstBE, bg.getBranchEdge( v, bg.edgeRef() ) );
		}

		// Other half, test that we branched correctly.
		final BranchVertex middleBV = bg.getBranchVertex( middle, bg.vertexRef() );
		assertNotNull( "Middle linked vertex should link to a branch vertex.", middleBV );
		assertEquals( "Middle branch vertex should link to middle linked vertex in the branch.",
				middle, bg.getLinkedVertex( middleBV, graph.vertexRef() ) );

		final ListenableTestEdge emiddle = middle.outgoingEdges().get( 0 );
		final BranchEdge emiddleBE = bg.getBranchEdge( emiddle, bg.edgeRef() );
		assertEquals( "Outgoing edge of a BV should link to the first edge of the linked branch.",
				middleBV.outgoingEdges().get( 0 ), emiddleBE );

		for ( int i = 4; i < 6; i++ )
		{
			final ListenableTestVertex v = vlist.get( i );
			assertNull( "Vertex in the middle of a branch should not link to a branch vertex.", bg.getBranchVertex( v, bg.vertexRef() ) );
			assertEquals( "Vertex  in the middle of a branch should link to a branch edge.", emiddleBE, bg.getBranchEdge( v, bg.edgeRef() ) );
		}

		final ListenableTestVertex last = vlist.get( 6 );
		final BranchVertex lastBV = bg.getBranchVertex( last, bg.vertexRef() );
		assertNotNull( "Last linked vertex should link to a branch vertex.", lastBV );
		assertEquals( "Last branch vertex should link to last linked vertex in the branch.",
				last, bg.getLinkedVertex( lastBV, graph.vertexRef() ) );

		// Half Y branch.
		final ListenableTestVertex other = vlist.get( 7 );
		final BranchVertex otherBV = bg.getBranchVertex( other, bg.vertexRef() );
		assertNotNull( "First linked vertex in the new branch should link to a branch vertex.", otherBV );
		assertEquals( "Branch vertex should link to new root in the branch.",
				other, bg.getLinkedVertex( otherBV, graph.vertexRef() ) );

		final ListenableTestEdge eother = other.outgoingEdges().get( 0 );
		final BranchEdge eotherBE = bg.getBranchEdge( eother, bg.edgeRef() );
		assertEquals( "Outgoing edge of a BV should link to the first edge of the linked branch.",
				otherBV.outgoingEdges().get( 0 ), eotherBE );

		for ( int i = 8; i <= 10; i++ )
		{
			final ListenableTestVertex v = vlist.get( i );
			assertNull( "Vertex in the middle of a branch should not link to a branch vertex.", bg.getBranchVertex( v, bg.vertexRef() ) );
			assertEquals( "Vertex  in the middle of a branch should link to a branch edge.", eotherBE, bg.getBranchEdge( v, bg.edgeRef() ) );
		}
	}
	
}
