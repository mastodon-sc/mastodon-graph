/*-
 * #%L
 * Mastodon Graphs
 * %%
 * Copyright (C) 2015 - 2025 Tobias Pietzsch, Jean-Yves Tinevez
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

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;

/**
 * Manages a collection of {@link SpatialIndex}es, arranged by time-points,
 * naturally ordered.
 *
 * @param <T>
 *            the type of objects in the {@link SpatialIndex}es of this
 *            collection.
 */
public interface SpatioTemporalIndex< T > extends Iterable< T >
{
	/**
	 * A {@link ReadLock} for this index. The lock should be acquired before
	 * doing any searches on the index.
	 *
	 * @return a reentrant {@link ReadLock} for this index.
	 */
	public Lock readLock();

	/**
	 * Get a {@link SpatialIndex} for objects with the given time-point.
	 *
	 * @param timepoint
	 *            the time-point.
	 * @return index for objects with the given time-point.
	 */
	public SpatialIndex< T > getSpatialIndex( final int timepoint );

	/**
	 * Get a {@link SpatialIndex} for objects in the given time-point range.
	 *
	 * @param fromTimepoint
	 *            first time-point (inclusive) of the range.
	 * @param toTimepoint
	 *            last time-point (inclusive) of the range.
	 * @return index for objects in the given time-point range.
	 */
	public SpatialIndex< T > getSpatialIndex( final int fromTimepoint, final int toTimepoint );
}
