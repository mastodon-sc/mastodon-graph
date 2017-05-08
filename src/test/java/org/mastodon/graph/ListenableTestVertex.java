package org.mastodon.graph;

import org.mastodon.graph.ref.AbstractListenableVertex;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.attributes.IntAttributeValue;
import org.mastodon.spatial.HasTimepoint;

public class ListenableTestVertex
		extends AbstractListenableVertex< ListenableTestVertex, ListenableTestEdge, ListenableTestVertexPool, ByteMappedElement >
		implements HasTimepoint
{

	private final IntAttributeValue id;

	private final IntAttributeValue timepoint;

	protected ListenableTestVertex( final ListenableTestVertexPool pool )
	{
		super( pool );
		id = pool.id.createQuietAttributeValue( this );
		timepoint = pool.timepoint.createQuietAttributeValue( this );
	}

	public ListenableTestVertex init( final int id, final int tp )
	{
		setId( id );
		setTimepointInternal( tp );
		initDone();
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
	public int getTimepoint()
	{
		return timepoint.get();
	}

	private void setTimepointInternal( final int tp )
	{
		timepoint.set( tp );
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
