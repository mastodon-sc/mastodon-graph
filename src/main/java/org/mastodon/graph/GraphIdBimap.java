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
package org.mastodon.graph;

import org.mastodon.RefPool;

/**
 * Bidirectional mappings between integer IDs and vertices and integer IDs
 * and edges.
 *
 * @param <V>
 *            the {@link Vertex} type.
 * @param <E>
 *            the {@link Edge} type.
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
// TOOD simplify and rename? This is just a pair of RefPools. "GraphRefPools", "GraphPools"? remove completely?
public class GraphIdBimap< V, E >
{
	private final RefPool< V > vertexBimap;
	private final RefPool< E > edgeBimap;

	public GraphIdBimap( final RefPool< V > vertexBimap, final RefPool< E > edgeBimap )
	{
		this.vertexBimap = vertexBimap;
		this.edgeBimap = edgeBimap;
	}

	public int getVertexId( final V v )
	{
		return vertexBimap.getId( v );
	}

	public V getVertex( final int id, final V ref )
	{
		return vertexBimap.getObject( id, ref );
	}

	public V getVertexIfExists( final int id, final V ref )
	{
		return vertexBimap.getObjectIfExists( id, ref );
	}

	public int getEdgeId( final E e )
	{
		return edgeBimap.getId( e );
	}

	public E getEdge( final int id, final E ref )
	{
		return edgeBimap.getObject( id, ref );
	}

	public E getEdgeIfExists( final int id, final E ref )
	{
		return edgeBimap.getObjectIfExists( id, ref );
	}

	public RefPool< V > vertexIdBimap()
	{
		return vertexBimap;
	}

	public RefPool< E > edgeIdBimap()
	{
		return edgeBimap;
	}
}
