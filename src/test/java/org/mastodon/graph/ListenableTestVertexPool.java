package org.mastodon.graph;

import org.mastodon.graph.ref.AbstractListenableVertexPool;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.ByteMappedElementArray;
import org.mastodon.pool.MemPool;
import org.mastodon.pool.PoolObject;
import org.mastodon.pool.SingleArrayMemPool;

public class ListenableTestVertexPool extends AbstractListenableVertexPool< ListenableTestVertex, ListenableTestEdge, ByteMappedElement >
{

	public ListenableTestVertexPool( final int initialCapacity )
	{
		this( initialCapacity, new ListenableTestVertexFactory() );
	}

	private ListenableTestVertexPool( final int initialCapacity, final ListenableTestVertexFactory f )
	{
		super( initialCapacity, f );
		f.vertexPool = this;
	}

	private static class ListenableTestVertexFactory implements PoolObject.Factory< ListenableTestVertex, ByteMappedElement >
	{
		private ListenableTestVertexPool vertexPool;

		@Override
		public int getSizeInBytes()
		{
			return ListenableTestVertex.SIZE_IN_BYTES;
		}

		@Override
		public ListenableTestVertex createEmptyRef()
		{
			return new ListenableTestVertex( vertexPool );
		}

		@Override
		public MemPool.Factory< ByteMappedElement > getMemPoolFactory()
		{
			return SingleArrayMemPool.factory( ByteMappedElementArray.factory );
		}

		@Override
		public Class< ListenableTestVertex > getRefClass()
		{
			return ListenableTestVertex.class;
		}
	};
}
