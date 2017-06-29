package org.mastodon.graph.object;

import java.util.Collection;
import java.util.Iterator;

import org.mastodon.graph.Edges;

class ObjectEdgesSourceToTarget< E extends AbstractObjectEdge< E, ? > > implements Edges< E >
{
	private final AbstractObjectVertex< ?, E > source;

	private final AbstractObjectVertex< ?, E > target;

	private final Collection< E > edges;

	private ObjectEdgesSourceToTargetIterator iterator;


	/**
	 * Returns a new collections of all the edges that link the source vertex to
	 * the target vertex.
	 *
	 * @param source
	 *            the source vertex.
	 * @param target
	 *            the target vertex.
	 * @param edges
	 *            the graph collection of edges. This is used for edge removal
	 *            in the iterator of this collection.
	 */
	public ObjectEdgesSourceToTarget( final AbstractObjectVertex< ?, E > source, final AbstractObjectVertex< ?, E > target, final Collection< E > edges )
	{
		this.source = source;
		this.target = target;
		this.edges = edges;
	}

	@Override
	public int size()
	{
		int numEdges = 0;
		for ( final E edge : source.outgoing )
			if ( target.incoming.edges.contains( edge ) )
				numEdges++;
		return numEdges;
	}

	@Override
	public boolean isEmpty()
	{
		return size() == 0;
	}

	@Override
	public E get( final int i )
	{
		int iEdge = 0;
		for ( final E edge : source.outgoing )
			if ( target.incoming.edges.contains( edge ) )
				if ( iEdge++ == i )
					return edge;
		return null;
	}

	@Override
	public E get( final int i, final E edge )
	{
		return get( i );
	}

	@Override
	public Iterator< E > iterator()
	{
		if ( iterator == null )
			iterator = new ObjectEdgesSourceToTargetIterator();
		else
			iterator.reset();
		return iterator;
	}

	@Override
	public Iterator< E > safe_iterator()
	{
		return iterator();
	}

	private class ObjectEdgesSourceToTargetIterator implements Iterator< E >
	{

		private Iterator< E > outgoingIterator;

		private E next;

		private E current;

		private boolean hasNext;

		public ObjectEdgesSourceToTargetIterator()
		{
			reset();
		}

		private void reset()
		{
			outgoingIterator = source.outgoingEdges().iterator();
			hasNext = true;
			next = null;
			prefetch();
		}

		private void prefetch()
		{
			while ( outgoingIterator.hasNext() )
			{
				next = outgoingIterator.next();
				if ( target.incoming.edges.contains( next ) )
				{
					hasNext = true;
					return;
				}
			}
			hasNext = false;
		}

		@Override
		public boolean hasNext()
		{
			return hasNext;
		}

		@Override
		public E next()
		{
			current = next;
			prefetch();
			return current;
		}

		@Override
		public void remove()
		{
			if ( edges.remove( current ) )
			{
				outgoingIterator.remove();
				target.incoming.edges.remove( current );
			}
		}
	}
}
