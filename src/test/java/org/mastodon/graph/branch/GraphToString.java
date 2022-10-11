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
package org.mastodon.graph.branch;

import org.mastodon.graph.Edge;
import org.mastodon.graph.ReadOnlyGraph;
import org.mastodon.graph.Vertex;

import java.util.StringJoiner;
import java.util.function.Function;

public class GraphToString
{
	public static <V extends Vertex<E>, E extends Edge<V>> String toString( ReadOnlyGraph<V, E> graph, Function<V, String> vertexToString) {
		V sRef = graph.vertexRef();
		V tRef = graph.vertexRef();
		try {
			StringJoiner joiner = new StringJoiner( ", " );
			graph.edges().stream()
					.map( edge -> vertexToString.apply( edge.getSource(sRef) ) + "->" +
							vertexToString.apply( edge.getTarget(tRef ) ) )
					.sorted()
					.forEach( joiner::add );
			graph.vertices().stream()
					.filter( vertex -> vertex.incomingEdges().isEmpty() && vertex.outgoingEdges().isEmpty() )
					.map( vertexToString )
					.sorted()
					.forEach( joiner::add );
			return joiner.toString();
		}
		finally
		{
			graph.releaseRef( sRef );
			graph.releaseRef( tRef );
		}
	}
}
