package org.mastodon.graph;

import org.mastodon.graph.ref.AbstractEdge;
import org.mastodon.graph.ref.AbstractEdgePool;
import org.mastodon.pool.ByteMappedElement;

public class TestBranchLinkEdge extends AbstractEdge< TestBranchLinkEdge, TestBranchLinkVertex, ByteMappedElement >
{

	protected static final int SIZE_IN_BYTES = AbstractEdge.SIZE_IN_BYTES;

	protected TestBranchLinkEdge( final AbstractEdgePool< TestBranchLinkEdge, TestBranchLinkVertex, ByteMappedElement > pool )
	{
		super( pool );
	}

	@Override
	public String toString()
	{
		final TestBranchLinkVertex v = this.vertexPool.createRef();
		final StringBuilder sb = new StringBuilder();
		sb.append( "e(" );
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