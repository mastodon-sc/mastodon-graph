package org.mastodon.graph.branch;

import static org.mastodon.pool.ByteUtils.INT_SIZE;

import org.mastodon.graph.ref.AbstractListenableVertex;
import org.mastodon.graph.ref.AbstractVertex;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.spatial.HasTimepoint;

public class BranchVertex
		extends AbstractListenableVertex< BranchVertex, BranchEdge, ByteMappedElement >
		implements HasTimepoint
{
	protected static final int TIMEPOINT_OFFSET = AbstractVertex.SIZE_IN_BYTES;
	protected static final int SIZE_IN_BYTES = TIMEPOINT_OFFSET + INT_SIZE;

	protected BranchVertex( final BranchVertexPool pool )
	{
		super( pool );
	}

	@Override
	public String toString()
	{
		return "bv(" + getInternalPoolIndex() + ") t=" + getTimepoint();
	}

	public BranchVertex init( final int timePoint )
	{
		setTimepointInternal( timePoint );
		initDone();
		return this;
	}

	private void setTimepointInternal( final int tp )
	{
		access.putInt( tp, TIMEPOINT_OFFSET );
	}

	@Override
	public int getTimepoint()
	{
		return access.getInt( TIMEPOINT_OFFSET );
	}
}