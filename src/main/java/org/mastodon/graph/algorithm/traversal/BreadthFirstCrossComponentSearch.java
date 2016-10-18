package org.mastodon.graph.algorithm.traversal;

import java.util.Iterator;

import org.mastodon.graph.Edge;
import org.mastodon.graph.ReadOnlyGraph;
import org.mastodon.graph.Vertex;
import org.mastodon.graph.algorithm.RootFinder;

public class BreadthFirstCrossComponentSearch< V extends Vertex< E >, E extends Edge< V > > extends AbstractBreadthFirstSearch< BreadthFirstCrossComponentSearch< V, E >, V, E >
{

	private final Iterator< V > rit;

	public BreadthFirstCrossComponentSearch( final ReadOnlyGraph< V, E > graph, final SearchDirection directed )
	{
		this( graph, directed, RootFinder.getRoots( graph ) );

	}

	public BreadthFirstCrossComponentSearch( final ReadOnlyGraph< V, E > graph, final SearchDirection directed, final Iterable< V > roots )
	{
		super( graph, directed );
		this.rit = roots.iterator();
	}

	@Override
	protected void visit( final V start )
	{
		super.visit( start );
		while (rit.hasNext() )
		{
			final V next = rit.next();
			if ( discovered.contains( next ) )
				continue;

			// When we jump to another root, we set its depth to 0.
			depths.put( next, 0 );
			queue.add( next );
			searchListener.crossComponent( unqueued, next, this );
			super.visit( next );
		}
	}
}
