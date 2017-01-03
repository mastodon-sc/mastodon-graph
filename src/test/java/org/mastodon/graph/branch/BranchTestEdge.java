package org.mastodon.graph.branch;

import org.mastodon.graph.ref.AbstractListenableEdge;
import org.mastodon.graph.ref.AbstractListenableEdgePool;
import org.mastodon.pool.ByteMappedElement;

public class BranchTestEdge extends AbstractListenableEdge< BranchTestEdge, BranchTestVertex, ByteMappedElement >
{
	protected static final int SIZE_IN_BYTES = AbstractListenableEdge.SIZE_IN_BYTES;

	public final AbstractListenableEdgePool< BranchTestEdge, BranchTestVertex, ByteMappedElement > creatingPool;

	protected BranchTestEdge( final AbstractListenableEdgePool< BranchTestEdge, BranchTestVertex, ByteMappedElement > pool )
	{
		super( pool );
		creatingPool = pool;
	}

	public BranchTestEdge init()
	{
		initDone();
		return this;
	}

	@Override
	public String toString()
	{
		final BranchTestVertex v = this.vertexPool.createRef();
		final StringBuilder sb = new StringBuilder();
		sb.append( "be(" );
		getSource( v );
		sb.append( v.getId() );
		sb.append( " -> " );
		getTarget( v );
		sb.append( v.getId() );
		sb.append( ")" );
		this.vertexPool.releaseRef( v );
		return sb.toString();
	}
}
