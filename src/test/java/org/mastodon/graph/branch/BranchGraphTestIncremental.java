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
package org.mastodon.graph.branch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.mastodon.collection.RefCollection;
import org.mastodon.collection.RefCollections;
import org.mastodon.collection.RefList;
import org.mastodon.graph.ListenableTestEdge;
import org.mastodon.graph.ListenableTestGraph;
import org.mastodon.graph.ListenableTestVertex;

public class BranchGraphTestIncremental
{

	private ListenableTestGraph graph;

	private BranchTestGraphIncremental bg;

	@Before
	public void setUp() throws Exception
	{
		this.graph = new ListenableTestGraph();
		this.bg = new BranchTestGraphIncremental( graph, new BranchTestEdgePool( 50, new BranchTestVertexPool( 50 ) ) );
	}

	@Test
	public void testAddOneVertex()
	{
		final ListenableTestVertex v0 = graph.addVertex().init( 0, 0 );

		final RefCollection< BranchTestVertex > vertices = bg.vertices();
		final int size = vertices.size();
		assertEquals( "Expected the branch graph to have 1 vertex.", 1, size );

		final BranchTestVertex bv = vertices.iterator().next();
		assertEquals( "Branch vertex does not link to expected linked vertex.",
				v0, bg.getLinkedVertex( bv, graph.vertexRef() ) );

		final BranchTestVertex bv0 = bg.getBranchVertex( v0, bg.vertexRef() );
		assertEquals( "Linked vertex does not link to expected branch vertex.",
				bv, bv0 );
	}

	@Test
	public void testAddTwoVertices()
	{
		final ListenableTestVertex v0 = graph.addVertex().init( 0, 0 );
		final ListenableTestVertex v1 = graph.addVertex().init( 1, 1 );

		final RefCollection< BranchTestVertex > vertices = bg.vertices();
		final int size = vertices.size();
		assertEquals( "Expected the branch graph to have 2 vertices.", 2, size );

		final BranchTestVertex bv0 = bg.getBranchVertex( v0, bg.vertexRef() );
		assertEquals( "Branch vertex does not link to expected linked vertex.",
				v0, bg.getLinkedVertex( bv0, graph.vertexRef() ) );

		final BranchTestVertex bv1 = bg.getBranchVertex( v1, bg.vertexRef() );
		assertEquals( "Branch vertex does not link to expected linked vertex.",
				v1, bg.getLinkedVertex( bv1, graph.vertexRef() ) );
	}

	@Test
	public void testLinkTwoVertices()
	{
		final ListenableTestVertex v0 = graph.addVertex().init( 0, 0 );
		final ListenableTestVertex v1 = graph.addVertex().init( 1, 1 );
		final ListenableTestEdge e0 = graph.addEdge( v0, v1 ).init();

		final RefCollection< BranchTestVertex > vertices = bg.vertices();
		final int vSize = vertices.size();
		assertEquals( "Expected the branch graph to have 2 vertices.", 2, vSize );

		final RefCollection< BranchTestEdge > edges = bg.edges();
		final int eSize = edges.size();
		assertEquals( "Expected the branch graph to have 1 edge.", 1, eSize );

		final BranchTestVertex bv0 = bg.getBranchVertex( v0, bg.vertexRef() );
		assertEquals( "Branch vertex does not link to expected linked vertex.",
				v0, bg.getLinkedVertex( bv0, graph.vertexRef() ) );

		final BranchTestVertex bv1 = bg.getBranchVertex( v1, bg.vertexRef() );
		assertEquals( "Branch vertex does not link to expected linked vertex.",
				v1, bg.getLinkedVertex( bv1, graph.vertexRef() ) );

		final BranchTestEdge be0 = bg.getBranchEdge( e0, bg.edgeRef() );
		assertEquals( "Branch edge does not link to expected linked vertex.",
				e0, bg.getLinkedEdge( be0, graph.edgeRef() ) );
	}

	@Test
	public void testLinkSeveralVertices()
	{
		// This order first: 2 vertices - 1 edge - 1 vertex - 1 edge.
		final ListenableTestVertex v0 = graph.addVertex().init( 0, 0 );
		final ListenableTestVertex v1 = graph.addVertex().init( 1, 1 );
		final ListenableTestEdge e0 = graph.addEdge( v0, v1 ).init();
		final ListenableTestVertex v2 = graph.addVertex().init( 2, 1 );
		final ListenableTestEdge e1 = graph.addEdge( v1, v2 ).init();

		final RefCollection< BranchTestVertex > vertices = bg.vertices();
		final int vSize = vertices.size();
		assertEquals( "Expected the branch graph to have 2 vertices.", 2, vSize );

		final RefCollection< BranchTestEdge > edges = bg.edges();
		final int eSize = edges.size();
		assertEquals( "Expected the branch graph to have 1 edge.", 1, eSize );

		final BranchTestVertex bv0 = bg.getBranchVertex( v0, bg.vertexRef() );
		assertEquals( "Branch vertex does not link to expected linked vertex.",
				v0, bg.getLinkedVertex( bv0, graph.vertexRef() ) );

		final BranchTestVertex bv1 = bg.getBranchVertex( v1, bg.vertexRef() );
		assertNull( "Branch vertex linked to a non-joint vertex should be null.", bv1 );

		final BranchTestVertex bv2 = bg.getBranchVertex( v2, bg.vertexRef() );
		assertEquals( "Branch vertex does not link to expected linked vertex.",
				v2, bg.getLinkedVertex( bv2, graph.vertexRef() ) );

		final BranchTestEdge be0 = bg.getBranchEdge( e0, bg.edgeRef() );
		assertEquals( "Branch edge does not link to expected linked vertex.",
				e0, bg.getLinkedEdge( be0, graph.edgeRef() ) );

		final BranchTestEdge be1 = bg.getBranchEdge( e1, bg.edgeRef() );
		assertEquals( "Non-join edge should link to the same branch edge.", be0, be1 );
	}

	@Test
	public void testLinkManyVertices()
	{
		// First all vertices then all edges.
		final RefList< ListenableTestVertex > vlist = RefCollections.createRefList( graph.vertices() );
		for ( int i = 0; i < 5; i++ )
			vlist.add( graph.addVertex().init( i, i ) );
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

		final RefCollection< BranchTestVertex > vertices = bg.vertices();
		final int vSize = vertices.size();
		assertEquals( "Expected the branch graph to have 2 vertices.", 2, vSize );

		final RefCollection< BranchTestEdge > edges = bg.edges();
		final int eSize = edges.size();
		assertEquals( "Expected the branch graph to have 1 edge.", 1, eSize );

		final ListenableTestVertex v0 = vlist.get( 0 );
		final BranchTestVertex bv0 = bg.getBranchVertex( v0, bg.vertexRef() );
		assertNotNull( "First linked vertex should link to a branch vertex.", bv0 );
		assertEquals( "First branch vertex should link to first linked vertex in the branch.",
				v0, bg.getLinkedVertex( bv0, graph.vertexRef() ) );

		final ListenableTestVertex vlast = vlist.get( vlist.size() - 1 );
		final BranchTestVertex bvlast = bg.getBranchVertex( vlast, bg.vertexRef() );
		assertNotNull( "Last linked vertex should link to a branch vertex.", bvlast );
		assertEquals( "Last branch vertex should link to last linked vertex in the branch.",
				vlast, bg.getLinkedVertex( bvlast, graph.vertexRef() ) );

		final BranchTestEdge be = bg.edges().iterator().next();
		final ListenableTestEdge e0 = elist.get( 0 );
		assertEquals( "Branch edge should link to first linked edge in the branch.",
				e0, bg.getLinkedEdge( be, graph.edgeRef() ) );


		final BranchTestEdge beref = bg.edgeRef();
		for ( int i = 1; i < vlist.size() - 1; i++ )
		{
			final ListenableTestVertex vi = vlist.get( i, ref1 );
			assertNull( "Middle vertex should not link to a branch vertex.", bg.getBranchVertex( vi, bg.vertexRef() ) );
			final BranchTestEdge bei = bg.getBranchEdge( vi, beref );
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
			vlist.add( graph.addVertex().init( i, i ) );
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

		final RefCollection< BranchTestVertex > vertices = bg.vertices();
		final int vSize = vertices.size();
		assertEquals( "Expected the branch graph to have 4 vertices.", 4, vSize );

		final RefCollection< BranchTestEdge > edges = bg.edges();
		final int eSize = edges.size();
		assertEquals( "Expected the branch graph to have 2 edges.", 2, eSize );

		// Does stuff link to new branch vertex?
		final BranchTestVertex newBVsource = bg.getBranchVertex( oldSource, bg.vertexRef() );
		assertNotNull( "Old source linked vertex should link to a branch vertex.", newBVsource );
		assertEquals( "New branch vertex should link to old source linked vertex.", oldSource, bg.getLinkedVertex( newBVsource, ref1 ) );

		final BranchTestVertex newBVtarget = bg.getBranchVertex( oldTarget, bg.vertexRef() );
		assertNotNull( "Old target linked vertex should link to a branch vertex.", newBVtarget );
		assertEquals( "New branch vertex should link to old target linked vertex.", oldTarget, bg.getLinkedVertex( newBVtarget, ref2 ) );

		// Does stuff link to branch edge?
		final BranchTestEdge newIncomingBE = newBVsource.incomingEdges().get( 0 );
		assertEquals( "New incoming branch edge should link to first linked edge in the branch.",
				elist.get( 0 ), bg.getLinkedEdge( newIncomingBE, graph.edgeRef() ) );
		for ( int i = 0; i < elist.size() / 2; i++ )
			assertEquals( "Linked edge in the new first branch should link to new incoming branch edge.",
					newIncomingBE, bg.getBranchEdge( elist.get( i ), bg.edgeRef() ) );
		for ( int i = 1; i < vlist.size() / 2 - 1; i++ )
			assertEquals( "Middle vertex in the new first branch should link to new incoming branch edge.",
					newIncomingBE, bg.getBranchEdge( vlist.get( i ), bg.edgeRef() ) );

		final BranchTestEdge newOutgoingBE = newBVtarget.outgoingEdges().get( 0 );
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
			vlist.add( graph.addVertex().init( i, i ) );
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

		final RefCollection< BranchTestVertex > vertices = bg.vertices();
		final int vSize = vertices.size();
		assertEquals( "Expected the branch graph to have 2 vertices.", 2, vSize );

		final RefCollection< BranchTestEdge > edges = bg.edges();
		final int eSize = edges.size();
		assertEquals( "Expected the branch graph to have 1 edge.", 1, eSize );

		final ListenableTestVertex v1 = vlist.get( 1 );
		final BranchTestVertex bv0 = bg.getBranchVertex( v1, bg.vertexRef() );
		assertNotNull( "First linked vertex should link to a branch vertex.", bv0 );
		assertEquals( "First branch vertex should link to first linked vertex in the branch.",
				v1, bg.getLinkedVertex( bv0, graph.vertexRef() ) );

		final ListenableTestVertex vlast = vlist.get( vlist.size() - 1 );
		final BranchTestVertex bvlast = bg.getBranchVertex( vlast, bg.vertexRef() );
		assertNotNull( "Last linked vertex should link to a branch vertex.", bvlast );
		assertEquals( "Last branch vertex should link to last linked vertex in the branch.",
				vlast, bg.getLinkedVertex( bvlast, graph.vertexRef() ) );

		final BranchTestEdge be = bg.edges().iterator().next();
		final ListenableTestEdge e1 = elist.get( 1 );
		assertEquals( "Branch edge should link to first linked edge (second before removal) in the branch.",
				e1, bg.getLinkedEdge( be, graph.edgeRef() ) );

		final BranchTestEdge beref = bg.edgeRef();
		for ( int i = 2; i < vlist.size() - 1; i++ )
		{
			final ListenableTestVertex vi = vlist.get( i, ref1 );
			assertNull( "Middle vertex should not link to a branch vertex.", bg.getBranchVertex( vi, bg.vertexRef() ) );
			final BranchTestEdge bei = bg.getBranchEdge( vi, beref );
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
			vlist.add( graph.addVertex().init( i, i ) );
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

		final RefCollection< BranchTestVertex > vertices = bg.vertices();
		final int vSize = vertices.size();
		assertEquals( "Expected the branch graph to have 4 vertices.", 4, vSize );

		final RefCollection< BranchTestEdge > edges = bg.edges();
		final int eSize = edges.size();
		assertEquals( "Expected the branch graph to have 2 edges.", 2, eSize );

		// First branch.
		final ListenableTestVertex v0 = vlist.get( 0 );
		final BranchTestVertex bv0 = bg.getBranchVertex( v0, bg.vertexRef() );
		assertNotNull( "First linked vertex should link to a branch vertex.", bv0 );
		assertEquals( "First branch vertex should link to first linked vertex in the branch.",
				v0, bg.getLinkedVertex( bv0, graph.vertexRef() ) );

		final BranchTestVertex bvNewTarget = bg.getBranchVertex( newTarget, bg.vertexRef() );
		assertNotNull( "Last linked vertex in the new branch should link to a branch vertex.", bvNewTarget );
		assertEquals( "Last branch vertex should link to last linked vertex in the first branch.",
				newTarget, bg.getLinkedVertex( bvNewTarget, graph.vertexRef() ) );

		final BranchTestEdge be1 = bv0.outgoingEdges().get( 0 );
		final ListenableTestEdge e0 = elist.get( 0 );
		assertEquals( "Branch edge should link to first linked edge in the branch.",
				e0, bg.getLinkedEdge( be1, graph.edgeRef() ) );

		final BranchTestEdge beref = bg.edgeRef();
		for ( int i = 2; i < vlist.size() / 2 - 1; i++ )
		{
			final ListenableTestVertex vi = vlist.get( i, ref1 );
			assertNull( "Middle vertex should not link to a branch vertex.", bg.getBranchVertex( vi, bg.vertexRef() ) );
			final BranchTestEdge bei = bg.getBranchEdge( vi, beref );
			assertNotNull( "Middle vertex should link to a branch edge.", bei );
			assertEquals( "Middle vertex shoud link to the branch edge.", be1, bei );
		}

		for ( int i = 1; i < elist.size() / 2; i++ )
			assertEquals( "Linked edge should link to the branch edge.",
					be1, bg.getBranchEdge( elist.get( i ), beref ) );

		// Second branch
		final BranchTestVertex bvNewSource = bg.getBranchVertex( newSource, bg.vertexRef() );
		assertNotNull( "First linked vertex in the new branch should link to a branch vertex.", bvNewSource );
		assertEquals( "First branch vertex should link to first linked vertex in the second branch.",
				newSource, bg.getLinkedVertex( bvNewSource, graph.vertexRef() ) );

		final ListenableTestVertex vlast = vlist.get( vlist.size() - 1 );
		final BranchTestVertex bvlast = bg.getBranchVertex( vlast, bg.vertexRef() );
		assertNotNull( "Last linked vertex should link to a branch vertex.", bvlast );
		assertEquals( "Last branch vertex should link to last linked vertex in the branch.",
				vlast, bg.getLinkedVertex( bvlast, graph.vertexRef() ) );

		final BranchTestEdge be2 = bvNewSource.outgoingEdges().get( 0 );
		final ListenableTestEdge ens = newSource.outgoingEdges().get( 0 );
		assertEquals( "Branch edge should link to first linked edge in the second branch.",
				ens, bg.getLinkedEdge( be2, graph.edgeRef() ) );

		for ( int i = vlist.size() / 2 + 2; i < vlist.size() - 1; i++ )
		{
			final ListenableTestVertex vi = vlist.get( i, ref1 );
			assertNull( "Middle vertex should not link to a branch vertex.",
					bg.getBranchVertex( vi, bg.vertexRef() ) );
			final BranchTestEdge bei = bg.getBranchEdge( vi, beref );
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
			vlist.add( graph.addVertex().init( i, i ) );
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

		final RefCollection< BranchTestVertex > vertices = bg.vertices();
		final int vSize = vertices.size();
		assertEquals( "Expected the branch graph to have 2 vertices.", 2, vSize );

		final RefCollection< BranchTestEdge > edges = bg.edges();
		final int eSize = edges.size();
		assertEquals( "Expected the branch graph to have 1 edge.", 1, eSize );

		final ListenableTestVertex v0 = vlist.get( 0 );
		final BranchTestVertex bv0 = bg.getBranchVertex( v0, bg.vertexRef() );
		assertNotNull( "First linked vertex should link to a branch vertex.", bv0 );
		assertEquals( "First branch vertex should link to first linked vertex in the branch.",
				v0, bg.getLinkedVertex( bv0, graph.vertexRef() ) );

		final ListenableTestVertex vforetolast = vlist.get( vlist.size() - 2 );
		final BranchTestVertex bvlast = bg.getBranchVertex( vforetolast, bg.vertexRef() );
		assertNotNull( "Last linked vertex should link to a branch vertex.", bvlast );
		assertEquals( "Last branch vertex should link to last linked vertex in the branch.",
				vforetolast, bg.getLinkedVertex( bvlast, graph.vertexRef() ) );

		final BranchTestEdge be = bg.edges().iterator().next();
		final ListenableTestEdge e0 = elist.get( 0 );
		assertEquals( "Branch edge should link to first linked edge in the branch.",
				e0, bg.getLinkedEdge( be, graph.edgeRef() ) );

		final BranchTestEdge beref = bg.edgeRef();
		for ( int i = 1; i < vlist.size() - 2; i++ )
		{
			final ListenableTestVertex vi = vlist.get( i, ref1 );
			assertNull( "Middle vertex should not link to a branch vertex.", bg.getBranchVertex( vi, bg.vertexRef() ) );
			final BranchTestEdge bei = bg.getBranchEdge( vi, beref );
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
			vlist.add( graph.addVertex().init( i, i ) );
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
			final ListenableTestVertex target = graph.addVertex().init( vlist.size() + i, middle.getTimepoint() + i + 1 );
			vlist.add( target );
			final ListenableTestEdge e = graph.addEdge( source, target, eref ).init();
			elist.add( e );
			source = target;
		}

		// Basic test on N vertices and N edges.
		final RefCollection< BranchTestVertex > vertices = bg.vertices();
		final int vSize = vertices.size();
		assertEquals( "Expected the branch graph to have 4 vertices.", 4, vSize );

		final RefCollection< BranchTestEdge > edges = bg.edges();
		final int eSize = edges.size();
		assertEquals( "Expected the branch graph to have 3 edges.", 3, eSize );

		// Test that we branched correctly.
		final BranchTestVertex middleBV = bg.getBranchVertex( middle, bg.vertexRef() );
		assertNotNull( "Middle linked vertex should link to a branch vertex.", middleBV );
		assertEquals( "Middle branch vertex should link to middle linked vertex in the branch.",
				middle, bg.getLinkedVertex( middleBV, graph.vertexRef() ) );

		final ListenableTestVertex first = vlist.get( 0 );
		final BranchTestVertex firstBV = bg.getBranchVertex( first, bg.vertexRef() );
		final ListenableTestEdge efirst = first.outgoingEdges().get( 0 );
		final BranchTestEdge efirstBE = bg.getBranchEdge( efirst, bg.edgeRef() );
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
			vlist.add( graph.addVertex().init( i, i ) );
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
		ListenableTestVertex source = graph.addVertex().init( vlist.size(), 0 );
		ListenableTestVertex target = null;
		vlist.add( source );
		for ( int i = 1; i < 4; i++ )
		{
			target = graph.addVertex().init( vlist.size(), i );
			vlist.add( target );
			final ListenableTestEdge e = graph.addEdge( source, target, eref ).init();
			elist.add( e );
			source = target;
		}
		graph.addEdge( target, middle, eref ).init();

		// Basic test on N vertices and N edges.
		final RefCollection< BranchTestVertex > vertices = bg.vertices();
		final int vSize = vertices.size();
		assertEquals( "Expected the branch graph to have 4 vertices.", 4, vSize );

		final RefCollection< BranchTestEdge > edges = bg.edges();
		final int eSize = edges.size();
		assertEquals( "Expected the branch graph to have 3 edges.", 3, eSize );

		// First branch.
		final ListenableTestVertex first = vlist.get( 0 );
		final BranchTestVertex firstBV = bg.getBranchVertex( first, bg.vertexRef() );
		final ListenableTestEdge efirst = first.outgoingEdges().get( 0 );
		final BranchTestEdge efirstBE = bg.getBranchEdge( efirst, bg.edgeRef() );
		assertEquals( "Outgoing edge of a BV should link to the first edge of the linked branch.",
				firstBV.outgoingEdges().get( 0 ), efirstBE );

		for ( int i = 1; i < 3; i++ )
		{
			final ListenableTestVertex v = vlist.get( i );
			assertNull( "Vertex in the middle of a branch should not link to a branch vertex.", bg.getBranchVertex( v, bg.vertexRef() ) );
			assertEquals( "Vertex  in the middle of a branch should link to a branch edge.", efirstBE, bg.getBranchEdge( v, bg.edgeRef() ) );
		}

		// Other half, test that we branched correctly.
		final BranchTestVertex middleBV = bg.getBranchVertex( middle, bg.vertexRef() );
		assertNotNull( "Middle linked vertex should link to a branch vertex.", middleBV );
		assertEquals( "Middle branch vertex should link to middle linked vertex in the branch.",
				middle, bg.getLinkedVertex( middleBV, graph.vertexRef() ) );

		final ListenableTestEdge emiddle = middle.outgoingEdges().get( 0 );
		final BranchTestEdge emiddleBE = bg.getBranchEdge( emiddle, bg.edgeRef() );
		assertEquals( "Outgoing edge of a BV should link to the first edge of the linked branch.",
				middleBV.outgoingEdges().get( 0 ), emiddleBE );

		for ( int i = 4; i < 6; i++ )
		{
			final ListenableTestVertex v = vlist.get( i );
			assertNull( "Vertex in the middle of a branch should not link to a branch vertex.", bg.getBranchVertex( v, bg.vertexRef() ) );
			assertEquals( "Vertex  in the middle of a branch should link to a branch edge.", emiddleBE, bg.getBranchEdge( v, bg.edgeRef() ) );
		}

		final ListenableTestVertex last = vlist.get( 6 );
		final BranchTestVertex lastBV = bg.getBranchVertex( last, bg.vertexRef() );
		assertNotNull( "Last linked vertex should link to a branch vertex.", lastBV );
		assertEquals( "Last branch vertex should link to last linked vertex in the branch.",
				last, bg.getLinkedVertex( lastBV, graph.vertexRef() ) );

		// Half Y branch.
		final ListenableTestVertex other = vlist.get( 7 );
		final BranchTestVertex otherBV = bg.getBranchVertex( other, bg.vertexRef() );
		assertNotNull( "First linked vertex in the new branch should link to a branch vertex.", otherBV );
		assertEquals( "Branch vertex should link to new root in the branch.",
				other, bg.getLinkedVertex( otherBV, graph.vertexRef() ) );

		final ListenableTestEdge eother = other.outgoingEdges().get( 0 );
		final BranchTestEdge eotherBE = bg.getBranchEdge( eother, bg.edgeRef() );
		assertEquals( "Outgoing edge of a BV should link to the first edge of the linked branch.",
				otherBV.outgoingEdges().get( 0 ), eotherBE );

		for ( int i = 8; i <= 10; i++ )
		{
			final ListenableTestVertex v = vlist.get( i );
			assertNull( "Vertex in the middle of a branch should not link to a branch vertex.", bg.getBranchVertex( v, bg.vertexRef() ) );
			assertEquals( "Vertex  in the middle of a branch should link to a branch edge.", eotherBE, bg.getBranchEdge( v, bg.edgeRef() ) );
		}
	}

	@Test
	public void testDiamond()
	{
		// Make a branch.
		final RefList< ListenableTestVertex > vlist = RefCollections.createRefList( graph.vertices() );
		for ( int i = 0; i < 4; i++ )
			vlist.add( graph.addVertex().init( i, i ) );
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
		final ListenableTestVertex first = vlist.get( 0 );
		final ListenableTestVertex last = vlist.get( vlist.size() - 1 );

		// Mess it by running a parallel branch to this one.
		final ListenableTestVertex v1 = graph.addVertex().init( vlist.size(), 0 );
		vlist.add( v1 );
		final ListenableTestEdge e1 = graph.addEdge( first, v1 ).init();
		elist.add( e1 );
		final ListenableTestVertex v2 = graph.addVertex().init( vlist.size(), 1 );
		vlist.add( v2 );
		final ListenableTestEdge e2 = graph.addEdge( v1, v2 ).init();
		elist.add( e2 );
		final ListenableTestEdge e3 = graph.addEdge( v2, last ).init();
		elist.add( e3 );

		// Basic test on N vertices and N edges.
		final RefCollection< BranchTestVertex > vertices = bg.vertices();
		final int vSize = vertices.size();
		assertEquals( "Expected the branch graph to have 2 vertices.", 2, vSize );

		final RefCollection< BranchTestEdge > edges = bg.edges();
		final int eSize = edges.size();
		assertEquals( "Expected the branch graph to have 2 edges.", 2, eSize );

		for ( final BranchTestEdge be : edges )
		{
			ListenableTestEdge e0 = bg.getLinkedEdge( be, graph.edgeRef() );
			do
			{
				assertEquals( "Linked edge in the first branch should link to the right branch edge.",
						be, bg.getBranchEdge( e0, bg.edgeRef() ) );
			}
			while ( ( !e0.getTarget().outgoingEdges().isEmpty() )
					&& ( e0 = e0.getTarget().outgoingEdges().get( 0 ) ) != null );
		}
	}

	@Test
	public void testRing()
	{
		final RefList< ListenableTestVertex > vlist = RefCollections.createRefList( graph.vertices() );
		for ( int i = 0; i < 4; i++ )
			vlist.add( graph.addVertex().init( i, i ) );
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
		final ListenableTestVertex first = vlist.get( 0 );
		final ListenableTestVertex last = vlist.get( vlist.size() - 1 );

		// Create a ring.
		graph.addEdge( last, first ).init();

		// Basic test on N vertices and N edges.
		final RefCollection< BranchTestEdge > edges = bg.edges();
		final int eSize = edges.size();
		assertEquals( "Expected the branch graph to have 1 edge.", 1, eSize );

		final RefCollection< BranchTestVertex > vertices = bg.vertices();
		final int vSize = vertices.size();
		assertEquals( "Expected the branch graph to have 1 vertex.", 1, vSize );

		final BranchTestVertex bv0 = bg.vertices().iterator().next();
		final ListenableTestVertex v0 = bg.getLinkedVertex( bv0, graph.vertexRef() );
		final BranchTestEdge be0 = bg.edges().iterator().next();

		assertNotNull( "The branch vertex linked to this vertex should not be null.", bg.getBranchVertex( v0, bg.vertexRef() ) );
		assertNull( "The branch edge linked to this vertex should be null.", bg.getBranchEdge( v0, bg.edgeRef() ) );

		vlist.remove( v0 );
		for ( final ListenableTestVertex lv : vlist )
		{
			assertNull( "The branch vertex linked to this vertex should be null.", bg.getBranchVertex( lv, bg.vertexRef() ) );
			assertEquals( "The branch edge linked to this vertex should be the one branch edge.",
					be0, bg.getBranchEdge( lv, bg.edgeRef() ) );
		}
		for ( final ListenableTestEdge le : elist )
		{
			assertEquals( "The branch edge linked to any edge in the loop should be the one branch edge.",
					be0, bg.getBranchEdge( le, bg.edgeRef() ) );
		}

		final ListenableTestEdge le0 = bg.getLinkedEdge( be0, graph.edgeRef() );
		assertEquals( "The one branch vertex should link to an edge that has the one branch vertex linked vertex as a source.",
				v0, le0.getSource() );
	}

	@Test
	public void testTwoSplittingBranches()
	{
		// The root.
		final ListenableTestVertex v0 = graph.addVertex().init( 0, 0 );

		// Make the first branch.
		final RefList< ListenableTestVertex > vlistA = RefCollections.createRefList( graph.vertices() );
		final RefList< ListenableTestEdge > elistA = RefCollections.createRefList( graph.edges() );
		final ListenableTestVertex source = graph.vertexRef();
		source.refTo( v0 );
		for ( int i = 0; i < 7; i++ )
		{
			final ListenableTestVertex target = graph.addVertex().init( i + 10, i + 1 );
			vlistA.add( target );
			final ListenableTestEdge e = graph.addEdge( source, target ).init();
			elistA.add( e );
			source.refTo( target );
		}

		// Make the second branch.
		final RefList< ListenableTestVertex > vlistB = RefCollections.createRefList( graph.vertices() );
		final RefList< ListenableTestEdge > elistB = RefCollections.createRefList( graph.edges() );
		source.refTo( v0 );
		for ( int i = 0; i < 7; i++ )
		{
			final ListenableTestVertex target = graph.addVertex().init( i + 100, i + 1 );
			vlistB.add( target );
			final ListenableTestEdge e = graph.addEdge( source, target ).init();
			elistB.add( e );
			source.refTo( target );
		}

		// Remove the root.
		graph.remove( v0 );

		assertNull( "There should be not branch vertex linked to a removed vertex.",
				bg.getBranchVertex( v0, bg.vertexRef() ) );
		assertNull( "There should be not branch edge linked to a removed vertex.",
				bg.getBranchEdge( v0, bg.edgeRef() ) );
		assertNull( "There should be not branch edge linked to a removed edge.",
				bg.getBranchEdge( elistA.get( 0 ), bg.edgeRef() ) );
		assertNull( "There should be not branch edge linked to a removed edge.",
				bg.getBranchEdge( elistB.get( 0 ), bg.edgeRef() ) );

		// Basic test on N vertices and N edges.
		final RefCollection< BranchTestEdge > edges = bg.edges();
		final int eSize = edges.size();
		assertEquals( "Expected the branch graph to have 2 edges.", 2, eSize );

		final RefCollection< BranchTestVertex > vertices = bg.vertices();
		final int vSize = vertices.size();
		assertEquals( "Expected the branch graph to have 4 vertices.", 4, vSize );
		graph.releaseRef( source );
	}

	@Test
	public void testTwoMergingBranches()
	{
		// The root.
		final ListenableTestVertex v0 = graph.addVertex().init( 0, 0 );

		// Make the first branch.
		final RefList< ListenableTestVertex > vlistA = RefCollections.createRefList( graph.vertices() );
		final RefList< ListenableTestEdge > elistA = RefCollections.createRefList( graph.edges() );
		ListenableTestVertex target = null;
		for ( int i = 0; i < 7; i++ )
		{
			final ListenableTestVertex source = graph.addVertex().init( i + 10, i + 1 );
			vlistA.add( source );
			if ( target == null )
			{
				target = graph.vertexRef();
				target.refTo( source );
			}
			else
			{
				final ListenableTestEdge e = graph.addEdge( source, target ).init();
				elistA.add( e );
				target.refTo( source );
			}
		}
		elistA.add( graph.addEdge( target, v0 ).init() );

		// Make the second branch.
		final RefList< ListenableTestVertex > vlistB = RefCollections.createRefList( graph.vertices() );
		final RefList< ListenableTestEdge > elistB = RefCollections.createRefList( graph.edges() );
		target = null;
		for ( int i = 0; i < 7; i++ )
		{
			final ListenableTestVertex source = graph.addVertex().init( i + 100, i + 1 );
			vlistB.add( source );
			if ( target == null )
			{
				target = graph.vertexRef();
				target.refTo( source );
			}
			else
			{
				final ListenableTestEdge e = graph.addEdge( source, target ).init();
				elistB.add( e );
				target.refTo( source );
			}
		}
		elistB.add( graph.addEdge( target, v0 ).init() );

		// Remove the root.
		graph.remove( v0 );

		assertNull( "There should be not branch vertex linked to a removed vertex.",
				bg.getBranchVertex( v0, bg.vertexRef() ) );
		assertNull( "There should be not branch edge linked to a removed vertex.",
				bg.getBranchEdge( v0, bg.edgeRef() ) );
		assertNull( "There should be not branch edge linked to a removed edge.",
				bg.getBranchEdge( elistA.get( elistA.size() - 1 ), bg.edgeRef() ) );
		assertNull( "There should be not branch edge linked to a removed edge.",
				bg.getBranchEdge( elistB.get( elistB.size() - 1 ), bg.edgeRef() ) );

		// Basic test on N vertices and N edges.
		final RefCollection< BranchTestEdge > edges = bg.edges();
		final int eSize = edges.size();
		assertEquals( "Expected the branch graph to have 2 edges.", 2, eSize );

		final RefCollection< BranchTestVertex > vertices = bg.vertices();
		final int vSize = vertices.size();
		assertEquals( "Expected the branch graph to have 4 vertices.", 4, vSize );
		graph.releaseRef( target );
	}

	@Test
	public void testGraphRebuilt()
	{
		// The root.
		final ListenableTestVertex v0 = graph.addVertex().init( 0, 0 );

		// Make the first branch.
		final RefList< ListenableTestVertex > vlistA = RefCollections.createRefList( graph.vertices() );
		final RefList< ListenableTestEdge > elistA = RefCollections.createRefList( graph.edges() );
		final ListenableTestVertex source = graph.vertexRef();
		source.refTo( v0 );
		for ( int i = 0; i < 7; i++ )
		{
			final ListenableTestVertex target = graph.addVertex().init( i + 10, i + 1 );
			vlistA.add( target );
			final ListenableTestEdge e = graph.addEdge( source, target ).init();
			elistA.add( e );
			source.refTo( target );
		}

		// Make the second branch.
		final RefList< ListenableTestVertex > vlistB = RefCollections.createRefList( graph.vertices() );
		final RefList< ListenableTestEdge > elistB = RefCollections.createRefList( graph.edges() );
		source.refTo( v0 );
		for ( int i = 0; i < 7; i++ )
		{
			final ListenableTestVertex target = graph.addVertex().init( i + 100, i + 1 );
			vlistB.add( target );
			final ListenableTestEdge e = graph.addEdge( source, target ).init();
			elistB.add( e );
			source.refTo( target );
		}

		// Build a new branch graph from this one.
		final BranchTestGraphIncremental bg2 = new BranchTestGraphIncremental( graph, new BranchTestEdgePool( 50, new BranchTestVertexPool( 50 ) ) );

		// Basic test on N vertices and N edges.
		final RefCollection< BranchTestEdge > edges = bg2.edges();
		final int eSize = edges.size();
		assertEquals( "Expected the branch graph to have 2 edges.", 2, eSize );

		final RefCollection< BranchTestVertex > vertices = bg2.vertices();
		final int vSize = vertices.size();
		assertEquals( "Expected the branch graph to have 3 vertices.", 3, vSize );

		// Stop listening to incremental changes.
		graph.removeGraphListener( bg2 );

		// Remove the root.
		graph.remove( v0 );

		// Trigger 'manually' the rebuild of the graph.
		bg2.graphRebuilt();

		assertNull( "There should be not branch vertex linked to a removed vertex.",
				bg.getBranchVertex( v0, bg.vertexRef() ) );
		assertNull( "There should be not branch edge linked to a removed vertex.",
				bg.getBranchEdge( v0, bg.edgeRef() ) );
		assertNull( "There should be not branch edge linked to a removed edge.",
				bg.getBranchEdge( elistA.get( 0 ), bg.edgeRef() ) );
		assertNull( "There should be not branch edge linked to a removed edge.",
				bg.getBranchEdge( elistB.get( 0 ), bg.edgeRef() ) );

		// Basic test on N vertices and N edges.
		assertEquals( "Expected the branch graph to have 2 edges.", 2, bg2.edges().size() );
		assertEquals( "Expected the branch graph to have 4 vertices.", 4, bg2.vertices().size() );
	}

}
