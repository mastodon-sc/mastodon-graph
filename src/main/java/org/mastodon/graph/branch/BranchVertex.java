package org.mastodon.graph.branch;

import static org.mastodon.pool.ByteUtils.INDEX_SIZE;

import org.mastodon.graph.ref.AbstractVertex;
import org.mastodon.pool.ByteMappedElement;

public class BranchVertex extends AbstractVertex< BranchVertex, BranchEdge, ByteMappedElement >
{
	protected static final int TIMEPOINT_OFFSET = AbstractVertex.SIZE_IN_BYTES;

	protected static final int LINKED_VERTEX_OFFSET = AbstractVertex.SIZE_IN_BYTES;

	protected static final int SIZE_IN_BYTES = LINKED_VERTEX_OFFSET + INDEX_SIZE;

	protected BranchVertex( final BranchVertexPool pool )
	{
		super( pool );
	}

	protected int getLinkedVertexId()
	{
		return access.getIndex( LINKED_VERTEX_OFFSET );
	}

	protected void setLinkedVertexId( final int id )
	{
		access.putIndex( id, LINKED_VERTEX_OFFSET );
	}

	@Override
	public String toString()
	{
		return "bv(" + getInternalPoolIndex() + ") -> id=" + getLinkedVertexId();
	}
}