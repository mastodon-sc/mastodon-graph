package org.mastodon.graph;

import static org.mastodon.pool.ByteUtils.INT_SIZE;

import org.mastodon.graph.ref.AbstractListenableVertex;
import org.mastodon.graph.ref.AbstractVertexPool;
import org.mastodon.pool.ByteMappedElement;

public class ListenableTestVertex extends AbstractListenableVertex< ListenableTestVertex, ListenableTestEdge, ByteMappedElement >
{

	protected static final int ID_OFFSET = AbstractListenableVertex.SIZE_IN_BYTES;

	protected static final int SIZE_IN_BYTES = ID_OFFSET + INT_SIZE;

	public final AbstractVertexPool< ListenableTestVertex, ?, ByteMappedElement > creatingPool;

	protected ListenableTestVertex( final AbstractVertexPool< ListenableTestVertex, ?, ByteMappedElement > pool )
	{
		super( pool );
		creatingPool = pool;
	}

	public ListenableTestVertex init( final int id )
	{
		setId( id );
		initDone();
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
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append( "lv(" );
		sb.append( getId() );
		sb.append( ")" );
		return sb.toString();
	}
}
