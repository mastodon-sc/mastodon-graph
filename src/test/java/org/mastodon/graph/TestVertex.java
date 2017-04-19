package org.mastodon.graph;

import org.mastodon.graph.ref.AbstractVertex;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.attributes.IntAttributeValue;

public class TestVertex extends AbstractVertex< TestVertex, TestEdge, TestVertexPool, ByteMappedElement >
{
	private final IntAttributeValue id;

	protected TestVertex( final TestVertexPool pool )
	{
		super( pool );
		id = pool.id.createQuietAttributeValue( this );
	}

	public TestVertex init( final int id )
	{
		setId( id );
		return this;
	}

	public int getId()
	{
		return id.get();
	}

	public void setId( final int id )
	{
		this.id.set( id );
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
