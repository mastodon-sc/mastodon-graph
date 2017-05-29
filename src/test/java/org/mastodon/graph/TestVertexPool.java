package org.mastodon.graph;

import org.mastodon.graph.ref.AbstractVertexPool;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.ByteMappedElementArray;
import org.mastodon.pool.SingleArrayMemPool;
import org.mastodon.pool.attributes.IntAttribute;

public class TestVertexPool extends AbstractVertexPool< TestVertex, TestEdge, ByteMappedElement >
{
	static class TestNonSimpleVertexLayout extends AbstractVertexLayout
	{
		final IntField id = intField();
	}

	static TestNonSimpleVertexLayout layout = new TestNonSimpleVertexLayout();

	final IntAttribute< TestVertex > id;

	public TestVertexPool( final int initialCapacity )
	{
		super( initialCapacity, layout, TestVertex.class, SingleArrayMemPool.factory( ByteMappedElementArray.factory ) );
		id = new IntAttribute<>( layout.id, this );
	}

	@Override
	protected TestVertex createEmptyRef()
	{
		return new TestVertex( this );
	}
}

