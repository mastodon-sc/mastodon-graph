package org.mastodon.graph.algorithm.traversal;

import java.util.Collections;
import java.util.Comparator;

import org.mastodon.collection.RefList;
import org.mastodon.graph.Edge;
import org.mastodon.graph.ReadOnlyGraph;
import org.mastodon.graph.Vertex;

public abstract class AbstractGraphSortedIteratorAlgorithm< V extends Vertex< E >, E extends Edge< V > > extends AbstractGraphIteratorAlgorithm< V, E >
{
	protected final Comparator< V > comparator;

	protected final RefList< V > list;

	public AbstractGraphSortedIteratorAlgorithm( final ReadOnlyGraph< V, E > graph, final Comparator< V > comparator )
	{
		super( graph );
		this.comparator = comparator;
		this.list = createVertexList();
	}

	@Override
	protected void fetchNext()
	{
		if ( canFetch() )
		{
			fetched = fetch( fetchedRef );
			list.clear();
			for ( final E e : neighbors( fetched ) )
			{
				final V target = targetOf( fetched, e, tmpRef );
				if ( !visited.contains( target ) )
				{
					visited.add( target );
					list.add( target );
				}
			}

			list.sort( comparator );
			// To have right order when pop from stack:
			for ( int i = 0; i < list.size(); i++ )
			{
				final V target = list.get( i );
				toss( target );
			}
		}
		else
			fetched = null;
	}

}
