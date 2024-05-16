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
package org.mastodon.graph.object;

import java.util.ArrayList;
import java.util.Iterator;

import org.mastodon.graph.Edge;
import org.mastodon.graph.Edges;
import org.mastodon.graph.Vertex;

public abstract class AbstractObjectVertex< V extends AbstractObjectVertex< V, E >, E extends Edge< ? > > implements Vertex< E >
{
	final ObjectEdges< E > incoming;

	final ObjectEdges< E > outgoing;

	private final AllEdges< E > all;

	protected AbstractObjectVertex()
	{
		incoming = new ObjectEdges<>();
		outgoing = new ObjectEdges<>();
		all = new AllEdges<>( incoming, outgoing );
	}

	@Override
	public Edges< E > incomingEdges()
	{
		return incoming;
	}

	@Override
	public Edges< E > outgoingEdges()
	{
		return outgoing;
	}

	@Override
	public Edges< E > edges()
	{
		return all;
	}

	static final class ObjectEdges< E extends Edge< ? > > implements Edges< E >
	{
		final ArrayList< E > edges;

		ObjectEdges()
		{
			edges = new ArrayList<>();
		}

		@Override
		public E get( final int i )
		{
			return edges.get( i );
		}

		@Override
		public E get( final int i, final E edge )
		{
			return get( i );
		}

		@Override
		public Iterator< E > iterator()
		{
			return edges.iterator();
		}

		@Override
		public Iterator< E > safe_iterator()
		{
			return iterator();
		}

		@Override
		public int size()
		{
			return edges.size();
		}

		@Override
		public boolean isEmpty()
		{
			return edges.isEmpty();
		}
	}

	static final class AllEdges< E extends Edge< ? > > implements Edges< E >
	{
		private final ObjectEdges< E > incoming;

		private final ObjectEdges< E > outgoing;

		AllEdges( final ObjectEdges< E > incoming, final ObjectEdges< E > outgoing )
		{
			this.incoming = incoming;
			this.outgoing = outgoing;
		}

		@Override
		public Iterator< E > iterator()
		{
			final Iterator< E > itin = incoming.iterator();
			final Iterator< E > itout = outgoing.iterator();
			return new Iterator< E >()
			{

				E next = fetch();

				E fetch()
				{
					if ( itin.hasNext() )
						return itin.next();
					else if ( itout.hasNext() )
						return itout.next();
					else
						return null;
				}

				@Override
				public boolean hasNext()
				{
					return next != null;
				}

				@Override
				public E next()
				{
					final E edge = next;
					next = fetch();
					return edge;
				}
			};
		}

		@Override
		public int size()
		{
			return incoming.size() + outgoing.size();
		}

		@Override
		public boolean isEmpty()
		{
			return incoming.isEmpty() && outgoing.isEmpty();
		}

		@Override
		public E get( final int i )
		{
			final int j = i - incoming.size();
			return j < 0 ? incoming.get( i ) : outgoing.get( j );
		}

		@Override
		public E get( final int i, final E edge )
		{
			return get( i );
		}

		@Override
		public Iterator< E > safe_iterator()
		{
			return iterator();
		}
	}
}
