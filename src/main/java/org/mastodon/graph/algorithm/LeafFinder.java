package org.mastodon.graph.algorithm;

import org.mastodon.collection.RefSet;
import org.mastodon.graph.Edge;
import org.mastodon.graph.ReadOnlyGraph;
import org.mastodon.graph.Vertex;

/**
 * Small algorithm that returns a set of vertices that have no outgoing edges.
 *
 * @author Jean-Yves Tinevez
 */
public class LeafFinder< V extends Vertex< E >, E extends Edge< V > > extends AbstractGraphAlgorithm< V, E >
{
	private final RefSet< V > leaves;

	public LeafFinder( final ReadOnlyGraph< V, E > graph )
	{
		super( graph );
		this.leaves = createVertexSet();
		fetchLeaves();
	}

	public RefSet< V > get()
	{
		return leaves;
	}

	private void fetchLeaves()
	{
		for ( final V v : graph.vertices() )
			if ( v.outgoingEdges().isEmpty() )
				leaves.add( v );
	}

	@SuppressWarnings( { "rawtypes", "unchecked" } )
	public static < V extends Vertex< ? > > RefSet< V > getLeaves( final ReadOnlyGraph< V, ? > graph )
	{
		return new LeafFinder( graph ).get();
	}

}
