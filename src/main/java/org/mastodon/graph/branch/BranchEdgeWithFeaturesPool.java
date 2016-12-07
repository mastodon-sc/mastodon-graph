package org.mastodon.graph.branch;

import org.mastodon.graph.ref.AbstractNonSimpleEdgeWithFeaturesPool;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.ByteMappedElementArray;
import org.mastodon.pool.MemPool;
import org.mastodon.pool.PoolObject;
import org.mastodon.pool.SingleArrayMemPool;

public class BranchEdgeWithFeaturesPool
		extends AbstractNonSimpleEdgeWithFeaturesPool< 
			BranchEdgeWithFeatures, 
			BranchVertexWithFeatures, 
			ByteMappedElement >
{

	public BranchEdgeWithFeaturesPool( final int initialCapacity, final BranchVertexWithFeaturesPool vertexPool )
	{
		this( initialCapacity, new BranchEdgeFactory(), vertexPool );
	}

	private BranchEdgeWithFeaturesPool( final int initialCapacity, final BranchEdgeFactory edgeFactory, final BranchVertexWithFeaturesPool vertexPool )
	{
		super( initialCapacity, edgeFactory, vertexPool );
		edgeFactory.edgePool = this;
	}

	private static class BranchEdgeFactory implements PoolObject.Factory< BranchEdgeWithFeatures, ByteMappedElement >
	{
		private BranchEdgeWithFeaturesPool edgePool;

		@Override
		public int getSizeInBytes()
		{
			return BranchEdge.SIZE_IN_BYTES;
		}

		@Override
		public BranchEdgeWithFeatures createEmptyRef()
		{
			return new BranchEdgeWithFeatures( edgePool );
		}

		@Override
		public MemPool.Factory< ByteMappedElement > getMemPoolFactory()
		{
			return SingleArrayMemPool.factory( ByteMappedElementArray.factory );
		}

		@Override
		public Class< BranchEdgeWithFeatures > getRefClass()
		{
			return BranchEdgeWithFeatures.class;
		}
	};

}
