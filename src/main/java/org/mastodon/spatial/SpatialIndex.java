/*-
 * #%L
 * Mastodon Graphs
 * %%
 * Copyright (C) 2015 - 2021 Tobias Pietzsch, Jean-Yves Tinevez
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

import org.mastodon.kdtree.ClipConvexPolytope;
import org.mastodon.kdtree.IncrementalNearestNeighborSearch;

import net.imglib2.neighborsearch.NearestNeighborSearch;

/**
 * Maintain a collection of objects which need to be searched and partitioned
 * spatially.
 *
 * @param <T>
 *            the type of objects managed by this class.
 * @author Tobias Pietzsch
 * @see SpatioTemporalIndex
 */
public interface SpatialIndex< T > extends Iterable< T >
{
	/**
	 * Get number of objects contained in this {@link SpatialIndex}.
	 *
	 * @return number of objects in the index.
	 */
	public int size();

	/**
	 * Check whether this index contains no objects.
	 *
	 * @return {@code true} if this index is empty.
	 */
	public boolean isEmpty();

	/**
	 * Returns a {@link NearestNeighborSearch} for the objects of this index,
	 * able to perform efficiently spatial searches.
	 *
	 * @return a {@link NearestNeighborSearch}.
	 */
	public NearestNeighborSearch< T > getNearestNeighborSearch();

	/**
	 * Returns an {@link IncrementalNearestNeighborSearch} for the objects of
	 * this index, able to perform efficiently spatial searches.
	 *
	 * @return an {@link IncrementalNearestNeighborSearch}.
	 */
	public IncrementalNearestNeighborSearch< T > getIncrementalNearestNeighborSearch();

	/**
	 * Returns a {@link ClipConvexPolytope} for the objects of this index, able
	 * to spatially partition the objects of this index in an efficient manner.
	 *
	 * @return a {@link ClipConvexPolytope}.
	 */
	public ClipConvexPolytope< T > getClipConvexPolytope();
}
