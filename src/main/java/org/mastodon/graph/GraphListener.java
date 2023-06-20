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
package org.mastodon.graph;

public interface GraphListener< V extends Vertex< E >, E extends Edge< V > >
{
	/**
	 * Called when the graph has been changed completely, for example, when it is loaded from a file.
	 * This should lead to a re-initialization of the listener.
	 */
	public void graphRebuilt();

	/**
	 * Called when a vertex was added to the graph.
	 *
	 * @param vertex
	 *            the vertex added.
	 */
	public void vertexAdded( V vertex );

	/**
	 * Called before a vertex is removed from the graph.
	 *
	 * @param vertex
	 *            the vertex removed.
	 */
	public void vertexRemoved( V vertex ); // TODO rename beforeRemoveVertex

	/**
	 * Call when an edge was added to the graph.
	 *
	 * @param edge
	 *            the edge added.
	 */
	public void edgeAdded( E edge );

	/**
	 * Called before an edge is removed from the graph.
	 *
	 * @param edge
	 *            the edge removed.
	 */
	public void edgeRemoved( E edge ); // TODO rename beforeRemoveEdge
}
