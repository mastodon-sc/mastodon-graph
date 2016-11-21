package org.mastodon.graph;

import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.ByteMappedElementArray;
import org.mastodon.pool.MemPool;
import org.mastodon.pool.PoolObject;
import org.mastodon.pool.SingleArrayMemPool;

public class TestBranchLinkEdgePool extends AbstractBranchLinkEdgePool< TestBranchLinkEdge, TestBranchLinkVertex, TestBranchVertex, TestBranchEdge, ByteMappedElement >
{

	public TestBranchLinkEdgePool(
			final int initialCapacity,
			final TestBranchLinkVertexPool vertexPool,
			final TestBranchEdgePool skeletonEdgePool,
			final TestBranchVertexPool skeletonVertexPool )
	{
		this( initialCapacity, new TestBranchEdgeLinkFactory(), vertexPool, skeletonEdgePool, skeletonVertexPool );
	}

	@Override
	protected TestBranchVertex split( final TestBranchLinkVertex v )
	{
		final TestBranchVertex sv = super.split( v );
		sv.setTimePoint( v.getTimepoint() );
		return sv;
	}

	private TestBranchLinkEdgePool( final int initialCapacity, final TestBranchEdgeLinkFactory f, final TestBranchLinkVertexPool vertexPool, final TestBranchEdgePool skeletonEdgePool, final TestBranchVertexPool skeletonVertexPool )
	{
		super( initialCapacity, f, vertexPool, skeletonEdgePool, skeletonVertexPool );
		skeletonEdgePool.linkSourceGraphEdgePool( this );
		f.edgePool = this;
	}

	private static class TestBranchEdgeLinkFactory implements PoolObject.Factory< TestBranchLinkEdge, ByteMappedElement >
	{
		private TestBranchLinkEdgePool edgePool;

		@Override
		public int getSizeInBytes()
		{
			return TestBranchLinkEdge.SIZE_IN_BYTES;
		}

		@Override
		public TestBranchLinkEdge createEmptyRef()
		{
			return new TestBranchLinkEdge( edgePool );
		}

		@Override
		public MemPool.Factory< ByteMappedElement > getMemPoolFactory()
		{
			return SingleArrayMemPool.factory( ByteMappedElementArray.factory );
		}

		@Override
		public Class< TestBranchLinkEdge > getRefClass()
		{
			return TestBranchLinkEdge.class;
		}
	}

}