package org.mastodon.graph.branch;
import static org.mastodon.pool.ByteUtils.INT_SIZE;

import org.mastodon.RefPool;
import org.mastodon.graph.ref.AbstractListenableVertex;
import org.mastodon.graph.ref.AbstractVertex;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.spatial.HasTimepoint;

import net.imglib2.RealLocalizable;

public class BranchVertex< V extends RealLocalizable & HasTimepoint >
		extends AbstractListenableVertex< BranchVertex< V >, BranchEdge< V >, ByteMappedElement >
		implements HasTimepoint, RealLocalizable
{
	protected static final int LINKED_VERTEX_ID = AbstractVertex.SIZE_IN_BYTES;
	protected static final int SIZE_IN_BYTES = LINKED_VERTEX_ID + INT_SIZE;

	private final RefPool< V > vertexBimap;

	protected BranchVertex( final BranchVertexPool< V > vertexPool, final RefPool< V > vertexBimap )
	{
		super( vertexPool );
		this.vertexBimap = vertexBimap;
	}

	@Override
	public String toString()
	{
		return "bv(" + getInternalPoolIndex() + ") t=" + getTimepoint();
	}

	protected int getLinkedVertexId()
	{
		return access.getIndex( LINKED_VERTEX_ID );
	}

	protected void setLinkedVertexId( final int id )
	{
		access.putIndex( id, LINKED_VERTEX_ID );
	}

	public BranchVertex< V > init( final V vertex )
	{
		setLinkedVertexId( vertexBimap.getId( vertex ) );
		initDone();
		return this;
	}


	@Override
	protected void setToUninitializedState() throws IllegalStateException
	{
		super.setToUninitializedState();
		setLinkedVertexId( -1 );
	}

	@Override
	public int numDimensions()
	{
		final V ref = vertexBimap.createRef();
		final int n = vertexBimap.getObject( getLinkedVertexId(), ref ).numDimensions();
		vertexBimap.releaseRef( ref );
		return n;
	}

	@Override
	public void localize( final float[] position )
	{
		final V ref = vertexBimap.createRef();
		vertexBimap.getObject( getLinkedVertexId(), ref ).localize( position );
		vertexBimap.releaseRef( ref );
	}

	@Override
	public void localize( final double[] position )
	{
		final V ref = vertexBimap.createRef();
		vertexBimap.getObject( getLinkedVertexId(), ref ).localize( position );
		vertexBimap.releaseRef( ref );
	}

	@Override
	public float getFloatPosition( final int d )
	{
		final V ref = vertexBimap.createRef();
		final float x = vertexBimap.getObject( getLinkedVertexId(), ref ).getFloatPosition( d );
		vertexBimap.releaseRef( ref );
		return x;
	}

	@Override
	public double getDoublePosition( final int d )
	{
		final V ref = vertexBimap.createRef();
		final double x = vertexBimap.getObject( getLinkedVertexId(), ref ).getDoublePosition( d );
		vertexBimap.releaseRef( ref );
		return x;
	}

	@Override
	public int getTimepoint()
	{
		final V ref = vertexBimap.createRef();
		final int t = vertexBimap.getObject( getLinkedVertexId(), ref ).getTimepoint();
		vertexBimap.releaseRef( ref );
		return t;
	}
}