/*-
 * #%L
 * Mastodon Graphs
 * %%
 * Copyright (C) 2015 - 2025 Tobias Pietzsch, Jean-Yves Tinevez
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package org.mastodon.undo;

import java.util.ArrayList;
import java.util.List;

import org.mastodon.graph.Edge;
import org.mastodon.graph.GraphIdBimap;
import org.mastodon.graph.GraphListener;
import org.mastodon.graph.ListenableGraph;
import org.mastodon.graph.Vertex;
import org.mastodon.io.AttributeSerializer;
import org.mastodon.pool.Attribute;
import org.mastodon.properties.Property;
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

	private final List< PropertyAndRecorder< V > > vertexPropertyRecorders;

	private final List< PropertyAndRecorder< E > > edgePropertyRecorders;

	static class PropertyAndRecorder< O >
	{
		final Property< O > property;

		final Recorder< O > recorder;

		PropertyAndRecorder(
				final Property< O > propertyMap,
				final Recorder< O > recorder )
		{
			this.property = propertyMap;
			this.recorder = recorder;
		}

		void recordIfSet( final O obj )
		{
			if ( property.isSet( obj ) )
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
			property.beforePropertyChangeListeners().add( vertex -> {
				if ( recording )
					recorder.record( vertex );
			} );
			if ( !( property instanceof Attribute ) )
				vertexPropertyRecorders.add( new PropertyAndRecorder<>( property, recorder ) );
		}

		for ( final Property< E > property  : edgeProperties )
		{
			final PropertyUndoRedoStack< E > propertyUndoRedoStack = property.createUndoRedoStack();
			final Recorder< E > recorder = edits.createSetEdgePropertyRecorder( propertyUndoRedoStack );
			property.beforePropertyChangeListeners().add( edge -> {
				if ( recording )
					recorder.record( edge );
			} );
			if ( !( property instanceof Attribute ) )
				edgePropertyRecorders.add( new PropertyAndRecorder<>( property, recorder ) );
		}
	}

	public < T extends UndoableEdit > Recorder< T > createGenericUndoableEditRecorder()
	{
		return edits.createGenericUndoableEditRecorder();
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

	public void setSavePoint()
	{
		edits.setSavePoint();
	}

	public boolean isSavePoint()
	{
		return edits.isSavePoint();
	}

	@Override
	public void graphRebuilt()
	{
//		System.out.println( "UndoRecorder.graphRebuilt()" );
		edits.clear();
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
