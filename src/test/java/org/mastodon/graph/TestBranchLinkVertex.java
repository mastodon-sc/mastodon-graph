package org.mastodon.graph;

import static org.mastodon.pool.ByteUtils.INT_SIZE;

import org.mastodon.pool.ByteMappedElement;
import org.mastodon.spatial.HasTimepoint;

public class TestBranchLinkVertex extends AbstractBranchLinkVertex< TestBranchLinkVertex, TestBranchVertex, TestBranchEdge, TestBranchLinkEdge, ByteMappedElement > implements HasTimepoint
{
	protected static final int ID_OFFSET = AbstractBranchLinkVertex.SIZE_IN_BYTES;

	protected static final int TIMEPOINT_OFFSET = ID_OFFSET + INT_SIZE;

	protected static final int SIZE_IN_BYTES = TIMEPOINT_OFFSET + INT_SIZE;

	protected TestBranchLinkVertex( final TestBranchLinkVertexPool pool, final TestBranchVertexPool skeletonVertexPool, final TestBranchEdgePool skeletonEdgePool )
	{
		super( pool, skeletonVertexPool, skeletonEdgePool );
	}

	public TestBranchLinkVertex init( final int id, final int timepoint )
	{
		setId( id );
		setTimePoint( timepoint );
		if ( isBranchGraphVertex() )
		{
			getBranchGraphVertex().setTimePoint( timepoint );
		}
		return this;
	}

	public int getId()
	{
		return access.getInt( ID_OFFSET );
	}

	public void setId( final int id )
	{
		access.putInt( id, ID_OFFSET );
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

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append( "v(" );
		sb.append( getId() );
		sb.append( ")" );
		return sb.toString();
	}
}