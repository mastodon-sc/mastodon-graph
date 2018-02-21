package org.mastodon.graph.algorithm.traversal;

import org.mastodon.collection.RefDeque;
import org.mastodon.graph.Edge;
import org.mastodon.graph.ReadOnlyGraph;
import org.mastodon.graph.Vertex;

/**
 * A Breadth-first iterator, that traverses edges only following their
 * direction.
 * <p>
 * With {@code A -> B}, the iterator will move from A to B, but not from B
 * to A.
 *
 * @author Jean-Yves Tinevez
 *
 * @param <V>
 *            the type of the graph vertices iterated.
 * @param <E>
 *            the type of the graph edges iterated.
 */
public class BreadthFirstIterator< V extends Vertex< E >, E extends Edge< V > > extends AbstractGraphIteratorAlgorithm< V, E >
{
	private final RefDeque< V > queue;

	public BreadthFirstIterator( final V root, final ReadOnlyGraph< V, E > graph )
	{
		super( graph );
		this.queue = createVertexDeque();
		queue.offer( root );
		fetchNext();
		visited.add( root );
	}

	@Override
	protected Iterable< E > neighbors( final V vertex )
	{
		return vertex.outgoingEdges();
	}

	@Override
	protected V fetch( final V ref )
	{
		return queue.poll( ref );
	}

	@Override
	protected void toss( final V vertex )
	{
		queue.offer( vertex );
	}

	@Override
	protected boolean canFetch()
	{
		return !queue.isEmpty();
	}
}
