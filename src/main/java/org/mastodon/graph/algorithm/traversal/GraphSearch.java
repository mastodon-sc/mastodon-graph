/*-
 * #%L
 * Mastodon Graphs
 * %%
 * Copyright (C) 2015 - 2024 Tobias Pietzsch, Jean-Yves Tinevez
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

import java.util.Comparator;

import org.mastodon.collection.RefRefMap;
import org.mastodon.collection.RefSet;
import org.mastodon.graph.Edge;
import org.mastodon.graph.ReadOnlyGraph;
import org.mastodon.graph.Vertex;
import org.mastodon.graph.algorithm.AbstractGraphAlgorithm;

public abstract class GraphSearch< T extends GraphSearch< T, V, E >, V extends Vertex< E >, E extends Edge< V > > extends AbstractGraphAlgorithm< V, E >
{
	public static enum SearchDirection
	{
		/**
		 * The graph will be iterated regardless of the edges direction.
		 */
		UNDIRECTED,
		/**
		 * The graph will be iterated following only edges direction.
		 */
		DIRECTED,
		/**
		 * The graph will be iterated following reversed edges direction (target
		 * to source).
		 */
		REVERSED;
	}

	protected final RefSet< V > discovered;

	protected final RefSet< V > processed;

	private boolean aborted;

	protected SearchListener< V, E, T > searchListener;

	protected final RefRefMap< V, V > parents;

	protected Comparator< V > comparator;

	public GraphSearch(final ReadOnlyGraph< V, E > graph)
	{
		super( graph );
		this.discovered = createVertexSet();
		this.processed = createVertexSet();
		this.parents = createVertexVertexMap();
	}

	/**
	 * Starts the search at the specified vertex.
	 * <p>
	 * This method returns when the search is complete, or when the
	 * {@link SearchListener} aborts the search by calling the {@link #abort()}
	 * method on this search.
	 *
	 * @param start
	 *            the vertex to start the search with.
	 */
	public void start( final V start )
	{
		discovered.clear();
		processed.clear();
		parents.clear();
		aborted = false;
		visit( start );
	}

	/**
	 * Sets the {@link SearchListener} to use for next search.
	 * <p>
	 * If it is not {@code null}, this listener will be notified in proper
	 * order when discovering vertices, crossing edges and finishing processing
	 * vertices. If {@code null}, there are no notifications.
	 *
	 * @param searchListener
	 *            the search listener to use for next search. Can be
	 *            {@code null}.
	 */
	public void setTraversalListener( final SearchListener< V, E, T > searchListener )
	{
		this.searchListener = searchListener;
	}

	/**
	 * Sets the comparator to use for next search.
	 * <p>
	 * This comparator is used when several children of the current vertex can
	 * be visited. If the specified comparator is not {@code null}, it is
	 * used to sort these children, which are then visited according to the
	 * order it sets. If it is {@code null}, the order is unspecified.
	 *
	 * @param comparator
	 *            the vertex comparator to use for next search. Can be
	 *            {@code null}.
	 */
	public void setComparator( final Comparator< V > comparator )
	{
		this.comparator = comparator;
	}

	/**
	 * Aborts the current search before its normal termination.
	 */
	public void abort()
	{
		aborted = true;
	}

	/**
	 * Returns {@code true} if the search was aborted before its normal
	 * completion.
	 *
	 * @return {@code true} if the search was aborted.
	 */
	public boolean wasAborted()
	{
		return aborted;
	}

	/**
	 * Returns the parent of the specified vertex in the current search tree.
	 * Returns {@code null} if the specified vertex has not been visited
	 * yet.
	 *
	 * @param child
	 *            the vertex to find the parent of.
	 * @return the vertex parent in the search tree.
	 */
	public V parent( final V child )
	{
		return parents.get( child );
	}

	/**
	 * Computes the specified edge class in the current search. Return
	 * {@link EdgeClass#UNCLASSIFIED} if the edge has not been visited yet.
	 *
	 * @param from
	 *            the vertex visited first while crossing the edge.
	 * @param to
	 *            the vertex visited last while crossing the edge.
	 * @return the edge class.
	 */
	public abstract EdgeClass edgeClass( final V from, final V to );

	/**
	 * Enumeration of the possible edge class during a graph search.
	 * <p>
	 * A graph search generates a spanning tree from the graph iterated. As they
	 * are crossed during the search, graph edges can be assigned a class
	 * depending on how they relate to the spanning tree.
	 *
	 * @author Jean-Yves Tinevez
	 */
	public static enum EdgeClass
	{
		/**
		 * Graph edges that belong to the search tree.
		 */
		TREE,
		/**
		 * Graph edges that link to an ancestor vertex in the search tree.
		 */
		BACK,
		/**
		 * Graph edges that link to a indirect descendant vertex in the search
		 * tree.
		 */
		FORWARD,
		/**
		 * Graph edges that link to another branch in the search tree.
		 */
		CROSS,
		/**
		 * Graph edges that have not been classified yet.
		 */
		UNCLASSIFIED;
	}

	protected abstract void visit( V vertex );

}
