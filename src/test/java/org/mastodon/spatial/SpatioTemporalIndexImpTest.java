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
