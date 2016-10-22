package org.mastodon.graph.algorithm.traversal;

import org.mastodon.graph.Edge;
import org.mastodon.graph.ReadOnlyGraph;
import org.mastodon.graph.Vertex;

/**
 * Breadth-first search for directed, reversed or undirected graph. Depth-first
 * searches are graph searches where the children of a vertex are iterated
 * before its siblings.
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

	/**
	 * Creates a breadth-first search over the specified graph with the
	 * specified direction.
	 *
	 * @param graph
	 *            the graph to search.
	 * @param directivity
	 *            the search direction (can be {@link SearchDirection#DIRECTED}
	 *            {@link SearchDirection#REVERSED} or
	 *            {@link SearchDirection#UNDIRECTED}).
	 */
	public BreadthFirstSearch( final ReadOnlyGraph< V, E > graph, final SearchDirection directivity )
	{
		super( graph, directivity );
	}
}
