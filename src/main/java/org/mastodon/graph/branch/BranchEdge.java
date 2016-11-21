package org.mastodon.graph.branch;

import static org.mastodon.pool.ByteUtils.INDEX_SIZE;

import org.mastodon.graph.ref.AbstractEdge;
import org.mastodon.pool.ByteMappedElement;

public class BranchEdge extends AbstractEdge< BranchEdge, BranchVertex, ByteMappedElement >
{
	protected static final int LINKED_EDGE_OFFSET = AbstractEdge.SIZE_IN_BYTES;

	protected static final int SIZE_IN_BYTES = LINKED_EDGE_OFFSET + INDEX_SIZE;

	protected BranchEdge( final BranchEdgePool pool )
	{
		super( pool );
	}

	protected int getLinkedEdgeId()
	{
		return access.getIndex( LINKED_EDGE_OFFSET );
	}

	protected void setLinkedEdgeId( final int id )
	{
		access.putIndex( id, LINKED_EDGE_OFFSET );
	}

	@Override
	public String toString()
	{
		return "be->id=" + getLinkedEdgeId();
	}
}