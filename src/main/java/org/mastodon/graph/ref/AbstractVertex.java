package org.mastodon.graph.ref;

import org.mastodon.graph.Vertex;
import org.mastodon.pool.MappedElement;
import org.mastodon.pool.PoolObject;

/**
 * TODO: javadoc
 *
 * @param <V>
 * @param <E>
 * @param <VP>
 * @param <T>
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public class AbstractVertex<
			V extends AbstractVertex< V, E, VP, T >,
			E extends AbstractNonSimpleEdge< E, ?, ?, ? >,
			VP extends AbstractVertexPool< V, ?, T >,
			T extends MappedElement >
		extends PoolObject< V, VP, T >
		implements Vertex< E >
{
	protected static final int FIRST_IN_EDGE_INDEX_OFFSET = AbstractVertexPool.layout.firstInEdge.getOffset();
	protected static final int FIRST_OUT_EDGE_INDEX_OFFSET = AbstractVertexPool.layout.firstOutEdge.getOffset();

	protected AbstractVertex( final VP pool )
	{
		super( pool );
	}

	protected int getFirstInEdgeIndex()
	{
		return access.getIndex( FIRST_IN_EDGE_INDEX_OFFSET );
	}

	protected void setFirstInEdgeIndex( final int index )
	{
		access.putIndex( index, FIRST_IN_EDGE_INDEX_OFFSET );
	}

	protected int getFirstOutEdgeIndex()
	{
		return access.getIndex( FIRST_OUT_EDGE_INDEX_OFFSET );
	}

	protected void setFirstOutEdgeIndex( final int index )
	{
		access.putIndex( index, FIRST_OUT_EDGE_INDEX_OFFSET );
	}

	@Override
	protected void setToUninitializedState()
	{
		setFirstInEdgeIndex( -1 );
		setFirstOutEdgeIndex( -1 );
	}

	private AbstractNonSimpleEdgePool< E, ?, ? > edgePool;

	private IncomingEdges< E > incomingEdges;

	private OutgoingEdges< E > outgoingEdges;

	private AllEdges< E > edges;

	@Override
	public IncomingEdges< E > incomingEdges()
	{
		return incomingEdges;
	}

	@Override
	public OutgoingEdges< E > outgoingEdges()
	{
		return outgoingEdges;
	}

	@Override
	public AllEdges< E > edges()
	{
		return edges;
	}

	void linkEdgePool( final AbstractNonSimpleEdgePool< E, ?, ? > edgePool )
	{
		if ( this.edgePool != edgePool )
		{
			this.edgePool = edgePool;
			incomingEdges = new IncomingEdges<>( this, edgePool );
			outgoingEdges = new OutgoingEdges<>( this, edgePool );
			edges = new AllEdges<>( this, edgePool );
		}
	}
}
