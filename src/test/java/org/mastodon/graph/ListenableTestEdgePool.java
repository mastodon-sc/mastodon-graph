package org.mastodon.graph;

import org.mastodon.graph.ref.AbstractListenableEdgePool;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.ByteMappedElementArray;
import org.mastodon.pool.MemPool;
import org.mastodon.pool.PoolObject;
import org.mastodon.pool.SingleArrayMemPool;

public class ListenableTestEdgePool extends AbstractListenableEdgePool< ListenableTestEdge, ListenableTestVertex, ByteMappedElement >
{
	public ListenableTestEdgePool( final int initialCapacity, final ListenableTestVertexPool vertexPool )
	{
		this( initialCapacity, new ListenableTestEdgeFactory(), vertexPool );
	}

	private ListenableTestEdgePool( final int initialCapacity, final ListenableTestEdgeFactory f, final ListenableTestVertexPool vertexPool )
	{
		super( initialCapacity, f, vertexPool );
		f.edgePool = this;
	}

	private static class ListenableTestEdgeFactory implements PoolObject.Factory< ListenableTestEdge, ByteMappedElement >
	{
		private ListenableTestEdgePool edgePool;

		@Override
		public int getSizeInBytes()
		{
			return ListenableTestEdge.SIZE_IN_BYTES;
		}

		@Override
		public ListenableTestEdge createEmptyRef()
		{
			return new ListenableTestEdge( edgePool );
		}

		@Override
		public MemPool.Factory< ByteMappedElement > getMemPoolFactory()
		{
			return SingleArrayMemPool.factory( ByteMappedElementArray.factory );
		}

		@Override
		public Class< ListenableTestEdge > getRefClass()
		{
			return ListenableTestEdge.class;
		}
	};
}
