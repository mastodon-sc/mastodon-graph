package org.mastodon.graph;

import org.mastodon.graph.AbstractBranchLinkVertexPool;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.ByteMappedElementArray;
import org.mastodon.pool.MemPool;
import org.mastodon.pool.PoolObject;
import org.mastodon.pool.SingleArrayMemPool;

public class TestBranchLinkVertexPool extends AbstractBranchLinkVertexPool< TestBranchLinkVertex, TestBranchVertex, TestBranchEdge, TestBranchLinkEdge, ByteMappedElement >
{

	public TestBranchLinkVertexPool( final int initialCapacity, final TestBranchVertexPool skeletonVertexPool, final TestBranchEdgePool skeletonEdgePool )
	{
		this( initialCapacity, new TestBranchLinkVertexFactory(), skeletonVertexPool, skeletonEdgePool );
	}

	private TestBranchLinkVertexPool( final int initialCapacity, final TestBranchLinkVertexFactory f, final TestBranchVertexPool skeletonVertexPool, final TestBranchEdgePool skeletonEdgePool )
	{
		super( initialCapacity, f, skeletonVertexPool );
		skeletonVertexPool.linkSourceGraphVertexPool( this );
		f.vertexPool = this;
		f.skeletonVertexPool = skeletonVertexPool;
		f.skeletonEdgePool = skeletonEdgePool;
	}

	private static class TestBranchLinkVertexFactory implements PoolObject.Factory< TestBranchLinkVertex, ByteMappedElement >
	{
		private TestBranchLinkVertexPool vertexPool;

		private TestBranchVertexPool skeletonVertexPool;

		private TestBranchEdgePool skeletonEdgePool;

		@Override
		public int getSizeInBytes()
		{
			return TestBranchLinkVertex.SIZE_IN_BYTES;
		}

		@Override
		public TestBranchLinkVertex createEmptyRef()
		{
			return new TestBranchLinkVertex( vertexPool, skeletonVertexPool, skeletonEdgePool );
		}

		@Override
		public MemPool.Factory< ByteMappedElement > getMemPoolFactory()
		{
			return SingleArrayMemPool.factory( ByteMappedElementArray.factory );
		}

		@Override
		public Class< TestBranchLinkVertex > getRefClass()
		{
			return TestBranchLinkVertex.class;
		}
	}
}