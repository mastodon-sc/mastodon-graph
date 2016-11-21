package org.mastodon.graph;

import org.mastodon.graph.ref.AbstractEdge;
import org.mastodon.graph.ref.AbstractVertexPool;
import org.mastodon.pool.MappedElement;
import org.mastodon.pool.PoolObject;

public class AbstractBranchLinkVertexPool< 
V extends AbstractBranchLinkVertex< V, SV, SE, E, T >, 
SV extends AbstractBranchVertex< SV, SE, V, T >, 
SE extends AbstractBranchEdge< SE, SV, E, T >, 
E extends AbstractEdge< E, V, ? >,
T extends MappedElement >
		extends AbstractVertexPool< V, E, T >
{
	protected final AbstractVertexPool< SV, SE, T > skeletonVertexPool;

	public AbstractBranchLinkVertexPool(
			final int initialCapacity,
			final PoolObject.Factory< V, T > vertexFactory,
			final AbstractVertexPool< SV, SE, T > skeletonVertexPool )
	{
		super( initialCapacity, vertexFactory );
		this.skeletonVertexPool = skeletonVertexPool;
	}

	@Override
	public void clear()
	{
		super.clear();
	}

	@Override
	public void delete( final V vertex )
	{
		if ( edgePool != null )
			edgePool.deleteAllLinkedEdges( vertex );

		final SV sv = vertex.getBranchGraphVertex();
		skeletonVertexPool.delete( sv );

		deleteByInternalPoolIndex( vertex.getInternalPoolIndex() );
	}

	@Override
	public V create( final V vertex )
	{
		super.create( vertex );
		final SV sv = skeletonVertexPool.createRef();
		skeletonVertexPool.create( sv );
		vertex.setIsBranchGraphVertex( true );
		vertex.setBranchGraphIndex( sv.getInternalPoolIndex() );
		sv.setLinkedVertexIndex( vertex.getInternalPoolIndex() );
		skeletonVertexPool.releaseRef( sv );
		return vertex;
	}
}