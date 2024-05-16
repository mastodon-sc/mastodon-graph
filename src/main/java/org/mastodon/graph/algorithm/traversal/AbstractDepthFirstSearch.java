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
package org.mastodon.graph.algorithm.traversal;

import org.mastodon.collection.RefIntMap;
import org.mastodon.collection.RefList;
import org.mastodon.graph.Edge;
import org.mastodon.graph.Edges;
import org.mastodon.graph.ReadOnlyGraph;
import org.mastodon.graph.Vertex;
import org.mastodon.graph.algorithm.util.Graphs;

public class AbstractDepthFirstSearch< T extends AbstractDepthFirstSearch< T, V, E >, V extends Vertex< E >, E extends Edge< V > > extends GraphSearch< T, V, E >
{

	private static final int NO_ENTRY_VALUE = -1;

	protected final SearchDirection directivity;

	protected final RefIntMap< V > entryTime;

	protected int time;

	/**
	 * Keeps track of the last vertex when reaching the end of a connected
	 * component.
	 */
	protected V unqueued;

	public AbstractDepthFirstSearch( final ReadOnlyGraph< V, E > graph, final SearchDirection directivity )
	{
		super( graph );
		this.directivity = directivity;
		this.entryTime = createVertexIntMap( NO_ENTRY_VALUE );
		this.unqueued = vertexRef();
	}

	@Override
	public void start( final V start )
	{
		time = 0;
		entryTime.clear();
		super.start( start );
	}

	@SuppressWarnings( "unchecked" )
	@Override
	protected void visit( final V vertex )
	{
		if ( wasAborted() )
			return;

		time++;
		entryTime.put( vertex, time );
		discovered.add( vertex );
		if ( null != searchListener )
			searchListener.processVertexEarly( vertex, ( T ) this );

		/*
		 * Collect target vertices and edges.
		 */

		final RefList< V > targets;
		final RefList< E > targetEdges;
		V target = vertexRef();
		switch ( directivity )
		{
		case DIRECTED:
		{
			final Edges< E > edges = vertex.outgoingEdges();
			targets = createVertexList( edges.size() );
			targetEdges = createEdgeList( edges.size() );
			for ( final E e : edges )
			{
				target = e.getTarget( target );
				targets.add( target );
				targetEdges.add( e );
			}
			break;
		}
		case REVERSED:
		{
			final Edges< E > edges = vertex.incomingEdges();
			targets = createVertexList( edges.size() );
			targetEdges = createEdgeList( edges.size() );
			for ( final E e : edges )
			{
				target = e.getSource( target );
				targets.add( target );
				targetEdges.add( e );
			}
			break;
		}
		case UNDIRECTED:
		default:
		{
			final Edges< E > edges = vertex.edges();
			targets = createVertexList( edges.size() );
			targetEdges = createEdgeList( edges.size() );
			for ( final E e : edges )
			{
				target = Graphs.getOppositeVertex( e, vertex, target );
				targets.add( target );
				targetEdges.add( e );
			}
			break;
		}
		}

		/*
		 * Potentially sort vertices and edges according to vertices sort order.
		 */

		if ( null != comparator && targets.size() > 1 )
		{
			Graphs.sort( targets, comparator, targetEdges );
		}

		/*
		 * Discover vertices across these edges.
		 */

		E edge = edgeRef();
		for ( int i = 0; i < targets.size(); i++ )
		{
			edge = targetEdges.get( i, edge );
			target = targets.get( i, target );

			if ( !discovered.contains( target ) )
			{
				parents.put( target, vertex );
				if ( null != searchListener )
					searchListener.processEdge( edge, vertex, target, ( T ) this );
				visit( target );
			}
			else if ( null != searchListener &&
					( directivity != SearchDirection.UNDIRECTED ||
							( !processed.contains( target ) && !parents.get( vertex ).equals( target ) ) ) )
			{
				searchListener.processEdge( edge, vertex, target, ( T ) this );
			}

			if ( wasAborted() )
				return;
		}

		if ( null != searchListener )
			searchListener.processVertexLate( vertex, ( T ) this );
		time++;

		unqueued = assign( vertex, unqueued );
		processed.add( vertex );
		releaseRef( target );
		releaseRef( edge );
	}

	/**
	 * Returns the time of visit for the specified vertex.
	 *
	 * @param vertex
	 *            the vertex to time.
	 * @return the vertex discovery time.
	 */
	public int timeOf( final V vertex )
	{
		return entryTime.get( vertex );
	}

	@Override
	public EdgeClass edgeClass( final V from, final V to )
	{
		if ( from.equals( parents.get( to ) ) ) { return EdgeClass.TREE; }
		if ( discovered.contains( to ) && !processed.contains( to ) ) { return EdgeClass.BACK; }
		if ( processed.contains( to ) )
		{
			if ( timeOf( from ) < timeOf( to ) )
			{
				return EdgeClass.FORWARD;
			}
			else
			{
				return EdgeClass.CROSS;
			}
		}
		return EdgeClass.UNCLASSIFIED;
	}
}
