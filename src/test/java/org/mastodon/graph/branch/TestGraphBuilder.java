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
package org.mastodon.graph.branch;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import org.mastodon.graph.ListenableTestEdge;
import org.mastodon.graph.ListenableTestGraph;
import org.mastodon.graph.ListenableTestVertex;

import java.util.Arrays;

/**
 * Utility class for initializing a non empty {@link ListenableTestGraph}, with
 * readably code. This example:
 * <pre>
 * ListenableTestGraph graph =
 *       TestGraphBuilder.build("0->1->2->3, 1->4");
 * </pre>
 * will initialize the following graph:
 * <pre>
 *       2 &#8594; 3
 *      /
 * 0 &#8594; 1
 *      \
 *       4
 * </pre>
 */
public class TestGraphBuilder
{
	private final ListenableTestGraph graph;
	private final TIntIntMap map;

	public TestGraphBuilder()
	{
		graph = new ListenableTestGraph();
		map = new TIntIntHashMap();
	}

	private boolean hasVertex(int id) {
		return map.containsKey( id );
	}

	private void addVertex(int id) {
		if( hasVertex( id ))
			return;
		ListenableTestVertex vertex = graph.addVertex().init( id, 0 );
		map.put(id, vertex.getInternalPoolIndex());
		graph.releaseRef( vertex );
	}

	public TestGraphBuilder addVertices( int ... ids )
	{
		for(int id : ids )
			addVertex( id );
		return this;
	}

	public TestGraphBuilder addEdges( String s )
	{
		int[] ids = Arrays.stream( s.split( "->" ) )
				.map( String::trim )
				.mapToInt( Integer::parseInt ).toArray();

		addVertices( ids );

		for ( int i = 0; i < ids.length - 1; i++ )
			addEdge( ids[ i ], ids[ i + 1 ] );

		return this;
	}

	private void addEdge( int sourceId, int targetId )
	{
		ListenableTestEdge eRef = graph.edgeRef();
		ListenableTestVertex sRef = graph.vertexRef();
		ListenableTestVertex tRef = graph.vertexRef();
		try {
			graph.addEdge(
					getVertexFromId( sourceId, sRef ),
					getVertexFromId( targetId, tRef ),
					eRef).init();
		} finally
		{
			graph.releaseRef( eRef );
			graph.releaseRef( sRef );
			graph.releaseRef( tRef );
		}
	}

	private ListenableTestVertex getVertexFromId( int sourceId, ListenableTestVertex ref )
	{
		return graph.vertices().getRefPool().getObject( map.get( sourceId ), ref );
	}

	public ListenableTestGraph build()
	{
		return graph;
	}

	public static ListenableTestGraph build( String description )
	{
		TestGraphBuilder builder = new TestGraphBuilder();
		for(String path : description.split( "," ))
			builder.addEdges( path );
		return builder.build();
	}

}
