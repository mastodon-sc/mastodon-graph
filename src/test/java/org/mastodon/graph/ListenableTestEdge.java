package org.mastodon.graph;

import org.mastodon.graph.ref.AbstractListenableEdge;
import org.mastodon.pool.ByteMappedElement;

public class ListenableTestEdge extends AbstractListenableEdge< ListenableTestEdge, ListenableTestVertex, ListenableTestEdgePool, ByteMappedElement >
{
	protected ListenableTestEdge( final ListenableTestEdgePool pool )
	{
		super( pool );
	}

	public ListenableTestEdge init()
	{
		initDone();
		return this;
	}

	@Override
	public String toString()
	{
		final ListenableTestVertex v = this.vertexPool.createRef();
		final StringBuilder sb = new StringBuilder();
		sb.append( "le(" );
		getSource( v );
		sb.append( v.getId() );
		sb.append( " -> " );
		getTarget( v );
		sb.append( v.getId() );
		sb.append( ")" );
		this.vertexPool.releaseRef( v );
		return sb.toString();
	}

	void notifyEdgeAdded()
	{
		super.initDone();
	}
}
