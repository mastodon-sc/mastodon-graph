package org.mastodon.graph.branch;

import org.mastodon.graph.ref.AbstractEdgePool;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.ByteMappedElementArray;
import org.mastodon.pool.MemPool;
import org.mastodon.pool.PoolObject;
import org.mastodon.pool.PoolObject.Factory;
import org.mastodon.pool.SingleArrayMemPool;

public class BranchEdgePool extends AbstractEdgePool< BranchEdge, BranchVertex, ByteMappedElement >
{

	public BranchEdgePool( final int initialCapacity, final BranchVertexPool vertexPool )
	{
		this( initialCapacity, new BranchEdgeFactory(), vertexPool );
	}

	private BranchEdgePool( final int initialCapacity, final Factory< BranchEdge, ByteMappedElement > edgeFactory, final BranchVertexPool vertexPool )
	{
		super( initialCapacity, edgeFactory, vertexPool );
	}

	private static class BranchEdgeFactory implements PoolObject.Factory< BranchEdge, ByteMappedElement >
	{
		private BranchEdgePool edgePool;

		@Override
		public int getSizeInBytes()
		{
			return BranchEdge.SIZE_IN_BYTES;
		}

		@Override
		public BranchEdge createEmptyRef()
		{
			return new BranchEdge( edgePool );
		}

		@Override
		public MemPool.Factory< ByteMappedElement > getMemPoolFactory()
		{
			return SingleArrayMemPool.factory( ByteMappedElementArray.factory );
		}

		@Override
		public Class< BranchEdge > getRefClass()
		{
			return BranchEdge.class;
		}
	};

}
