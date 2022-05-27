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
package org.mastodon.graph.algorithm.traversal;

import org.mastodon.collection.RefStack;
import org.mastodon.graph.Edge;
import org.mastodon.graph.ReadOnlyGraph;
import org.mastodon.graph.Vertex;
import org.mastodon.graph.algorithm.util.Graphs;

/**
 * A Depth-first iterator, that traverses edges regardless of direction.
 * <p>
 * With {@code A -> B}, the iterator will move from A to B, and also from B
 * to A.
 *
 * @author Jean-Yves Tinevez
 * @author Tobias Pietzsch
 *
 * @param <V>
 *            the type of the graph vertices iterated.
 * @param <E>
 *            the type of the graph edges iterated.
 */
public class UndirectedDepthFirstIterator< V extends Vertex< E >, E extends Edge< V > > extends AbstractGraphIteratorAlgorithm< V, E >
{
	private final RefStack< V > stack;

	public UndirectedDepthFirstIterator( final V root, final ReadOnlyGraph< V, E > graph )
	{
		super( graph );
		stack = createVertexStack();
		reset( root );
	}

	public UndirectedDepthFirstIterator( final ReadOnlyGraph< V, E > graph )
	{
		super( graph );
		stack = createVertexStack();
	}

	public void reset( final V root )
	{
		super.reset();
		stack.push( root );
		fetchNext();
		visited.add( root );
	}

	@Override
	protected Iterable< E > neighbors( final V vertex )
	{
		return vertex.edges();
	}

	@Override
	protected V targetOf( final V source, final E edge, final V ref )
	{
		return Graphs.getOppositeVertex( edge, source, ref );
	}

	@Override
	protected V fetch( final V ref )
	{
		return stack.pop( ref );
	}

	@Override
	protected void toss( final V vertex )
	{
		stack.push( vertex );
	}

	@Override
	protected boolean canFetch()
	{
		return !stack.isEmpty();
	}
}
