package org.mastodon.graph.branch;

import org.mastodon.RefPool;
import org.mastodon.graph.Edge;
import org.mastodon.graph.ListenableGraph;
import org.mastodon.graph.Vertex;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.spatial.HasTimepoint;

import net.imglib2.RealLocalizable;

public class DefaultBranchGraph<
	V extends Vertex< E > & HasTimepoint & RealLocalizable,
	E extends Edge< V > >
		extends BranchGraphImp< V, E, BranchVertex< V >, BranchEdge< V >, BranchVertexPool< V >, BranchEdgePool< V >, ByteMappedElement >
{

	public DefaultBranchGraph( final ListenableGraph< V, E > graph, final RefPool< V > vertexBimap )
	{
		super( graph,
				new BranchEdgePool< V >( 1000, new BranchVertexPool< V >( vertexBimap, 1000 ) ),
				new BranchGraphImp.Initializer< BranchVertex< V >, V >()
				{
					@Override
					public BranchVertex< V > initialize( final BranchVertex< V > bv, final V v )
					{
						return bv.init( v );
					}},
				new BranchGraphImp.Initializer< BranchEdge< V >, E >()
				{
					@Override
					public BranchEdge< V > initialize( final BranchEdge< V > be, final E e )
					{
						return be.init();
					}
				} );
	}
}
