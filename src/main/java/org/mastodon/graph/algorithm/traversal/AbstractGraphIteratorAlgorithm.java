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
package org.mastodon.graph.algorithm.traversal;

import java.util.Iterator;

import org.mastodon.collection.MaybeRefIterator;
import org.mastodon.collection.RefSet;
import org.mastodon.graph.Edge;
import org.mastodon.graph.ReadOnlyGraph;
import org.mastodon.graph.Vertex;
import org.mastodon.graph.algorithm.AbstractGraphAlgorithm;
import org.mastodon.pool.PoolObject;

public abstract class AbstractGraphIteratorAlgorithm< V extends Vertex< E >, E extends Edge< V > > extends AbstractGraphAlgorithm< V, E > implements MaybeRefIterator, Iterator< V >
{
	/**
	 * Keep track of visited vertices.
	 */
	protected final RefSet< V > visited;

	/**
	 * Is returned by {@link #next()}.
	 */
	protected V next;

	/**
	 * Will be returned by following {@link #next()}.
	 */
	protected V fetched;

	/**
	 * utility refs.
	 */
	protected final V tmpRef;
	protected final V nextRef;
	protected final V fetchedRef;

	public AbstractGraphIteratorAlgorithm( final ReadOnlyGraph< V, E > graph )
	{
		super( graph );
		visited = createVertexSet();
		tmpRef = vertexRef();
		nextRef = vertexRef();
		fetchedRef = vertexRef();
		reset();
	}

	protected void reset()
	{
		visited.clear();
		next = null;
		fetched = null;
	}

	@Override
	public boolean isRefIterator()
	{
		final V v = graph.vertexRef();
		final boolean isRefIterator = v instanceof PoolObject;
		graph.releaseRef( v );
		return isRefIterator;
	}

	@Override
	public boolean hasNext()
	{
		return fetched != null;
	}

	@Override
	public V next()
	{
		next = assign( fetched, nextRef );
		fetchNext();
		return next;
	}

	protected void fetchNext()
	{
		if ( canFetch() )
		{
			fetched = fetch( fetchedRef );
			for ( final E e : neighbors( fetched ) )
			{
				final V target = targetOf( fetched, e, tmpRef );
				if ( !visited.contains( target ) )
				{
					visited.add( target );
					toss( target );
				}
			}
		}
		else
			fetched = null;
	}

	@Override
	public void remove()
	{
		throw new UnsupportedOperationException( "Remove is not supported for " + this.getClass() + "." );
	}

	/**
	 * Returns the target vertex of the specified edge.
	 * <p>
	 * By default, this method returns the actual target of the edge, through
	 * {@link Edge#getTarget(Vertex)}. Overriding this method allows for coding
	 * reverse iterators.
	 *
	 * @param source
	 *            the vertex from which this edge is iterated (the source of the
	 *            edge from the point of view of this iterator.)
	 * @param edge
	 *            the edge to return the target of.
	 * @param ref
	 *            a reference object, that might be used or discarded by this
	 *            call.
	 * @return the target of the edge, as defined by this concrete iterator
	 *         implementation.
	 */
	protected V targetOf( V source, E edge, V ref )
	{
		return edge.getTarget( tmpRef );
	}

	/**
	 * Returns the edges neighboring the specified vertex, in whatever sense the
	 * concrete implementation sees this neighborhood.
	 *
	 * @param vertex
	 *            the vertex.
	 * @return an {@link Iterable} over neighbor edges.
	 */
	protected abstract Iterable< E > neighbors( final V vertex );

	/**
	 * Returns a vertex to process by the iterator main loop.
	 *
	 * @param ref
	 *            a reference object, that might be used or discarded by this
	 *            call.
	 *
	 * @return a vertex, which neighbors will be inspected.
	 */
	protected abstract V fetch( V ref );

	/**
	 * Adds the specified vertex to the collection of vertices to process.
	 *
	 * @param vertex
	 *            the vertex to add.
	 */
	protected abstract void toss( V vertex );

	/**
	 * Returns whether more elements can be fetched for processing.
	 *
	 * @return {@code false} if there are no more vertices to process.
	 */
	protected abstract boolean canFetch();
}
