package org.mastodon.kdtree;

import static org.mastodon.pool.ByteUtils.DOUBLE_SIZE;

import org.mastodon.graph.ref.AbstractEdge;
import org.mastodon.graph.ref.AbstractEdgePool;
import org.mastodon.graph.ref.AbstractVertex;
import org.mastodon.graph.ref.AbstractVertexPool;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.ByteMappedElementArray;
import org.mastodon.pool.MemPool;
import org.mastodon.pool.PoolObject;
import org.mastodon.pool.SingleArrayMemPool;

import net.imglib2.RealLocalizable;

public class RealLocalizableVertices
{
	static class MyVertex extends AbstractVertex< MyVertex, MyEdge, ByteMappedElement > implements RealLocalizable
	{
		protected static final int X_OFFSET = AbstractVertex.SIZE_IN_BYTES;

		protected static final int SIZE_IN_BYTES = X_OFFSET + 3 * DOUBLE_SIZE;

		private final int n = 3;

		protected MyVertex( final AbstractVertexPool< MyVertex, ?, ByteMappedElement > pool )
		{
			super( pool );
		}

		// === subset of RealPositionable ===

		public void setPosition( final double[] position )
		{
			for ( int d = 0; d < n; ++d )
				access.putDouble( position[ d ], X_OFFSET + d * DOUBLE_SIZE );
		}

		public void setPosition( final RealLocalizable position )
		{
			for ( int d = 0; d < n; ++d )
				access.putDouble( position.getDoublePosition( d ), X_OFFSET + d * DOUBLE_SIZE );
		}

		public void setPosition( final double position, final int d )
		{
			access.putDouble( position, X_OFFSET + d * DOUBLE_SIZE );
		}

		// === RealLocalizable ===

		@Override
		public int numDimensions()
		{
			return n;
		}

		@Override
		public void localize( final float[] position )
		{
			for ( int d = 0; d < n; ++d )
				position[ d ] = ( float ) access.getDouble( X_OFFSET + d * DOUBLE_SIZE );
		}

		@Override
		public void localize( final double[] position )
		{
			for ( int d = 0; d < n; ++d )
				position[ d ] = access.getDouble( X_OFFSET + d * DOUBLE_SIZE );
		}

		@Override
		public float getFloatPosition( final int d )
		{
			return ( float ) getDoublePosition( d );
		}

		@Override
		public double getDoublePosition( final int d )
		{
			return access.getDouble( X_OFFSET + d * DOUBLE_SIZE );
		}
	}

	static class MyEdge extends AbstractEdge< MyEdge, MyVertex, ByteMappedElement >
	{
		protected static final int SIZE_IN_BYTES = AbstractEdge.SIZE_IN_BYTES;

		protected MyEdge( final AbstractEdgePool< MyEdge, MyVertex, ByteMappedElement > pool )
		{
			super( pool );
		}
	}

	static class MyVertexPool extends AbstractVertexPool< MyVertex, MyEdge, ByteMappedElement >
	{
		public MyVertexPool( final int initialCapacity )
		{
			this( initialCapacity, new MyVertexFactory() );
		}

		private MyVertexPool( final int initialCapacity, final MyVertexFactory f )
		{
			super( initialCapacity, f );
			f.vertexPool = this;
		}

		static class MyVertexFactory implements PoolObject.Factory< MyVertex, ByteMappedElement >
		{
			private MyVertexPool vertexPool;

			@Override
			public int getSizeInBytes()
			{
				return MyVertex.SIZE_IN_BYTES;
			}

			@Override
			public MyVertex createEmptyRef()
			{
				return new MyVertex( vertexPool );
			}

			@Override
			public MemPool.Factory< ByteMappedElement > getMemPoolFactory()
			{
				return SingleArrayMemPool.factory( ByteMappedElementArray.factory );
			}

			@Override
			public Class< MyVertex > getRefClass()
			{
				return MyVertex.class;
			}
		};
	}
}