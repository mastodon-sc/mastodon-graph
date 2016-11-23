package org.mastodon.graph;

import org.mastodon.graph.ref.AbstractListenableEdge;
import org.mastodon.graph.ref.AbstractListenableEdgePool;
import org.mastodon.pool.ByteMappedElement;

public class ListenableTestEdge extends AbstractListenableEdge< ListenableTestEdge, ListenableTestVertex, ByteMappedElement >
{
	protected static final int SIZE_IN_BYTES = AbstractListenableEdge.SIZE_IN_BYTES;

	public final AbstractListenableEdgePool< ListenableTestEdge, ListenableTestVertex, ByteMappedElement > creatingPool;

	protected ListenableTestEdge( final AbstractListenableEdgePool< ListenableTestEdge, ListenableTestVertex, ByteMappedElement > pool )
	{
		super( pool );
		creatingPool = pool;
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
}
