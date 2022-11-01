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
