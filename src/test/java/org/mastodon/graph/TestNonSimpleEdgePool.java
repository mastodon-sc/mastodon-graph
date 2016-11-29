package org.mastodon.graph;

import org.mastodon.graph.ref.AbstractNonSimpleEdgePool;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.ByteMappedElementArray;
import org.mastodon.pool.MemPool;
import org.mastodon.pool.PoolObject;
import org.mastodon.pool.SingleArrayMemPool;

public class TestNonSimpleEdgePool extends AbstractNonSimpleEdgePool< TestNonSimpleEdge, TestNonSimpleVertex, ByteMappedElement >
{
	public TestNonSimpleEdgePool( final int initialCapacity, final TestNonSimpleVertexPool vertexPool )
	{
		this( initialCapacity, new TestNonSimpleEdgeFactory(), vertexPool );
	}

	private TestNonSimpleEdgePool( final int initialCapacity, final TestNonSimpleEdgeFactory f, final TestNonSimpleVertexPool vertexPool )
	{
		super( initialCapacity, f, vertexPool );
		f.edgePool = this;
	}

	private static class TestNonSimpleEdgeFactory implements PoolObject.Factory< TestNonSimpleEdge, ByteMappedElement >
	{
		private TestNonSimpleEdgePool edgePool;

		@Override
		public int getSizeInBytes()
		{
			return TestNonSimpleEdge.SIZE_IN_BYTES;
		}

		@Override
		public TestNonSimpleEdge createEmptyRef()
		{
			return new TestNonSimpleEdge( edgePool );
		}

		@Override
		public MemPool.Factory< ByteMappedElement > getMemPoolFactory()
		{
			return SingleArrayMemPool.factory( ByteMappedElementArray.factory );
		}

		@Override
		public Class< TestNonSimpleEdge > getRefClass()
		{
			return TestNonSimpleEdge.class;
		}
	};
}
