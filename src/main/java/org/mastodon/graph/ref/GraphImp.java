package org.mastodon.graph.ref;

import java.util.Iterator;

import org.mastodon.graph.Graph;
import org.mastodon.pool.MappedElement;
import org.mastodon.pool.PoolCollectionWrapper;

public class GraphImp<
VP extends AbstractVertexPool< V, E, T >,
EP extends AbstractEdgePool< E, V, T >,
V extends AbstractVertex< V, E, VP, T >,
E extends AbstractEdge< E, V, EP, T >,
T extends MappedElement >
implements Graph< V, E >
{

	protected final VP vertexPool;

	protected final EP edgePool;

	public GraphImp( final VP vertexPool, final EP edgePool )
	{
		this.vertexPool = vertexPool;
		this.edgePool = edgePool;
		vertexPool.linkEdgePool( edgePool );
	}

	@SuppressWarnings( "unchecked" )
	public GraphImp( final EP edgePool )
	{
		this.vertexPool = ( VP ) edgePool.vertexPool;
		this.edgePool = edgePool;
		vertexPool.linkEdgePool( edgePool );
	}

	@Override
	public V addVertex()
	{
		return vertexPool.create( vertexRef() );
	}

	@Override
	public V addVertex( final V vertex )
	{
		return vertexPool.create( vertex );
	}

	@Override
	public E addEdge( final V source, final V target )
	{
		return edgePool.addEdge( source, target, edgeRef() );
	}

	@Override
	public E addEdge( final V source, final V target, final E edge )
	{
		return edgePool.addEdge( source, target, edge );
	}

	@Override
	public E insertEdge( final V source, final int sourceOutIndex, final V target, final int targetInIndex )
	{
		return edgePool.insertEdge( source, sourceOutIndex, target, targetInIndex, edgeRef() );
	}

	@Override
	public E insertEdge( final V source, final int sourceOutIndex, final V target, final int targetInIndex, final E edge )
	{
		return edgePool.insertEdge( source, sourceOutIndex, target, targetInIndex, edge );
	}

	@Override
	public E getEdge( final V source, final V target )
	{
		return edgePool.getEdge( source, target, edgeRef() );
	}

	@Override
	public E getEdge( final V source, final V target, final E edge )
	{
		return edgePool.getEdge( source, target, edge );
	}

	@Override
	public PoolCollectionWrapper< V > vertices()
	{
		return vertexPool.asRefCollection();
	}

	@Override
	public PoolCollectionWrapper< E > edges()
	{
		return edgePool.asRefCollection();
	}

	@Override
	public void remove( final V vertex )
	{
		vertexPool.delete( vertex );
	}

	@Override
	public void remove( final E edge )
	{
		edgePool.delete( edge );
	}

	@Override
	public void removeAllLinkedEdges( final V vertex )
	{
		edgePool.deleteAllLinkedEdges( vertex );
	}

	@Override
	public V vertexRef()
	{
		return vertexPool.createRef();
	}

	@Override
	public E edgeRef()
	{
		return edgePool.createRef();
	}

	@Override
	public void releaseRef( final V ref )
	{
		vertexPool.releaseRef( ref );
	}

	@Override
	public void releaseRef( final E ref )
	{
		edgePool.releaseRef( ref );
	}

	protected void clear()
	{
		vertexPool.clear();
		edgePool.clear();
	}

	@Override
	public Iterator< E > getEdges( final V source, final V target )
	{
		return getEdges( source, target, null );
	}

	@Override
	public Iterator< E > getEdges( final V source, final V target, Iterator< E > ref )
	{
		if ( null == ref || ( !( ref instanceof GraphImp.MyIterator ) ) )
			ref = new MyIterator();

		final MyIterator it = ( MyIterator ) ref;
		it.outgoingIterator = source.outgoingEdges().iterator();
		it.target = target;
		it.reset();
		return it;
	}

	private class MyIterator implements Iterator< E >
	{

		private final V vref = vertexRef();

		private final E current = edgeRef();

		private OutgoingEdges< E >.OutgoingEdgesIterator outgoingIterator;

		private V target;

		private boolean hasNext;

		private E next;

		private void reset()
		{
			hasNext = true;
			next = null;
			prefetch();
		}

		private void prefetch()
		{
			while ( outgoingIterator.hasNext() )
			{
				next = outgoingIterator.next();
				if (next.getTarget( vref ).equals( target ))
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
			current.refTo( next );
			prefetch();
			return current;
		}

		@Override
		public void remove()
		{
			GraphImp.this.remove( current );
		}

	}
}
