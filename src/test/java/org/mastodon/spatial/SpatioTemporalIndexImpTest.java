package org.mastodon.spatial;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;
import org.mastodon.collection.RefCollections;
import org.mastodon.collection.RefList;
import org.mastodon.graph.TestSimpleSpatialEdge;
import org.mastodon.graph.TestSimpleSpatialGraph;
import org.mastodon.graph.TestSimpleSpatialVertex;

public class SpatioTemporalIndexImpTest
{

	private static final int N_TIMEPOINTS = 5;

	private static final int N_VERTICES = 3;

	private TestSimpleSpatialGraph graph;

	private RefList< TestSimpleSpatialVertex > vs;

	@Before
	public void setUp() throws Exception
	{
		this.graph = new TestSimpleSpatialGraph();
		final TestSimpleSpatialVertex ref = graph.vertexRef();
		this.vs = RefCollections.createRefList( graph.vertices() );
		int id = 0;
		for ( int tp = 0; tp < N_TIMEPOINTS; tp++ )
		{
			for ( int j = 0; j < N_VERTICES; j++ )
			{
				final TestSimpleSpatialVertex v = graph.addVertex( ref ).init( id++, 3 * tp, j + tp * 1.5 );
				vs.add( v );
			}
		}
		graph.releaseRef( ref );
	}

	@Test
	public void testIterator()
	{
		final SpatioTemporalIndexImp< TestSimpleSpatialVertex, TestSimpleSpatialEdge > sti = new SpatioTemporalIndexImp<>( graph, graph.getVertexPool() );
		// Create empty time-slices.
		sti.getSpatialIndex( 1 );
		sti.getSpatialIndex( 150 );

		final Iterator< TestSimpleSpatialVertex > it = sti.iterator();
		int iterated = 0;
		while ( it.hasNext() )
		{
			final TestSimpleSpatialVertex v = it.next();
			iterated++;
			assertTrue( "Iterated object  " + v + " should be in the graph vertex collection.", vs.contains( v ) );
		}
		assertEquals( "Did not iterate over all objects in graph vertex collection.", vs.size(), iterated );
	}
}
