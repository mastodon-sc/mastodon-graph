package org.mastodon.graph.algorithm.traversal;

import org.mastodon.collection.RefStack;
import org.mastodon.graph.Edge;
import org.mastodon.graph.ReadOnlyGraph;
import org.mastodon.graph.Vertex;
import org.mastodon.graph.algorithm.util.Graphs;

/**
 * A Depth-first iterator, that traverses edges regardless of direction.
 * <p>
 * With {@code A -> B}, the iterator will move from A to B, and also from B
 * to A.
 *
 * @author Jean-Yves Tinevez
 * @author Tobias Pietzsch
 *
 * @param <V>
 *            the type of the graph vertices iterated.
 * @param <E>
 *            the type of the graph edges iterated.
 */
public class UndirectedDepthFirstIterator< V extends Vertex< E >, E extends Edge< V > > extends AbstractGraphIteratorAlgorithm< V, E >
{
	private final RefStack< V > stack;

	public UndirectedDepthFirstIterator( final V root, final ReadOnlyGraph< V, E > graph )
	{
		super( graph );
		stack = createVertexStack();
		reset( root );
	}

	public UndirectedDepthFirstIterator( final ReadOnlyGraph< V, E > graph )
	{
		super( graph );
		stack = createVertexStack();
	}

	public void reset( final V root )
	{
		super.reset();
		stack.push( root );
		fetchNext();
		visited.add( root );
	}

	@Override
	protected Iterable< E > neighbors( final V vertex )
	{
		return vertex.edges();
	}

	@Override
	protected V targetOf( final V source, final E edge, final V ref )
	{
		return Graphs.getOppositeVertex( edge, source, ref );
	}

	@Override
	protected V fetch( final V ref )
	{
		return stack.pop( ref );
	}

	@Override
	protected void toss( final V vertex )
	{
		stack.push( vertex );
	}

	@Override
	protected boolean canFetch()
	{
		return !stack.isEmpty();
	}
}
