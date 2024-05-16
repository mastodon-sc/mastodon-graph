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

import java.util.Iterator;

/**
 * An iterable list of edges.
 *
 * @param <E>
 *            the {@link Edge} type of the {@link Graph}.
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public interface Edges< E extends Edge< ? > > extends Iterable< E >
{
	/**
	 * How many edges does this list contain?
	 *
	 * @return number of edges in this list.
	 */
	public int size();

	public boolean isEmpty();

	/**
	 * Get the <em>i</em>th edge in this list.
	 *
	 * <p>
	 * This allocates a new proxy object to hold the reference to the edge
	 * vertex. It is recommended to use the allocation-free
	 * {@link #get(int, Edge)} instead.
	 *
	 * @param i
	 *            index
	 * @return the <em>i</em>th edge in this list.
	 */
	public E get( final int i );

	/**
	 * Get the <em>i</em>th edge in this list.
	 *
	 * @param i
	 *            index
	 * @param edge
	 *            proxy object that will be set to reference the
	 *            <em>i</em>th edge.
	 * @return {@code edge} (the <em>i</em>th edge in this list).
	 */
	public E get( int i, final E edge );

	/**
	 * This creates an iterator that internally uses a new proxy object.
	 *
	 * <p>
	 * In contrast to this, {@link #iterator()} may reuse a proxy object if iterating
	 * <ul>
	 * <li> the same edge set {@link Vertex#edges()}, {@link Vertex#incomingEdges()}, or {@link Vertex#outgoingEdges()}
	 * <li> of the same vertex.
	 * </ul>
	 *
	 * So for the following: <pre>
	 * for ( E e0 : v.edges() )
	 *     for ( E e1 : v.edges() )
	 *         boolean b = ( e1 == e0 );</pre> it holds that {@code b == true}.
	 * For this kind of nested loops, {@code safe_iterator} is necessary.
	 *
	 * @return an iterator of the edge list.
	 */
	public Iterator< E > safe_iterator();
}
