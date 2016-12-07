package org.mastodon.graph.branch;

import org.mastodon.graph.ref.AbstractVertexWithFeaturesPool;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.ByteMappedElementArray;
import org.mastodon.pool.MemPool;
import org.mastodon.pool.PoolObject;
import org.mastodon.pool.SingleArrayMemPool;

public class BranchVertexWithFeaturesPool extends AbstractVertexWithFeaturesPool< BranchVertexWithFeatures, BranchEdgeWithFeatures, ByteMappedElement >
{
	public BranchVertexWithFeaturesPool( final int initialCapacity )
	{
		this( initialCapacity, new BranchVertexFactory() );
	}

	private BranchVertexWithFeaturesPool( final int initialCapacity, final BranchVertexFactory vertexFactory )
	{
		super( initialCapacity, vertexFactory );
		vertexFactory.vertexPool = this;
	}

	private static class BranchVertexFactory implements PoolObject.Factory< BranchVertexWithFeatures, ByteMappedElement >
	{
		private BranchVertexWithFeaturesPool vertexPool;

		@Override
		public int getSizeInBytes()
		{
			return BranchVertex.SIZE_IN_BYTES;
		}

		@Override
		public BranchVertexWithFeatures createEmptyRef()
		{
			return new BranchVertexWithFeatures( vertexPool );
		}

		@Override
		public MemPool.Factory< ByteMappedElement > getMemPoolFactory()
		{
			return SingleArrayMemPool.factory( ByteMappedElementArray.factory );
		}

		@Override
		public Class< BranchVertexWithFeatures > getRefClass()
		{
			return BranchVertexWithFeatures.class;
		}
	};

}
