/*-
 * #%L
 * Mastodon Graphs
 * %%
 * Copyright (C) 2015 - 2020 Tobias Pietzsch, Jean-Yves Tinevez
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

import org.mastodon.collection.RefList;
import org.mastodon.graph.Edge;
import org.mastodon.graph.Graph;
import org.mastodon.graph.Vertex;
import org.mastodon.graph.algorithm.traversal.BreadthFirstSearch;
import org.mastodon.graph.algorithm.traversal.GraphSearch.SearchDirection;
import org.mastodon.graph.algorithm.traversal.SearchListener;

/**
 * A plain shortest path for unweighted graphs, directed or not. Simply based on
 * {@link BreadthFirstSearch}.
 *
 * @author Jean-Yves Tinevez
 *
 * @param <V>
 *            the {@link Vertex} type of the {@link Graph}.
 * @param <E>
 *            the {@link Edge} type of the {@link Graph}.
 */
public class ShortestPath< V extends Vertex< E >, E extends Edge< V > > extends AbstractGraphAlgorithm< V, E >
{
	private final SearchDirection directivity;

	/**
	 * Creates a new shortest path searcher.
	 *
	 * @param graph
	 *            the graph to traverse.
	 * @param directivity
	 *            whether the search takes into account the direction of edges.
	 */
	public ShortestPath( final Graph< V, E > graph, final SearchDirection directivity )
	{
		super( graph );
		this.directivity = directivity;
	}

	/**
	 * Finds the path between the specified vertices.
	 * <p>
	 * The success and result of this search strongly depends on whether this
	 * search is directed or undirected.
	 *
	 * @param from
	 *            the vertex to start search from.
	 * @param to
	 *            the vertex to reach.
	 * @return a new {@link RefList}, containing the path found <b>in reverse
	 *         order</b> ({@code to → from}). Returns {@code null} if
	 *         a path cannot be found between the specified vertices.
	 */
	public RefList< V > findPath( final V from, final V to )
	{
		final BreadthFirstSearch< V, E > search = new BreadthFirstSearch<>( graph, directivity );
		final VertexFinderListener vfl = new VertexFinderListener( to );
		search.setTraversalListener( vfl );
		search.start( from );

		if ( search.wasAborted() )
		{
			// Path found. Create list in reverse order.
			V tmp = vertexRef();
			final RefList< V > path = createVertexList( search.depthOf( to ) + 1 );
			tmp = assign( to, tmp );
			path.add( tmp );
			while ( !tmp.equals( from ) )
			{
				tmp = assign( search.parent( tmp ), tmp );
				path.add( tmp );
			}
			releaseRef( tmp );
			return path;
		}
		else
		{
			// Path not found.
			return null;
		}
	}

	private class VertexFinderListener implements SearchListener< V, E, BreadthFirstSearch< V, E > >
	{

		private final V target;

		public VertexFinderListener( final V target )
		{
			this.target = target;
		}

		@Override
		public void processVertexLate( final V vertex, final BreadthFirstSearch< V, E > search )
		{}

		@Override
		public void processVertexEarly( final V vertex, final BreadthFirstSearch< V, E > search )
		{
			if ( vertex.equals( target ) )
			{
				search.abort();
			}
		}

		@Override
		public void processEdge( final E edge, final V from, final V to, final BreadthFirstSearch< V, E > search )
		{}

		@Override
		public void crossComponent( final V from, final V to, final BreadthFirstSearch< V, E > search )
		{}

	}
}
