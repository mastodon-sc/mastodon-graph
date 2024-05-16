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
package org.mastodon.graph;

/**
 * A graph consisting of vertices of type {@code V} and edges of type {@code E}.
 *
 * @param <V>
 *            the {@link Vertex} type of the {@link Graph}.
 * @param <E>
 *            the {@link Edge} type of the {@link Graph}.
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public interface Graph< V extends Vertex< E >, E extends Edge< V > > extends ReadOnlyGraph< V, E >
{
	public V addVertex();

	public V addVertex( final V ref );

	/**
	 * Add a new directed {@link Edge} from {@code source} to {@code target}.
	 *
	 * @param source
	 *            the source vertex.
	 * @param target
	 *            the target vertex.
	 * @return the newly created edge.
	 */
	public E addEdge( final V source, final V target );

	/**
	 * Add a new directed {@link Edge} from {@code source} to {@code target}.
	 *
	 * <p>
	 * This method is a (potentially) allocation-free version of
	 * {@link #addEdge(Vertex, Vertex)}.
	 *
	 * @param source
	 *            the source vertex.
	 * @param target
	 *            the target vertex.
	 * @param ref
	 *            an edge reference that can be used for returning the newly
	 *            created edge. Depending on concrete implementation, this
	 *            object can be cleared, ignored or re-used.
	 * @return the newly created edge. The object actually returned might be the
	 *         specified {@code ref}, depending on concrete implementation.
	 */
	public E addEdge( final V source, final V target, final E ref );

	/**
	 * Add a new {@link Edge} between {@code source} and {@code target}. The new
	 * edge is inserted in the source and target edge lists such that
	 * {@link Edge#getSourceOutIndex()}{@code == sourceOutIndex} and
	 * {@link Edge#getTargetInIndex()}{@code == targetInIndex}.
	 *
	 * <p>
	 * Optional operation implemented by graphs that maintain edge order.
	 *
	 * @param source
	 *            the source vertex.
	 * @param sourceOutIndex
	 *            the index at which to insert the source vertex in the source
	 *            list.
	 * @param target
	 *            the target vertex.
	 * @param targetInIndex
	 *            the index at which to insert the target vertex in the target
	 *            list.
	 * @return the newly created edge.
	 */
	public E insertEdge( final V source, final int sourceOutIndex, final V target, final int targetInIndex );

	/**
	 * Add a new {@link Edge} between {@code source} and {@code target}. The new
	 * edge is inserted in the source and target edge lists such that
	 * {@link Edge#getSourceOutIndex()}{@code == sourceOutIndex} and
	 * {@link Edge#getTargetInIndex()}{@code == targetInIndex}.
	 *
	 * <p>
	 * Optional operation implemented by graphs that maintain edge order.
	 *
	 * <p>
	 * This method is a (potentially) allocation-free version of
	 * {@link #insertEdge(Vertex, int, Vertex, int)}.
	 *
	 * @param source
	 *            the source vertex.
	 * @param sourceOutIndex
	 *            the index at which to insert the source vertex in the source
	 *            list.
	 * @param target
	 *            the target vertex.
	 * @param targetInIndex
	 *            the index at which to insert the target vertex in the target
	 *            list.
	 * @param ref
	 *            an edge reference that can be used for returning the newly
	 *            created edge. Depending on concrete implementation, this
	 *            object can be cleared, ignored or re-used.
	 * @return the newly created edge. The object actually returned might be the
	 *         specified {@code ref}, depending on concrete implementation.
	 */
	public E insertEdge( final V source, final int sourceOutIndex, final V target, final int targetInIndex, final E ref );

	public void remove( final V vertex );

	public void remove( final E edge );
}
