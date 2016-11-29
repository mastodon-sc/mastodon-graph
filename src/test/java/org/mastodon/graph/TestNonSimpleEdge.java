package org.mastodon.graph;

import org.mastodon.graph.ref.AbstractEdge;
import org.mastodon.graph.ref.AbstractNonSimpleEdgePool;
import org.mastodon.pool.ByteMappedElement;

public class TestNonSimpleEdge extends AbstractEdge< TestNonSimpleEdge, TestNonSimpleVertex, ByteMappedElement >
{
	protected static final int SIZE_IN_BYTES = AbstractEdge.SIZE_IN_BYTES;

	public final AbstractNonSimpleEdgePool< TestNonSimpleEdge, TestNonSimpleVertex, ByteMappedElement > creatingPool;

	protected TestNonSimpleEdge( final AbstractNonSimpleEdgePool< TestNonSimpleEdge, TestNonSimpleVertex, ByteMappedElement > pool )
	{
		super( pool );
		creatingPool = pool;
	}

	@Override
	public String toString()
	{
		final TestNonSimpleVertex v = this.vertexPool.createRef();
		final StringBuilder sb = new StringBuilder();
		sb.append( "nse(" );
		getSource( v );
		sb.append( v.getId() );
		sb.append( " -> " );
		getTarget( v );
		sb.append( v.getId() );
		sb.append( ")" );
		this.vertexPool.releaseRef( v );
		return sb.toString();
	}
}
