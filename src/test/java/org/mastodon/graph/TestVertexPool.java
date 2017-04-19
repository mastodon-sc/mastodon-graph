package org.mastodon.graph;

import org.mastodon.graph.ref.AbstractVertexPool;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.ByteMappedElementArray;
import org.mastodon.pool.SingleArrayMemPool;
import org.mastodon.pool.attributes.IntAttribute;

public class TestVertexPool extends AbstractVertexPool< TestVertex, TestEdge, ByteMappedElement >
{
	static class TestVertexLayout extends AbstractVertexLayout
	{
		IntField id = intField();
	}

	static TestVertexLayout layout = new TestVertexLayout();

	final IntAttribute< TestVertex > id;

	public TestVertexPool( final int initialCapacity )
	{
		super(
				initialCapacity,
				layout,
				TestVertex.class,
				SingleArrayMemPool.factory( ByteMappedElementArray.factory ) );
		id = new IntAttribute<>( layout.id );
	}

	@Override
	protected TestVertex createEmptyRef()
	{
		return new TestVertex( this );
	}
}
