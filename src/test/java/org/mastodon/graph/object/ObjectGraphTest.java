/*-
 * #%L
 * Mastodon Graphs
 * %%
 * Copyright (C) 2015 - 2023 Tobias Pietzsch, Jean-Yves Tinevez
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
package org.mastodon.graph.object;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ObjectGraphTest
{

	@Test
	public void testEdgePresence()
	{
		final ObjectGraph< Integer > graph = new ObjectGraph<>();

		final ObjectVertex< Integer > v0 = graph.addVertex().init( 0 );
		final ObjectVertex< Integer > v1 = graph.addVertex().init( 1 );
		final ObjectEdge< Integer > e = graph.addEdge( v0, v1 );

		assertTrue( "Incoming edges for vertex " + v0 + " should be empty.", v0.incomingEdges().isEmpty() );

		assertEquals( "Outgoing edges for vertex " + v0 + " does not have the expected size.", 1, v0.outgoingEdges().size() );
		assertEquals( "Unexpected outgoing edge.", e, v0.outgoingEdges().iterator().next() );

		assertEquals( "All edges for vertex " + v0 + " does not have the expected size.", 1, v0.edges().size() );
		assertEquals( "Unexpected edge.", e, v0.edges().iterator().next() );

		assertEquals( "Incoming edges for vertex " + v1 + " does not have the expected size.", 1, v1.incomingEdges().size() );
		assertEquals( "Unexpected incoming edge.", e, v1.incomingEdges().iterator().next() );

		assertTrue( "Outgoing edges for vertex " + v1 + " should be empty.", v1.outgoingEdges().isEmpty() );

		assertEquals( "All edges for vertex " + v1 + " does not have the expected size.", 1, v1.edges().size() );
		assertEquals( "Unexpected edge.", e, v1.edges().iterator().next() );

	}

}
