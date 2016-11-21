package org.mastodon.graph.branch;

import org.mastodon.graph.ref.AbstractVertexPool;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.ByteMappedElementArray;
import org.mastodon.pool.MemPool;
import org.mastodon.pool.PoolObject;
import org.mastodon.pool.PoolObject.Factory;
import org.mastodon.pool.SingleArrayMemPool;

public class BranchVertexPool extends AbstractVertexPool< BranchVertex, BranchEdge, ByteMappedElement >
{
	public BranchVertexPool( final int initialCapacity )
	{
		this( initialCapacity, new BranchVertexFactory() );
	}

	private BranchVertexPool( final int initialCapacity, final Factory< BranchVertex, ByteMappedElement > vertexFactory )
	{
		super( initialCapacity, vertexFactory );
	}

	private static class BranchVertexFactory implements PoolObject.Factory< BranchVertex, ByteMappedElement >
	{
		private BranchVertexPool vertexPool;

		@Override
		public int getSizeInBytes()
		{
			return BranchVertex.SIZE_IN_BYTES;
		}

		@Override
		public BranchVertex createEmptyRef()
		{
			return new BranchVertex( vertexPool );
		}

		@Override
		public MemPool.Factory< ByteMappedElement > getMemPoolFactory()
		{
			return SingleArrayMemPool.factory( ByteMappedElementArray.factory );
		}

		@Override
		public Class< BranchVertex > getRefClass()
		{
			return BranchVertex.class;
		}
	};

}
