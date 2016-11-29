package org.mastodon.graph;

import static org.mastodon.pool.ByteUtils.INT_SIZE;

import org.mastodon.graph.ref.AbstractVertex;
import org.mastodon.graph.ref.AbstractVertexPool;
import org.mastodon.pool.ByteMappedElement;

public class TestNonSimpleVertex extends AbstractVertex< TestNonSimpleVertex, TestNonSimpleEdge, ByteMappedElement >
{
	protected static final int ID_OFFSET = AbstractVertex.SIZE_IN_BYTES;

	protected static final int SIZE_IN_BYTES = ID_OFFSET + INT_SIZE;

	public final AbstractVertexPool< TestNonSimpleVertex, ?, ByteMappedElement > creatingPool;

	protected TestNonSimpleVertex( final AbstractVertexPool< TestNonSimpleVertex, ?, ByteMappedElement > pool )
	{
		super( pool );
		creatingPool = pool;
	}

	public TestNonSimpleVertex init( final int id )
	{
		setId( id );
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
		sb.append( "nsv(" );
		sb.append( getId() );
		sb.append( ")" );
		return sb.toString();
	}
}