package org.mastodon.graph;

import static org.mastodon.pool.ByteUtils.INDEX_SIZE;

import org.mastodon.graph.ref.AbstractEdge;
import org.mastodon.graph.ref.AbstractEdgePool;
import org.mastodon.pool.MappedElement;

public class AbstractBranchEdge< 
SE extends AbstractBranchEdge< SE, SV, E, T >, 
SV extends AbstractBranchVertex< SV, ?, ?, ? >, 
E extends AbstractEdge< E, ?, ? >, 
T extends MappedElement >
		extends AbstractEdge< SE, SV, T >
{
	protected static final int LINKED_EDGE_OFFSET = AbstractEdge.SIZE_IN_BYTES;

	protected static final int SIZE_IN_BYTES = LINKED_EDGE_OFFSET + INDEX_SIZE;

	private final AbstractEdgePool< E, ?, ? > sourceGraphEdgePool;

	protected AbstractBranchEdge( final AbstractEdgePool< SE, SV, T > pool, final AbstractEdgePool< E, ?, ? > sourceGraphEdgePool )
	{
		super( pool );
		this.sourceGraphEdgePool = sourceGraphEdgePool;
	}

	protected int getLinkedEdgeIndex()
	{
		return access.getIndex( LINKED_EDGE_OFFSET );
	}

	protected void setLinkedEdgeIndex( final int index )
	{
		access.putIndex( index, LINKED_EDGE_OFFSET );
	}

	public E getSourceGraphEdge()
	{
		return getSourceGraphEdge( sourceGraphEdgePool.createRef() );
	}

	public E getSourceGraphEdge( final E edge )
	{
		sourceGraphEdgePool.getObject( getLinkedEdgeIndex(), edge );
		return edge;
	}
}