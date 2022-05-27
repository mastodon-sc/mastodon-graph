/*-
 * #%L
 * Mastodon Graphs
 * %%
 * Copyright (C) 2015 - 2022 Tobias Pietzsch, Jean-Yves Tinevez
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package org.mastodon.spatial;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.mastodon.RefPool;
import org.mastodon.kdtree.ClipConvexPolytope;
import org.mastodon.kdtree.IncrementalNearestNeighborSearch;

import net.imglib2.RealLocalizable;
import net.imglib2.neighborsearch.NearestNeighborSearch;

/**
 * Spatial index of {@link RealLocalizable} objects.
 *
 * TODO: figure out locking and locking API.
 *
 * @param <O>
 *            type of objects in the index
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public class SpatialIndexImp< O extends RealLocalizable > implements SpatialIndex< O >
{
	private SpatialIndexData< O > data;

    private final Lock readLock;

    private final Lock writeLock;

	public SpatialIndexImp( final Collection< O > objs, final RefPool< O > objPool )
	{
		data = new SpatialIndexData<>( objs, objPool );
		final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
	    readLock = rwl.readLock();
	    writeLock = rwl.writeLock();
	}

	void rebuild()
	{
		readLock.lock();
		try
		{
			data = new SpatialIndexData<>( data );
		}
		finally
		{
			readLock.unlock();
		}
	}

	@Override
	public Iterator< O > iterator()
	{
		return data.iterator();
	}

	@Override
	public NearestNeighborSearch< O > getNearestNeighborSearch()
	{
		return data.getNearestNeighborSearch();
	}

	@Override
	public IncrementalNearestNeighborSearch< O > getIncrementalNearestNeighborSearch()
	{
		return data.getIncrementalNearestNeighborSearch();
	}

	@Override
	public ClipConvexPolytope< O > getClipConvexPolytope()
	{
		return data.getClipConvexPolytope();
	}

	@Override
	public int size()
	{
		return data.size();
	}

	@Override
	public boolean isEmpty()
	{
		return size() == 0;
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
	boolean add( final O obj )
	{
		writeLock.lock();
		try
		{
			return data.add( obj );
		}
		finally
		{
			writeLock.unlock();
		}
	}

	/**
	 * Remove an object from the index.
	 *
	 * @param obj object to remove.
	 * @return {@code true} if this index contained the specified object.
	 */
	boolean remove( final O obj )
	{
		writeLock.lock();
		try
		{
			return data.remove( obj );
		}
		finally
		{
			writeLock.unlock();
		}
	}

	int modCount()
	{
		return data.modCount();
	}
}
