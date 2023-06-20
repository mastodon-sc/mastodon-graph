/*-
 * #%L
 * Mastodon Graphs
 * %%
 * Copyright (C) 2015 - 2023 Tobias Pietzsch, Jean-Yves Tinevez
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

import org.mastodon.graph.GraphIdBimap;

public class AbstractObjectIdGraph< V extends AbstractObjectIdVertex< V, E >, E extends AbstractObjectIdEdge< E, V > > extends AbstractObjectGraph< V, E >
{
	class EdgeBimap extends AbstractObjectIdBimap< E >
	{
		public EdgeBimap( final Class< E > klass )
		{
			super( klass );
		}

		@Override
		public int getId( final E edge )
		{
			if ( edge.id < 0 )
				edge.id = this.createId( edge );
			return edge.id;
		}
	}

	class VertexBimap extends AbstractObjectIdBimap< V >
	{
		public VertexBimap( final Class< V > klass )
		{
			super( klass );
		}

		@Override
		public int getId( final V vertex )
		{
			if ( vertex.id < 0 )
				vertex.id = this.createId( vertex );
			return vertex.id;
		}
	}

	private final GraphIdBimap< V, E > idmap;

	protected AbstractObjectIdGraph(
			final Factory< V, E > factory,
			final Class< V > vertexClass,
			final Class< E > edgeClass,
			final Collection< V > vertices,
			final Collection< E > edges )
	{
		super( factory, vertices, edges );
		idmap = new GraphIdBimap<>( new VertexBimap( vertexClass ), new EdgeBimap( edgeClass ) );
	}

	public GraphIdBimap< V, E > getIdBimap()
	{
		return idmap;
	}
}
