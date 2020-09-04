package org.mastodon.graph;

import org.mastodon.graph.ref.AbstractListenableVertex;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.attributes.DoubleAttributeValue;
import org.mastodon.pool.attributes.IntAttributeValue;
import org.mastodon.spatial.HasTimepoint;

import net.imglib2.RealLocalizable;

public class TestSimpleSpatialVertex extends AbstractListenableVertex< TestSimpleSpatialVertex, TestSimpleSpatialEdge, TestSimpleSpatialVertexPool, ByteMappedElement > implements HasTimepoint, RealLocalizable
{

	private final IntAttributeValue id;

	private final IntAttributeValue timepoint;

	private final DoubleAttributeValue pos;

	protected TestSimpleSpatialVertex( final TestSimpleSpatialVertexPool pool )
	{
		super( pool );
		this.id = pool.id.createQuietAttributeValue( this );
		this.timepoint = pool.timepoint.createQuietAttributeValue( this );
		this.pos = pool.pos.createQuietAttributeValue( this );
	}

	public TestSimpleSpatialVertex init(final int id, final int timepoint, final double pos)
	{
		setId(id);
		setTimepoint(timepoint);
		setPosition(pos);
		initDone();
		return this;
	}

	public void setPosition( final double pos )
	{
		this.pos.set( pos );
	}

	public void setTimepoint( final int timepoint )
	{
		this.timepoint.set( timepoint );
	}

	public void setId( final int id )
	{
		this.id.set( id );
	}

	public int getId()
	{
		return id.get();
	}

	@Override
	public int numDimensions()
	{
		return 1;
	}

	@Override
	public void localize( final float[] position )
	{
		position[ 0 ] = ( float ) pos.get();
	}

	@Override
	public void localize( final double[] position )
	{
		position[ 0 ] = pos.get();
	}

	@Override
	public float getFloatPosition( final int d )
	{
		return ( float ) pos.get();
	}

	@Override
	public double getDoublePosition( final int d )
	{
		return pos.get();
	}

	@Override
	public int getTimepoint()
	{
		return timepoint.get();
	}

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append( "v(" );
		sb.append( getId() );
		sb.append( ", t = " + getTimepoint() );
		sb.append( String.format( ", x = %.1f", getDoublePosition( 0 ) ) );
		sb.append( ")" );
		return sb.toString();
	}
}
