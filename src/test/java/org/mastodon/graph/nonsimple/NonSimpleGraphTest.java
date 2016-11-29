package org.mastodon.graph.nonsimple;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.mastodon.collection.RefCollections;
import org.mastodon.collection.RefList;
import org.mastodon.graph.TestNonSimpleEdge;
import org.mastodon.graph.TestNonSimpleGraph;
import org.mastodon.graph.TestNonSimpleVertex;

public class NonSimpleGraphTest
{

	private TestNonSimpleGraph graph;

	@Before
	public void setUp() throws Exception
	{
		this.graph = new TestNonSimpleGraph();
	}

	@Test
	public void testSingleBranch()
	{
		final RefList< TestNonSimpleVertex > vlist = RefCollections.createRefList( graph.vertices() );
		for ( int i = 0; i < 5; i++ )
			vlist.add( graph.addVertex().init( i ) );
		final RefList< TestNonSimpleEdge > elist = RefCollections.createRefList( graph.edges() );
		final TestNonSimpleVertex ref1 = graph.vertexRef();
		final TestNonSimpleVertex ref2 = graph.vertexRef();
		final TestNonSimpleEdge eref = graph.edgeRef();
		for ( int i = 0; i < vlist.size() - 1; i++ )
		{
			final TestNonSimpleVertex source = vlist.get( i, ref1 );
			final TestNonSimpleVertex target = vlist.get( i + 1, ref2 );
			final TestNonSimpleEdge edge = graph.addEdge( source, target, eref );
			elist.add( edge );
		}
	}

	@Test
	public void testBranchY()
	{
		// Make a long branch.
		final RefList< TestNonSimpleVertex > vlist = RefCollections.createRefList( graph.vertices() );
		for ( int i = 0; i < 7; i++ )
			vlist.add( graph.addVertex().init( i ) );
		final RefList< TestNonSimpleEdge > elist = RefCollections.createRefList( graph.edges() );
		final TestNonSimpleVertex ref1 = graph.vertexRef();
		final TestNonSimpleVertex ref2 = graph.vertexRef();
		final TestNonSimpleEdge eref = graph.edgeRef();
		for ( int i = 0; i < vlist.size() - 1; i++ )
		{
			final TestNonSimpleVertex source = vlist.get( i, ref1 );
			final TestNonSimpleVertex target = vlist.get( i + 1, ref2 );
			final TestNonSimpleEdge edge = graph.addEdge( source, target, eref );
			elist.add( edge );
		}
		final TestNonSimpleVertex middle = vlist.get( vlist.size() / 2 );

		// Branch from its middle vertex in Y shape (merge event).
		TestNonSimpleVertex source = graph.addVertex().init( vlist.size() );
		TestNonSimpleVertex target = null;
		vlist.add( source );
		for ( int i = 1; i < 4; i++ )
		{
			target = graph.addVertex().init( vlist.size() );
			vlist.add( target );
			final TestNonSimpleEdge e = graph.addEdge( source, target );
			elist.add( e );
			source = target;
		}
		graph.addEdge( target, middle );
	}

	@Test
	public void testBranchingLambda()
	{
		// Make a long branch.
		final RefList< TestNonSimpleVertex > vlist = RefCollections.createRefList( graph.vertices() );
		for ( int i = 0; i < 7; i++ )
			vlist.add( graph.addVertex().init( i ) );
		final RefList< TestNonSimpleEdge > elist = RefCollections.createRefList( graph.edges() );
		final TestNonSimpleVertex ref1 = graph.vertexRef();
		final TestNonSimpleVertex ref2 = graph.vertexRef();
		final TestNonSimpleEdge eref = graph.edgeRef();
		for ( int i = 0; i < vlist.size() - 1; i++ )
		{
			final TestNonSimpleVertex source = vlist.get( i, ref1 );
			final TestNonSimpleVertex target = vlist.get( i + 1, ref2 );
			final TestNonSimpleEdge edge = graph.addEdge( source, target, eref );
			elist.add( edge );
		}

		// Branch from its middle vertex in lambda shape (split event).
		final TestNonSimpleVertex middle = vlist.get( vlist.size() / 2 );
		TestNonSimpleVertex source = middle;
		for ( int i = 0; i < vlist.size() / 2; i++ )
		{
			final TestNonSimpleVertex target = graph.addVertex().init( vlist.size() + i );
			vlist.add( target );
			final TestNonSimpleEdge e = graph.addEdge( source, target );
			elist.add( e );
			source = target;
		}
	}

	@Test
	public void testMultipleEdges()
	{
		final TestNonSimpleVertex s = graph.addVertex().init( 0 );
		final TestNonSimpleVertex t = graph.addVertex().init( 1 );

		// Add 10 edges betwen the same 2 vertices.
		final RefList< TestNonSimpleEdge > elist = RefCollections.createRefList( graph.edges() );
		for ( int i = 0; i < 10; i++ )
		{
			final TestNonSimpleEdge e = graph.addEdge( s, t );
			elist.add( e );
		}
		
		// Remove them one by one.
		for ( int i = 0; i < elist.size(); i++ )
		{
			final TestNonSimpleEdge e = graph.getEdge( s, t );
			assertNotNull( "There still should be at least one edge between source and target.", e );
			graph.remove( e );
		}
		assertNull( "There should be no edge left between source and target.", graph.getEdge( s, t ) );
	}

}
