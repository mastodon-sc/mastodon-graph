package org.mastodon.graph.algorithm.traversal;

import java.util.Iterator;

import org.mastodon.graph.Edge;
import org.mastodon.graph.ReadOnlyGraph;
import org.mastodon.graph.Vertex;
import org.mastodon.graph.algorithm.LeafFinder;
import org.mastodon.graph.algorithm.RootFinder;

public class DepthFirstCrossComponentSearch< V extends Vertex< E >, E extends Edge< V > > extends AbstractDepthFirstSearch< DepthFirstCrossComponentSearch< V, E >, V, E >
{

	private final Iterator< V > rit;

	public DepthFirstCrossComponentSearch( final ReadOnlyGraph< V, E > graph, final SearchDirection directed )
	{
		this( graph, directed, directed.equals( SearchDirection.REVERSED ) ? LeafFinder.getLeaves( graph ) : RootFinder.getRoots( graph ) );
	}

	public DepthFirstCrossComponentSearch( final ReadOnlyGraph< V, E > graph, final SearchDirection directed, final Iterable<V> roots)
	{
		super( graph, directed );
		this.rit = roots.iterator();
	}

	@Override
	public void start( final V start )
	{
		super.start( start );
		while ( rit.hasNext() )
		{
			final V next = rit.next();
			if ( discovered.contains( next ) )
				continue;

			searchListener.crossComponent( unqueued, next, this );
			visit( next );
		}
	}

}
