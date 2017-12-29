package org.mastodon.graph;

import org.mastodon.graph.ref.AbstractEdgePool;
import org.mastodon.graph.ref.AbstractListenableEdgePool;
import org.mastodon.io.AttributeSerializer;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.ByteMappedElementArray;
import org.mastodon.pool.PoolObjectAttributeSerializer;
import org.mastodon.pool.SingleArrayMemPool;

public class ListenableTestEdgePool extends AbstractListenableEdgePool< ListenableTestEdge, ListenableTestVertex, ByteMappedElement >
{
	public ListenableTestEdgePool( final int initialCapacity, final ListenableTestVertexPool vertexPool )
	{
		super(
				initialCapacity,
				AbstractEdgePool.layout,
				ListenableTestEdge.class,
				SingleArrayMemPool.factory( ByteMappedElementArray.factory ),
				vertexPool );
	}

	@Override
	protected ListenableTestEdge createEmptyRef()
	{
		return new ListenableTestEdge( this );
	}

	public static final AttributeSerializer< ListenableTestEdge > edgeSerializer = new PoolObjectAttributeSerializer< ListenableTestEdge >(
			AbstractEdgePool.layout.getSizeInBytes(), 0 )
	{
		@Override
		public void notifySet( final ListenableTestEdge edge )
		{
			edge.notifyEdgeAdded();
		}
	};
}
