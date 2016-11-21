package org.mastodon.graph;

import static org.mastodon.pool.ByteUtils.BOOLEAN_SIZE;
import static org.mastodon.pool.ByteUtils.INDEX_SIZE;

import org.mastodon.graph.ref.AbstractEdge;
import org.mastodon.graph.ref.AbstractEdgePool;
import org.mastodon.graph.ref.AbstractVertex;
import org.mastodon.graph.ref.AbstractVertexPool;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.pool.MappedElement;

/**
 * An {@link AbstractVertex} that links into the corresponding skeleton-graph.
 *
 * @param <V>
 *            recursive type of this {@link AbstractBranchLinkVertex}.
 * @param <SV>
 *            the {@link Vertex} type of the corresponding skeleton-graph.
 * @param <SE>
 *            the {@link Edge} type of the corresponding skeleton-graph.
 * @param <E>
 *            the {@link Edge} type of the graph.
 * @param <T>
 *            the MappedElement type, for example {@link ByteMappedElement}.
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class AbstractBranchLinkVertex< 
V extends AbstractBranchLinkVertex< V, SV, SE, E, T >, 
SV extends AbstractVertex< SV, SE, T >, 
SE extends AbstractEdge< SE, SV, T >, 
E extends AbstractEdge< E, ?, ? >, 
T extends MappedElement >
		extends AbstractVertex< V, E, T >
{
	protected static final int SKELETON_GRAPH_INDEX_OFFSET = AbstractVertex.SIZE_IN_BYTES;

	protected static final int IS_SKELETON_GRAPH_VERTEX_OFFSET = SKELETON_GRAPH_INDEX_OFFSET + INDEX_SIZE;

	protected static final int SIZE_IN_BYTES = IS_SKELETON_GRAPH_VERTEX_OFFSET + BOOLEAN_SIZE;

	private final AbstractVertexPool< SV, ?, T > skeletonVertexPool;

	private final AbstractEdgePool< SE, ?, T > skeletonEdgePool;

	protected AbstractBranchLinkVertex(
			final AbstractVertexPool< V, ?, T > pool,
			final AbstractVertexPool< SV, ?, T > skeletonVertexPool,
			final AbstractEdgePool< SE, ?, T > skeletonEdgePool )
	{
		super( pool );
		this.skeletonVertexPool = skeletonVertexPool;
		this.skeletonEdgePool = skeletonEdgePool;
	}

	/**
	 * Get the index of the corresponding skeleton graph entity. Depending on
	 * {@link #getIsBranchGraphVertex()}, this is either:
	 * <ul>
	 * <li>The index of the corresponding {@link Vertex} in the skeleton-graph,
	 * or
	 * <li>The index of the corresponding {@link Edge} in the skeleton-graph,
	 * i.e., the skeleton edge that this vertex is part of.
	 * </ul>
	 *
	 * @return the index of the corresponding skeleton graph entity.
	 */
	protected int getBranchGraphIndex()
	{
		return access.getIndex( SKELETON_GRAPH_INDEX_OFFSET );
	}

	protected void setBranchGraphIndex( final int index )
	{
		access.putIndex( index, SKELETON_GRAPH_INDEX_OFFSET );
	}

	/**
	 * Returns {@code true} if the skeleton-graph entity corresponding to this
	 * vertex is a {@link Vertex}, and {@code false} if the corresponding entity
	 * is an {@link Edge}.
	 *
	 * @return {@code true} if this vertex corresponds to skeleton-graph vertex,
	 *         {@code false} if it corresponds to an edge.
	 */
	protected boolean getIsBranchGraphVertex()
	{
		return access.getBoolean( IS_SKELETON_GRAPH_VERTEX_OFFSET );
	}

	protected void setIsBranchGraphVertex( final boolean b )
	{
		access.putBoolean( b, IS_SKELETON_GRAPH_VERTEX_OFFSET );
	}

	@Override
	protected void setToUninitializedState()
	{
		super.setToUninitializedState();
	}

	/**
	 * Returns {@code true} if the skeleton-graph entity corresponding to this
	 * vertex is a {@link Vertex}, and {@code false} if the corresponding entity
	 * is an {@link Edge}.
	 *
	 * @return {@code true} if this vertex corresponds to skeleton-graph vertex,
	 *         {@code false} if it corresponds to an edge.
	 */
	public boolean isBranchGraphVertex()
	{
		return getIsBranchGraphVertex();
	}

	/**
	 * Get the corresponding skeleton-graph vertex. Throws an
	 * {@link UnsupportedOperationException} if this vertex does not correspond
	 * to a skeleton-graph vertex.
	 *
	 * <p>
	 * This allocates a new proxy object to hold the reference to the
	 * skeleton-graph vertex. It is recommended to use the allocation-free
	 * {@link #getBranchGraphVertex(AbstractVertex)} instead.
	 *
	 * @return the corresponding skeleton-graph vertex.
	 */
	public SV getBranchGraphVertex()
	{
		return getBranchGraphVertex( skeletonVertexPool.createRef() );
	}

	/**
	 * Get the corresponding skeleton-graph vertex. Throws an
	 * {@link UnsupportedOperationException} if this vertex does not correspond
	 * to a skeleton-graph vertex.
	 *
	 * @param vertex
	 *            proxy object that will be set to reference the skeleton-graph
	 *            vertex.
	 *
	 * @return the corresponding skeleton-graph vertex.
	 */
	public SV getBranchGraphVertex( final SV vertex )
	{
		if ( !getIsBranchGraphVertex() )
			throw new UnsupportedOperationException( "this vertex has no associated skeleton-graph vertex" );
		skeletonVertexPool.getObject( getBranchGraphIndex(), vertex );
		return vertex;
	}

	/**
	 * Get the corresponding skeleton-graph edge. Throws an
	 * {@link UnsupportedOperationException} if this vertex does not correspond
	 * to a skeleton-graph edge.
	 *
	 * <p>
	 * This allocates a new proxy object to hold the reference to the
	 * skeleton-graph edge. It is recommended to use the allocation-free
	 * {@link #getBranchGraphEdge(AbstractEdge)} instead.
	 *
	 * @return the corresponding skeleton-graph edge.
	 */
	public SE getBranchGraphEdge()
	{
		return getBranchGraphEdge( skeletonEdgePool.createRef() );
	}

	/**
	 * Get the corresponding skeleton-graph edge. Throws an
	 * {@link UnsupportedOperationException} if this vertex does not correspond
	 * to a skeleton-graph edge.
	 *
	 * @param edge
	 *            proxy object that will be set to reference the skeleton-graph
	 *            edge.
	 *
	 * @return the corresponding skeleton-graph edge.
	 */
	public SE getBranchGraphEdge( final SE edge )
	{
		if ( getIsBranchGraphVertex() )
			throw new UnsupportedOperationException( "this vertex has no associated skeleton-graph edge" );
		skeletonEdgePool.getObject( getBranchGraphIndex(), edge );
		return edge;
	}
}