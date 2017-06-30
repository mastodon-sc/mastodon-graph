package org.mastodon.graph;

import org.mastodon.graph.ref.AbstractVertexPool;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.ByteMappedElementArray;
import org.mastodon.pool.SingleArrayMemPool;
import org.mastodon.pool.attributes.IntAttribute;

public class TestSimpleVertexPool extends AbstractVertexPool< TestSimpleVertex, TestSimpleEdge, ByteMappedElement >
{
	static class TestVertexLayout extends AbstractVertexLayout
	{
		final IntField id = intField();
	}

	static TestVertexLayout layout = new TestVertexLayout();

	final IntAttribute< TestSimpleVertex > id;

	public TestSimpleVertexPool( final int initialCapacity )
	{
		super(
				initialCapacity,
				layout,
				TestSimpleVertex.class,
				SingleArrayMemPool.factory( ByteMappedElementArray.factory ) );
		id = new IntAttribute<>( layout.id, this );
	}

	@Override
	protected TestSimpleVertex createEmptyRef()
	{
		return new TestSimpleVertex( this );
	}
}
