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
package org.mastodon.graph.algorithm.traversal;

import org.mastodon.collection.RefDeque;
import org.mastodon.collection.RefIntMap;
import org.mastodon.graph.Edge;
import org.mastodon.graph.Edges;
import org.mastodon.graph.ReadOnlyGraph;
import org.mastodon.graph.Vertex;
import org.mastodon.graph.algorithm.util.Graphs;

/**
 * Base class for breadth-first searches.
 * <p>
 * 
 * @author Jean-Yves Tinevez
 *
 * @param <T>
 *            the type of the concrete search.
 * @param <V>
 *            the type of vertices iterated.
 * @param <E>
 *            the type of edges iterated.
 */
public class AbstractBreadthFirstSearch< T extends AbstractBreadthFirstSearch< T, V, E >, V extends Vertex< E >, E extends Edge< V > > extends GraphSearch< T, V, E >
{

	private static final int NO_ENTRY_VALUE = -1;

	protected final RefDeque< V > queue;

	protected final RefIntMap< V > depths;

	private final SearchDirection directed;

	/** Last vertex unqueued. */
	protected V unqueued;

	public AbstractBreadthFirstSearch( final ReadOnlyGraph< V, E > graph, final SearchDirection directed )
	{
		super( graph );
		this.directed = directed;
		this.queue = createVertexDeque();
		this.depths = createVertexIntMap( NO_ENTRY_VALUE );
		this.unqueued = graph.vertexRef();
	}

	@Override
	public void start( final V start )
	{
		queue.clear();
		queue.add( start );
		depths.clear();
		depths.put( start, 0 );
		super.start( start );
	}

	public int depthOf( final V vertex )
	{
		return depths.get( vertex );
	}

	@SuppressWarnings( "unchecked" )
	@Override
	protected void visit( final V start )
	{
		discovered.add( start );

		final V tmpRef = vertexRef();
		while ( !queue.isEmpty() )
		{
			if ( wasAborted() )
				return;

			final V vertex = queue.poll( tmpRef );
			unqueued = assign( vertex, unqueued );
			final int level = depths.get( vertex );

			if ( null != searchListener )
				searchListener.processVertexEarly( vertex, ( T ) this );
			processed.add( vertex );

			final Edges< E > edges;
			switch ( directed )
			{
			case DIRECTED:
				edges = vertex.outgoingEdges();
				break;

			case REVERSED:
				edges = vertex.incomingEdges();
				break;

			case UNDIRECTED:
			default:
				edges = vertex.edges();
				break;
			}

			V target = vertexRef();
			for ( final E edge : edges )
			{
				target = Graphs.getOppositeVertex( edge, vertex, target );
				if ( !discovered.contains( target ) )
				{
					queue.add( target );
					parents.put( target, vertex );
					depths.put( target, level + 1 );
				}
				if ( null != searchListener &&
						( directed != SearchDirection.UNDIRECTED ||
								!processed.contains( target ) ) )
				{
					searchListener.processEdge( edge, vertex, target, ( T ) this );
				}
				if ( !discovered.contains( target ) )
				{
					discovered.add( target );
				}
				if ( wasAborted() )
					return;
			}
			if ( null != searchListener )
				searchListener.processVertexLate( vertex, ( T ) this );
		}
	}

	@Override
	public EdgeClass edgeClass( final V from, final V to )
	{
		if ( !discovered.contains( to ) ) { return EdgeClass.TREE; }

		int toDepth = depths.get( to );
		int fromDepth = depths.get( from );

		V b = vertexRef();
		b = assign( to, b );
		while ( toDepth > 0 && fromDepth < toDepth )
		{
			b = parents.get( b, b );
			toDepth = depths.get( b );
		}

		V a = vertexRef();
		a = assign( from, a );
		while ( fromDepth > 0 && toDepth < fromDepth )
		{
			a = parents.get( a, a );
			fromDepth = depths.get( a );
		}

		if ( a.equals( b ) )
		{
			releaseRef( a );
			releaseRef( b );
			return EdgeClass.BACK;
		}
		else
		{
			releaseRef( a );
			releaseRef( b );
			return EdgeClass.CROSS;
		}

	}

}
