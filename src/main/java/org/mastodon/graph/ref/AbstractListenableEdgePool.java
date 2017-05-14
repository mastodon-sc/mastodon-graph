package org.mastodon.graph.ref;

import org.mastodon.graph.ref.AbstractSimpleEdgePool.AbstractEdgeLayout;
import org.mastodon.pool.MappedElement;
import org.mastodon.pool.MemPool;
import org.mastodon.pool.Properties;

/**
 * Mother class for edge pools of <b>directed, listenable</b> graphs.
 * <p>
 * Graphs based on this edge pool do not have a limitation on the number of
 * edges between a source and target vertices.
 */
public abstract class AbstractListenableEdgePool<
			E extends AbstractListenableEdge< E, V, ?, T >,
			V extends AbstractVertex< V, ?, ?, ? >,
			T extends MappedElement >
		extends AbstractEdgePool< E, V, T >
{
	public AbstractListenableEdgePool(
			final int initialCapacity,
			final AbstractEdgeLayout layout,
			final Class< E > edgeClass,
			final MemPool.Factory< T > memPoolFactory,
			final AbstractVertexPool< V, ?, ? > vertexPool )
	{
		super( initialCapacity, layout, edgeClass, memPoolFactory, vertexPool );
	}

	NotifyPostInit< ?, E > notifyPostInit;

	public void linkNotify( final NotifyPostInit< ?, E > notifyPostInit )
	{
		this.notifyPostInit = notifyPostInit;
	}

	@Override
	protected Properties< E > getProperties()
	{
		return super.getProperties();
	}

	/*
	 * Debug helper. Uncomment to do additional verifyInitialized() whenever a
	 * Ref is pointed to an edge.
	 */
//	@Override
//	public E getObject( final int index, final E obj )
//	{
//		final E e = super.getObject( index, obj );
//		e.verifyInitialized();
//		return e;
//	}
}
