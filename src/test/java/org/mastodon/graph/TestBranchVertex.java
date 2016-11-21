package org.mastodon.graph;

import static org.mastodon.pool.ByteUtils.INT_SIZE;

import org.mastodon.graph.AbstractBranchVertex;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.spatial.HasTimepoint;

public class TestBranchVertex extends AbstractBranchVertex< TestBranchVertex, TestBranchEdge, TestBranchLinkVertex, ByteMappedElement > implements HasTimepoint
{
	protected static final int TIMEPOINT_OFFSET = AbstractBranchVertex.SIZE_IN_BYTES;

	protected static final int SIZE_IN_BYTES = TIMEPOINT_OFFSET + INT_SIZE;

	protected TestBranchVertex( final TestBranchVertexPool pool, final TestBranchLinkVertexPool sourceGraphVertexPool )
	{
		super( pool, sourceGraphVertexPool );
	}

	public TestBranchVertex init( final int timepoint )
	{
		setTimePoint( timepoint );
		return this;
	}

	@Override
	public String toString()
	{
		return "sv-" + getSourceGraphVertex().toString();
	}

	@Override
	public int getTimepoint()
	{
		return access.getInt( TIMEPOINT_OFFSET );
	}

	protected void setTimePoint( final int timepoint )
	{
		access.putInt( timepoint, TIMEPOINT_OFFSET );
	}

}