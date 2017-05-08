package org.mastodon.graph.branch;

import org.mastodon.graph.ref.AbstractEdgePool;
import org.mastodon.graph.ref.AbstractListenableEdgePool;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.ByteMappedElementArray;
import org.mastodon.pool.SingleArrayMemPool;

public class BranchTestEdgePool extends AbstractListenableEdgePool< BranchTestEdge, BranchTestVertex, ByteMappedElement >
{
	public BranchTestEdgePool( final int initialCapacity, final BranchTestVertexPool vertexPool )
	{
		super( initialCapacity, AbstractEdgePool.layout, BranchTestEdge.class, SingleArrayMemPool.factory( ByteMappedElementArray.factory ), vertexPool );
	}

	@Override
	protected BranchTestEdge createEmptyRef()
	{
		return new BranchTestEdge( this );
	}
}
