package org.mastodon.revisedundo;

import java.util.ArrayList;
import java.util.List;

import org.mastodon.graph.Edge;
import org.mastodon.graph.GraphIdBimap;
import org.mastodon.graph.GraphListener;
import org.mastodon.graph.ListenableGraph;
import org.mastodon.graph.Vertex;
import org.mastodon.properties.PropertyMap;
import org.mastodon.properties.undo.PropertyUndoRedoStack;
import org.mastodon.revisedundo.attributes.Attribute;
import org.mastodon.revisedundo.attributes.AttributeSerializer;

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

	private final List< PropertyMap< V, ? > > vertexProps;

	private final List< Recorder< V > > vertexPropertyRecorders;

	private final List< PropertyMap< E, ? > > edgeProps;

	private final List< Recorder< E > > edgePropertyRecorders;

	public GraphUndoRecorder(
			final int initialCapacity,
			final ListenableGraph< V, E > graph,
			final GraphIdBimap< V, E > idmap,
			final AttributeSerializer< V > vertexSerializer,
			final AttributeSerializer< E > edgeSerializer,
			final List< Attribute< V > > vertexAttributes,
			final List< Attribute< E > > edgeAttributes,
			final List< PropertyMap< V, ? > > vertexProperties,
			final List< PropertyMap< E, ? > > edgeProperties )
	{
		recording = true;
		final UndoIdBimap< V > vertexUndoIdBimap = new UndoIdBimap<>( idmap.vertexIdBimap() );
		final UndoIdBimap< E > edgeUndoIdBimap = new UndoIdBimap<>( idmap.edgeIdBimap() );
		edits = new GraphUndoRedoStack<>( initialCapacity, graph, vertexSerializer, edgeSerializer, vertexUndoIdBimap, edgeUndoIdBimap );
		graph.addGraphListener( this );

		vertexProps = new ArrayList<>();
		vertexPropertyRecorders = new ArrayList<>();
		edgeProps = new ArrayList<>();
		edgePropertyRecorders = new ArrayList<>();

		addVertex = edits.createAddVertexRecorder();
		removeVertex = edits.createRemoveVertexRecorder();
		addEdge = edits.createAddEdgeRecorder();
		removeEdge = edits.createRemoveEdgeRecorder();

		for ( final Attribute< V > attribute : vertexAttributes )
		{
			final Recorder< V > recorder = edits.createSetVertexAttributeRecorder( attribute );
			attribute.addBeforeAttributeChangeListener( ( a, v ) -> recorder.record( v ) );
		}

		for ( final Attribute< E > attribute : edgeAttributes )
		{
			final Recorder< E > recorder = edits.createSetEdgeAttributeRecorder( attribute );
			attribute.addBeforeAttributeChangeListener( ( a, v ) -> recorder.record( v ) );
		}

		for ( final PropertyMap< V, ? > property  : vertexProperties )
		{
			final PropertyUndoRedoStack< V > propertyUndoRedoStack = property.createUndoRedoStack();
			final Recorder< V > recorder = edits.createSetVertexPropertyRecorder( propertyUndoRedoStack );
			property.addBeforePropertyChangeListener( ( p, vertex ) -> {
				if ( recording )
					recorder.record( vertex );
			} );
			vertexProps.add( property );
			vertexPropertyRecorders.add( recorder );
		}

		for ( final PropertyMap< E, ? > property  : edgeProperties )
		{
			final PropertyUndoRedoStack< E > propertyUndoRedoStack = property.createUndoRedoStack();
			final Recorder< E > recorder = edits.createSetEdgePropertyRecorder( propertyUndoRedoStack );
			property.addBeforePropertyChangeListener( ( p, edge ) -> {
				if ( recording )
					recorder.record( edge );
			} );
			edgeProps.add( property );
			edgePropertyRecorders.add( recorder );
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
			for ( int i = 0; i < vertexProps.size(); ++i )
				if ( vertexProps.get( i ).isSet( vertex ) )
					vertexPropertyRecorders.get( i ).record( vertex );
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
			for ( int i = 0; i < edgeProps.size(); ++i )
				if ( edgeProps.get( i ).isSet( edge ) )
					edgePropertyRecorders.get( i ).record( edge );
			removeEdge.record( edge );
		}
	}
}
