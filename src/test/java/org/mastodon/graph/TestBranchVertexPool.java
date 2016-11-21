package org.mastodon.graph;

import org.mastodon.graph.ref.AbstractVertexPool;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.ByteMappedElementArray;
import org.mastodon.pool.MemPool;
import org.mastodon.pool.PoolObject;
import org.mastodon.pool.SingleArrayMemPool;

public class TestBranchVertexPool extends AbstractVertexPool< TestBranchVertex, TestBranchEdge, ByteMappedElement >
{

	private final TestBranchVertexFactory factory;

	public TestBranchVertexPool( final int initialCapacity )
	{
		this( initialCapacity, new TestBranchVertexFactory() );
	}

	private TestBranchVertexPool( final int initialCapacity, final TestBranchVertexFactory f )
	{
		super( initialCapacity, f );
		this.factory = f;
		factory.vertexPool = this;
	}

	private static class TestBranchVertexFactory implements PoolObject.Factory< TestBranchVertex, ByteMappedElement >
	{
		private TestBranchLinkVertexPool sourceGraphVertexPool;

		private TestBranchVertexPool vertexPool;

		@Override
		public int getSizeInBytes()
		{
			return TestBranchVertex.SIZE_IN_BYTES;
		}

		@Override
		public TestBranchVertex createEmptyRef()
		{
			return new TestBranchVertex( vertexPool, sourceGraphVertexPool );
		}

		@Override
		public MemPool.Factory< ByteMappedElement > getMemPoolFactory()
		{
			return SingleArrayMemPool.factory( ByteMappedElementArray.factory );
		}

		@Override
		public Class< TestBranchVertex > getRefClass()
		{
			return TestBranchVertex.class;
		}
	}

	public void linkSourceGraphVertexPool( final TestBranchLinkVertexPool sourceGraphVertexPool )
	{
		factory.sourceGraphVertexPool = sourceGraphVertexPool;
	}

}