package org.mastodon.graph;

import org.mastodon.graph.ref.AbstractNonSimpleEdge;
import org.mastodon.pool.ByteMappedElement;

public class TestNonSimpleEdge extends AbstractNonSimpleEdge< TestNonSimpleEdge, TestNonSimpleVertex, TestNonSimpleEdgePool, ByteMappedElement >
{
	protected TestNonSimpleEdge( final TestNonSimpleEdgePool pool )
	{
		super( pool );
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
