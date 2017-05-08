package org.mastodon.graph;

import org.mastodon.graph.ref.AbstractVertexPool;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.ByteMappedElementArray;
import org.mastodon.pool.SingleArrayMemPool;
import org.mastodon.pool.attributes.IntAttribute;

public class TestNonSimpleVertexPool extends AbstractVertexPool< TestNonSimpleVertex, TestNonSimpleEdge, ByteMappedElement >
{
	static class TestNonSimpleVertexLayout extends AbstractVertexLayout
	{
		final IntField id = intField();
	}

	static TestNonSimpleVertexLayout layout = new TestNonSimpleVertexLayout();

	final IntAttribute< TestNonSimpleVertex > id;

	public TestNonSimpleVertexPool( final int initialCapacity )
	{
		super( initialCapacity, layout, TestNonSimpleVertex.class, SingleArrayMemPool.factory( ByteMappedElementArray.factory ) );
		id = new IntAttribute<>( layout.id, this );
	}

	@Override
	protected TestNonSimpleVertex createEmptyRef()
	{
		return new TestNonSimpleVertex( this );
	}
}

