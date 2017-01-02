package org.mastodon.graph.branch;

import org.mastodon.graph.ref.AbstractListenableEdgePool;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.ByteMappedElementArray;
import org.mastodon.pool.MemPool;
import org.mastodon.pool.PoolObject;
import org.mastodon.pool.SingleArrayMemPool;
import org.mastodon.spatial.HasTimepoint;

import net.imglib2.RealLocalizable;

public class BranchEdgePool< V extends RealLocalizable & HasTimepoint >
		extends AbstractListenableEdgePool<
			BranchEdge< V >,
			BranchVertex< V >,
			ByteMappedElement >
{

	public BranchEdgePool( final int initialCapacity, final BranchVertexPool< V > vertexPool )
	{
		this( initialCapacity, new BranchEdgeFactory< V >(), vertexPool );
	}

	private BranchEdgePool( final int initialCapacity, final BranchEdgeFactory< V > edgeFactory, final BranchVertexPool< V > vertexPool )
	{
		super( initialCapacity, edgeFactory, vertexPool );
		edgeFactory.edgePool = this;
	}

	private static class BranchEdgeFactory< V extends RealLocalizable & HasTimepoint >
			implements PoolObject.Factory< BranchEdge< V >, ByteMappedElement >
	{
		private BranchEdgePool< V > edgePool;

		@Override
		public int getSizeInBytes()
		{
			return BranchEdge.SIZE_IN_BYTES;
		}

		@Override
		public BranchEdge< V > createEmptyRef()
		{
			return new BranchEdge< V >( edgePool );
		}

		@Override
		public MemPool.Factory< ByteMappedElement > getMemPoolFactory()
		{
			return SingleArrayMemPool.factory( ByteMappedElementArray.factory );
		}

		@Override
		public Class< BranchEdge< V > > getRefClass()
		{
			throw new UnsupportedOperationException();
		}
	};

}
