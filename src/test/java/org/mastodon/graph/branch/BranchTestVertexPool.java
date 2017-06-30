package org.mastodon.graph.branch;

import org.mastodon.graph.ref.AbstractListenableVertexPool;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.ByteMappedElementArray;
import org.mastodon.pool.SingleArrayMemPool;
import org.mastodon.pool.attributes.IntAttribute;

public class BranchTestVertexPool extends AbstractListenableVertexPool< BranchTestVertex, BranchTestEdge, ByteMappedElement >
{
	static class BranchTestVertexLayout extends AbstractVertexLayout
	{
		final IntField id = intField();
		final IntField timepoint = intField();
	}

	static BranchTestVertexLayout layout = new BranchTestVertexLayout();

	final IntAttribute< BranchTestVertex > id;
	final IntAttribute< BranchTestVertex > timepoint;

	public BranchTestVertexPool( final int initialCapacity )
	{
		super(
				initialCapacity,
				layout,
				BranchTestVertex.class,
				SingleArrayMemPool.factory( ByteMappedElementArray.factory ) );
		id = new IntAttribute<>( layout.id, this );
		timepoint = new IntAttribute<>( layout.timepoint, this );
	}

	@Override
	protected BranchTestVertex createEmptyRef()
	{
		return new BranchTestVertex( this );
	}
}
