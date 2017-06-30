package org.mastodon.graph.ref;

import org.mastodon.pool.MappedElement;
import org.mastodon.pool.MemPool;
import org.mastodon.pool.Pool;
import org.mastodon.pool.PoolObjectLayout;

public abstract class AbstractVertexPool<
			V extends AbstractVertex< V, E, ?, T >,
			E extends AbstractEdge< E, ?, ?, ? >,
			T extends MappedElement >
		extends Pool< V, T >
{
	// TODO make it private again when we do not need this anymore.
	protected AbstractEdgePool< E, ?, ? > edgePool;

	public static class AbstractVertexLayout extends PoolObjectLayout
	{
		final IndexField firstInEdge = indexField();
		final IndexField firstOutEdge = indexField();
	}

	public static AbstractVertexLayout layout = new AbstractVertexLayout();

	public AbstractVertexPool(
			final int initialCapacity,
			final AbstractVertexLayout layout,
			final Class< V > vertexClass,
			final MemPool.Factory< T > memPoolFactory )
	{
		super( initialCapacity, layout, vertexClass, memPoolFactory );
	}

	public void linkEdgePool( final AbstractEdgePool< E, ?, ? > edgePool )
	{
		this.edgePool = edgePool;
	}

	@Override
	public V createRef()
	{
		final V vertex = super.createRef();
		if ( edgePool != null )
			vertex.linkEdgePool( edgePool );
		return vertex;
	}

	@Override
	public V create( final V vertex )
	{
		return super.create( vertex );
	}

	@Override
	public void delete( final V vertex )
	{
		if ( edgePool != null )
			edgePool.deleteAllLinkedEdges( vertex );
		super.delete( vertex );
	}
}
