package org.mastodon.graph.branch;

import org.mastodon.graph.ref.AbstractListenableEdge;
import org.mastodon.pool.ByteMappedElement;

public class BranchTestEdge extends AbstractListenableEdge< BranchTestEdge, BranchTestVertex, BranchTestEdgePool, ByteMappedElement >
{
	protected BranchTestEdge( final BranchTestEdgePool pool )
	{
		super( pool );
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
