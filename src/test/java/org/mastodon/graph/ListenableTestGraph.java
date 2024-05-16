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

import org.mastodon.graph.ref.ListenableGraphImp;
import org.mastodon.pool.ByteMappedElement;

public class ListenableTestGraph extends ListenableGraphImp< ListenableTestVertexPool, ListenableTestEdgePool, ListenableTestVertex, ListenableTestEdge, ByteMappedElement >
{
	public ListenableTestGraph( final int initialCapacity )
	{
		super( new ListenableTestEdgePool( initialCapacity, new ListenableTestVertexPool( initialCapacity ) ) );
	}

	public ListenableTestGraph()
	{
		this( 10 );
	}

	public ListenableTestVertexPool getVertexPool()
	{
		return vertexPool;
	}

	public ListenableTestEdgePool getEdgePool()
	{
		return edgePool;
	}

	@Override
	public String toString()
	{
		final StringBuffer sb = new StringBuffer( "ListenableGraphTest {\n" );
		sb.append( "  vertices = {\n" );
		for ( final ListenableTestVertex v : vertexPool )
			sb.append( "    " + v + "\n" );
		sb.append( "  },\n" );

		sb.append( "  edges = {\n" );
		for ( final ListenableTestEdge e : edgePool )
			sb.append( "    " + e + "\n" );
		sb.append( "  }\n" );
		sb.append( "}" );
		return sb.toString();
	}
}
