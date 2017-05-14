package org.mastodon.graph;

import org.mastodon.graph.ref.AbstractSimpleEdgePool;
import org.mastodon.graph.ref.AbstractListenableEdgePool;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.ByteMappedElementArray;
import org.mastodon.pool.SingleArrayMemPool;

public class ListenableTestEdgePool extends AbstractListenableEdgePool< ListenableTestEdge, ListenableTestVertex, ByteMappedElement >
{
	public ListenableTestEdgePool( final int initialCapacity, final ListenableTestVertexPool vertexPool )
	{
		super(
				initialCapacity,
				AbstractSimpleEdgePool.layout,
				ListenableTestEdge.class,
				SingleArrayMemPool.factory( ByteMappedElementArray.factory ),
				vertexPool );
	}

	@Override
	protected ListenableTestEdge createEmptyRef()
	{
		return new ListenableTestEdge( this );
	}
}
