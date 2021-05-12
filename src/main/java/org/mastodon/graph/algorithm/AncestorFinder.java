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
package org.mastodon.graph.algorithm;

import java.util.Set;

import org.mastodon.collection.RefSet;
import org.mastodon.graph.Edge;
import org.mastodon.graph.ReadOnlyGraph;
import org.mastodon.graph.Vertex;
import org.mastodon.graph.algorithm.traversal.InverseDepthFirstIterator;

public class AncestorFinder< V extends Vertex< E >, E extends Edge< V > > extends AbstractGraphAlgorithm< V, E >
{
	/**
	 * Gets the ancestors of the specified {@code initial} vertices in the
	 * specified {@code graph}. (The set of ancestors includes the
	 * {@code initial} vertices.)
	 * 
	 * @param graph
	 *            the graph.
	 * @param initial
	 *            the vertices.
	 * @param <V>
	 *            the type of vertices in the graph.
	 * @param <E>
	 *            the type of edges in the graph.
	 * @return a new {@link RefSet}.
	 */
	public static < V extends Vertex< E >, E extends Edge< V > >
		RefSet< V > ancestors( final ReadOnlyGraph< V, E > graph, final Set< V > initial )
	{
		return new AncestorFinder<>( graph ).get( initial );
	}

	public AncestorFinder( final ReadOnlyGraph< V, E > graph )
	{
		super( graph );
		iter = new Iter( graph );
	}

	/**
	 * Returns the set of ancestors of the specified {@code initial} vertices in the graph.
	 * (The set of ancestors includes the {@code initial} vertices.)
	 * <p>
	 * The returned ancestor set is a snapshot of the graph
	 * connectivity when this method is called. Subsequent calls to this method
	 * will return a new set, that accounts for the latest graph modifications.
	 *
	 * @param initial
	 *  vertices from which to start traversal.
	 * @return a new {@link Set} containing the ancestors of {@code initial}.
	 */
	public RefSet< V > get( final Set< V > initial )
	{
		final RefSet< V > ancestors = createVertexSet();
		iter.reset();
		iter.clearVisited();
		for ( final V v : initial )
		{
			iter.reset( v );
			while ( iter.hasNext() )
				ancestors.add( iter.next() );
		}
		return ancestors;
	}

	private Iter iter;

	private class Iter extends InverseDepthFirstIterator< V, E >
	{
		Iter( final ReadOnlyGraph< V, E > graph )
		{
			super( graph );
		}

		@Override
		public void reset( final V root )
		{
			if ( visited.contains( root ) )
				reset();
			else
				super.reset( root );
		}

		@Override
		protected void reset()
		{
			// DOES NOT CLEAR VISITED
			next = null;
			fetched = null;
		}

		void clearVisited()
		{
			visited.clear();
		}
	}
}
