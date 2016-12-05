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

/**
 * Branch graph: a branch simplification view of a source graph.
 * <p>
 * This class implements a view of a source {@link ListenableGraph} (called here
 * <i>linked graph</i>) that offers a coarser level of details by representing
 * branches of the linked graph as a single vertex and edge in the branch graph.
 * <p>
 * A branch in the linked graph is defined as connected vertices that have
 * exactly 1 incoming edge and 1 outgoing edge. In the branch graph, a branch is
 * represented as follow. If in the linked graph a branch is like this:
 * 
 * <pre>
 * v0 &#8594; v1 &#8594; v2 &#8594; v3
 * </pre>
 * 
 * The its representation in the branch graph is:
 * 
 * <pre>
 *  bv0 &#8594; bv1
 * </pre>
 * 
 * where:
 * <ul>
 * <li><code>bv0</code> links to <code>v0</code>,
 * <li><code>bv1</code> links to <code>v3</code>,
 * <li>all linked vertices in-between linked to the branch edge between
 * <code>bv0</code> and <code>bv1</code>,
 * <li>as well as all the linked edges in the branch.
 * <li>The branch edge between <code>bv0</code> and <code>bv1</code> links to
 * the outgoing edge of <code>v0</code>.
 * </ul>
 * <p>
 * For instance, a graph laid as following:
 * 
 * <pre>
 *                 v3 &#8594; v4 &#8594; v5 
 *              /
 * v0 &#8594; v1 &#8594; v2 
 *              \
 *                 v6 &#8594; v7 &#8594; v8
 * </pre>
 * 
 * will be represented by the following branch graph:
 * 
 * <pre>
 *            bv2
 *          /
 * bv0 &#8594; bv1
 *          \
 *            bv3
 * </pre>
 * 
 * In the example above, <code>v0</code>, <code>v2</code>, <code>v5</code> and
 * <code>v8</code> are <b>branch extremities</b>; they are linked to a unique
 * branch vertex in the branch graph. <code>v1</code>, <code>v3</code>,
 * <code>v4</code>, <code>v6</code> and <code>v7</code> belongs to a branch.
 * They link to a branch edge.
 * 
 * <p>
 * The branch graph is based on a non-simple directed graph. There might be more
 * than one edge between the same source and target branch vertices. This
 * happens for instance when there is a diamond-like shape in the linked graph:
 * 
 * <pre>
 *     v1 &#8594; v2 &#8594; v3 
 *   /             \
 * v0               v7
 *   \             /
 *     v4 &#8594; v5 &#8594; v6
 * </pre>
 * 
 * In the branch graph, this becomes:
 * 
 * <pre>
 *     ____
 *    /    \
 * bv0      bv1
 *    \____/
 * </pre>
 * 
 * The branch graph can also handle ring-link structures in the linked graph. In
 * that case, such a ring;
 * 
 * <pre>
 *     v1 &#8594; v2 &#8594; v3 
 *   /             \
 * v0               v4
 *   \             /
 *     v7 &#8592; v6 &#8592; v5
 * </pre>
 * 
 * is represented by a single vertex having a loop-edge:
 * 
 * <pre>
 *    _
 * bv0 \
 *  \__/
 * </pre>
 * 
 * In such a loop, the vertex linked to <code>bv0</code> and the edge linked to
 * the branch edge are determined by the order in which the vertices and edges
 * are added in the linked graph.
 * <p>
 * The branch graph is defined and tested only for <b>simple, directed
 * graphs</b> as linked graph. Using any other classes of graphs will result in
 * unexpected behavior.
 * <p>
 * The linked graph must be listenable. The branch graph registers as a listener
 * to it, and reflect changes in the linked graph properly. The branch graph
 * itself is read-only. Trying to call its {@link #addVertex()} and other graph
 * modification methods will result in an exception to be thrown.
 * 
 * @author Jean-Yves Tinevez
 * @author Tobias Pietzsch
 *
 * @param <V>
 *            the type of linked vertices.
 * @param <E>
 *            the type of linked edges.
 */
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

	/**
	 * Instantiates a branch graph linked to the specified graph.
	 * This instance registers itself as a listener of the linked graph.
	 * 
	 * @param graph
	 *            the graph to link to.
	 * @param idBimap
	 *            an id bidirectional map of the linked graph.
	 */
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

	/**
	 * Unsupported operation as the branch graph is read-only.
	 * 
	 * @throws UnsupportedOperationException
	 *             when called.
	 */
	@Override
	public BranchEdge addEdge( final BranchVertex source, final BranchVertex target )
	{
		throw new UnsupportedOperationException( "Cannot modify a branch graph." );
	}

	/**
	 * Unsupported operation as the branch graph is read-only.
	 * 
	 * @throws UnsupportedOperationException
	 *             when called.
	 */
	@Override
	public BranchEdge addEdge( final BranchVertex source, final BranchVertex target, final BranchEdge edge )
	{
		throw new UnsupportedOperationException( "Cannot modify a branch graph." );
	}

	/**
	 * Unsupported operation as the branch graph is read-only.
	 * 
	 * @throws UnsupportedOperationException
	 *             when called.
	 */
	@Override
	public BranchVertex addVertex()
	{
		throw new UnsupportedOperationException( "Cannot modify a branch graph." );
	}

	/**
	 * Unsupported operation as the branch graph is read-only.
	 * 
	 * @throws UnsupportedOperationException
	 *             when called.
	 */
	@Override
	public BranchVertex addVertex( final BranchVertex vertex )
	{
		throw new UnsupportedOperationException( "Cannot modify a branch graph." );
	}

	/**
	 * Unsupported operation as the branch graph is read-only.
	 * 
	 * @throws UnsupportedOperationException
	 *             when called.
	 * 
	 */
	@Override
	public void remove( final BranchEdge edge )
	{
		throw new UnsupportedOperationException( "Cannot modify a branch graph." );
	}

	/**
	 * Unsupported operation as the branch graph is read-only.
	 * 
	 * @throws UnsupportedOperationException
	 *             when called.
	 */
	@Override
	public void remove( final BranchVertex vertex )
	{
		throw new UnsupportedOperationException( "Cannot modify a branch graph." );
	}

	/**
	 * Unsupported operation as the branch graph is read-only.
	 * 
	 * @throws UnsupportedOperationException
	 *             when called.
	 */
	@Override
	public void removeAllLinkedEdges( final BranchVertex vertex )
	{
		throw new UnsupportedOperationException( "Cannot modify a branch graph." );
	}

	/*
	 * Query branch graph.
	 */

	/**
	 * Returns the branch vertex linked to the specified vertex if it is a
	 * branch extremity. Returns <code>null</code> if the specified vertex
	 * belongs to inside a branch.
	 * 
	 * @param vertex
	 *            the linked vertex.
	 * @param ref
	 *            a reference object to {@link BranchVertex} used for retrieval.
	 * @return a branch vertex or <code>null</code>.
	 */
	public BranchVertex getBranchVertex( final V vertex, final BranchVertex ref )
	{
		return vbvMap.get( idBimap.getVertexId( vertex ), ref );
	}

	/**
	 * Returns the branch vertex linked to the specified edge if it belongs to a
	 * branch. Returns <code>null</code> if the specified vertex is a branch
	 * extremity.
	 * 
	 * @param vertex
	 *            the linked vertex.
	 * @param ref
	 *            a reference object to {@link BranchEdge} used for retrieval.
	 * @return a branch edge or <code>null</code>.
	 */
	public BranchEdge getBranchEdge( final V vertex, final BranchEdge ref )
	{
		return vbeMap.get( idBimap.getVertexId( vertex ), ref );
	}

	/**
	 * Returns the branch edge linked to the specified edge.
	 * 
	 * @param edge
	 *            the linked edge.
	 * @param ref
	 *            a reference object to {@link BranchEdge} used for retrieval.
	 * @return a branch edge.
	 */
	public BranchEdge getBranchEdge( final E edge, final BranchEdge ref )
	{
		return ebeMap.get( idBimap.getEdgeId( edge ), ref );
	}

	/**
	 * Returns the vertex linked to the specified branch vertex. The linked
	 * vertex is a branch extremity.
	 * 
	 * @param bv
	 *            the branch vertex.
	 * @param ref
	 *            a reference to a linked graph vertex used for retrieval.
	 *            Depending on concrete implementation of the linked graph, this
	 *            object can be cleared, ignored or re-used.
	 * @return the linked vertex.
	 */
	public V getLinkedVertex( final BranchVertex bv, final V ref )
	{
		return idBimap.getVertex( bv.getLinkedVertexId(), ref );
	}

	/**
	 * Returns the edge linked to the specified branch edge. The linked edge is
	 * the single outgoing edge of the branch extremity.
	 * 
	 * @param be
	 *            the branch edge.
	 * @param ref
	 *            a reference to a linked graph edge used for retrieval.
	 *            Depending on concrete implementation of the linked graph, this
	 *            object can be cleared, ignored or re-used.
	 * @return the linked edge.
	 */
	public E getLinkedEdge( final BranchEdge be, final E ref )
	{
		return idBimap.getEdge( be.getLinkedEdgeId(), ref );
	}

	/*
	 * Private methods (do the work).
	 */

	/**
	 * If branch graph vertex {@code w} has exactly one incoming and one
	 * outgoing edge, remove it and merge the incoming and outgoing edges.
	 *
	 * @param bv
	 *            the branch vertex.
	 */
	private void checkFuse( final BranchVertex bv )
	{
		if ( bv.incomingEdges().size() == 1 && bv.outgoingEdges().size() == 1 )
		{
			// Careful for syntax: we know that the BranchGraph pools are actual
			// pool objects.
			final BranchEdge refBE1 = edgeRef();
			final BranchEdge refBE2 = edgeRef();
			final BranchEdge refBE3 = edgeRef();
			final BranchVertex refBV1 = vertexRef();
			final BranchVertex refBV2 = vertexRef();
			// But the source graph might be an object graph.
			final V refLV1 = graph.vertexRef();
			final V refLV2 = graph.vertexRef();
			final E refLE = graph.edgeRef();

			// beIn := branch edge to bv.
			final BranchEdge beIn = bv.incomingEdges().get( 0, refBE1 );
			// beOut := branch edge from bv.
			final BranchEdge beOut = bv.outgoingEdges().get( 0, refBE2 );

			/*
			 * Only fuse the branch vertex if we do not have a loop. Loop are
			 * edges that have the same vertex as source and target. They
			 * represent a loop in the linked graph.
			 */
			if ( !beIn.equals( beOut ) )
			{

				// bvSource := source branch vertex of beIn.
				final BranchVertex bvSource = beIn.getSource( refBV1 );
				// bvTarget := target branch vertex of beOut
				final BranchVertex bvTarget = beOut.getTarget( refBV2 );

				// le := edge linked to beIn.
				final E le = idBimap.getEdge( beIn.getLinkedEdgeId(), refLE );
				/*
				 * lv1 := target vertex of le ==> first source vertex on new
				 * branch edge.
				 */
				final V lv1 = le.getTarget( refLV1 );

				/*
				 * lv2 := source vertex corresponding to bvTarget ==> terminates
				 * new branch edge.
				 */
				final V lv2 = idBimap.getVertex( bvTarget.getLinkedVertexId(), refLV2 );

				// Remember source edge linked from beIn.
				final int beInLinkedEdgeIndex = beIn.getLinkedEdgeId();

				// Remove bv, beIn, beOut from branch graph.
				super.remove( bv );

				// beNew := new branch edge between bvSource and bvTarget.
				final BranchEdge beNew = super.addEdge( bvSource, bvTarget, refBE3 );

				// reference f3 from every source graph vertex on the path
				linkBranchEdge( lv1, lv2, beNew );

				// link from f3 to source edge that was previously linked from
				// f1
				beNew.setLinkedEdgeId( beInLinkedEdgeIndex );
			}

			graph.releaseRef( refLV1 );
			graph.releaseRef( refLV2 );
			graph.releaseRef( refLE );
			releaseRef( refBV1 );
			releaseRef( refBV2 );
			releaseRef( refBE1 );
			releaseRef( refBE2 );
			releaseRef( refBE3 );
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

		super.addEdge( ws, w, f1 );
		f1.setLinkedEdgeId( idBimap.getEdgeId( fe ) );
		linkBranchEdge( v1, v, f1 );

		super.addEdge( w, wt, f2 );
		f2.setLinkedEdgeId( idBimap.getEdgeId( e ) );
		linkBranchEdge( v, v2, f2 );

		w.setLinkedVertexId( vertexId );
		vbvMap.put( vertexId, w );
		vbeMap.remove( vertexId );

		graph.releaseRef( v2 );
		graph.releaseRef( v1 );
		releaseRef( wt );
		releaseRef( ws );
		graph.releaseRef( fe );
		graph.releaseRef( e );
		releaseRef( f2 );
		releaseRef( f1 );
		releaseRef( f );

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
			vbvMap.remove( vertexId );
			vbeMap.put( vertexId, branchEdge );
			v = v.outgoingEdges().get( 0, e ).getTarget( v );

			ebeMap.put( idBimap.getEdgeId( e ), branchEdge );
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
		for ( final V v : graph.vertices() )
			vertexAdded( v );

		for ( final E e : graph.edges() )
			edgeAdded( e );
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
		final V target = edge.getTarget( ref2 );
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

		for ( final BranchVertex bv : vertices() )
			sb.append( "    " + str( bv ) + "\n" );
		sb.append( "  },\n" );
		sb.append( "  edges = {\n" );

		for ( final BranchEdge be : edges() )
			sb.append( "    " + str( be ) + "\n" );
		sb.append( "  }\n" );
		sb.append( "}" );
		return sb.toString();
	}

	private String str( final BranchEdge be )
	{
		final BranchVertex v1 = vertexRef();
		final BranchVertex v2 = vertexRef();
		be.getSource( v1 );
		be.getTarget( v2 );
		final E e = graph.edgeRef();
		final String str = "be(" + v1.getInternalPoolIndex() + " -> " + v2.getInternalPoolIndex() +
				")->" + idBimap.getEdge( be.getLinkedEdgeId(), e );
		releaseRef( v1 );
		releaseRef( v2 );
		graph.releaseRef( e );
		return str;
	}

	private String str( final BranchVertex bv )
	{
		final V ref = graph.vertexRef();
		final String str = "bv(" + bv.getInternalPoolIndex() + ")->" + idBimap.getVertex( bv.getLinkedVertexId(), ref );
		graph.releaseRef( ref );
		return str;
	};
}
