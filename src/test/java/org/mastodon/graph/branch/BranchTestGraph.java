package org.mastodon.graph.branch;

import org.mastodon.graph.ListenableGraph;
import org.mastodon.graph.ListenableTestEdge;
import org.mastodon.graph.ListenableTestVertex;
import org.mastodon.pool.ByteMappedElement;

public class BranchTestGraph extends BranchGraphImp<
	ListenableTestVertex,
	ListenableTestEdge,
	BranchTestVertex,
	BranchTestEdge,
	BranchTestVertexPool,
	BranchTestEdgePool,
	ByteMappedElement >
{

	public BranchTestGraph( final ListenableGraph< ListenableTestVertex, ListenableTestEdge > graph, final BranchTestEdgePool branchEdgePool )
	{
		super( graph, branchEdgePool );
	}

	@Override
	public BranchTestVertex init( final BranchTestVertex bv, final ListenableTestVertex v )
	{
		return bv.init( v.getId(), v.getTimepoint() );
	}

	@Override
	public BranchTestEdge init( final BranchTestEdge be, final ListenableTestEdge e )
	{
		return be.init();
	}

}
