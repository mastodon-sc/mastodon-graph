package org.mastodon.graph.algorithm.traversal;

import org.mastodon.graph.Edge;
import org.mastodon.graph.Graph;
import org.mastodon.graph.Vertex;
import org.mastodon.graph.algorithm.LeafFinder;

/**
 * Same as {@link DepthFirstCrossComponentIterator} on a graph where all
 * directed edges are pointing in the opposite direction.
 * <p>
 * It is therefore capital to specify the leaves of the graph to ensure
 * iteration of the whole graph.
 *
 * @author Jean-Yves Tinevez
 */
public class InverseDepthFirstCrossComponentIterator< V extends Vertex< E >, E extends Edge< V > > extends DepthFirstCrossComponentIterator< V, E >
{

	/**
	 * Creates a depth-first, cross-component inverse iterator starting from the
	 * specified vertex.
	 * <p>
	 * The collection of leaves is determined automatically at creation.
	 *
	 * @param start
	 *            the vertex to start iteration with.
	 * @param graph
	 *            the graph to iterate over.
	 */
	public InverseDepthFirstCrossComponentIterator( final V start, final Graph< V, E > graph )
	{
		this( start, graph, LeafFinder.getLeaves( graph ) );
	}

	/**
	 * Creates a depth-first, cross-component inverse iterator starting from the
	 * specified vertex, using the specified collection of leaves to jump across
	 * connected-components.
	 * <p>
	 * The order in which the connected-components are iterated can be specified
	 * by using this constructor, using a list with the desired order. For this
	 * iterator to operate properly and indeed iterate through all the vertices
	 * of the graph exactly once, the specified collection of leaves must
	 * include all the leaves of the graph, that is: all the vertices that have
	 * no outgoing edges.
	 *
	 * @param start
	 *            the vertex to start iteration with.
	 * @param graph
	 *            the graph to iterate over.
	 * @param leaves
	 *            an iterable over the collection of leaves.
	 */
	public InverseDepthFirstCrossComponentIterator( final V start, final Graph< V, E > graph, final Iterable< V > leaves )
	{
		super( start, graph, leaves );
	}

	@Override
	protected Iterable< E > neighbors( final V vertex )
	{
		return vertex.incomingEdges();
	}

	@Override
	protected V targetOf( final E edge, final V ref )
	{
		return edge.getSource( ref );
	}
}
