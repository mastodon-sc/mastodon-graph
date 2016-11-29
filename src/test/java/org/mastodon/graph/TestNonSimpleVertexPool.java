package org.mastodon.graph;

import org.mastodon.graph.ref.AbstractVertexPool;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.ByteMappedElementArray;
import org.mastodon.pool.MemPool;
import org.mastodon.pool.PoolObject;
import org.mastodon.pool.SingleArrayMemPool;

public class TestNonSimpleVertexPool extends AbstractVertexPool< TestNonSimpleVertex, TestNonSimpleEdge, ByteMappedElement >
{
	public TestNonSimpleVertexPool( final int initialCapacity )
	{
		this( initialCapacity, new TestVertexFactory() );
	}

	private TestNonSimpleVertexPool( final int initialCapacity, final TestVertexFactory f )
	{
		super( initialCapacity, f );
		f.vertexPool = this;
	}

	private static class TestVertexFactory implements PoolObject.Factory< TestNonSimpleVertex, ByteMappedElement >
	{
		private TestNonSimpleVertexPool vertexPool;

		@Override
		public int getSizeInBytes()
		{
			return TestNonSimpleVertex.SIZE_IN_BYTES;
		}

		@Override
		public TestNonSimpleVertex createEmptyRef()
		{
			return new TestNonSimpleVertex( vertexPool );
		}

		@Override
		public MemPool.Factory< ByteMappedElement > getMemPoolFactory()
		{
			return SingleArrayMemPool.factory( ByteMappedElementArray.factory );
		}

		@Override
		public Class< TestNonSimpleVertex > getRefClass()
		{
			return TestNonSimpleVertex.class;
		}
	};
}
