package org.mastodon.graph.branch;

import org.mastodon.graph.ref.AbstractListenableVertexPool;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.ByteMappedElementArray;
import org.mastodon.pool.MemPool;
import org.mastodon.pool.PoolObject;
import org.mastodon.pool.SingleArrayMemPool;

public class BranchTestVertexPool extends AbstractListenableVertexPool< BranchTestVertex, BranchTestEdge, ByteMappedElement >
{

	public BranchTestVertexPool( final int initialCapacity )
	{
		this( initialCapacity, new ListenableTestVertexFactory() );
	}

	private BranchTestVertexPool( final int initialCapacity, final ListenableTestVertexFactory f )
	{
		super( initialCapacity, f );
		f.vertexPool = this;
	}

	private static class ListenableTestVertexFactory implements PoolObject.Factory< BranchTestVertex, ByteMappedElement >
	{
		private BranchTestVertexPool vertexPool;

		@Override
		public int getSizeInBytes()
		{
			return BranchTestVertex.SIZE_IN_BYTES;
		}

		@Override
		public BranchTestVertex createEmptyRef()
		{
			return new BranchTestVertex( vertexPool );
		}

		@Override
		public MemPool.Factory< ByteMappedElement > getMemPoolFactory()
		{
			return SingleArrayMemPool.factory( ByteMappedElementArray.factory );
		}

		@Override
		public Class< BranchTestVertex > getRefClass()
		{
			return BranchTestVertex.class;
		}
	};
}
