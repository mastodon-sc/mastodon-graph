package org.mastodon.graph.ref;

import org.mastodon.pool.MappedElement;
import org.mastodon.pool.MemPool;

public abstract class AbstractListenableVertexPool<
			V extends AbstractListenableVertex< V, E, ?, T >,
			E extends AbstractEdge< E, ?, ?, ? >,
			T extends MappedElement >
		extends AbstractVertexPool< V, E, T >
{
	public AbstractListenableVertexPool(
			final int initialCapacity,
			final AbstractVertexLayout layout,
			final Class< V > vertexClass,
			final MemPool.Factory< T > memPoolFactory )
	{
		super( initialCapacity, layout, vertexClass, memPoolFactory );
	}

	NotifyPostInit< V, ? > notifyPostInit;

	public void linkNotify( final NotifyPostInit< V, ? > notifyPostInit )
	{
		this.notifyPostInit = notifyPostInit;
	}

	/*
	 * Debug helper. Uncomment to do additional verifyInitialized() whenever a
	 * Ref is pointed to a vertex.
	 */
//	@Override
//	public V getObject( final int index, final V obj )
//	{
//		final V v = super.getObject( index, obj );
//		v.verifyInitialized();
//		return v;
//	}
}
