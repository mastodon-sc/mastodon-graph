package org.mastodon.graph.branch;

import org.mastodon.graph.ref.AbstractEdge;
import org.mastodon.pool.ByteMappedElement;

public class BranchEdge extends AbstractEdge< BranchEdge, BranchVertex, ByteMappedElement >
{
	protected static final int SIZE_IN_BYTES = AbstractEdge.SIZE_IN_BYTES;

	protected BranchEdge( final BranchEdgePool pool )
	{
		super( pool );
	}

	@Override
	public String toString()
	{
		return "be(" + getInternalPoolIndex() + ")";
	}
}