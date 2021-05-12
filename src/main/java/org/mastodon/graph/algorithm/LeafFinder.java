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

import org.mastodon.collection.RefSet;
import org.mastodon.graph.Edge;
import org.mastodon.graph.ReadOnlyGraph;
import org.mastodon.graph.Vertex;

/**
 * Small algorithm that returns a set of vertices that have no outgoing edges.
 * 
 * @param <V>
 *            the type of vertices in the graph.
 * @param <E>
 *            the type of edges in the graph.
 * @author Jean-Yves Tinevez
 */
public class LeafFinder< V extends Vertex< E >, E extends Edge< V > > extends AbstractGraphAlgorithm< V, E >
{
	private final RefSet< V > leaves;

	public LeafFinder( final ReadOnlyGraph< V, E > graph )
	{
		super( graph );
		this.leaves = createVertexSet();
		fetchLeaves();
	}

	public RefSet< V > get()
	{
		return leaves;
	}

	private void fetchLeaves()
	{
		for ( final V v : graph.vertices() )
			if ( v.outgoingEdges().isEmpty() )
				leaves.add( v );
	}

	@SuppressWarnings( { "rawtypes", "unchecked" } )
	public static < V extends Vertex< ? > > RefSet< V > getLeaves( final ReadOnlyGraph< V, ? > graph )
	{
		return new LeafFinder( graph ).get();
	}

}
