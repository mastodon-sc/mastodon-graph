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
package org.mastodon.graph.object;

import org.mastodon.graph.Edge;
import org.mastodon.graph.Vertex;

public abstract class AbstractObjectEdge< E extends AbstractObjectEdge< E, V >, V extends Vertex< ? > > implements Edge< V >
{
	private final V source;

	private final V target;

	protected AbstractObjectEdge( final V source, final V target )
	{
		this.source = source;
		this.target = target;
	}

	@Override
	public V getSource()
	{
		return source;
	}

	@Override
	public V getSource( final V vertex )
	{
		return source;
	}

	@Override
	public int getSourceOutIndex()
	{
		int outIndex = 0;
		for ( final Object e : source.outgoingEdges() )
		{
			if ( e.equals( this ) )
				break;
			++outIndex;
		}
		return outIndex;
	}

	@Override
	public V getTarget()
	{
		return target;
	}

	@Override
	public V getTarget( final V vertex )
	{
		return target;
	}

	@Override
	public int getTargetInIndex()
	{
		int inIndex = 0;
		for ( final Object e : target.incomingEdges() )
		{
			if ( e.equals( this ) )
				break;
			++inIndex;
		}
		return inIndex;
	}

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append( "e(" );
		sb.append( source.toString() );
		sb.append( " -> " );
		sb.append( target.toString() );
		sb.append( ")" );
		return sb.toString();
	}
}
