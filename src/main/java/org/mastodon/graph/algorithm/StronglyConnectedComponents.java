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

import java.util.HashSet;
import java.util.Set;

import org.mastodon.collection.RefSet;
import org.mastodon.collection.RefStack;
import org.mastodon.graph.Edge;
import org.mastodon.graph.ReadOnlyGraph;
import org.mastodon.graph.Vertex;
import org.mastodon.properties.IntPropertyMap;

/**
 * A class to generate the strongly connected components of a directed graph.
 * Implements <a href=
 * "https://en.wikipedia.org/wiki/Tarjan%27s_strongly_connected_components_algorithm">Tarjan's
 * strongly connected components algorithm</a>.
 * <p>
 * TODO: The current implementation is recursive, processes the whole graph
 * immediately, and collects all strongly connected components into a
 * {@code HashSet}. It is therefore not really suited to very large or deep
 * graphs. It would be better to convert recursion to iteration, then process
 * until one SCC is completed, and provide access to SCCs through an iterator.
 *
 * @author Tobias Pietzsch.
 *
 * @param <V>
 *            the type of the vertices of the graph.
 * @param <E>
 *            the type of the edges of the graph.
 */
public class StronglyConnectedComponents< V extends Vertex< E >, E extends Edge< V > > extends AbstractGraphAlgorithm< V, E >
{
	/**
	 * Get the strongly connected components of the specified directed
	 * {@code graph}.
	 * 
	 * @param graph
	 *            the graph.
	 * @param <V>
	 *            the type of vertices in the graph.
	 * @param <E>
	 *            the type of edges in the graph.
	 * @return a new set of sets. One set of vertices per connected component.
	 */
	public static < V extends Vertex< E >, E extends Edge< V > >
			Set< RefSet< V > > stronglyConnectedComponents( final ReadOnlyGraph< V, E > graph )
	{
		return new StronglyConnectedComponents<>( graph ).get();
	}

	/**
	 * Creates a new strongly-connected-components algorithm.
	 *
	 * @param graph
	 *            the graph to inspect.
	 */
	public StronglyConnectedComponents( final ReadOnlyGraph< V, E > graph )
	{
		super( graph );
		index = new IntPropertyMap<>( graph.vertices(), -1 );
		lowlink = new IntPropertyMap<>( graph.vertices(), -1 );
		onStack = createVertexSet();
		stack = createVertexStack();
	}

	/**
	 * Returns the set of strongly connected components of the graph.
	 * <p>
	 * The returned strongly connected components are a snapshot of the graph
	 * connectivity when this method is called. Subsequent calls to this method
	 * will return a new set, that accounts for the latest graph modifications.
	 *
	 * @return a new {@link Set} containing the strongly connected components of
	 *         the graph.
	 */
	public Set< RefSet< V > > get()
	{
		reset();
		compute();
		return components;
	}

	private int i;

	private final IntPropertyMap< V > index;

	private final IntPropertyMap< V > lowlink;

	private final RefSet< V > onStack;

	private final RefStack< V > stack;

	private HashSet< RefSet< V > > components;

	private void reset()
	{
		i = 0;
		index.beforeClearPool(); // just clears without emitting events
		lowlink.beforeClearPool(); // just clears without emitting events
		onStack.clear();
		stack.clear();
		components = new HashSet<>();
	}

	private void compute()
	{
		for ( final V v : graph.vertices() )
		{
			if ( !index.isSet( v ) )
				strongconnect( v );
		}
	}

	private void strongconnect( final V v )
	{
		// Set the depth index for v to the smallest unused index
		index.set( v, i );
		lowlink.set( v, i );
		++i;
		stack.push( v );
		onStack.add( v );

		// Consider successors of v
		final V ref = graph.vertexRef();
		for ( final E e : v.outgoingEdges() )
		{
			final V w = e.getTarget( ref );
			if ( !index.isSet( w ) )
			{
				// Successor w has not yet been visited; recurse on it
				strongconnect( w );
				lowlink.set( v, Math.min( lowlink.getInt( v ), lowlink.getInt( w ) ) );
			}
			else if ( onStack.contains( w ) )
			{
				// Successor w is in stack S and hence in the current SCC
				// If w is not on stack, then (v, w) is a cross-edge in the DFS tree and must be ignored
				// Note: The next line may look odd - but is correct.
				// It says w.index not w.lowlink; that is deliberate and from the original paper
				lowlink.set( v, Math.min( lowlink.getInt( v ), index.getInt( w ) ) );
			}
		}

		// If v is a root node, pop the stack and generate an SCC
		if ( lowlink.getInt( v ) == index.getInt( v ) )
		{
			// start a new strongly connected component
			final RefSet< V > scc = createVertexSet();
			V w;
			do
			{
				w = stack.pop( ref );
				onStack.remove( w );
				scc.add( w );
			}
			while ( !w.equals( v ) );
			// output the current strongly connected component
			components.add( scc );
		}
		graph.releaseRef( ref );
	}
}
