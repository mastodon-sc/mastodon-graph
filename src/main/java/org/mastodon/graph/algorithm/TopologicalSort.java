/*-
 * #%L
 * Mastodon Graphs
 * %%
 * Copyright (C) 2015 - 2025 Tobias Pietzsch, Jean-Yves Tinevez
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

import java.util.Iterator;

import org.mastodon.collection.RefList;
import org.mastodon.collection.RefSet;
import org.mastodon.graph.Edge;
import org.mastodon.graph.Graph;
import org.mastodon.graph.Vertex;

/**
 * A topological order sort for a direct acyclic graph.
 * <p>
 * If the graph provided is not acyclic, the flag returned by the
 * {@link #hasFailed()} method set to {@code true} to indicate the problem.
 *
 * @param <V>
 *            the type of vertices in the graph.
 * @param <E>
 *            the type of edges in the graph.
 * @author Jean-Yves Tinevez
 */
public class TopologicalSort< V extends Vertex< E >, E extends Edge< V > > extends AbstractGraphAlgorithm< V, E >
{
	private boolean failed;

	private RefSet< V > marked;

	private RefSet< V > temporaryMarked;

	private final RefList< V > list;

	public TopologicalSort( final Graph< V, E > graph )
	{
		super( graph );
		this.failed = false;
		this.marked = createVertexSet();
		this.temporaryMarked = createVertexSet();
		this.list = createVertexList();
		fetchList();
	}

	/**
	 * Returns the topologically sorted vertices in a list.
	 *
	 * @return a new {@link RefList} resulting from this sort operation.
	 */
	public RefList< V > get()
	{
		return list;
	}

	/**
	 * Returns {@code true} if the graph iterated has a cycle.
	 *
	 * @return {@code true} if the graph iterated is not a directed acyclic
	 *         graph.
	 */
	public boolean hasFailed()
	{
		return failed;
	}

	private void fetchList()
	{
		final Iterator< V > vit = graph.vertices().iterator();
		while ( vit.hasNext() && !failed )
		{
			final V v1 = vit.next();
			if ( !marked.contains( v1 ) )
			{
				visit( v1 );
			}
		}
		marked = null;
		temporaryMarked = null;
	}

	private void visit( final V vertex )
	{
		if ( temporaryMarked.contains( vertex ) )
		{
			failed = true;
			return;
		}

		if ( !marked.contains( vertex ) )
		{
			V v2 = graph.vertexRef();
			temporaryMarked.add( vertex );
			for ( final Edge< V > e : vertex.outgoingEdges() )
			{
				v2 = e.getTarget( v2 );
				visit( v2 );
			}
			graph.releaseRef( v2 );

			marked.add( vertex );
			temporaryMarked.remove( vertex );
			list.add( vertex );
		}
	}
}
