/*-
 * #%L
 * Mastodon Graphs
 * %%
 * Copyright (C) 2015 - 2025 Tobias Pietzsch, Jean-Yves Tinevez
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

import org.junit.Test;
import org.mastodon.graph.TestSimpleSpatialEdge;
import org.mastodon.graph.TestSimpleSpatialGraph;
import org.mastodon.graph.TestSimpleSpatialVertex;

/**
 * Tests {@link SpatioTemporalIndexImpRebuilderThread}.
 */
public class SpatioTemporalIndexImpRebuilderThreadTest
{

	/**
	 * Test that the {@link SpatioTemporalIndexImpRebuilderThread} allows the
	 * garbage collection of the {@link SpatioTemporalIndexImp} and the
	 * associated graph.
	 */
	@Test
	public void testGarbageCollection()
	{
		// NB: This test creates many "largeObject"s. The test fails with
		// an OutOfMemoryException if the objects can not be garbage collected.

		long totalMemory = Runtime.getRuntime().totalMemory();
		int largeObjectSize = (int) Math.min( Integer.MAX_VALUE - 20, totalMemory / 4 );

		// How many large objects do we need to cause an OutOfMemoryException?
		long n = 2 * totalMemory / largeObjectSize;

		for ( int i = 0; i < n; i++ )
		{
			TestSimpleSpatialGraph graph = new TestSimpleSpatialGraph() {
				private final Object largeObject = new byte[ largeObjectSize ];
			};
			SpatioTemporalIndexImp< TestSimpleSpatialVertex, TestSimpleSpatialEdge > stIndex = new SpatioTemporalIndexImp<>( graph, graph.getVertexPool() );
			new SpatioTemporalIndexImpRebuilderThread( "spatial-temporal-index-rebuild-thread", stIndex, 100, 1000, true ).start();
		}
	}
}
