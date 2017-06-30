package org.mastodon.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Test;
import org.mastodon.graph.ref.AllEdges;
import org.mastodon.graph.ref.IncomingEdges;
import org.mastodon.graph.ref.OutgoingEdges;

public class EdgeCollectionsTest
{

	@Test( expected = NoSuchElementException.class )
	public void testOugoingEdges()
	{
		final TestGraph graph = new TestGraph();
		final TestVertex ref0 = graph.vertexRef();
		final TestVertex ref1 = graph.vertexRef();
		final TestEdge edgeRef = graph.edgeRef();

		final int nTargets = 10;

		final TestVertex v0 = graph.addVertex( ref0 ).init( 0 );
		for ( int i = 0; i < nTargets; i++ )
		{
			final TestVertex vi = graph.addVertex( ref1 ).init( i + 1 );
			graph.addEdge( v0, vi, edgeRef );
		}

		final OutgoingEdges< TestEdge > edges = v0.outgoingEdges();
		assertEquals( "Outgoing edges do not have the expected size.", nTargets, edges.size() );

		// Remove them all.
		for ( final TestEdge edge : edges )
			graph.remove( edge );

		assertEquals( "Outgoing edges should be of size 0.", 0, edges.size() );
		assertTrue( "Outgoing edges should be empty.", edges.isEmpty() );

		final Iterator< TestEdge > it = edges.iterator();
		assertFalse( "Edges iterator should not have a next element.", it.hasNext() );

		// Should trigger NoSuchElementException.
		it.next();

		graph.releaseRef( ref0 );
		graph.releaseRef( ref1 );
		graph.releaseRef( edgeRef );
	}

	@Test( expected = NoSuchElementException.class )
	public void testIncomingEdges()
	{
		final TestGraph graph = new TestGraph();
		final TestVertex ref0 = graph.vertexRef();
		final TestVertex ref1 = graph.vertexRef();
		final TestEdge edgeRef = graph.edgeRef();

		final int nTargets = 10;

		final TestVertex v0 = graph.addVertex( ref0 ).init( 0 );
		for ( int i = 0; i < nTargets; i++ )
		{
			final TestVertex vi = graph.addVertex( ref1 ).init( i + 1 );
			graph.addEdge( vi, v0, edgeRef );
		}

		final IncomingEdges< TestEdge > edges = v0.incomingEdges();
		assertEquals( "Incoming edges do not have the expected size.", nTargets, edges.size() );

		// Remove them all.
		for ( final TestEdge edge : edges )
			graph.remove( edge );

		assertEquals( "Incoming edges should be of size 0.", 0, edges.size() );
		assertTrue( "Incoming edges should be empty.", edges.isEmpty() );

		final Iterator< TestEdge > it = edges.iterator();
		assertFalse( "Edges iterator should not have a next element.", it.hasNext() );

		// Should trigger NoSuchElementException.
		it.next();

		graph.releaseRef( ref0 );
		graph.releaseRef( ref1 );
		graph.releaseRef( edgeRef );
	}

	@Test( expected = NoSuchElementException.class )
	public void testEdges()
	{
		final TestGraph graph = new TestGraph();
		final TestVertex ref0 = graph.vertexRef();
		final TestVertex ref1 = graph.vertexRef();
		final TestEdge edgeRef = graph.edgeRef();

		final int nTargets = 10;

		final TestVertex v0 = graph.addVertex( ref0 ).init( 0 );
		for ( int i = 0; i < nTargets; i++ )
		{
			final TestVertex vi = graph.addVertex( ref1 ).init( i + 1 );
			graph.addEdge( vi, v0, edgeRef );
			final TestVertex vo = graph.addVertex( ref1 ).init( nTargets + i + 1 );
			graph.addEdge( v0, vo, edgeRef );
		}

		final AllEdges< TestEdge > edges = v0.edges();
		assertEquals( "Edges do not have the expected size.", 2 * nTargets, edges.size() );

		// Remove them all.
		for ( final TestEdge edge : edges )
			graph.remove( edge );

		assertEquals( "Edges should be of size 0.", 0, edges.size() );
		assertTrue( "Edges should be empty.", edges.isEmpty() );

		final Iterator< TestEdge > it = edges.iterator();
		assertFalse( "Edges iterator should not have a next element.", it.hasNext() );

		// Should trigger NoSuchElementException.
		it.next();

		graph.releaseRef( ref0 );
		graph.releaseRef( ref1 );
		graph.releaseRef( edgeRef );
	}

	@Test( expected = NoSuchElementException.class )
	public void testNormalIterator()
	{
		final Iterator< Object > it = Collections.emptyList().iterator();
		it.next();
	}
}
