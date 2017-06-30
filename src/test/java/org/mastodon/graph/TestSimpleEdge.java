package org.mastodon.graph;

import org.mastodon.graph.ref.AbstractEdge;
import org.mastodon.pool.ByteMappedElement;

public class TestSimpleEdge extends AbstractEdge< TestSimpleEdge, TestSimpleVertex, TestSimpleEdgePool, ByteMappedElement >
{
	protected TestSimpleEdge( final TestSimpleEdgePool pool )
	{
		super( pool );
	}

	@Override
	public String toString()
	{
		final TestSimpleVertex v = this.vertexPool.createRef();
		final StringBuilder sb = new StringBuilder();
		sb.append( "e(" );
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
