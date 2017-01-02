package org.mastodon.graph.branch;

import org.mastodon.RefPool;
import org.mastodon.graph.ref.AbstractListenableVertexPool;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.ByteMappedElementArray;
import org.mastodon.pool.MemPool;
import org.mastodon.pool.PoolObject;
import org.mastodon.pool.SingleArrayMemPool;
import org.mastodon.spatial.HasTimepoint;

import net.imglib2.RealLocalizable;

public class BranchVertexPool< V extends RealLocalizable & HasTimepoint >
		extends AbstractListenableVertexPool< BranchVertex< V >, BranchEdge< V >, ByteMappedElement >
{
	public BranchVertexPool( final RefPool< V > vertexBimap, final int initialCapacity )
	{
		this( vertexBimap, initialCapacity, new BranchVertexFactory< V >() );
	}

	private BranchVertexPool( final RefPool< V > vertexBimap, final int initialCapacity, final BranchVertexFactory< V > vertexFactory )
	{
		super( initialCapacity, vertexFactory );
		vertexFactory.vertexPool = this;
		vertexFactory.vertexBimap = vertexBimap;
	}

	private static class BranchVertexFactory< V extends RealLocalizable & HasTimepoint >
			implements PoolObject.Factory< BranchVertex< V >, ByteMappedElement >
	{
		private RefPool< V > vertexBimap;

		private BranchVertexPool< V > vertexPool;

		@Override
		public int getSizeInBytes()
		{
			return BranchVertex.SIZE_IN_BYTES;
		}

		@Override
		public BranchVertex< V > createEmptyRef()
		{
			return new BranchVertex< V >( vertexPool, vertexBimap );
		}

		@Override
		public MemPool.Factory< ByteMappedElement > getMemPoolFactory()
		{
			return SingleArrayMemPool.factory( ByteMappedElementArray.factory );
		}

		@Override
		public Class< BranchVertex< V > > getRefClass()
		{
			throw new UnsupportedOperationException();
		}
	};

}
