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
package org.mastodon.graph.io;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.mastodon.graph.Edge;
import org.mastodon.graph.Graph;
import org.mastodon.graph.GraphIdBimap;
import org.mastodon.graph.ReadOnlyGraph;
import org.mastodon.graph.Vertex;
import org.mastodon.io.AttributeSerializer;
import org.mastodon.io.FileIdToObjectMap;
import org.mastodon.io.ObjectToFileIdMap;

import gnu.trove.map.hash.TIntIntHashMap;

/**
 * Write/read a {@link Graph} to/from an ObjectStream. For each {@link Graph}
 * class, the {@link GraphSerializer} interface needs to be implemented
 * that serializes vertex and edge attributes to/from a byte array.
 *
 * @author Tobias Pietzsch
 */
public class RawGraphIO
{
	public static final class GraphToFileIdMap< V extends Vertex< E >, E extends Edge< V > >
	{
		private final ObjectToFileIdMap< V > vertices;

		private final ObjectToFileIdMap< E > edges;

		public GraphToFileIdMap(
				final ObjectToFileIdMap< V > vertexToFileIdMap,
				final ObjectToFileIdMap< E > edgeToFileIdMap )
		{
			vertices = vertexToFileIdMap;
			edges = edgeToFileIdMap;
		}

		public ObjectToFileIdMap< V > vertices()
		{
			return vertices;
		}

		public ObjectToFileIdMap< E > edges()
		{
			return edges;
		}
	}

	public static final class FileIdToGraphMap< V extends Vertex< E >, E extends Edge< V > >
	{
		private final FileIdToObjectMap< V > vertices;

		private final FileIdToObjectMap< E > edges;

		public FileIdToGraphMap(
				final FileIdToObjectMap< V > fileIdToVertexMap,
				final FileIdToObjectMap< E > fileIdToEdgeMap )
		{
			vertices = fileIdToVertexMap;
			edges = fileIdToEdgeMap;
		}

		public FileIdToObjectMap< V > vertices()
		{
			return vertices;
		}

		public FileIdToObjectMap< E > edges()
		{
			return edges;
		}
	}

	public static < V extends Vertex< E >, E extends Edge< V > >
			GraphToFileIdMap< V, E > write(
					final ReadOnlyGraph< V, E > graph,
					final GraphIdBimap< V, E > idmap,
					final GraphSerializer< V, E > io,
					final ObjectOutputStream oos )
			throws IOException
	{
		final int numVertices = graph.vertices().size();
		oos.writeInt( numVertices );

		final AttributeSerializer< V > vio = io.getVertexSerializer();
		final AttributeSerializer< E > eio = io.getEdgeSerializer();

		final byte[] vbytes = new byte[ vio.getNumBytes() ];
		final boolean writeVertexBytes = vio.getNumBytes() > 0;
		final TIntIntHashMap vertexIdToFileIndex = new TIntIntHashMap( 2 * numVertices, 0.75f, -1, -1 );
		int i = 0;
		for ( final V v : graph.vertices() )
		{
			if ( writeVertexBytes )
			{
				vio.getBytes( v, vbytes );
				oos.write( vbytes );
			}

			final int id = idmap.getVertexId( v );
			vertexIdToFileIndex.put( id, i );
			++i;
		}

		final int numEdges = graph.edges().size();
		oos.writeInt( numEdges );

		final byte[] ebytes = new byte[ eio.getNumBytes() ];
		final boolean writeEdgeBytes = eio.getNumBytes() > 0;
		final V v = graph.vertexRef();
		final TIntIntHashMap edgeIdToFileIndex = new TIntIntHashMap( 2 * numEdges, 0.75f, -1, -1 );
		i = 0;
		for( final E e : graph.edges() )
		{
			final int from = vertexIdToFileIndex.get( idmap.getVertexId( e.getSource( v ) ) );
			final int to = vertexIdToFileIndex.get( idmap.getVertexId( e.getTarget( v ) ) );
			final int sourceOutIndex = e.getSourceOutIndex();
			final int targetInIndex = e.getTargetInIndex();
			oos.writeInt( from );
			oos.writeInt( to );
			oos.writeInt( sourceOutIndex );
			oos.writeInt( targetInIndex );

			if ( writeEdgeBytes )
			{
				eio.getBytes( e, ebytes );
				oos.write( ebytes );
			}

			final int id = idmap.getEdgeId( e );
			edgeIdToFileIndex.put( id, i );
			++i;
		}
		graph.releaseRef( v );

		final ObjectToFileIdMap< V > vertexToFileIdMap = new ObjectToFileIdMap<>( vertexIdToFileIndex, idmap.vertexIdBimap() );
		final ObjectToFileIdMap< E > edgeToFileIdMap = new ObjectToFileIdMap<>( edgeIdToFileIndex, idmap.edgeIdBimap() );
		return new GraphToFileIdMap<>( vertexToFileIdMap, edgeToFileIdMap );
	}

	public static < V extends Vertex< E >, E extends Edge< V > >
			FileIdToGraphMap< V, E > read(
					final Graph< V, E > graph,
					final GraphIdBimap< V, E > idmap,
					final GraphSerializer< V, E > io,
					final ObjectInputStream ois )
			throws IOException
	{
		final int numVertices = ois.readInt();
		final V v1 = graph.vertexRef();
		final V v2 = graph.vertexRef();
		final E e = graph.edgeRef();

		final AttributeSerializer< V > vio = io.getVertexSerializer();
		final AttributeSerializer< E > eio = io.getEdgeSerializer();

		final byte[] vbytes = new byte[ vio.getNumBytes() ];
		final boolean readVertexBytes = vio.getNumBytes() > 0;
		final TIntIntHashMap fileIndexToVertexId = new TIntIntHashMap( 2 * numVertices, 0.75f, -1, -1 );
		for ( int i = 0; i < numVertices; ++i )
		{
			graph.addVertex( v1 );
			if ( readVertexBytes )
			{
				ois.readFully( vbytes );
				vio.setBytes( v1, vbytes );
			}
			vio.notifySet( v1 );
			fileIndexToVertexId.put( i, idmap.getVertexId( v1 ) );
		}

		final int numEdges = ois.readInt();
		final byte[] ebytes = new byte[ eio.getNumBytes() ];
		final boolean readEdgeBytes = eio.getNumBytes() > 0;
		final TIntIntHashMap fileIndexToEdgeId = new TIntIntHashMap( 2 * numEdges, 0.75f, -1, -1 );
		for ( int i = 0; i < numEdges; ++i )
		{
			final int from = fileIndexToVertexId.get( ois.readInt() );
			final int to = fileIndexToVertexId.get( ois.readInt() );
			final int sourceOutIndex = ois.readInt();
			final int targetInIndex = ois.readInt();
			idmap.getVertex( from, v1 );
			idmap.getVertex( to, v2 );
			graph.insertEdge( v1, sourceOutIndex, v2, targetInIndex, e );
			if ( readEdgeBytes )
			{
				ois.readFully( ebytes );
				eio.setBytes( e, ebytes );
			}
			eio.notifySet( e );
			fileIndexToEdgeId.put( i, idmap.getEdgeId( e ) );
		}

		graph.releaseRef( v1 );
		graph.releaseRef( v2 );
		graph.releaseRef( e );

		final FileIdToObjectMap< V > fileIdToVertexMap = new FileIdToObjectMap<>( fileIndexToVertexId, idmap.vertexIdBimap() );
		final FileIdToObjectMap< E > fileIdToEdgeMap = new FileIdToObjectMap<>( fileIndexToEdgeId, idmap.edgeIdBimap() );
		return new FileIdToGraphMap<>( fileIdToVertexMap, fileIdToEdgeMap );
	}
}
