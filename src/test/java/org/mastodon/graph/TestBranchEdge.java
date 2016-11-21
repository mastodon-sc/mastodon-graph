package org.mastodon.graph;

import org.mastodon.graph.AbstractBranchEdge;
import org.mastodon.pool.ByteMappedElement;

public class TestBranchEdge extends AbstractBranchEdge< TestBranchEdge, TestBranchVertex, TestBranchLinkEdge, ByteMappedElement >
{

	protected TestBranchEdge( final TestBranchEdgePool pool, final TestBranchLinkEdgePool sourceGraphEdgePool )
	{
		super( pool, sourceGraphEdgePool );
	}

	@Override
	public String toString()
	{
		final TestBranchVertex v = vertexPool.createRef();
		final StringBuilder sb = new StringBuilder();
		sb.append( "se[ " );
		getSource( v );
		sb.append( v + " -> " );
		getTarget( v );
		sb.append( v + " ] -> " );
		sb.append( getSourceGraphEdge() );
		this.vertexPool.releaseRef( v );
		return sb.toString();
	}
}