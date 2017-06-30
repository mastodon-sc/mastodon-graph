package org.mastodon.undo;

import java.util.ArrayList;
import java.util.List;

import org.mastodon.graph.Edge;
import org.mastodon.graph.GraphIdBimap;
import org.mastodon.graph.GraphListener;
import org.mastodon.graph.ListenableGraph;
import org.mastodon.graph.Vertex;
import org.mastodon.io.AttributeSerializer;
import org.mastodon.properties.Property;
import org.mastodon.properties.PropertyMap;
import org.mastodon.properties.undo.PropertyUndoRedoStack;

public class GraphUndoRecorder<
			V extends Vertex< E >,
			E extends Edge< V > >
		implements GraphListener< V, E >, UndoPointMarker
{
	protected boolean recording;

	private final GraphUndoRedoStack< V, E > edits;

	private final Recorder< V > addVertex;

	private final Recorder< E > addEdge;

	private final Recorder< V > removeVertex;

	private final Recorder< E > removeEdge;

	private final List< PropertyMapAndRecorder< V > > vertexPropertyRecorders;

	private final List< PropertyMapAndRecorder< E > > edgePropertyRecorders;

	static class PropertyMapAndRecorder< O >
	{
		final PropertyMap< O, ? > propertyMap;

		final Recorder< O > recorder;

		PropertyMapAndRecorder(
				final PropertyMap< O, ? > propertyMap,
				final Recorder< O > recorder )
		{
			this.propertyMap = propertyMap;
			this.recorder = recorder;
		}

		void recordIfSet( final O obj )
		{
			if ( propertyMap.isSet( obj ) )
				recorder.record( obj );
		}
	}

	public GraphUndoRecorder(
			final int initialCapacity,
			final ListenableGraph< V, E > graph,
			final GraphIdBimap< V, E > idmap,
			final AttributeSerializer< V > vertexSerializer,
			final AttributeSerializer< E > edgeSerializer,
			final List< Property< V > > vertexProperties,
			final List< Property< E > > edgeProperties )
	{
		recording = true;
		final UndoIdBimap< V > vertexUndoIdBimap = new UndoIdBimap<>( idmap.vertexIdBimap() );
		final UndoIdBimap< E > edgeUndoIdBimap = new UndoIdBimap<>( idmap.edgeIdBimap() );
		edits = new GraphUndoRedoStack<>( initialCapacity, graph, vertexSerializer, edgeSerializer, vertexUndoIdBimap, edgeUndoIdBimap );
		graph.addGraphListener( this );

		vertexPropertyRecorders = new ArrayList<>();
		edgePropertyRecorders = new ArrayList<>();

		addVertex = edits.createAddVertexRecorder();
		removeVertex = edits.createRemoveVertexRecorder();
		addEdge = edits.createAddEdgeRecorder();
		removeEdge = edits.createRemoveEdgeRecorder();

		for ( final Property< V > property  : vertexProperties )
		{
			final PropertyUndoRedoStack< V > propertyUndoRedoStack = property.createUndoRedoStack();
			final Recorder< V > recorder = edits.createSetVertexPropertyRecorder( propertyUndoRedoStack );
			property.addBeforePropertyChangeListener( vertex -> {
				if ( recording )
					recorder.record( vertex );
			} );
			if ( property instanceof PropertyMap )
			{
				@SuppressWarnings( "unchecked" )
				final PropertyMap< V, ? > pm = ( PropertyMap< V, ? > ) property;
				vertexPropertyRecorders.add( new PropertyMapAndRecorder<>( pm, recorder ) );
			}
		}

		for ( final Property< E > property  : edgeProperties )
		{
			final PropertyUndoRedoStack< E > propertyUndoRedoStack = property.createUndoRedoStack();
			final Recorder< E > recorder = edits.createSetEdgePropertyRecorder( propertyUndoRedoStack );
			property.addBeforePropertyChangeListener( edge -> {
				if ( recording )
					recorder.record( edge );
			} );
			if ( property instanceof PropertyMap )
			{
				@SuppressWarnings( "unchecked" )
				final PropertyMap< E, ? > pm = ( PropertyMap< E, ? > ) property;
				edgePropertyRecorders.add( new PropertyMapAndRecorder<>( pm, recorder ) );
			}
		}
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
			addVertex.record( vertex );
		}
	}

	@Override
	public void vertexRemoved( final V vertex )
	{
		if ( recording )
		{
//			System.out.println( "UndoRecorder.vertexRemoved()" );
			vertexPropertyRecorders.forEach( r -> r.recordIfSet( vertex ) );
			removeVertex.record( vertex );
		}
	}

	@Override
	public void edgeAdded( final E edge )
	{
		if ( recording )
		{
//			System.out.println( "UndoRecorder.edgeAdded()" );
			addEdge.record( edge );
		}
	}

	@Override
	public void edgeRemoved( final E edge )
	{
		if ( recording )
		{
//			System.out.println( "UndoRecorder.edgeRemoved()" );
			edgePropertyRecorders.forEach( r -> r.recordIfSet( edge ) );
			removeEdge.record( edge );
		}
	}
}
