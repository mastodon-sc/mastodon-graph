/*-
 * #%L
 * Mastodon Graphs
 * %%
 * Copyright (C) 2015 - 2023 Tobias Pietzsch, Jean-Yves Tinevez
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

import java.util.HashSet;
import java.util.Set;

import org.mastodon.collection.RefSet;
import org.mastodon.graph.Edge;
import org.mastodon.graph.ReadOnlyGraph;
import org.mastodon.graph.Vertex;
import org.mastodon.graph.algorithm.util.Graphs;

/**
 * A class to generate the connected components of a graph, regardless of edge
 * direction.
 * <p>
 * This version of the algorithm does not listen to changes in the graph. It
 * simply generates a new set of components each time the {@link #get()} method
 * is called.
 *
 * @author Jean-Yves Tinevez.
 *
 * @param <V>
 *            the type of the vertices of the graph.
 * @param <E>
 *            the type of the edges of the graph.
 */
public class ConnectedComponents< V extends Vertex< E >, E extends Edge< V > > extends AbstractGraphAlgorithm< V, E >
{
	private final RefSet< V > visited;

	private final int minimalSize;

	/**
	 * Creates a new connected-components algorithm.
	 *
	 * @param graph
	 *            the graph to inspect.
	 * @param minimalSize
	 *            the minimal size for the components. Components with a size
	 *            smaller that this one will be ignored, and won't be present in
	 *            the set returned by {@link #get()}.
	 */
	public ConnectedComponents( final ReadOnlyGraph< V, E > graph, final int minimalSize )
	{
		super( graph );
		if ( minimalSize < 1 )
			throw new IllegalArgumentException( "Minimal size cannot be lower than 1, was " + minimalSize + "." );
		this.minimalSize = minimalSize;
		this.visited = createVertexSet();
	}

	/**
	 * Creates a new connected-components algorithm with a minimal size of 1.
	 * <p>
	 * With this minimal size, all the vertices of the graph will be returned in
	 * the set of components.
	 *
	 * @param graph
	 *            the graph to inspect.
	 */
	public ConnectedComponents( final ReadOnlyGraph< V, E > graph )
	{
		this( graph, 1 );
	}

	private void visit( final V v, final RefSet< V > currentComponent )
	{
		currentComponent.add( v );
		visited.add( v );
		final V tmp = vertexRef();
		for ( final E e : v.edges() )
		{
			final V o = Graphs.getOppositeVertex( e, v, tmp );
			if ( !visited.contains( o ) )
			{
				visit( o, currentComponent );
			};
		}
	}

	/**
	 * Returns the set of connected components of the graph. Edges are traversed
	 * regardless of their direction.
	 * <p>
	 * Unless they belong to a component of size below
	 * {@link ConnectedComponents#minimalSize}, it is ensured that all the
	 * vertices of the graph will be found exactly once in the connected
	 * component set.
	 * <p>
	 * The returned connected components are a snapshot of the graph
	 * connectivity when this method is called. Subsequent calls to this method
	 * will return a new set, that accounts for the latest graph modifications.
	 *
	 * @return a new {@link Set} containing the connected components of the
	 *         graph.
	 */
	public Set< RefSet< V > > get()
	{
		final HashSet< RefSet< V >> components = new HashSet< RefSet< V > >();
		visited.clear();

		for ( final V v : graph.vertices() )
		{
			if ( visited.contains( v ) )
			{
				continue;
			}

			final RefSet< V > currentComponent = createVertexSet();
			visit( v, currentComponent );
			if ( currentComponent.size() >= minimalSize )
			{
				components.add( currentComponent );
			}
		}
		return components;
	}
}
