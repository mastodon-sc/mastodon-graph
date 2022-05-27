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
package org.mastodon.graph.algorithm.traversal;

import java.util.Iterator;

import org.mastodon.graph.Edge;
import org.mastodon.graph.ReadOnlyGraph;
import org.mastodon.graph.Vertex;
import org.mastodon.graph.algorithm.LeafFinder;
import org.mastodon.graph.algorithm.RootFinder;

/**
 * A cross-component, depth-first search. Depth-first searches are graph
 * searches where the children of a vertex are iterated before its siblings.
 * <p>
 * This search is a cross-component search, meaning that when the iteration
 * through a connected-component of the graph is finished, the iterator jumps to
 * the next component automatically. This ensures that all the vertices of the
 * graph are iterated exactly once with this search.
 * <p>
 * The order in which the connected-components are iterated can be specified by
 * using the constructor that specifies an iterable over the collection of roots
 * (or leaves in the {@link SearchDirection#REVERSED} case), using a list with
 * the desired order. For this search to operate properly and indeed iterate
 * through all the vertices of the graph exactly once, the specified collection
 * must include all the roots (or leaves in the {@link SearchDirection#REVERSED}
 * case) of the graph, that is: all the vertices that have no incoming edges (or
 * all the vertices with no outgoing edges).
 * <p>
 * Within a single connected-component, the order of iteration is depth-first.
 * The iterator only jumps to another component only when all the vertices of
 * the currently iterated component have been iterated.
 *
 * @author Jean-Yves Tinevez
 *
 * @param <V>
 *            the type of the graph vertices.
 * @param <E>
 *            the type of the graph edges.
 */
public class DepthFirstCrossComponentSearch< V extends Vertex< E >, E extends Edge< V > > extends AbstractDepthFirstSearch< DepthFirstCrossComponentSearch< V, E >, V, E >
{

	private final Iterator< V > rit;

	/**
	 * Creates a depth-first, cross-component search over the specified graph
	 * with the specified direction.
	 * <p>
	 * The collection of roots or leaves is determined automatically at
	 * creation.
	 *
	 * @param graph
	 *            the graph to search.
	 * @param directivity
	 *            the search direction (can be {@link SearchDirection#DIRECTED}
	 *            {@link SearchDirection#REVERSED} or
	 *            {@link SearchDirection#UNDIRECTED}).
	 */
	public DepthFirstCrossComponentSearch( final ReadOnlyGraph< V, E > graph, final SearchDirection directivity )
	{
		this( graph, directivity, directivity.equals( SearchDirection.REVERSED ) ? LeafFinder.getLeaves( graph ) : RootFinder.getRoots( graph ) );
	}

	/**
	 * Creates a depth-first, cross-component search over the specified graph
	 * with the specified direction, using the specified collection of roots or
	 * leaves.
	 * <p>
	 * The order in which the connected-components are searched can be specified
	 * by using this constructor, using a list with the desired order. For this
	 * search to operate properly and indeed search through all the vertices of
	 * the graph exactly once, the specified collection must include all the
	 * roots (for {@link SearchDirection#DIRECTED} searches) or leaves (for
	 * {@link SearchDirection#REVERSED} searches) of the graph. That is: all the
	 * vertices that have no incoming edges, or no outgoing edges respectively.
	 * For {@link SearchDirection#UNDIRECTED} searches, a collection including
	 * at least one vertex per connected component is sufficient.
	 *
	 * @param graph
	 *            the graph to search.
	 * @param directivity
	 *            the search direction (can be {@link SearchDirection#DIRECTED}
	 *            {@link SearchDirection#REVERSED} or
	 *            {@link SearchDirection#UNDIRECTED}).
	 * @param cc
	 *            an iterable over roots or leaves of the graph.
	 */
	public DepthFirstCrossComponentSearch( final ReadOnlyGraph< V, E > graph, final SearchDirection directivity, final Iterable<V> cc)
	{
		super( graph, directivity );
		this.rit = cc.iterator();
	}

	@Override
	public void start( final V start )
	{
		super.start( start );
		while ( rit.hasNext() )
		{
			final V next = rit.next();
			if ( discovered.contains( next ) )
				continue;

			searchListener.crossComponent( unqueued, next, this );
			visit( next );
		}
	}

}
