package org.mastodon.graph.branch;

import org.mastodon.graph.ref.AbstractEdge;
import org.mastodon.graph.ref.AbstractListenableEdge;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.spatial.HasTimepoint;

import net.imglib2.RealLocalizable;

public class BranchEdge< V extends RealLocalizable & HasTimepoint >
		extends AbstractListenableEdge< BranchEdge< V >, BranchVertex< V >, ByteMappedElement >
{
	protected static final int SIZE_IN_BYTES = AbstractEdge.SIZE_IN_BYTES;

	protected BranchEdge( final BranchEdgePool< V > pool )
	{
		super( pool );
	}

	public BranchEdge< V > init()
	{
		initDone();
		return this;
	}

	@Override
	public String toString()
	{
		return "be(" + getInternalPoolIndex() + ")";
	}
}