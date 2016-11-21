package org.mastodon.graph.branch;

import org.mastodon.graph.Edge;
import org.mastodon.graph.GraphListener;
import org.mastodon.graph.ListenableGraph;
import org.mastodon.graph.Vertex;
import org.mastodon.graph.ref.GraphImp;
import org.mastodon.pool.ByteMappedElement;


public class BranchGraph< V extends Vertex< E >, E extends Edge< V > > extends GraphImp< BranchVertexPool, BranchEdgePool, BranchVertex, BranchEdge, ByteMappedElement >
{

	public BranchGraph( final ListenableGraph< V, E > graph )
	{
		super( new BranchEdgePool( graph.edges().size() / 50, new BranchVertexPool( graph.vertices().size() / 50 ) ) );
		graph.addGraphListener( new MyGraphListener() );
	}

	/*
	 * Make graph read-only.
	 */

	@Override
	public BranchEdge addEdge( final BranchVertex source, final BranchVertex target )
	{
		throw new UnsupportedOperationException( "Cannot modify a branch graph." );
	}

	@Override
	public BranchEdge addEdge( final BranchVertex source, final BranchVertex target, final BranchEdge edge )
	{
		throw new UnsupportedOperationException( "Cannot modify a branch graph." );
	}

	@Override
	public BranchVertex addVertex()
	{
		throw new UnsupportedOperationException( "Cannot modify a branch graph." );
	}

	@Override
	public BranchVertex addVertex( final BranchVertex vertex )
	{
		throw new UnsupportedOperationException( "Cannot modify a branch graph." );
	}

	@Override
	public void remove( final BranchEdge edge )
	{
		throw new UnsupportedOperationException( "Cannot modify a branch graph." );
	}

	@Override
	public void remove( final BranchVertex vertex )
	{
		throw new UnsupportedOperationException( "Cannot modify a branch graph." );
	}

	@Override
	public void removeAllLinkedEdges( final BranchVertex vertex )
	{
		throw new UnsupportedOperationException( "Cannot modify a branch graph." );
	}

	/*
	 * Graph listener.
	 */

	private class MyGraphListener implements GraphListener< V, E >
	{

		@Override
		public void graphRebuilt()
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void vertexAdded( final V vertex )
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void vertexRemoved( final V vertex )
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void edgeAdded( final E edge )
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void edgeRemoved( final E edge )
		{
			// TODO Auto-generated method stub

		}

	}


}
