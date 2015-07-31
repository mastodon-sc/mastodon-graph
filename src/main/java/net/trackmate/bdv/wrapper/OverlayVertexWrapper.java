package net.trackmate.bdv.wrapper;

import java.util.Iterator;

import net.trackmate.graph.Edge;
import net.trackmate.graph.Edges;
import net.trackmate.graph.Vertex;
import net.trackmate.trackscheme.TrackSchemeEdge;
import net.trackmate.trackscheme.TrackSchemeVertex;

public class OverlayVertexWrapper< V extends Vertex< E >, E extends Edge< V > >
	implements OverlayVertex< OverlayVertexWrapper< V, E >, OverlayEdgeWrapper< V, E > >, HasTrackSchemeVertex
{
	private final OverlayGraphWrapper< V, E > wrapper;

	final TrackSchemeVertex tsv;

	V mv;

	private final EdgesWrapper incomingEdges;

	private final EdgesWrapper outgoingEdges;

	private final EdgesWrapper edges;

	private final OverlayProperties< V > overlayProperties;

	OverlayVertexWrapper( final OverlayGraphWrapper< V, E > wrapper )
	{
		this.wrapper = wrapper;
		tsv = wrapper.trackSchemeGraph.vertexRef();
		mv = wrapper.modelGraph.vertexRef();
		incomingEdges = new EdgesWrapper( tsv.incomingEdges() );
		outgoingEdges = new EdgesWrapper( tsv.outgoingEdges() );
		edges = new EdgesWrapper( tsv.edges() );
		overlayProperties = wrapper.overlayProperties;
	}

	void updateModelVertexRef()
	{
		mv = wrapper.idmap.getVertex( tsv.getModelVertexId(), mv );
	}

	@Override
	public int getInternalPoolIndex()
	{
		return tsv.getInternalPoolIndex();
	}

	@Override
	public OverlayVertexWrapper< V, E > refTo( final OverlayVertexWrapper< V, E > obj )
	{
		tsv.refTo( obj.tsv );
		updateModelVertexRef();
		return this;
	}

	@Override
	public void localize( final float[] position )
	{
		overlayProperties.localize( mv, position );
	}

	@Override
	public void localize( final double[] position )
	{
		overlayProperties.localize( mv, position );
	}

	@Override
	public float getFloatPosition( final int d )
	{
		return overlayProperties.getFloatPosition( mv, d );
	}

	@Override
	public double getDoublePosition( final int d )
	{
		return overlayProperties.getDoublePosition( mv, d );
	}

	@Override
	public int numDimensions()
	{
		return overlayProperties.numDimensions( mv );
	}

	@Override
	public void getCovariance( final double[][] mat )
	{
		overlayProperties.getCovariance( mv, mat );
	}

	@Override
	public boolean isSelected()
	{
		return tsv.isSelected();
	}

	@Override
	public int getTimePoint()
	{
		return tsv.getTimePoint();
	}

	@Override
	public Edges< OverlayEdgeWrapper< V, E > > incomingEdges()
	{
		return incomingEdges;
	}


	@Override
	public Edges< OverlayEdgeWrapper< V, E > > outgoingEdges()
	{
		return outgoingEdges;
	}


	@Override
	public Edges< OverlayEdgeWrapper< V, E > > edges()
	{
		return edges;
	}

	@Override
	public TrackSchemeVertex getTrackSchemeVertex()
	{
		return tsv;
	}

	private class EdgesWrapper implements Edges< OverlayEdgeWrapper< V, E > >
	{
		private final Edges< TrackSchemeEdge > edges;

		private final TrackSchemeEdge e;

		private EdgesIteratorWrapper< V, E > iterator = null;

		public EdgesWrapper( final Edges< TrackSchemeEdge > edges )
		{
			this.edges = edges;
			e = wrapper.trackSchemeGraph.edgeRef();
		}

		@Override
		public Iterator< OverlayEdgeWrapper< V, E > > iterator()
		{
			if ( iterator == null )
				iterator = new EdgesIteratorWrapper< V, E >( wrapper.edgeRef(), edges.iterator() );
			else
				// a bit of a hack: this causes edges.iterator.reset()
				edges.iterator();
			return iterator;
		}

		@Override
		public int size()
		{
			return edges.size();
		}

		@Override
		public boolean isEmpty()
		{
			return edges.isEmpty();
		}

		@Override
		public OverlayEdgeWrapper< V, E > get( final int i )
		{
			return get( i, wrapper.edgeRef() );
		}

		@Override
		public OverlayEdgeWrapper< V, E > get( final int i, final OverlayEdgeWrapper< V, E > edge )
		{
			edge.tse.refTo( edges.get( i, e ) );
			return edge;
		}

		@Override
		public Iterator< OverlayEdgeWrapper< V, E > > safe_iterator()
		{
			return new EdgesIteratorWrapper< V, E >( wrapper.edgeRef(), edges.iterator() );
		}
	}

	private static class EdgesIteratorWrapper< V extends Vertex< E >, E extends Edge< V > > implements Iterator< OverlayEdgeWrapper< V, E > >
	{
		private final OverlayEdgeWrapper< V, E > edge;

		private final Iterator< TrackSchemeEdge > wrappedIterator;

		public EdgesIteratorWrapper( final OverlayEdgeWrapper< V, E > edge, final Iterator< TrackSchemeEdge > wrappedIterator )
		{
			this.edge = edge;
			this.wrappedIterator = wrappedIterator;
		}

		@Override
		public boolean hasNext()
		{
			return wrappedIterator.hasNext();
		}

		@Override
		public OverlayEdgeWrapper< V, E > next()
		{
			edge.tse.refTo( wrappedIterator.next() );
			return edge;
		}

		@Override
		public void remove()
		{
			throw new UnsupportedOperationException();
		}
	}
}