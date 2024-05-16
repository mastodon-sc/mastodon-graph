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

import org.mastodon.graph.Edge;
import org.mastodon.graph.ReadOnlyGraph;
import org.mastodon.graph.Vertex;
import org.mastodon.graph.algorithm.LeafFinder;

/**
 * Same as {@link DepthFirstCrossComponentIterator} on a graph where all
 * directed edges are pointing in the opposite direction.
 * <p>
 * It is therefore capital to specify the leaves of the graph to ensure
 * iteration of the whole graph.
 *
 * @param <V>
 *            the type of vertices in the graph.
 * @param <E>
 *            the type of edges in the graph.
 * @author Jean-Yves Tinevez
 */
public class InverseDepthFirstCrossComponentIterator< V extends Vertex< E >, E extends Edge< V > > extends DepthFirstCrossComponentIterator< V, E >
{

	/**
	 * Creates a depth-first, cross-component inverse iterator starting from the
	 * specified vertex.
	 * <p>
	 * The collection of leaves is determined automatically at creation.
	 *
	 * @param start
	 *            the vertex to start iteration with.
	 * @param graph
	 *            the graph to iterate over.
	 */
	public InverseDepthFirstCrossComponentIterator( final V start, final ReadOnlyGraph< V, E > graph )
	{
		this( start, graph, LeafFinder.getLeaves( graph ) );
	}

	/**
	 * Creates a depth-first, cross-component inverse iterator starting from the
	 * specified vertex, using the specified collection of leaves to jump across
	 * connected-components.
	 * <p>
	 * The order in which the connected-components are iterated can be specified
	 * by using this constructor, using a list with the desired order. For this
	 * iterator to operate properly and indeed iterate through all the vertices
	 * of the graph exactly once, the specified collection of leaves must
	 * include all the leaves of the graph, that is: all the vertices that have
	 * no outgoing edges.
	 *
	 * @param start
	 *            the vertex to start iteration with.
	 * @param graph
	 *            the graph to iterate over.
	 * @param leaves
	 *            an iterable over the collection of leaves.
	 */
	public InverseDepthFirstCrossComponentIterator( final V start, final ReadOnlyGraph< V, E > graph, final Iterable< V > leaves )
	{
		super( start, graph, leaves );
	}

	@Override
	protected Iterable< E > neighbors( final V vertex )
	{
		return vertex.incomingEdges();
	}

	@Override
	protected V targetOf( final V source, final E edge, final V ref )
	{
		return edge.getSource( ref );
	}
}
