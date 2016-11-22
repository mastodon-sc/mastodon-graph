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

public class BranchGraph< V extends Vertex< E >, E extends Edge< V > >
		extends GraphImp< BranchVertexPool, BranchEdgePool, BranchVertex, BranchEdge, ByteMappedElement >
		implements GraphListener< V, E >
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
		super( new BranchEdgePool( 1000, new BranchVertexPool( 1000 ) ) );
		this.graph = graph;
		this.idBimap = idBimap;
		this.vbvMap = new IntRefHashMap<>( vertexPool, -1 );
		this.vbeMap = new IntRefHashMap<>( edgePool, -1 );
		this.ebeMap = new IntRefHashMap<>( edgePool, -1 );
		final V vertexRef = graph.vertexRef();
		this.assigner = Assigner.getFor( vertexRef );
		graph.releaseRef( vertexRef );
		graphRebuilt();
		graph.addGraphListener( this );
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

	private BranchVertex split( final V v, final BranchVertex ref )
	{
		final int vertexId = idBimap.getVertexId( v );
		assert !vbvMap.containsKey( vertexId ) && vbeMap.containsKey( vertexId );

		final BranchEdge f = edgeRef();
		final BranchEdge f1 = edgeRef();
		final BranchEdge f2 = edgeRef();
		E e = graph.edgeRef();
		E fe = graph.edgeRef();
		final BranchVertex ws = vertexRef();
		final BranchVertex wt = vertexRef();
		V v1 = graph.vertexRef();
		V v2 = graph.vertexRef();

		vbeMap.get( vertexId, f );
		e = v.outgoingEdges().get( 0, e );

		fe = idBimap.getEdge( f.getLinkedEdgeId(), fe );
		f.getSource( ws );
		f.getTarget( wt );
		v1 = fe.getTarget( v1 );
		v2 = idBimap.getVertex( wt.getLinkedVertexId(), v2 );

		super.remove( f );
		final BranchVertex w = super.addVertex( ref );
		w.setLinkedVertexId( idBimap.getVertexId( v ) );

		super.addEdge( ws, w, f1 );
		f1.setLinkedEdgeId( idBimap.getEdgeId( fe ) );
		linkBranchEdge( v1, v, f1 );

		super.addEdge( w, wt, f2 );
		f2.setLinkedEdgeId( idBimap.getEdgeId( e ) );
		linkBranchEdge( v, v2, f2 );

		graph.releaseRef( v2 );
		graph.releaseRef( v1 );
		releaseRef( wt );
		releaseRef( ws );
		graph.releaseRef( fe );
		graph.releaseRef( e );
		releaseRef( f2 );
		releaseRef( f1 );
		releaseRef( f );

		vbvMap.put( vertexId, w );
		vbeMap.remove( vertexId );

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

	private void releaseBranchEdgeFor( final E edge )
	{
		final V source = edge.getSource();
		final int sourceId = idBimap.getVertexId( source );

		final BranchVertex vertexRef1 = vertexRef();
		final BranchVertex svs;
		if ( vbvMap.containsKey( sourceId ) )
			svs = vbvMap.get( sourceId, vertexRef1 );
		else
			svs = split( source, vertexRef1 );

		final V target = edge.getTarget();
		final int targetId = idBimap.getVertexId( target );

		final BranchVertex vertexRef2 = vertexRef();
		final BranchVertex svt;
		if ( vbvMap.containsKey( targetId ) )
			svt = vbvMap.get( targetId, vertexRef2 );
		else
			svt = split( target, vertexRef2 );

		for ( final BranchEdge se : svs.outgoingEdges() )
			if ( se.getTarget().equals( svt ) )
				super.remove( se );

		releaseRef( vertexRef1 );
		releaseRef( vertexRef2 );
	}

	/*
	 * Graph listener.
	 */

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
		final BranchVertex branchVertex = super.addVertex( ref );
		branchVertex.setLinkedVertexId( id );
		vbvMap.put( id, branchVertex );
		releaseRef( ref );
	}

	@Override
	public void vertexRemoved( final V vertex )
	{
		final BranchVertex vertexRef = vertexRef();
		final BranchVertex w = vbvMap.get( idBimap.getVertexId( vertex ), vertexRef );
		super.remove( w );
		releaseRef( vertexRef );
	}

	@Override
	public void edgeAdded( final E edge )
	{
		final int edgeId = idBimap.getEdgeId( edge );

		final V ref1 = graph.vertexRef();
		final V ref2 = graph.vertexRef();
		final BranchVertex vref1 = vertexRef();
		final BranchVertex vref2 = vertexRef();

		final V source = edge.getSource( ref1 );
		final V target = edge.getSource( ref2 );
		final BranchVertex sourceBranchVertex = vbvMap.get( idBimap.getVertexId( source ), vref1 );
		final BranchVertex targetBranchVertex = vbvMap.get( idBimap.getVertexId( target ), vref2 );

		if ( null != sourceBranchVertex && null != targetBranchVertex )
		{
			final BranchEdge edgeRef = edgeRef();
			final BranchEdge branchEdge = super.addEdge( sourceBranchVertex, targetBranchVertex, edgeRef );
			branchEdge.setLinkedEdgeId( edgeId );
			ebeMap.put( edgeId, branchEdge );

			checkFuse( sourceBranchVertex );
			checkFuse( targetBranchVertex );

			releaseRef( edgeRef );

		}
		else if ( null == sourceBranchVertex && null != targetBranchVertex )
		{
			final BranchVertex vertexRef = vertexRef();
			final BranchVertex newSourceBranchVertex = split( source, vertexRef );

			final BranchEdge edgeRef = edgeRef();
			final BranchEdge se = super.addEdge( newSourceBranchVertex, targetBranchVertex, edgeRef );
			se.setLinkedEdgeId( edgeId );

			checkFuse( targetBranchVertex );

			releaseRef( edgeRef );
			releaseRef( vertexRef );
		}
		else if ( null != sourceBranchVertex && null == targetBranchVertex )
		{
			final BranchVertex vertexRef = vertexRef();
			final BranchVertex newTargetBranchVertex = split( target, vertexRef );

			final BranchEdge edgeRef = edgeRef();
			final BranchEdge se = super.addEdge( sourceBranchVertex, newTargetBranchVertex, edgeRef );
			se.setLinkedEdgeId( edgeId );

			checkFuse( sourceBranchVertex );

			releaseRef( edgeRef );
			releaseRef( vertexRef );
		}
		else
		{
			final BranchVertex vertexRef1 = vertexRef();
			final BranchVertex vertexRef2 = vertexRef();

			final BranchVertex newSourceBranchVertex = split( source, vertexRef1 );
			final BranchVertex newTargetBranchVertex = split( target, vertexRef2 );

			final BranchEdge edgeRef = edgeRef();

			final BranchEdge se = super.addEdge( newSourceBranchVertex, newTargetBranchVertex, edgeRef );
			se.setLinkedEdgeId( edgeId );

			checkFuse( newSourceBranchVertex );
			checkFuse( newTargetBranchVertex );

			releaseRef( edgeRef );
			releaseRef( vertexRef1 );
			releaseRef( vertexRef2 );
		}

		graph.releaseRef( ref1 );
		graph.releaseRef( ref2 );
		releaseRef( vref1 );
		releaseRef( vref2 );
	}

	@Override
	public void edgeRemoved( final E edge )
	{
		releaseBranchEdgeFor( edge );
	}

	/*
	 * Display. Mainly for debug.
	 */

	@Override
	public String toString()
	{
		final StringBuffer sb = new StringBuffer( "BranchGraph {\n" );
		sb.append( "  vertices = {\n" );

		for ( final BranchVertex bv : vertexPool )
			sb.append( "    " + str( bv ) + "\n" );
		sb.append( "  },\n" );
		sb.append( "  edges = {\n" );

		for ( final BranchEdge be : edgePool )
			sb.append( "    " + be + "\n" );
		sb.append( "  }\n" );
		sb.append( "}" );
		return sb.toString();
	}

	private String str( final BranchVertex bv )
	{
		final V ref = graph.vertexRef();
		final String str = "bv(" + bv.getInternalPoolIndex() + ")->" + idBimap.getVertex( bv.getLinkedVertexId(), ref );
		graph.releaseRef( ref );
		return str;
	};
}
