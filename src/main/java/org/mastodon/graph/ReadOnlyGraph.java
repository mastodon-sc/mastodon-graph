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

import org.mastodon.collection.RefCollection;

/**
 * A read-only graph consisting of vertices of type {@code V} and edges of type
 * {@code E}. "Read-only" means that the graph cannot be modified through this
 * interface. However, this does not imply that the graph is immutable.
 *
 * @param <V>
 *            the {@link Vertex} type of the {@link ReadOnlyGraph}.
 * @param <E>
 *            the {@link Edge} type of the {@link ReadOnlyGraph}.
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public interface ReadOnlyGraph< V extends Vertex< E >, E extends Edge< V > >
{
	/**
	 * Returns the directed edge from vertex {@code source} to {@code target} if
	 * it exists, or {@code null} otherwise.
	 *
	 * @param source
	 *            the source vertex of the directed edge.
	 * @param target
	 *            the target vertex of the directed edge.
	 * @return the directed edge from vertex {@code source} to {@code target} if
	 *         it exists, or {@code null} otherwise.
	 */
	public E getEdge( final V source, final V target );

	/**
	 * Returns the directed edge from vertex {@code source} to {@code target} if
	 * it exists, or {@code null} otherwise.
	 *
	 * <p>
	 * This method is a (potentially) allocation-free version of
	 * {@link #getEdge(Vertex, Vertex)}
	 *
	 * @param source
	 *            the source vertex of the directed edge.
	 * @param target
	 *            the target vertex of the directed edge.
	 * @param ref
	 *            an edge reference that can be used for retrieval. Depending on
	 *            concrete implementation, this object can be cleared, ignored
	 *            or re-used.
	 * @return the directed edge from vertex {@code source} to {@code target} if
	 *         it exists, or {@code null} otherwise. The object actually
	 *         returned might be the specified {@code ref}, depending on
	 *         concrete implementation.
	 */
	public E getEdge( final V source, final V target, final E ref );

	/**
	 * Returns the collection of directed edges linking vertex {@code source} to
	 * {@code target}.
	 * <p>
	 * For simple graphs, there might be 0 or 1 such edges, but not more. For
	 * general graphs, there might be 0, 1 or several edges linking
	 * {@code source} to {@code target}. When there is no edge from
	 * {@code source} to {@code target}, the collection returned is empty, but
	 * never {@code null}. Iterators over this collection support element
	 * removal.
	 *
	 * @param source
	 *            the source vertex.
	 * @param target
	 *            the target vertex.
	 * @return the collection of edges linking vertex {@code source} to
	 *         {@code target}.
	 */
	public Edges< E > getEdges( final V source, final V target );

	/**
	 * Returns the collection of directed edges linking vertex {@code source} to
	 * {@code target}.
	 * <p>
	 * For simple graphs, there might be 0 or 1 such edges, but not more. For
	 * general graphs, there might be 0, 1 or several edges linking
	 * {@code source} to {@code target}. When there is no edge from
	 * {@code source} to {@code target}, the collection returned is empty, but
	 * never {@code null}. Iterators over this collection support element
	 * removal.
	 * <p>
	 * This method is a (potentially) allocation-free version of
	 * {@link #getEdges(Vertex, Vertex)}
	 *
	 *
	 * @param source
	 *            the source vertex.
	 * @param target
	 *            the target vertex.
	 * @param ref
	 *            a vertex reference that can be used for retrieval. Depending
	 *            on concrete implementation, this object can be cleared,
	 *            ignored or re-used.
	 * @return the collection of edges linking vertex {@code source} to
	 *         {@code target}.
	 */
	public Edges< E > getEdges( final V source, final V target, final V ref );

	/**
	 * Generates a vertex reference that can be used for retrieval. Depending on
	 * concrete implementation this method may return {@code null.}
	 *
	 * @return a new, uninitialized, vertex reference.
	 */
	public V vertexRef();

	/**
	 * Generates an edge reference that can be used for retrieval. Depending on
	 * concrete implementation this method may return {@code null.}
	 *
	 * @return a new, uninitialized, edge reference.
	 */
	public E edgeRef();

	/**
	 * Releases a previously created vertex reference. Depending on concrete
	 * implementation, this method might not do anything.
	 *
	 * @param ref
	 *            the vertex reference to release.
	 */
	public void releaseRef( final V ref );

	/**
	 * Releases a previously created edge reference. Depending on concrete
	 * implementation, this method might not do anything.
	 *
	 * @param ref
	 *            the edge reference to release.
	 */
	public void releaseRef( final E ref );

	/**
	 * Returns the vertices of this graph as an unmodifiable collection. In the
	 * returned {@link RefCollection}, only {@code isEmpty(),} {@code size(),}
	 * {@code iterator(),} {@code createRef()}, and {@code releaseRef()} are
	 * guaranteed to be implemented.
	 *
	 * @return unmodifiable collection of vertices. Only {@code isEmpty(),}
	 *         {@code size(),} {@code iterator(),} {@code createRef()}, and
	 *         {@code releaseRef()} are guaranteed to be implemented.
	 */
	public RefCollection< V > vertices();

	/**
	 * Returns the edges of this graph as an unmodifiable collection. In the
	 * returned {@link RefCollection}, only {@code isEmpty(),} {@code size(),}
	 * {@code iterator(),} {@code createRef()}, and {@code releaseRef()} are
	 * guaranteed to be implemented.
	 *
	 * @return unmodifiable collection of edges. Only {@code isEmpty(),}
	 *         {@code size(),} {@code iterator(),} {@code createRef()}, and
	 *         {@code releaseRef()} are guaranteed to be implemented.
	 */
	public RefCollection< E > edges();
}
