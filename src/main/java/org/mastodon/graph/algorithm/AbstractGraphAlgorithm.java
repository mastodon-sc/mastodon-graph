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
package org.mastodon.graph.algorithm;

import java.util.Iterator;

import org.mastodon.collection.RefCollections;
import org.mastodon.collection.RefDeque;
import org.mastodon.collection.RefIntMap;
import org.mastodon.collection.RefList;
import org.mastodon.collection.RefMaps;
import org.mastodon.collection.RefObjectMap;
import org.mastodon.collection.RefRefMap;
import org.mastodon.collection.RefSet;
import org.mastodon.collection.RefStack;
import org.mastodon.graph.Edge;
import org.mastodon.graph.Graph;
import org.mastodon.graph.ReadOnlyGraph;
import org.mastodon.graph.Vertex;

/**
 * Base class for graph algorithms. Provides helper functions aimed at
 * minimizing the pain of dealing with graphs that may or may not be ref based.
 *
 * @param <V>
 *            the {@link Vertex} type of the {@link Graph}.
 * @param <E>
 *            the {@link Edge} type of the {@link Graph}.
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public abstract class AbstractGraphAlgorithm< V extends Vertex< E >, E extends Edge< V > >
{
	protected final ReadOnlyGraph< V, E > graph;

	private final Assigner< V > vertexAssigner;

	private final Assigner< E > edgeAssigner;

	public AbstractGraphAlgorithm( final ReadOnlyGraph< V, E > graph )
	{
		this.graph = graph;
		final V v = graph.vertexRef();
		vertexAssigner = Assigner.getFor( v );
		graph.releaseRef( v );
		final E e = graph.edgeRef();
		edgeAssigner = Assigner.getFor( e );
		graph.releaseRef( e );
	}

	protected V assign( final V value, final V target )
	{
		return vertexAssigner.assign( value, target );
	}

	protected E assign( final E value, final E target )
	{
		return edgeAssigner.assign( value, target );
	}

	protected V vertexRef()
	{
		return graph.vertexRef();
	}

	protected E edgeRef()
	{
		return graph.edgeRef();
	}

	protected void releaseRef( final V ref )
	{
		graph.releaseRef( ref );
	}

	protected void releaseRef( final E ref )
	{
		graph.releaseRef( ref );
	}

	protected RefSet< V > createVertexSet()
	{
		return RefCollections.createRefSet( graph.vertices() );
	}

	protected RefSet< V > createVertexSet( final int initialCapacity )
	{
		return RefCollections.createRefSet( graph.vertices(), initialCapacity );
	}

	protected RefSet< E > createEdgeSet()
	{
		return RefCollections.createRefSet( graph.edges() );
	}

	protected RefSet< E > createEdgeSet( final int initialCapacity )
	{
		return RefCollections.createRefSet( graph.edges(), initialCapacity );
	}

	protected RefList< V > createVertexList()
	{
		return RefCollections.createRefList( graph.vertices() );
	}

	protected RefList< V > createVertexList( final int initialCapacity )
	{
		return RefCollections.createRefList( graph.vertices(), initialCapacity );
	}

	protected RefList< E > createEdgeList()
	{
		return RefCollections.createRefList( graph.edges() );
	}

	protected RefList< E > createEdgeList( final int initialCapacity )
	{
		return RefCollections.createRefList( graph.edges(), initialCapacity );
	}

	protected RefDeque< V > createVertexDeque()
	{
		return RefCollections.createRefDeque( graph.vertices() );
	}

	protected RefDeque< V > createVertexDeque( final int initialCapacity )
	{
		return RefCollections.createRefDeque( graph.vertices(), initialCapacity );
	}

	protected RefDeque< E > createEdgeDeque()
	{
		return RefCollections.createRefDeque( graph.edges() );
	}

	protected RefDeque< E > createEdgeDeque( final int initialCapacity )
	{
		return RefCollections.createRefDeque( graph.edges(), initialCapacity );
	}

	protected RefStack< V > createVertexStack()
	{
		return RefCollections.createRefStack( graph.vertices() );
	}

	protected RefStack< V > createVertexStack( final int initialCapacity )
	{
		return RefCollections.createRefStack( graph.vertices(), initialCapacity );
	}

	protected RefStack< E > createEdgeStack()
	{
		return RefCollections.createRefStack( graph.edges() );
	}

	protected RefStack< E > createEdgeStack( final int initialCapacity )
	{
		return RefCollections.createRefStack( graph.edges(), initialCapacity );
	}

	protected < O > RefObjectMap< V, O > createVertexObjectMap()
	{
		return RefMaps.createRefObjectMap( graph.vertices() );
	}

	protected < O > RefObjectMap< E, O > createEdgeObjectMap()
	{
		return RefMaps.createRefObjectMap( graph.edges() );
	}

	protected RefRefMap< V, V > createVertexVertexMap()
	{
		return RefMaps.createRefRefMap( graph.vertices() );
	}

	protected RefRefMap< V, V > createVertexVertexMap( final int initialCapacity )
	{
		return RefMaps.createRefRefMap( graph.vertices(), initialCapacity );
	}

	protected RefRefMap< E, E > createEdgeEdgeMap()
	{
		return RefMaps.createRefRefMap( graph.edges() );
	}

	protected RefRefMap< E, E > createEdgeEdgeMap( final int initialCapacity )
	{
		return RefMaps.createRefRefMap( graph.edges(), initialCapacity );
	}

	protected RefIntMap< V > createVertexIntMap( final int noEntryValue )
	{
		return RefMaps.createRefIntMap( graph.vertices(), noEntryValue );
	}

	protected RefIntMap< V > createVertexIntMap( final int noEntryValue, final int initialCapacity )
	{
		return RefMaps.createRefIntMap( graph.vertices(), noEntryValue, initialCapacity );
	}

	protected RefIntMap< E > createEdgeIntMap( final int noEntryValue )
	{
		return RefMaps.createRefIntMap( graph.edges(), noEntryValue );
	}

	protected RefIntMap< E > createEdgeIntMap( final int noEntryValue, final int initialCapacity )
	{
		return RefMaps.createRefIntMap( graph.edges(), noEntryValue, initialCapacity );
	}

	protected Iterator< V > safeVertexIterator( final Iterator< V > iterator )
	{
		return RefCollections.safeIterator( iterator, graph.vertices() );
	}

	protected Iterator< E > safeEdgeIterator( final Iterator< E > iterator )
	{
		return RefCollections.safeIterator( iterator, graph.edges() );
	}
}
