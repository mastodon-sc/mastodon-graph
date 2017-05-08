package org.mastodon.graph.branch;

import org.mastodon.graph.ref.AbstractListenableVertex;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.attributes.IntAttributeValue;
import org.mastodon.spatial.HasTimepoint;

public class BranchTestVertex
		extends AbstractListenableVertex< BranchTestVertex, BranchTestEdge, BranchTestVertexPool, ByteMappedElement >
		implements HasTimepoint
{

	private final IntAttributeValue id;

	private final IntAttributeValue timepoint;

	protected BranchTestVertex( final BranchTestVertexPool pool )
	{
		super( pool );
		id = pool.id.createQuietAttributeValue( this );
		timepoint = pool.timepoint.createQuietAttributeValue( this );
	}

	public BranchTestVertex init( final int id, final int tp )
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
		return this.timepoint.get();
	}

	private void setTimepointInternal( final int tp )
	{
		this.timepoint.set( tp );
	}

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append( "bv(" );
		sb.append( getId() );
		sb.append( ")" );
		return sb.toString();
	}

}
