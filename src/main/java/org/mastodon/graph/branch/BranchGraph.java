package org.mastodon.graph.branch;

import org.mastodon.collection.ref.IntRefHashMap;
import org.mastodon.graph.Edge;
import org.mastodon.graph.GraphIdBimap;
import org.mastodon.graph.GraphListener;
import org.mastodon.graph.ListenableGraph;
import org.mastodon.graph.Vertex;
import org.mastodon.graph.algorithm.Assigner;
import org.mastodon.graph.ref.GraphImp;
import org.mastodon.pool.ByteMappedElement;


public class BranchGraph< V extends Vertex< E >, E extends Edge< V > > extends GraphImp< BranchVertexPool, BranchEdgePool, BranchVertex, BranchEdge, ByteMappedElement >
{

	private final ListenableGraph< V, E > graph;

	private final GraphIdBimap< V, E > idBimap;

	/**
	 * Maps from source graph vertex ID to {@link BranchVertex}. Only contains
	 * mappings for vertices that are actually linked to a branch vertex. If a
	 * key (a source graph vertex) is not present in this map, it means that it
	 * is linked to a {@link BranchEdge}.
	 */
	private final IntRefHashMap< BranchVertex > vbvMap;

	/**
	 * Maps from source graph vertex ID to {@link BranchEdge}. Only contains
	 * mappings for vertices that are actually linked to a branch edge. If a key
	 * (a source graph vertex) is not present in this map, it means that it is
	 * linked to a {@link BranchVertex}.
	 */
	private final IntRefHashMap< BranchEdge > vbeMap;

	/**
	 * Maps from source graph edge ID to {@link BranchEdge}.
	 */
	private final IntRefHashMap< BranchEdge > ebeMap;

	private final Assigner< V > assigner;


	public BranchGraph( final ListenableGraph< V, E > graph, final GraphIdBimap< V, E > idBimap )
	{
		super( new BranchEdgePool( graph.edges().size() / 50, new BranchVertexPool( graph.vertices().size() / 50 ) ) );
		this.graph = graph;
		this.idBimap = idBimap;
		this.vbvMap = new IntRefHashMap<>( vertexPool, -1 );
		this.vbeMap = new IntRefHashMap<>( edgePool, -1 );
		this.ebeMap = new IntRefHashMap<>( edgePool, -1 );
		final V vertexRef = graph.vertexRef();
		this.assigner = Assigner.getFor( vertexRef );
		graph.releaseRef( vertexRef );
		graph.addGraphListener( new MyGraphListener() );
	}

	/*
	 * Make graph read-only.
	 */

	@Override
	public BranchEdge addEdge( final BranchVertex source, final BranchVertex target )
	{
		throw new UnsupportedOperationException( "Cannot modify a branch graph." );
	}

	@Override
	public BranchEdge addEdge( final BranchVertex source, final BranchVertex target, final BranchEdge edge )
	{
		throw new UnsupportedOperationException( "Cannot modify a branch graph." );
	}

	@Override
	public BranchVertex addVertex()
	{
		throw new UnsupportedOperationException( "Cannot modify a branch graph." );
	}

	@Override
	public BranchVertex addVertex( final BranchVertex vertex )
	{
		throw new UnsupportedOperationException( "Cannot modify a branch graph." );
	}

	@Override
	public void remove( final BranchEdge edge )
	{
		throw new UnsupportedOperationException( "Cannot modify a branch graph." );
	}

	@Override
	public void remove( final BranchVertex vertex )
	{
		throw new UnsupportedOperationException( "Cannot modify a branch graph." );
	}

	@Override
	public void removeAllLinkedEdges( final BranchVertex vertex )
	{
		throw new UnsupportedOperationException( "Cannot modify a branch graph." );
	}

	/*
	 * Private methods (do the work).
	 */

	/**
	 * If branch graph vertex {@code w} has exactly one incoming and one
	 * outgoing edge, remove it and merge the incoming and outgoing edges.
	 *
	 * @param w
	 *            the branch vertex.
	 */
	private void checkFuse( final BranchVertex w )
	{
		if ( w.incomingEdges().size() == 1 && w.outgoingEdges().size() == 1 )
		{
			// Careful for syntax: we know that the BranchGraph pools are actual
			// pool objects.
			final BranchEdge f1 = edgeRef();
			final BranchEdge f2 = edgeRef();
			final BranchEdge f3 = edgeRef();
			final BranchVertex f1s = vertexRef();
			final BranchVertex f2t = vertexRef();
			// But the source graph might be an object graph.
			V v1 = graph.vertexRef();
			V v2 = graph.vertexRef();
			E f1e = graph.edgeRef();

			// f1 := branch edge to w
			w.incomingEdges().get( 0, f1 );
			// f2 := branch edge from w
			w.outgoingEdges().get( 0, f2 );

			// f1s := source branch vertex of f1
			f1.getSource( f1s );
			// f2t := target branch vertex of f2
			f2.getTarget( f2t );

			// f1e := source edge corresponding to f1
			f1e = idBimap.getEdge( f1.getLinkedEdgeId(), f1e );
			// v1 := target vertex of f1e ==> first source vertex on new
			// skeleton edge
			v1 = f1e.getTarget( v1 );

			// v2 := source vertex corresponding to f2t ==> terminates new
			// skeleton edge
			v2 = idBimap.getVertex( f2t.getLinkedVertexId(), v2 );

			// remember source edge linked from f1
			final int f1linkedEdgeIndex = f1.getLinkedEdgeId();

			// remove w, f1, f2 from branch graph
			remove( w );

			// f3 := new branch edge between f1s and f2t
			addEdge( f1s, f2t, f3 );

			// reference f3 from every source graph vertex on the path
			linkBranchEdge( v1, v2, f3 );

			// link from f3 to source edge that was previously linked from f1
			f3.setLinkedEdgeId( f1linkedEdgeIndex );

			graph.releaseRef( f1e );
			graph.releaseRef( v2 );
			graph.releaseRef( v1 );
			releaseRef( f2t );
			releaseRef( f1s );
			releaseRef( f3 );
			releaseRef( f2 );
			releaseRef( f1 );
		}
	}

	private BranchVertex split( final V v )
	{
		assert !v.isBranchGraphVertex();

		final SE f = skeletonEdgePool.createRef();
		final SE f1 = skeletonEdgePool.createRef();
		final SE f2 = skeletonEdgePool.createRef();
		final E e = createRef();
		final E fe = createRef();
		final SV w = skeletonVertexPool.createRef();
		final SV ws = skeletonVertexPool.createRef();
		final SV wt = skeletonVertexPool.createRef();
		final V v1 = vertexPool.createRef();
		final V v2 = vertexPool.createRef();

		v.getBranchGraphEdge( f );
		v.outgoingEdges().get( 0, e );
		f.getSourceGraphEdge( fe );
		f.getSource( ws );
		f.getTarget( wt );
		fe.getTarget( v1 );
		wt.getSourceGraphVertex( v2 );

		skeletonEdgePool.delete( f );

		skeletonVertexPool.create( w );
		w.setLinkedVertexIndex( v.getInternalPoolIndex() );

		skeletonEdgePool.addEdge( ws, w, f1 );
		f1.setLinkedEdgeIndex( fe.getInternalPoolIndex() );
		linkBranchEdge( v1, v, f1 );

		skeletonEdgePool.addEdge( w, wt, f2 );
		f2.setLinkedEdgeIndex( e.getInternalPoolIndex() );
		linkBranchEdge( v, v2, f2 );

		vertexPool.releaseRef( v2 );
		vertexPool.releaseRef( v1 );
		skeletonVertexPool.releaseRef( wt );
		skeletonVertexPool.releaseRef( ws );
//			skeletonVertexPool.releaseRef( w );
		releaseRef( fe );
		releaseRef( e );
		skeletonEdgePool.releaseRef( f2 );
		skeletonEdgePool.releaseRef( f1 );
		skeletonEdgePool.releaseRef( f );

		v.setIsBranchGraphVertex( true );
		v.setBranchGraphIndex( w.getInternalPoolIndex() );

		return w;
	}

	/**
	 * Link source graph vertices from {@code begin} (inclusive) to {@code end}
	 * (exclusive) to the {@link BranchEdge}.
	 *
	 * @param begin
	 *            the source graph vertex that starts the branch.
	 * @param end
	 *            the source graph vertex that finishes the branch.
	 * @param branchEdge
	 *            the branchEdge to link the branch to. All source graph
	 *            vertices of the branch will be mapped to this branch edge.
	 */
	private void linkBranchEdge( final V begin, final V end, final BranchEdge branchEdge )
	{
		final E e = graph.edgeRef();
		V v = graph.vertexRef();
		v = assigner.assign( begin, v );
		while ( !v.equals( end ) )
		{
			final int vertexId = idBimap.getVertexId( v );
			vbeMap.put( vertexId, branchEdge );
			vbvMap.remove( vertexId );
			v = v.outgoingEdges().get( 0, e ).getTarget( v );
		}
		graph.releaseRef( e );
		graph.releaseRef( v );
	}

	/*
	 * Graph listener.
	 */

	private class MyGraphListener implements GraphListener< V, E >
	{

		@Override
		public void graphRebuilt()
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void vertexAdded( final V vertex )
		{
			final int id = idBimap.getVertexId( vertex );
			final BranchVertex ref = vertexRef();
			final BranchVertex branchVertex = vertexPool.create( ref );
			branchVertex.setLinkedVertexId( id );
			vbvMap.put( id, branchVertex );
			releaseRef( ref );
		}

		@Override
		public void vertexRemoved( final V vertex )
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void edgeAdded( final E edge )
		{
			final int edgeId = idBimap.getEdgeId( edge );

			final V ref1 = graph.vertexRef();
			final V ref2 = graph.vertexRef();
			final V source = edge.getSource( ref1 );
			final V target = edge.getSource( ref1 );
			final BranchVertex vref1 = vertexRef();
			final BranchVertex vref2 = vertexRef();

			final BranchVertex sourceBranchVertex = vbvMap.get( idBimap.getVertexId( source ), vref1 );
			final BranchVertex targetBranchVertex = vbvMap.get( idBimap.getVertexId( target ), vref2 );

			if ( null != sourceBranchVertex && null != targetBranchVertex )
			{
				final BranchEdge edgeRef = edgeRef();
				final BranchEdge branchEdge = edgePool.addEdge( sourceBranchVertex, targetBranchVertex, edgeRef );
				branchEdge.setLinkedEdgeId( edgeId );
				ebeMap.put( edgeId, branchEdge );

				checkFuse( sourceBranchVertex );
				checkFuse( targetBranchVertex );

				releaseRef( edgeRef );

			}
			else if ( null == sourceBranchVertex && null != targetBranchVertex )
			{
				final SV svs = split( source );

				final int idt = t.getBranchGraphIndex();
				final SV svt = skeletonVertexPool.createRef();
				skeletonVertexPool.getObject( idt, svt );

				SE se = skeletonEdgePool.createRef();
				se = skeletonEdgePool.addEdge( svs, svt, se );
				se.setLinkedEdgeIndex( edge.getInternalPoolIndex() );

				checkFuse( svt );

				skeletonEdgePool.releaseRef( se );
				skeletonVertexPool.releaseRef( svt );
				skeletonVertexPool.releaseRef( svs );
			}
//			else if ( s.isBranchGraphVertex() && !t.isBranchGraphVertex() )
//			{
//				final SV svt = split( t );
//
//				final int ids = s.getBranchGraphIndex();
//				final SV svs = skeletonVertexPool.createRef();
//				skeletonVertexPool.getObject( ids, svs );
//
//				SE se = skeletonEdgePool.createRef();
//				se = skeletonEdgePool.addEdge( svs, svt, se );
//				se.setLinkedEdgeIndex( edge.getInternalPoolIndex() );
//
//				checkFuse( svs );
//
//				skeletonEdgePool.releaseRef( se );
//				skeletonVertexPool.releaseRef( svs );
//				skeletonVertexPool.releaseRef( svt );
//			}
//			else
//			{
//				final SV svs = split( s );
//				final SV svt = split( t );
//
//				SE se = skeletonEdgePool.createRef();
//				se = skeletonEdgePool.addEdge( svs, svt, se );
//				se.setLinkedEdgeIndex( edge.getInternalPoolIndex() );
//
//				checkFuse( svs );
//				checkFuse( svt );
//
//				skeletonEdgePool.releaseRef( se );
//			}

			graph.releaseRef( ref1 );
			graph.releaseRef( ref2 );
			releaseRef( vref1 );
			releaseRef( vref2 );
		}

		@Override
		public void edgeRemoved( final E edge )
		{
			// TODO Auto-generated method stub

		}

	}


}
