/*-
 * #%L
 * Mastodon Graphs
 * %%
 * Copyright (C) 2015 - 2020 Tobias Pietzsch, Jean-Yves Tinevez
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
package org.mastodon.graph;

import java.util.ArrayList;
import java.util.List;

import org.mastodon.labels.LabelSets;
import org.mastodon.properties.ObjPropertyMap;
import org.mastodon.properties.Property;
import org.mastodon.undo.GraphUndoRecorder;

public class GraphLabelsUndoExample
{
	private final ListenableTestGraph graph;

	private final LabelSets< ListenableTestVertex, Integer > labelsets;

	private final ObjPropertyMap< ListenableTestVertex, String > name;

	private final GraphUndoRecorder< ListenableTestVertex, ListenableTestEdge > undoRecorder;

	private int id;

	public static void main( final String[] args )
	{
		new GraphLabelsUndoExample().run();
	}

	public GraphLabelsUndoExample()
	{
		final int initialCapacity = 1000;
		graph = new ListenableTestGraph( initialCapacity );
		labelsets = new LabelSets<>( graph.getVertexPool() );
		name = new ObjPropertyMap<>( graph.getVertexPool() );

		final GraphIdBimap< ListenableTestVertex, ListenableTestEdge > idmap = new GraphIdBimap<>( graph.getVertexPool(), graph.getEdgePool() );
		final List< Property< ListenableTestVertex > > vertexUndoableProperties = new ArrayList<>();
		vertexUndoableProperties.add( labelsets );
		vertexUndoableProperties.add( name );
		final List< Property< ListenableTestEdge > > edgeUndoableProperties = new ArrayList<>();
		undoRecorder = new GraphUndoRecorder<>(
				initialCapacity,
				graph,
				idmap,
				ListenableTestVertexPool.vertexSerializer,
				ListenableTestEdgePool.edgeSerializer,
				vertexUndoableProperties,
				edgeUndoableProperties );
	}

	void run()
	{
		id = 0;

		final ListenableTestVertex a = graph.addVertex().init( 0, 0 );
		record();

		final ListenableTestVertex b = graph.addVertex().init( 1, 0 );
		record();

		labelsets.getLabels( a ).add( 13 );
		record();

		labelsets.getLabels( a ).add( 42 );
		record();

		labelsets.getLabels( b ).add( 42 );
		record();

		final ListenableTestVertex c = graph.addVertex().init( 2, 0 );
		record();

		labelsets.getLabels( c ).add( 1 );
		record();

		labelsets.getLabels( a ).remove( 42 );
		record();

		labelsets.getLabels( b ).add( 99 );
		record();

		labelsets.getLabels( b ).remove( 99 );
		record();

		name.set( a, "AAA" );
		record();

		graph.remove( a );
		record();

		labelsets.getLabels( c ).remove( 1 );
		record();

		while ( id > 1 )
		{
			undoRecorder.undo();
			print( --id );
		}
	}

	void record()
	{
		undoRecorder.setUndoPoint();
		print( ++id );
	}

	void print( final int id )
	{
		System.out.println(graph.vertices());
		for ( final int label : new int[] { 1, 13, 42, 99 } )
		{
			System.out.print("label " + label + ":");
			System.out.println(labelsets.getLabeledWith( label ));
		}
		for ( final ListenableTestVertex v : graph.vertices() )
			System.out.println( "name(" + v + ") = " + name.get( v ));
		System.out.println();
	}
}
