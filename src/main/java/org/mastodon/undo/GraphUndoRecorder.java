package org.mastodon.undo;

import org.mastodon.features.Feature;
import org.mastodon.features.FeatureChangeListener;
import org.mastodon.features.Features;
import org.mastodon.graph.Edge;
import org.mastodon.graph.GraphIdBimap;
import org.mastodon.graph.GraphListener;
import org.mastodon.graph.ListenableGraph;
import org.mastodon.graph.Vertex;
import org.mastodon.undo.attributes.Attribute;
import org.mastodon.undo.attributes.AttributeChangeListener;
import org.mastodon.undo.attributes.Attributes;

/**TODO: figure out when mappings can be removed from
 * UndoIdBimaps. */
/**
 * TODO: move to package model.undo (?)
 */
/**
 * Recorder for undoable graph modification events. This class is the central
 * entry-point into the <b>graph undo framework</b>, and offers undo
 * capabilities to the graph specified.
 * <p>
 * This class registers itself as a listener of the specified graph, and listen
 * to graph modification events:
 * <ul>
 * <li>vertex added/removed.
 * <li>edge added/removed.
 * <li>edge/vertex feature changes.
 * <li>edge/vertex attribute changes.
 * </ul>
 * These edits are recorded efficiently in a data structure that keeps track of
 * them, the {@link GraphUndoableEditList}. Consumers can call the
 * {@link #setUndoPoint()} to mark a state that may be recalled later by calls
 * to {@link #undo()}, modifying the graph. Several consecutive calls
 * {@link #undo()} walk back in time through the undo-point stack. Calls to
 * {@link #redo()} move forward in time and redo changes that were undo-ed by
 * calls to {@link #undo()}. Only these 3 methods:
 * <ul>
 * <li>{@link #setUndoPoint()}
 * <li>{@link #undo()}
 * <li>{@link #redo()}
 * </ul>
 * are relevant for consumers.
 * <p>
 * The number of recent graph changes that can be undo-ed by this class are
 * determined by the {@link GraphUndoableEditList} class wrapped in the graph
 * undo recorder.
 * <p>
 * As some of the graph vertices or edges need to be serialized within the
 * {@link GraphUndoableEditList}, a serializer - specific to the graph - must be
 * provided. A specific interface - {@link GraphUndoSerializer} - is used for
 * the undo framework.
 * <p>
 * To stop the undo-redo mechanism, de-register the graph undo recorder from the
 * graph listeners. Re-registering it after some changes have been made to the
 * graph will generate unexpected behaviors.
 *
 * @param <V>
 *            the vertex type.
 * @param <E>
 *            the edge type.
 * @param <L>
 *            the type of the graph undoable edit list.
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public class GraphUndoRecorder<
			V extends Vertex< E >,
			E extends Edge< V >,
			L extends GraphUndoableEditList< V, E > >
		implements GraphListener< V, E >, UndoPointMarker
{
	private static final int defaultCapacity = 1000;

	protected boolean recording;

	protected final L edits;

	/**
	 * Creates a graph undo recorder, using the default
	 * {@link GraphUndoableEditList} to store edits. Undo/redo are available
	 * when this method returns.
	 *
	 * @param graph
	 *            the graph to listen to.
	 * @param vertexFeatures
	 *            the graph vertex features.
	 * @param edgeFeatures
	 *            the graph edge features.
	 * @param vertexAttributes
	 *            the graph vertex attributes.
	 * @param edgeAttributes
	 *            the graph edge attributes.
	 * @param idmap
	 *            an ID bi-directional map for the graph.
	 * @param serializer
	 *            a serializer for the graph edges and vertices.
	 * @return a new graph undo recorder, registered as a listener to the
	 *         specified graph.
	 * @param <V>
	 *            the vertex type.
	 * @param <E>
	 *            the edge type.
	 */
	public static < V extends Vertex< E >, E extends Edge< V > >
		GraphUndoRecorder< V, E, GraphUndoableEditList< V, E > > create(
				final ListenableGraph< V, E > graph,
				final Features< V > vertexFeatures,
				final Features< E > edgeFeatures,
				final Attributes< V > vertexAttributes,
				final Attributes< E > edgeAttributes,
				final GraphIdBimap< V, E > idmap,
				final GraphUndoSerializer< V, E > serializer )
	{
		final UndoIdBimap< V > vertexUndoIdBimap = new UndoIdBimap<>( idmap.vertexIdBimap() );
		final UndoIdBimap< E > edgeUndoIdBimap = new UndoIdBimap<>( idmap.edgeIdBimap() );
		final GraphUndoableEditList< V, E > edits = new GraphUndoableEditList<>(
				defaultCapacity,
				graph,
				vertexFeatures,
				edgeFeatures,
				vertexAttributes,
				edgeAttributes,
				serializer,
				vertexUndoIdBimap,
				edgeUndoIdBimap );
		return new GraphUndoRecorder<>(
				graph,
				vertexFeatures,
				edgeFeatures,
				vertexAttributes,
				edgeAttributes,
				edits );
	}

	/**
	 * Instantiates a new graph recorder.
	 * <p>
	 * The instance registers itself as a listener to the specified graph.
	 * Undo/redo are available immediately after construction.
	 *
	 * @param graph
	 *            the graph to listen.
	 * @param vertexFeatures
	 *            the graph vertex features.
	 * @param edgeFeatures
	 *            the graph edge features.
	 * @param vertexAttributes
	 *            the graph vertex attributes.
	 * @param edgeAttributes
	 *            the graph edge attributes.
	 * @param edits
	 *            the data structure to keep tracks of graph edits.
	 */
	public GraphUndoRecorder(
			final ListenableGraph< V, E > graph,
			final Features< V > vertexFeatures,
			final Features< E > edgeFeatures,
			final Attributes< V > vertexAttributes,
			final Attributes< E > edgeAttributes,
			final L edits )
	{
		recording = true;
		this.edits = edits;
		graph.addGraphListener( this );
		vertexFeatures.addFeatureChangeListener( beforeVertexFeatureChange );
		edgeFeatures.addFeatureChangeListener( beforeEdgeFeatureChange );
		vertexAttributes.addAttributeChangeListener( beforeVertexAttributeChange );
		edgeAttributes.addAttributeChangeListener( beforeEdgeAttributeChange );
	}

	@Override
	public void setUndoPoint()
	{
		edits.setUndoPoint();
	}

	public void undo()
	{
//		System.out.println( "UndoRecorder.undo()" );
		recording = false;
		edits.undo();
		recording = true;
	}

	public void redo()
	{
//		System.out.println( "UndoRecorder.redo()" );
		recording = false;
		edits.redo();
		recording = true;
	}

	@Override
	public void graphRebuilt()
	{
		System.out.println( "UndoRecorder.graphRebuilt()" );
		System.out.println( "TODO!!!!" );
	}

	@Override
	public void vertexAdded( final V vertex )
	{
		if ( recording )
		{
//			System.out.println( "UndoRecorder.vertexAdded()" );
			edits.recordAddVertex( vertex );
		}
	}

	@Override
	public void vertexRemoved( final V vertex )
	{
		if ( recording )
		{
//			System.out.println( "UndoRecorder.vertexRemoved()" );
			edits.recordRemoveVertex( vertex );
		}
	}

	@Override
	public void edgeAdded( final E edge )
	{
		if ( recording )
		{
//			System.out.println( "UndoRecorder.edgeAdded()" );
			edits.recordAddEdge( edge );
		}
	}

	@Override
	public void edgeRemoved( final E edge )
	{
		if ( recording )
		{
//			System.out.println( "UndoRecorder.edgeRemoved()" );
			edits.recordRemoveEdge( edge );
		}
	}

	private final FeatureChangeListener< V > beforeVertexFeatureChange = new FeatureChangeListener< V >()
	{
		@Override
		public void beforeFeatureChange( final Feature< ?, V, ? > feature, final V vertex )
		{
			if ( recording )
				edits.recordSetVertexFeature( feature, vertex );
		}
	};

	private final FeatureChangeListener< E > beforeEdgeFeatureChange = new FeatureChangeListener< E >()
	{
		@Override
		public void beforeFeatureChange( final Feature< ?, E, ? > feature, final E edge )
		{
			if ( recording )
				edits.recordSetEdgeFeature( feature, edge );
		}
	};

	private final AttributeChangeListener< V > beforeVertexAttributeChange = new AttributeChangeListener< V >()
	{
		@Override
		public void beforeAttributeChange( final Attribute< V > attribute, final V vertex )
		{
			if ( recording )
				edits.recordSetVertexAttribute( attribute, vertex );
		}
	};

	private final AttributeChangeListener< E > beforeEdgeAttributeChange = new AttributeChangeListener< E >()
	{
		@Override
		public void beforeAttributeChange( final Attribute< E > attribute, final E edge )
		{
			if ( recording )
				edits.recordSetEdgeAttribute( attribute, edge );
		}
	};
}
