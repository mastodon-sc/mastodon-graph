package org.mastodon.graph.algorithm;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mastodon.collection.RefCollection;
import org.mastodon.collection.RefCollections;
import org.mastodon.collection.RefDeque;
import org.mastodon.collection.RefList;
import org.mastodon.collection.RefMaps;
import org.mastodon.collection.RefObjectMap;
import org.mastodon.collection.RefSet;
import org.mastodon.graph.Edge;
import org.mastodon.graph.ReadOnlyGraph;
import org.mastodon.graph.Vertex;
import org.mastodon.graph.algorithm.traversal.DepthFirstSearch;
import org.mastodon.graph.algorithm.traversal.GraphSearch.SearchDirection;
import org.mastodon.graph.algorithm.traversal.SearchListener;

/**
 * Class to generate a map of 'tracks' from a graph.
 * <p>
 * A track is a connected component of the graph. Here, the connected components
 * are exposed via their collection of vertices or edges, and can be accessed
 * via a map where a root vertex is used as a key.
 * <p>
 * A track map is only a 'snapshot' of the graph, and is not kept in sync with
 * it if it is modified.
 * <p>
 * It is ensured that each vertex and edge is present exactly once in the track
 * maps. Even if a track has more than 1 root, it is present only once in the
 * maps. Since we use roots to build and index the maps, connected components
 * that have no roots (loops) will not be included in the map.
 *
 * @author Jean-Yves Tinevez
 */
public class TrackMap< V extends Vertex< E >, E extends Edge< V > >
{
	private final RefList< V > roots;

	private final Map< V, RefSet< V > > vertices;

	private final Map< V, RefSet< E > > edges;

	public static final < V extends Vertex< E >, E extends Edge< V > > TrackMap< V, E > build( final ReadOnlyGraph< V, E > graph )
	{

		final RefDeque< V > deque = RefCollections.createRefDeque( graph.vertices() );
		deque.addAll( RootFinder.getRoots( graph ) );

		final RefList< V > foundRoots = RefCollections.createRefList( graph.vertices(), deque.size() );
		foundRoots.addAll( deque );


		final RefObjectMap< V, RefSet< V > > trackVertexMap = RefMaps.createRefObjectMap( graph.vertices(), deque.size() );
		final RefObjectMap< V, RefSet< E > > trackEdgeMap = RefMaps.createRefObjectMap( graph.vertices(), deque.size() );

		final V vref = graph.vertexRef();
		final DepthFirstSearch< V, E > search = new DepthFirstSearch<>( graph, SearchDirection.UNDIRECTED );
		while ( !deque.isEmpty() )
		{
			final V root = deque.pop( vref );
			final RefSet< V > setVertex = RefCollections.createRefSet( graph.vertices() );
			final RefSet< E > setEdge = RefCollections.createRefSet( graph.edges() );

			trackVertexMap.put( root, setVertex );
			trackEdgeMap.put( root, setEdge );

			final MySearchListener< V, E > searchListener = new MySearchListener<>( setVertex, setEdge, deque );
			search.setTraversalListener( searchListener );
			search.start( root );

		}
		graph.releaseRef( vref );

		return new TrackMap<>( foundRoots, trackVertexMap, trackEdgeMap );
	}

	public List< V > getTracks()
	{
		return Collections.unmodifiableList( roots );
	}

	public Set< V > getTrackVertices(final V track)
	{
		return Collections.unmodifiableSet( vertices.get( track ) );
	}

	public Set< E > getTrackEdges(final V track)
	{
		return Collections.unmodifiableSet( edges.get( track ) );
	}

	private static class MySearchListener< V extends Vertex< E >, E extends Edge< V > > implements SearchListener< V, E, DepthFirstSearch< V, E > >
	{

		private final RefSet< V > vertices;

		private final RefSet< E > edges;

		private final RefCollection< V > roots;

		public MySearchListener( final RefSet< V > vertices, final RefSet< E > edges, final RefCollection< V > roots )
		{
			this.vertices = vertices;
			this.edges = edges;
			this.roots = roots;
		}

		@Override
		public void processVertexLate( final V vertex, final DepthFirstSearch< V, E > search )
		{}

		@Override
		public void processVertexEarly( final V vertex, final DepthFirstSearch< V, E > search )
		{
			vertices.add( vertex );

			// Check if it is a root. Then remove it from the root collection.
			if ( vertex.incomingEdges().isEmpty() )
				roots.remove( vertex );
		}

		@Override
		public void processEdge( final E edge, final V from, final V to, final DepthFirstSearch< V, E > search )
		{
			edges.add( edge );
		}

		@Override
		public void crossComponent( final V from, final V to, final DepthFirstSearch< V, E > search )
		{}
	}

	private TrackMap( final RefList< V > roots, final Map< V, RefSet< V > > vertices, final Map< V, RefSet< E > > edges )
	{
		this.roots = roots;
		this.vertices = vertices;
		this.edges = edges;

	}

}
