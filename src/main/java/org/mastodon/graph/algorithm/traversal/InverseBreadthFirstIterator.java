/**
 *
 */
package org.mastodon.graph.algorithm.traversal;

import org.mastodon.graph.Edge;
import org.mastodon.graph.ReadOnlyGraph;
import org.mastodon.graph.Vertex;

/**
 * Same as {@link BreadthFirstIterator} on a graph where all directed edges are
 * pointing in the opposite direction.
 *
 * @param <V>
 *            the type of vertices in the graph.
 * @param <E>
 *            the type of edges in the graph.
 * @author Florian Jug
 * @author Jean-Yves Tinevez
 */
public class InverseBreadthFirstIterator< V extends Vertex< E >, E extends Edge< V > > extends BreadthFirstIterator< V, E >
{
	public InverseBreadthFirstIterator( final V root, final ReadOnlyGraph< V, E > graph )
	{
		super( root, graph );
	}

	@Override
	protected Iterable< E > neighbors( final V vertex )
	{
		return vertex.incomingEdges();
	}

	@Override
	protected V targetOf( final V source, final E edge, final V ref )
	{
		return edge.getSource( ref );
	}
}
