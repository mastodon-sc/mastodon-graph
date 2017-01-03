package org.mastodon.graph.branch;

import org.mastodon.graph.ref.AbstractListenableEdgePool;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.ByteMappedElementArray;
import org.mastodon.pool.MemPool;
import org.mastodon.pool.PoolObject;
import org.mastodon.pool.SingleArrayMemPool;

public class BranchTestEdgePool extends AbstractListenableEdgePool< BranchTestEdge, BranchTestVertex, ByteMappedElement >
{
	public BranchTestEdgePool( final int initialCapacity, final BranchTestVertexPool vertexPool )
	{
		this( initialCapacity, new BranchTestEdgeFactory(), vertexPool );
	}

	private BranchTestEdgePool( final int initialCapacity, final BranchTestEdgeFactory f, final BranchTestVertexPool vertexPool )
	{
		super( initialCapacity, f, vertexPool );
		f.edgePool = this;
	}

	private static class BranchTestEdgeFactory implements PoolObject.Factory< BranchTestEdge, ByteMappedElement >
	{
		private BranchTestEdgePool edgePool;

		@Override
		public int getSizeInBytes()
		{
			return BranchTestEdge.SIZE_IN_BYTES;
		}

		@Override
		public BranchTestEdge createEmptyRef()
		{
			return new BranchTestEdge( edgePool );
		}

		@Override
		public MemPool.Factory< ByteMappedElement > getMemPoolFactory()
		{
			return SingleArrayMemPool.factory( ByteMappedElementArray.factory );
		}

		@Override
		public Class< BranchTestEdge > getRefClass()
		{
			return BranchTestEdge.class;
		}
	};
}
