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
package org.mastodon.graph.object;

import java.util.Collection;
import java.util.Collections;

import org.mastodon.collection.RefCollection;
import org.mastodon.collection.wrap.RefCollectionWrapper;
import org.mastodon.graph.Edges;
import org.mastodon.graph.Graph;

public abstract class AbstractObjectGraph< V extends AbstractObjectVertex< V, E >, E extends AbstractObjectEdge< E, V > > implements Graph< V, E >
{

	public interface Factory< V, E >
	{
		public V createVertex();

		public E createEdge( V source, V target );
	}

	private final Factory< V, E > factory;

	private final Collection< V > vertices;

	private final Collection< E > edges;

	private final RefCollectionWrapper< V > unmodifiableVertices;

	private final RefCollectionWrapper< E > unmodifiableEdges;

	protected AbstractObjectGraph( final Factory< V, E > factory, final Collection< V > vertices, final Collection< E > edges )
	{
		this.factory = factory;
		this.vertices = vertices;
		this.edges = edges;
		unmodifiableVertices = new RefCollectionWrapper<>( Collections.unmodifiableCollection( vertices ) );
		unmodifiableEdges = new RefCollectionWrapper<>( Collections.unmodifiableCollection( edges ) );
	}

	protected void clear()
	{
		vertices.clear();
		edges.clear();
	}

	@Override
	public V addVertex()
	{
		final V vertex = factory.createVertex();
		vertices.add( vertex );
		return vertex;
	}

	@Override
	public V addVertex( final V ref )
	{
		return addVertex();
	}

	@Override
	public E addEdge( final V source, final V target )
	{
		final E edge = factory.createEdge( source, target );
		source.outgoing.edges.add( edge );
		target.incoming.edges.add( edge );
		edges.add( edge );
		return edge;
	}

	@Override
	public E addEdge( final V source, final V target, final E ref )
	{
		return addEdge( source, target );
	}

	@Override
	public E insertEdge( final V source, final int sourceOutIndex, final V target, final int targetInIndex )
	{
		final E edge = factory.createEdge( source, target );
		source.outgoing.edges.add( Math.min( Math.max( 0, sourceOutIndex ), source.outgoing.size() ), edge );
		target.incoming.edges.add( Math.min( Math.max( 0, targetInIndex ), target.incoming.size() ), edge );
		edges.add( edge );
		return edge;
	}

	@Override
	public E insertEdge( final V source, final int sourceOutIndex, final V target, final int targetInIndex, final E ref )
	{
		return insertEdge( source, sourceOutIndex, target, targetInIndex );
	}

	@Override
	public E getEdge( final V source, final V target )
	{
		for ( final E edge : source.outgoing )
			if ( target.incoming.edges.contains( edge ) )
				return edge;
		return null;
	}

	@Override
	public E getEdge( final V source, final V target, final E ref )
	{
		return getEdge( source, target );
	}

	@Override
	public Edges< E > getEdges( final V source, final V target, final V ref )
	{
		return getEdges( source, target );
	}

	@Override
	public Edges< E > getEdges( final V source, final V target )
	{
		return new ObjectEdgesSourceToTarget<>( source, target, edges );
	}

	@Override
	public void remove( final V vertex )
	{
		if ( vertices.remove( vertex ) )
		{
			for ( final E edge : vertex.incoming )
			{
				edge.getSource().outgoing.edges.remove( edge );
				edges.remove( edge );
			}
			for ( final E edge : vertex.outgoing )
			{
				edge.getTarget().incoming.edges.remove( edge );
				edges.remove( edge );
			}
		}
 	}

	@Override
	public void remove( final E edge )
	{
		if ( edges.remove( edge ) )
		{
			edge.getSource().outgoing.edges.remove( edge );
			edge.getTarget().incoming.edges.remove( edge );
		}
	}

	@Override
	public RefCollection< V > vertices()
	{
		return unmodifiableVertices;
	}

	@Override
	public RefCollection< E > edges()
	{
		return unmodifiableEdges;
	}

	@Override
	public V vertexRef()
	{
		return null;
	}

	@Override
	public E edgeRef()
	{
		return null;
	}

	@Override
	public void releaseRef( final V ref )
	{}

	@Override
	public void releaseRef( final E ref )
	{}
}
