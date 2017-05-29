package org.mastodon.graph.nonsimple;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.mastodon.collection.RefCollections;
import org.mastodon.collection.RefList;
import org.mastodon.graph.TestEdge;
import org.mastodon.graph.TestGraph;
import org.mastodon.graph.TestVertex;

public class NonSimpleGraphTest
{

	private TestGraph graph;

	@Before
	public void setUp() throws Exception
	{
		this.graph = new TestGraph();
	}

	@Test
	public void testSingleBranch()
	{
		final RefList< TestVertex > vlist = RefCollections.createRefList( graph.vertices() );
		for ( int i = 0; i < 5; i++ )
			vlist.add( graph.addVertex().init( i ) );
		final RefList< TestEdge > elist = RefCollections.createRefList( graph.edges() );
		final TestVertex ref1 = graph.vertexRef();
		final TestVertex ref2 = graph.vertexRef();
		final TestEdge eref = graph.edgeRef();
		for ( int i = 0; i < vlist.size() - 1; i++ )
		{
			final TestVertex source = vlist.get( i, ref1 );
			final TestVertex target = vlist.get( i + 1, ref2 );
			final TestEdge edge = graph.addEdge( source, target, eref );
			elist.add( edge );
		}
	}

	@Test
	public void testBranchY()
	{
		// Make a long branch.
		final RefList< TestVertex > vlist = RefCollections.createRefList( graph.vertices() );
		for ( int i = 0; i < 7; i++ )
			vlist.add( graph.addVertex().init( i ) );
		final RefList< TestEdge > elist = RefCollections.createRefList( graph.edges() );
		final TestVertex ref1 = graph.vertexRef();
		final TestVertex ref2 = graph.vertexRef();
		final TestEdge eref = graph.edgeRef();
		for ( int i = 0; i < vlist.size() - 1; i++ )
		{
			final TestVertex source = vlist.get( i, ref1 );
			final TestVertex target = vlist.get( i + 1, ref2 );
			final TestEdge edge = graph.addEdge( source, target, eref );
			elist.add( edge );
		}
		final TestVertex middle = vlist.get( vlist.size() / 2 );

		// Branch from its middle vertex in Y shape (merge event).
		TestVertex source = graph.addVertex().init( vlist.size() );
		TestVertex target = null;
		vlist.add( source );
		for ( int i = 1; i < 4; i++ )
		{
			target = graph.addVertex().init( vlist.size() );
			vlist.add( target );
			final TestEdge e = graph.addEdge( source, target );
			elist.add( e );
			source = target;
		}
		graph.addEdge( target, middle );
	}

	@Test
	public void testBranchingLambda()
	{
		// Make a long branch.
		final RefList< TestVertex > vlist = RefCollections.createRefList( graph.vertices() );
		for ( int i = 0; i < 7; i++ )
			vlist.add( graph.addVertex().init( i ) );
		final RefList< TestEdge > elist = RefCollections.createRefList( graph.edges() );
		final TestVertex ref1 = graph.vertexRef();
		final TestVertex ref2 = graph.vertexRef();
		final TestEdge eref = graph.edgeRef();
		for ( int i = 0; i < vlist.size() - 1; i++ )
		{
			final TestVertex source = vlist.get( i, ref1 );
			final TestVertex target = vlist.get( i + 1, ref2 );
			final TestEdge edge = graph.addEdge( source, target, eref );
			elist.add( edge );
		}

		// Branch from its middle vertex in lambda shape (split event).
		final TestVertex middle = vlist.get( vlist.size() / 2 );
		TestVertex source = middle;
		for ( int i = 0; i < vlist.size() / 2; i++ )
		{
			final TestVertex target = graph.addVertex().init( vlist.size() + i );
			vlist.add( target );
			final TestEdge e = graph.addEdge( source, target );
			elist.add( e );
			source = target;
		}
	}

	@Test
	public void testMultipleEdges()
	{
		final TestVertex s = graph.addVertex().init( 0 );
		final TestVertex t = graph.addVertex().init( 1 );

		// Add 10 edges betwen the same 2 vertices.
		final RefList< TestEdge > elist = RefCollections.createRefList( graph.edges() );
		for ( int i = 0; i < 10; i++ )
		{
			final TestEdge e = graph.addEdge( s, t );
			elist.add( e );
		}
		
		// Remove them one by one.
		for ( int i = 0; i < elist.size(); i++ )
		{
			final TestEdge e = graph.getEdge( s, t );
			assertNotNull( "There still should be at least one edge between source and target.", e );
			graph.remove( e );
		}
		assertNull( "There should be no edge left between source and target.", graph.getEdge( s, t ) );
	}

}
