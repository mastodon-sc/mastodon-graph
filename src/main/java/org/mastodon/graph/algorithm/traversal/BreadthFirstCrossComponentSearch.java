package org.mastodon.graph.algorithm.traversal;

import java.util.Iterator;

import org.mastodon.graph.Edge;
import org.mastodon.graph.ReadOnlyGraph;
import org.mastodon.graph.Vertex;
import org.mastodon.graph.algorithm.LeafFinder;
import org.mastodon.graph.algorithm.RootFinder;

/**
 * A cross-component, breadth-first search. Breadth-first searches are graph
 * searches where all the siblings of a vertex are iterated before their
 * descendants are explored.
 * <p>
 * This search is a cross-component search, meaning that when the iteration
 * through a connected-component of the graph is finished, the iterator jumps to
 * the next component automatically. This ensures that all the vertices of the
 * graph are iterated exactly once with this search.
 * <p>
 * The order in which the connected-components are iterated can be specified by
 * using the constructor that specifies an iterable over the collection of roots
 * (or leaves in the {@link SearchDirection#REVERSED} case), using a list with
 * the desired order. For this search to operate properly and indeed iterate
 * through all the vertices of the graph exactly once, the specified collection
 * must include all the roots (or leaves in the {@link SearchDirection#REVERSED}
 * case) of the graph, that is: all the vertices that have no incoming edges (or
 * all the vertices with no outgoing edges).
 * <p>
 * Within a single connected-component, the order of iteration is breadth-first.
 * The iterator only jumps to another component only when all the vertices of
 * the currently iterated component have been iterated.
 *
 * @author Jean-Yves Tinevez
 *
 * @param <V>
 *            the type of the graph vertices.
 * @param <E>
 *            the type of the graph edges.
 */
public class BreadthFirstCrossComponentSearch< V extends Vertex< E >, E extends Edge< V > > extends AbstractBreadthFirstSearch< BreadthFirstCrossComponentSearch< V, E >, V, E >
{

	private final Iterator< V > rit;

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

	public BreadthFirstCrossComponentSearch( final ReadOnlyGraph< V, E > graph, final SearchDirection directivity )
	{
		this( graph, directivity, directivity.equals( SearchDirection.REVERSED ) ? LeafFinder.getLeaves( graph ) : RootFinder.getRoots( graph ) );
	}

	/**
	 * Creates a breadth-first, cross-component search over the specified graph
	 * with the specified direction, using the specified collection of roots or
	 * leaves.
	 * <p>
	 * The order in which the connected-components are searched can be specified
	 * by using this constructor, using a list with the desired order. For this
	 * search to operate properly and indeed search through all the vertices of
	 * the graph exactly once, the specified collection must include all the
	 * roots (for {@link SearchDirection#DIRECTED} searches) or leaves (for
	 * {@link SearchDirection#REVERSED} searches) of the graph. That is: all the
	 * vertices that have no incoming edges, or no outgoing edges respectively.
	 * For {@link SearchDirection#UNDIRECTED} searches, a collection including
	 * at least one vertex per connected component is sufficient.
	 *
	 * @param graph
	 *            the graph to search.
	 * @param directivity
	 *            the search direction (can be {@link SearchDirection#DIRECTED}
	 *            {@link SearchDirection#REVERSED} or
	 *            {@link SearchDirection#UNDIRECTED}).
	 * @param cc
	 *            an iterable over roots or leaves of the graph.
	 */
	public BreadthFirstCrossComponentSearch( final ReadOnlyGraph< V, E > graph, final SearchDirection directivity, final Iterable< V > cc )
	{
		super( graph, directivity );
		this.rit = cc.iterator();
	}

	@Override
	protected void visit( final V start )
	{
		super.visit( start );
		while ( rit.hasNext() )
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
