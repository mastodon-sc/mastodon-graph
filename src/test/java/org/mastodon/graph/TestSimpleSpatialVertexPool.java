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
package org.mastodon.graph;

import org.mastodon.graph.ref.AbstractListenableVertexPool;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.ByteMappedElementArray;
import org.mastodon.pool.SingleArrayMemPool;
import org.mastodon.pool.attributes.DoubleAttribute;
import org.mastodon.pool.attributes.IntAttribute;

public class TestSimpleSpatialVertexPool extends AbstractListenableVertexPool< TestSimpleSpatialVertex, TestSimpleSpatialEdge, ByteMappedElement >
{
	static class TestSpatialVertexLayout extends AbstractVertexLayout
	{
		final IntField id;

		final IntField timepoint;

		final DoubleField pos;

		public TestSpatialVertexLayout()
		{
			this.id = intField();
			this.timepoint = intField();
			this.pos = doubleField();
		}
	}

	final static TestSpatialVertexLayout layout = new TestSpatialVertexLayout();

	final IntAttribute< TestSimpleSpatialVertex > id;

	final IntAttribute< TestSimpleSpatialVertex > timepoint;

	final DoubleAttribute< TestSimpleSpatialVertex > pos;

	public TestSimpleSpatialVertexPool( final int initialCapacity )
	{
		super(
				initialCapacity,
				layout,
				TestSimpleSpatialVertex.class,
				SingleArrayMemPool.factory( ByteMappedElementArray.factory ) );
		id = new IntAttribute<>( layout.id, this );
		timepoint = new IntAttribute<>( layout.timepoint, this );
		pos = new DoubleAttribute<>( layout.pos, this );
	}

	@Override
	protected TestSimpleSpatialVertex createEmptyRef()
	{
		return new TestSimpleSpatialVertex( this );
	}
}
