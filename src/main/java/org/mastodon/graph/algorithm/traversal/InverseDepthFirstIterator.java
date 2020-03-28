/**
 *
 */
package org.mastodon.graph.algorithm.traversal;

import org.mastodon.graph.Edge;
import org.mastodon.graph.ReadOnlyGraph;
import org.mastodon.graph.Vertex;

/**
 * Same as {@link DepthFirstIterator} on a graph where all directed edges are
 * pointing in the opposite direction.
 *
 * @param <V>
 *            the type of vertices in the graph.
 * 
 * @param <E>
 *            the type of edges in the graph.
 * 
 * @author Jean-Yves Tinevez
 */
public class InverseDepthFirstIterator< V extends Vertex< E >, E extends Edge< V > > extends DepthFirstIterator< V, E >
{
	public InverseDepthFirstIterator( final V root, final ReadOnlyGraph< V, E > graph )
	{
		super( root, graph );
	}

	public InverseDepthFirstIterator( final ReadOnlyGraph< V, E > graph )
	{
		super( graph );
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
