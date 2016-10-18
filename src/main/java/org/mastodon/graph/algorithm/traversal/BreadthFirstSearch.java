package org.mastodon.graph.algorithm.traversal;

import org.mastodon.graph.Edge;
import org.mastodon.graph.ReadOnlyGraph;
import org.mastodon.graph.Vertex;

/**
 * Breadth-first search for directed or undirected graph, following the
 * framework of Skiena. http://www.algorist.com/
 *
 * @author Jean-Yves Tinevez
 *
 * @param <V>
 *            the type of the graph vertices iterated.
 * @param <E>
 *            the type of the graph edges iterated.
 */
public class BreadthFirstSearch< V extends Vertex< E >, E extends Edge< V > > extends AbstractBreadthFirstSearch< BreadthFirstSearch< V, E >, V, E >
{

	public BreadthFirstSearch( final ReadOnlyGraph< V, E > graph, final SearchDirection directed )
	{
		super( graph, directed );
	}
}
