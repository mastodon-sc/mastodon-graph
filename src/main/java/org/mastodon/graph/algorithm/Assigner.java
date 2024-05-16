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
package org.mastodon.graph.algorithm;

import org.mastodon.pool.PoolObject;

public abstract class Assigner< O >
{
	public abstract O assign( final O value, final O target );

	@SuppressWarnings( "unchecked" )
	public static < O > Assigner< O > getFor( final O obj )
	{
		if ( obj != null && obj instanceof PoolObject )
			return RefAssign.instance;
		else
			return ObjectAssign.instance;
	}

	static class RefAssign< O extends PoolObject< O, ?, ? > > extends Assigner< O >
	{
		@Override
		public O assign( final O value, final O target )
		{
			return target.refTo( value );
		}

		@SuppressWarnings( "rawtypes" )
		static RefAssign instance = new RefAssign();
	}

	static class ObjectAssign< O > extends Assigner< O >
	{
		@Override
		public O assign( final O value, final O target )
		{
			return value;
		}

		@SuppressWarnings( "rawtypes" )
		static ObjectAssign instance = new ObjectAssign();
	}
}
