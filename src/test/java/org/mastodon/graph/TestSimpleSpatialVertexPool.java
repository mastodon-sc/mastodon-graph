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
