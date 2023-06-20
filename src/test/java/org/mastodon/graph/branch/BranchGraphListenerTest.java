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
package org.mastodon.graph.branch;

import org.junit.Test;
import org.mastodon.graph.GraphChangeListener;
import org.mastodon.graph.GraphListener;
import org.mastodon.graph.ListenableTestGraph;

import java.util.StringJoiner;

import static org.junit.Assert.assertEquals;

/**
 * Test if {@link BranchGraphImp} calls listener events correctly.
 */
public class BranchGraphListenerTest
{

	/**
	 * {@link BranchGraphImp#graphRebuilt()} is expected to call
	 * {@link GraphListener#graphRebuilt()} and
	 * {@link GraphChangeListener#graphChanged()} once at the end.
	 * The other methods of the {@link GraphListener} like for example
	 * {@link GraphListener#vertexRemoved}  should not be called.
	 */
	@Test
	public void testGraphRebuilt() {
		// setup
		ListenableTestGraph graph = TestGraphBuilder.build( "0->1->2->3, 1->4" );
		BranchTestGraph branchGraph = new BranchTestGraph( graph );
		Listener listener = new Listener();
		branchGraph.addGraphListener( listener );
		branchGraph.addGraphChangeListener( listener );
		// process
		branchGraph.graphRebuilt();
		branchGraph.graphRebuilt();
		// test
		String expected = "graph rebuilt, graph changed, graph rebuilt, graph changed";
		assertEquals( expected, listener.toString());
	}

	private static class Listener
			implements GraphListener<BranchTestVertex, BranchTestEdge>,
			GraphChangeListener
	{

		private final StringJoiner log = new StringJoiner( ", " );

		@Override
		public void graphRebuilt()
		{
			log.add("graph rebuilt");
		}

		@Override
		public void vertexAdded( BranchTestVertex vertex )
		{
			log.add("vertex added");
		}

		@Override
		public void vertexRemoved( BranchTestVertex vertex )
		{
			log.add("vertex removed" );
		}

		@Override
		public void edgeAdded( BranchTestEdge edge )
		{
			log.add("edge added");
		}

		@Override
		public void edgeRemoved( BranchTestEdge edge )
		{
			log.add("edge removed");
		}

		@Override
		public void graphChanged()
		{
			log.add("graph changed");
		}

		@Override
		public String toString()
		{
			return log.toString();
		}
	}
}
