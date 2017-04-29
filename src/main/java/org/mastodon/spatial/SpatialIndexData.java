package org.mastodon.spatial;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

import org.mastodon.RefPool;
import org.mastodon.collection.RefList;
import org.mastodon.collection.RefRefMap;
import org.mastodon.collection.RefSet;
import org.mastodon.collection.ref.RefArrayList;
import org.mastodon.collection.ref.RefArrayPriorityQueueComparator;
import org.mastodon.collection.ref.RefSetImp;
import org.mastodon.kdtree.ClipConvexPolytope;
import org.mastodon.kdtree.ClipConvexPolytopeKDTree;
import org.mastodon.kdtree.IncrementalNearestNeighborSearch;
import org.mastodon.kdtree.IncrementalNearestValidNeighborSearchOnKDTree;
import org.mastodon.kdtree.KDTree;
import org.mastodon.kdtree.KDTreeNode;
import org.mastodon.kdtree.KDTreeValidIterator;
import org.mastodon.kdtree.NearestValidNeighborSearchOnKDTree;
import org.mastodon.pool.DoubleMappedElement;

import gnu.trove.iterator.TIntIterator;
import net.imglib2.RealLocalizable;
import net.imglib2.Sampler;
import net.imglib2.algorithm.kdtree.ConvexPolytope;
import net.imglib2.algorithm.kdtree.HyperPlane;
import net.imglib2.neighborsearch.NearestNeighborSearch;

/**
 * Spatial index of {@link RealLocalizable} objects.
 * <p>
 * When the index is {@link #SpatialIndexData(Collection, RefPool) constructed},
 * a KDTree of objects is built. The index can be modified by adding, changing,
 * and removing objects. These changes do not trigger a rebuild of the KDTree.
 * Instead, affected nodes in the KDTree are marked as invalid and the modified
 * objects are maintained in a separate set.
 * <p>
 * The idea is that a new {@link SpatialIndexData} is built after a certain
 * number of modifications.
 * <p>
 * This class is not threadsafe!
 *
 *
 *
 * TODO: the added set should not simply store refs to the original objects. It should have refs and copies of their locations, similar to KDTreeNode.
 *
 *
 *
 * @param <O>
 *            type of indexed {@link RealLocalizable} objects.
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
class SpatialIndexData< O extends RealLocalizable >
		implements Iterable< O >
{
	private final RefPool< O > objPool;

	/**
	 * KDTree of objects that were provided at construction. When changes are
	 * made to the set of objects (i.e. {@link #add(RealLocalizable)},
	 * {@link #remove(RealLocalizable)}) the corresponding nodes in the KDTree
	 * are marked as invalid.
	 */
	private final KDTree< O, DoubleMappedElement > kdtree;

	/**
	 * Objects that were modified ({@link #add(RealLocalizable)}) since
	 * construction. These override invalid objects in the KDTree.
	 */
	private final RefSet< O > added;

	/**
	 * maps objects to corresponding nodes in the KDTree.
	 */
	private final RefRefMap< O, KDTreeNode< O, DoubleMappedElement > > nodeMap;

	/**
	 * temporary ref.
	 */
	private final KDTreeNode< O, DoubleMappedElement > node;

	/**
	 * Keeps track of the number of (valid) objects maintained in this index.
	 */
	private int size;

	/**
	 * Construct index from a {@link Collection} of objects.
	 *
	 * @param objs
	 *            {@link RealLocalizable} objects to index.
	 * @param objPool
	 *            pool for creating refs, collections, etc.
	 */
	SpatialIndexData( final Collection< O > objs, final RefPool< O > objPool )
	{
		this.objPool = objPool;
		kdtree = KDTree.kdtree( objs, objPool );
		nodeMap = KDTree.createRefToKDTreeNodeMap( kdtree );
		added = new RefSetImp<>( objPool );
		node = kdtree.createRef();
	    size = kdtree.size();
	}

	/**
	 * Construct index from another {@link SpatialIndexData}.
	 * Puts all the objects from the other index into the KDTree.
	 */
	SpatialIndexData( final SpatialIndexData< O > si )
	{
		objPool = si.objPool;
		final Collection< O > collection = new AbstractCollection< O >()
		{
			@Override
			public Iterator< O > iterator()
			{
				return si.iterator();
			}

			@Override
			public int size()
			{
				return si.size;
			}
		};
		kdtree = KDTree.kdtree( collection, objPool );
		nodeMap = KDTree.createRefToKDTreeNodeMap( kdtree );
		added = new RefSetImp<>( objPool );
		node = kdtree.createRef();
	    size = kdtree.size();
	}

	int modCount()
	{
		final int invalid = kdtree.size() + added.size() - size;
		final int modCount = added.size() + invalid;
		return modCount;
	}

	@Override
	public Iterator< O > iterator()
	{
		final Iterator< O > kdtreeIter = KDTreeValidIterator.create( kdtree );
		final Iterator< O > addedIter = added.iterator();
		return new Iter<>( objPool, kdtreeIter, addedIter );
	}

	public NearestNeighborSearch< O > getNearestNeighborSearch()
	{
		return new NNS();
	}

	public IncrementalNearestNeighborSearch< O > getIncrementalNearestNeighborSearch()
	{
		return new INNS();
	}

	public ClipConvexPolytope< O > getClipConvexPolytope()
	{
		return new CCP();
	}

	/**
	 * Add a new object to the index. Also use this to indicate that an existing
	 * object was moved.
	 *
	 * @param obj
	 *            object to add.
	 * @return {@code true} if this index did not already contain the specified
	 *         object.
	 */
	public boolean add( final O obj )
	{
		final KDTreeNode< O, DoubleMappedElement > n = nodeMap.get( obj, node );
		if ( n != null )
		{
			if ( n.isValid() )
			{
				n.setValid( false );
				--size;
			}
		}

		if ( added.add( obj ) )
		{
			++size;
			return true;
		}

		return false;
	}

	/**
	 * Remove an object from the index.
	 *
	 * @param obj object to remove.
	 * @return {@code true} if this index contained the specified object.
	 */
	public boolean remove( final O obj )
	{
		final KDTreeNode< O, DoubleMappedElement > n = nodeMap.get( obj, node );
		if ( n != null )
		{
			if ( n.isValid() )
			{
				n.setValid( false );
				final KDTreeNode< O, DoubleMappedElement > ref = nodeMap.createValueRef();
				nodeMap.removeWithRef( obj, ref );
				nodeMap.releaseValueRef( ref );
				--size;
				return true;
			}
		}
		else if ( added.remove( obj ) )
		{
			--size;
			return true;
		}

		return false;
	}

	/**
	 * Get number of objects in the index.
	 *
	 * @return number of objects in the index.
	 */
	public int size()
	{
		return size;
	}

	static class Iter< O > implements Iterator< O >
	{
		private final RefPool< O > pool;

		private final O ref;

		private O next;

		private final Iterator< O > kdtreeIter;

		private final Iterator< O > addedIter;

		private boolean hasNext;

		public Iter(
				final RefPool< O > objPool,
				final Iterator< O > kdtreeIter,
				final Iterator< O > addedIter )
		{
			this.pool = objPool;
			ref = objPool.createRef();
			this.kdtreeIter = kdtreeIter;
			this.addedIter = addedIter;
			hasNext = prepareNext();
		}

		private boolean prepareNext()
		{
			if ( kdtreeIter.hasNext() )
			{
				next = kdtreeIter.next();
				return true;
			}
			if ( addedIter.hasNext() )
			{
				next = addedIter.next();
				return true;
			}
			return false;
		}

		@Override
		public boolean hasNext()
		{
			return hasNext;
		}

		@Override
		public O next()
		{
			if ( hasNext )
			{
				final O current = pool.getObject( pool.getId( next ), ref );
				hasNext = prepareNext();
				return current;
			}
			return null;
		}
	}

	class NNS implements NearestNeighborSearch< O >, Sampler< O >
	{
		private final NearestValidNeighborSearchOnKDTree< O, DoubleMappedElement > search;

		private double bestSquDistance;

		private int bestVertexIndex;

		private final O ref;

		private O bestVertex;

		private final int n;

		final double[] pos;

		public NNS()
		{
			search = new NearestValidNeighborSearchOnKDTree<>( kdtree );
			bestVertexIndex = -1;
			ref = objPool.createRef();
			n = search.numDimensions();
			pos = new double[ n ];
		}

		@Override
		public int numDimensions()
		{
			return n;
		}

		@Override
		public void search( final RealLocalizable query )
		{
			bestSquDistance = Double.MAX_VALUE;
			bestVertexIndex = -1;

			search.search( query );
			if ( search.get() != null )
			{
				bestSquDistance = search.getSquareDistance();
				bestVertexIndex = objPool.getId( search.get() );
			}

			query.localize( pos );
			for ( final O v : added )
			{
				double sum = 0;
				for ( int d = 0; d < n; ++d )
				{
					final double diff = v.getDoublePosition( d ) - pos[ d ];
					sum += diff * diff;
				}
				if ( sum < bestSquDistance )
				{
					bestSquDistance = sum;
					bestVertexIndex = objPool.getId( v );
				}
			}

			bestVertex = ( bestVertexIndex >= 0 )
					? objPool.getObject( bestVertexIndex, ref )
					: null;
		}

		@Override
		public Sampler< O > getSampler()
		{
			return this;
		}

		@Override
		public RealLocalizable getPosition()
		{
			return bestVertex;
		}

		@Override
		public double getSquareDistance()
		{
			return bestSquDistance;
		}

		@Override
		public double getDistance()
		{
			return Math.sqrt( bestSquDistance );
		}

		@Override
		public NNS copy()
		{
			final NNS copy = new NNS();
			copy.bestSquDistance = bestSquDistance;
			copy.bestVertexIndex = bestVertexIndex;
			if ( bestVertexIndex != -1 )
				copy.bestVertex = objPool.getObject( bestVertexIndex, copy.ref );
			return copy;
		}

		@Override
		public O get()
		{
			return bestVertex;
		}
	}

	class INNS implements IncrementalNearestNeighborSearch< O >
	{
		private final IncrementalNearestValidNeighborSearchOnKDTree< O, DoubleMappedElement > search;

		private final RefArrayPriorityQueueComparator< O > addedQueue;

		private final int n;

		final double[] pos;

		private final O ref1;

		private final O ref2;

		private int numSteps;

		private O nextAdded;

		private O nextTree;

		private O current;

		private double currentSquDistance;

		private final Comparator< O > comparator = new Comparator< O >()
		{
			@Override
			public final int compare( final O o1, final O o2 )
			{
				double sum = 0;
				for ( int d = 0; d < n; ++d)
				{
					final double p = pos[ d ];
					final double d1 = o1.getDoublePosition( d ) - p;
					final double d2 = o2.getDoublePosition( d ) - p;
					sum += d1 * d1 - d2 * d2;
				}
				return Double.compare( sum, 0 );
			}
		};

		public INNS()
		{
			search = new IncrementalNearestValidNeighborSearchOnKDTree<>( kdtree );
			addedQueue = new RefArrayPriorityQueueComparator<>( objPool, comparator, added.size() );
			n = search.numDimensions();
			pos = new double[ n ];
			ref1 = objPool.createRef();
			ref2 = objPool.createRef();
		}

		public INNS( final INNS that )
		{
			search = that.search.copy();
			n = that.n;
			pos = that.pos.clone();
			ref1 = objPool.createRef();
			ref2 = objPool.createRef();
			numSteps = that.numSteps;

			addedQueue = new RefArrayPriorityQueueComparator<>( objPool, comparator, added.size() );
			final TIntIterator it = that.addedQueue.getIndexCollection().iterator();
			while( it.hasNext() )
				this.addedQueue.offer( objPool.getObject( it.next(), ref2 ) );

			nextAdded = addedQueue.peek( ref1 );
			nextTree = that.nextTree == null
					? null
					: search.get();
			current = that.current == null
					? null
					: objPool.getObject( objPool.getId( that.current ), ref2 );

			currentSquDistance = that.currentSquDistance;
		}

		@Override
		public int numDimensions()
		{
			return n;
		}

		@Override
		public void search( final RealLocalizable query )
		{
			search.search( query );
			query.localize( pos );
			resetx();
		}

		private void resetx()
		{
			addedQueue.reset();
			addedQueue.addAll( added );
			nextAdded = addedQueue.peek( ref1 );
			nextTree = search.hasNext() ? search.next() : null;
			numSteps = 0;
		}

		@Override
		public void reset()
		{
			search.reset();
			resetx();
		}

		@Override
		public void fwd()
		{
			if ( nextTree == null && search.hasNext() )
				nextTree = search.next();

			if ( nextTree == null || ( nextAdded != null && comparator.compare( nextAdded, nextTree ) > 0 ) )
			{
				current = addedQueue.poll( ref2 );
				if ( current != null )
				{
					currentSquDistance = 0;
					for ( int d = 0; d < n; ++d )
					{
						final double diff = current.getDoublePosition( d ) - pos[ d ];
						currentSquDistance += diff * diff;
					}
				}
				nextAdded = addedQueue.peek( ref1 );
			}
			else
			{
				current = nextTree;
				currentSquDistance = search.getSquareDistance();
				nextTree = null;
			}

			++numSteps;
		}

		@Override
		public boolean hasNext()
		{
			return numSteps < size;
		}

		@Override
		public O get()
		{
			return current;
		}

		@Override
		public O next()
		{
			fwd();
			return get();
		}

		@Override
		public double getSquareDistance()
		{
			return currentSquDistance;
		}

		@Override
		public double getDistance()
		{
			return Math.sqrt( currentSquDistance );
		}

		@Override
		public void jumpFwd( final long steps )
		{
			for ( int i = 0; i < ( int ) steps; ++i )
				fwd();
		}

		@Override
		public INNS copyCursor()
		{
			return copy();
		}

		@Override
		public INNS copy()
		{
			return new INNS( this );
		}

		@Override
		public void localize( final float[] position )
		{
			current.localize( position );
		}

		@Override
		public void localize( final double[] position )
		{
			current.localize( position );
		}

		@Override
		public float getFloatPosition( final int d )
		{
			return current.getFloatPosition( d );
		}

		@Override
		public double getDoublePosition( final int d )
		{
			return current.getDoublePosition( d );
		}
	}

	class CCP implements ClipConvexPolytope< O >
	{
		private final ClipConvexPolytopeKDTree< O, DoubleMappedElement > clip;

		private final RefList< O > inside;

		private final RefList< O > outside;

		private final int n;

		public CCP()
		{
			clip = new ClipConvexPolytopeKDTree<>( kdtree );
			inside = new RefArrayList<>( objPool );
			outside = new RefArrayList<>( objPool );
			n = clip.numDimensions();
		}

		@Override
		public int numDimensions()
		{
			return n;
		}

		@Override
		public void clip( final ConvexPolytope polytope )
		{
			clip.clip( polytope );
			clipAdded( polytope );
		}

		@Override
		public void clip( final double[][] planes )
		{
			clip.clip( planes );
			clipAdded( planes );
		}

		@Override
		public Iterable< O > getInsideValues()
		{
			return new Iterable< O >()
			{
				@Override
				public Iterator< O > iterator()
				{
					final Iterator< O > kdtreeIter = clip.getValidInsideValues().iterator();
					final Iterator< O > addedIter = inside.iterator();
					return new Iter<>( objPool, kdtreeIter, addedIter );
				}
			};
		}

		@Override
		public Iterable< O > getOutsideValues()
		{
			return new Iterable< O >()
			{
				@Override
				public Iterator< O > iterator()
				{
					final Iterator< O > kdtreeIter = clip.getValidOutsideValues().iterator();
					final Iterator< O > addedIter = outside.iterator();
					return new Iter<>( objPool, kdtreeIter, addedIter );
				}
			};
		}

		private void clipAdded( final ConvexPolytope polytope )
		{
			final Collection< ? extends HyperPlane > hyperplanes = polytope.getHyperplanes();
			final double[][] planes = new double[ hyperplanes.size() ][];
			int i = 0;
			for ( final HyperPlane hyperplane : hyperplanes )
			{
				final double[] plane = new double[ n + 1 ];
				System.arraycopy( hyperplane.getNormal(), 0, plane, 0, n );
				plane[ n ] = hyperplane.getDistance();
				planes[ i++ ] = plane;
			}
			clipAdded( planes );
		}

		private void clipAdded( final double[][] planes )
		{
			final int nPlanes = planes.length;
			inside.clear();
			outside.clear();
			A: for ( final O p : added )
			{
				for ( int i = 0; i < nPlanes; ++i )
				{
					final double[] plane = planes[ i ];
					double dot = 0;
					for ( int d = 0; d < n; ++d )
						dot += p.getDoublePosition( d ) * plane[ d ];
					if ( dot < plane[ n ] )
					{
						outside.add( p );
						continue A;
					}
				}
				inside.add( p );
			}
		}
	}
}
