package org.mastodon.graph;

import static org.mastodon.pool.ByteUtils.INDEX_SIZE;

import org.mastodon.graph.ref.AbstractVertex;
import org.mastodon.graph.ref.AbstractVertexPool;
import org.mastodon.pool.MappedElement;

public class AbstractBranchVertex< 
SV extends AbstractBranchVertex< SV, SE, V, T >, 
SE extends AbstractBranchEdge< SE, ?, ?, ? >, 
V extends AbstractVertex< V, ?, ? >, 
T extends MappedElement >
		extends AbstractVertex< SV, SE, T >
{
	protected static final int LINKED_VERTEX_OFFSET = AbstractVertex.SIZE_IN_BYTES;

	protected static final int SIZE_IN_BYTES = LINKED_VERTEX_OFFSET + INDEX_SIZE;

	private final AbstractVertexPool< V, ?, ? > sourceGraphVertexPool;

	protected AbstractBranchVertex( final AbstractVertexPool< SV, ?, T > pool, final AbstractVertexPool< V, ?, ? > sourceGraphVertexPool )
	{
		super( pool );
		this.sourceGraphVertexPool = sourceGraphVertexPool;
	}

	protected int getLinkedVertexIndex()
	{
		return access.getIndex( LINKED_VERTEX_OFFSET );
	}

	protected void setLinkedVertexIndex( final int index )
	{
		access.putIndex( index, LINKED_VERTEX_OFFSET );
	}

	public V getSourceGraphVertex()
	{
		return getSourceGraphVertex( sourceGraphVertexPool.createRef() );
	}

	public V getSourceGraphVertex( final V vertex )
	{
		sourceGraphVertexPool.getObject( getLinkedVertexIndex(), vertex );
		return vertex;
	}
}