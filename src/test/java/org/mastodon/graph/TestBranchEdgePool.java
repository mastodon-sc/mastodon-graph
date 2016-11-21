package org.mastodon.graph;

import org.mastodon.graph.ref.AbstractEdgePool;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.ByteMappedElementArray;
import org.mastodon.pool.MemPool;
import org.mastodon.pool.PoolObject;
import org.mastodon.pool.SingleArrayMemPool;

public class TestBranchEdgePool extends AbstractEdgePool< TestBranchEdge, TestBranchVertex, ByteMappedElement >
{

	private final TestBranchEdgeFactory factory;

	public TestBranchEdgePool( final int initialCapacity, final TestBranchVertexPool skeletonVertexPool )
	{
		this( initialCapacity, new TestBranchEdgeFactory(), skeletonVertexPool );
	}

	private TestBranchEdgePool( final int initialCapacity, final TestBranchEdgeFactory f, final TestBranchVertexPool skeletonVertexPool )
	{
		super( initialCapacity, f, skeletonVertexPool );
		this.factory = f;
		f.edgePool = this;
	}

	private static class TestBranchEdgeFactory implements PoolObject.Factory< TestBranchEdge, ByteMappedElement >
	{
		private TestBranchEdgePool edgePool;

		private TestBranchLinkEdgePool sourceGraphEdgePool;

		@Override
		public int getSizeInBytes()
		{
			return TestBranchEdge.SIZE_IN_BYTES;
		}

		@Override
		public TestBranchEdge createEmptyRef()
		{
			return new TestBranchEdge( edgePool, sourceGraphEdgePool );
		}

		@Override
		public MemPool.Factory< ByteMappedElement > getMemPoolFactory()
		{
			return SingleArrayMemPool.factory( ByteMappedElementArray.factory );
		}

		@Override
		public Class< TestBranchEdge > getRefClass()
		{
			return TestBranchEdge.class;
		}
	}

	public void linkSourceGraphEdgePool( final TestBranchLinkEdgePool sourceGraphEdgePool )
	{
		factory.sourceGraphEdgePool = sourceGraphEdgePool;
	}

}