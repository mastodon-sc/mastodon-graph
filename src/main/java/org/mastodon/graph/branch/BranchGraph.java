package org.mastodon.graph.branch;

import org.mastodon.collection.RefMaps;
import org.mastodon.collection.RefRefMap;
import org.mastodon.graph.Edge;
import org.mastodon.graph.GraphListener;
import org.mastodon.graph.ListenableGraph;
import org.mastodon.graph.Vertex;
import org.mastodon.graph.algorithm.Assigner;
import org.mastodon.graph.ref.ListenableGraphImp;
import org.mastodon.pool.ByteMappedElement;
import org.mastodon.spatial.HasTimepoint;

/**
 * Branch graph: a branch simplification view of a source graph.
 * <p>
 * This specific instance supports branch edges and vertices with features.
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
public class BranchGraph< V extends Vertex< E > & HasTimepoint, E extends Edge< V > >
		extends ListenableGraphImp< 
			BranchVertexPool, 
			BranchEdgePool, 
			BranchVertex, 
			BranchEdge, 
			ByteMappedElement >
		implements GraphListener< V, E >
{

	/**
	 * Maps from linked graph vertex to a branch vertex. Only contains mappings
	 * for vertices that are actually linked to a branch vertex. If a key (a
	 * source graph vertex) is not present in this map, it means that it is
	 * linked to a branch edge.
	 */
	final RefRefMap< V, BranchVertex > vbvMap;

	/**
	 * Maps from linked graph vertex to branch edge. Only contains mappings for
	 * vertices that are actually linked to a branch edge. If a key (a source
	 * graph vertex) is not present in this map, it means that it is linked to a
	 * branch vertex.
	 */
	final RefRefMap< V, BranchEdge > vbeMap;

	/**
	 * Maps from linked graph edge to a branch edge.
	 */
	final RefRefMap< E, BranchEdge > ebeMap;

	/**
	 * Maps from branch graph edge to a linked graph edge.
	 */
	final RefRefMap< BranchEdge, E > beeMap;

	/**
	 * Maps from branch graph vertex to a linked graph vertex.
	 */
	final RefRefMap< BranchVertex, V > bvvMap;

	private final Assigner< V > assigner;

	private final ListenableGraph< V, E > graph;

	/**
	 * Instantiates a branch graph linked to the specified graph.
	 * This instance registers itself as a listener of the linked graph.
	 * 
	 * @param graph
	 *            the graph to link to.
	 * @param idBimap
	 *            an id bidirectional map of the linked graph.
	 */
	public BranchGraph( final ListenableGraph< V, E > graph )
	{
		super( new BranchEdgePool( 1000, new BranchVertexPool( 1000 ) ) );
		this.graph = graph;
		this.vbvMap = RefMaps.createRefRefMap( graph.vertices(), vertices() );
		this.vbeMap = RefMaps.createRefRefMap( graph.vertices(), edges() );
		this.ebeMap = RefMaps.createRefRefMap( graph.edges(), edges() );
		this.bvvMap = RefMaps.createRefRefMap( vertices(), graph.vertices() );
		this.beeMap = RefMaps.createRefRefMap( edges(), graph.edges() );
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
	 *            a reference object to {@link BranchVertex} used
	 *            for retrieval.
	 * @return a branch vertex or <code>null</code>.
	 */
	public BranchVertex getBranchVertex( final V vertex, final BranchVertex ref )
	{
		return vbvMap.get( vertex, ref );
	}

	/**
	 * Returns the branch vertex linked to the specified edge if it belongs to a
	 * branch. Returns <code>null</code> if the specified vertex is a branch
	 * extremity.
	 * 
	 * @param vertex
	 *            the linked vertex.
	 * @param ref
	 *            a reference object to {@link BranchEdge} used for
	 *            retrieval.
	 * @return a branch edge or <code>null</code>.
	 */
	public BranchEdge getBranchEdge( final V vertex, final BranchEdge ref )
	{
		return vbeMap.get( vertex, ref );
	}

	/**
	 * Returns the branch edge linked to the specified edge.
	 * 
	 * @param edge
	 *            the linked edge.
	 * @param ref
	 *            a reference object to {@link BranchEdge} used for
	 *            retrieval.
	 * @return a branch edge.
	 */
	public BranchEdge getBranchEdge( final E edge, final BranchEdge ref )
	{
		return ebeMap.get( edge, ref );
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
		return bvvMap.get( bv, ref );
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
		return beeMap.get( be, ref );
	}

	/*
	 * Graph listener
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
			final BranchEdge refBE1 = edgeRef();
			final BranchEdge refBE2 = edgeRef();
			final BranchEdge refBE3 = edgeRef();
			final BranchVertex refBV1 = vertexRef();
			final BranchVertex refBV2 = vertexRef();
			final V refLV1 = graph.vertexRef();
			final V refLV2 = graph.vertexRef();
			final V refLV3 = graph.vertexRef();
			final E refLE1 = graph.edgeRef();
			final E refLE2 = graph.edgeRef();
			final E refLE3 = graph.edgeRef();
			final E refLE4 = graph.edgeRef();

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
				final E le = beeMap.get( beIn, refLE1 );
				/*
				 * lv1 := target vertex of le ==> first source vertex on new
				 * branch edge.
				 */
				final V lv1 = le.getTarget( refLV1 );

				/*
				 * lv2 := source vertex corresponding to bvTarget ==> terminates
				 * new branch edge.
				 */
				final V lv2 = bvvMap.get( bvTarget, refLV2 );

				// Remove bv, beIn, beOut from branch graph.
				super.remove( bv );
				bvvMap.removeWithRef( bv, refLV3 );
				beeMap.removeWithRef( beIn, refLE3 );
				beeMap.removeWithRef( beOut, refLE4 );

				// beNew := new branch edge between bvSource and bvTarget.
				final BranchEdge beNew =
						super.addEdge( bvSource, bvTarget, refBE3 ).init();

				// reference f3 from every source graph vertex on the path
				linkBranchEdge( lv1, lv2, beNew );

				// link from f3 to source edge that was previously linked from
				// f1
				beeMap.put( beNew, le, refLE2 );
			}

			graph.releaseRef( refLV1 );
			graph.releaseRef( refLV2 );
			graph.releaseRef( refLV3 );
			graph.releaseRef( refLE1 );
			graph.releaseRef( refLE2 );
			graph.releaseRef( refLE3 );
			graph.releaseRef( refLE4 );
			releaseRef( refBV1 );
			releaseRef( refBV2 );
			releaseRef( refBE1 );
			releaseRef( refBE2 );
			releaseRef( refBE3 );
		}
	}

	private BranchVertex split( final V v, final BranchVertex ref )
	{
		final E refE1 = graph.edgeRef();
		final E refE2 = graph.edgeRef();
		final E refE3 = graph.edgeRef();
		final E refE4 = graph.edgeRef();
		final E refE5 = graph.edgeRef();
		final V refV1 = graph.vertexRef();
		final V refV2 = graph.vertexRef();
		final BranchEdge refBE0 = edgeRef();
		final BranchVertex refBV1 = vertexRef();
		final BranchVertex refBV2 = vertexRef();
		final BranchEdge refBE1 = edgeRef();
		final BranchEdge refBE2 = edgeRef();
		final BranchEdge refBE3 = edgeRef();

		final BranchEdge initialBE = vbeMap.get( v, refBE0 );
		final E outgoingEdge = v.outgoingEdges().get( 0, refE1 );

		final E branchStartingEdge = beeMap.get( initialBE, refE2 );

		final BranchVertex beSource = initialBE.getSource( refBV1 );
		final BranchVertex beTarget = initialBE.getTarget( refBV2 );
		final V branchSecondVertex = branchStartingEdge.getTarget( refV1 );
		final V branchLastVertex = bvvMap.get( beTarget, refV2 );

		beeMap.removeWithRef( initialBE, refE5 );
		super.remove( initialBE );

		final BranchVertex newVertex = super.addVertex( ref ).init( v.getTimepoint() );

		final BranchEdge newEdge1 = super.addEdge( beSource, newVertex, refBE1 ).init();
		beeMap.put( newEdge1, branchStartingEdge, refE3 );
		linkBranchEdge( branchSecondVertex, v, newEdge1 );

		final BranchEdge newEdge2 = super.addEdge( newVertex, beTarget, refBE2 ).init();
		beeMap.put( newEdge2, outgoingEdge, refE4 );
		linkBranchEdge( v, branchLastVertex, newEdge2 );

		bvvMap.put( newVertex, v );
		vbvMap.put( v, newVertex );
		vbeMap.removeWithRef( v, refBE3 );

		graph.releaseRef( refE1 );
		graph.releaseRef( refE2 );
		graph.releaseRef( refE3 );
		graph.releaseRef( refE4 );
		graph.releaseRef( refE5 );
		graph.releaseRef( refV1 );
		graph.releaseRef( refV2 );
		releaseRef( refBV1 );
		releaseRef( refBV2 );
		releaseRef( refBE3 );
		releaseRef( refBE0 );
		releaseRef( refBE1 );
		releaseRef( refBE2 );

		return newVertex;
	}

	/**
	 * Link source graph vertices from {@code begin} (inclusive) to {@code end}
	 * (exclusive) to the branch edge.
	 *
	 * @param begin
	 *            the source graph vertex that starts the branch.
	 * @param end
	 *            the source graph vertex that finishes the branch.
	 * @param branchEdge
	 *            the branch edge to link the branch to. All source graph
	 *            vertices of the branch will be mapped to this branch edge.
	 */
	private void linkBranchEdge( final V begin, final V end, final BranchEdge branchEdge )
	{
		final E eRef = graph.edgeRef();
		final V vRef = graph.vertexRef();
		final BranchVertex bvRef = vertexRef();
		final BranchEdge beRef = edgeRef();
		V v = assigner.assign( begin, vRef );

		while ( !v.equals( end ) )
		{
			vbvMap.removeWithRef( v, bvRef );
			vbeMap.put( v, branchEdge, beRef );
			final E e = v.outgoingEdges().get( 0, eRef );
			v = e.getTarget( vRef );
			ebeMap.put( e, branchEdge, beRef );
		}

		graph.releaseRef( eRef );
		graph.releaseRef( vRef );
		releaseRef( beRef );
		releaseRef( bvRef );
	}

	private void releaseBranchEdgeFor( final E edge )
	{
		final V vRef1 = graph.vertexRef();
		final V source = edge.getSource( vRef1 );
		final BranchVertex vertexRef1 = vertexRef();
		final E eRef = graph.edgeRef();

		final BranchVertex svs;
		if ( vbvMap.containsKey( source ) )
			svs = vbvMap.get( source, vertexRef1 );
		else
			svs = split( source, vertexRef1 );

		final V vRef2 = graph.vertexRef();
		final V target = edge.getTarget( vRef2 );

		final BranchVertex vertexRef2 = vertexRef();
		final BranchVertex svt;
		if ( vbvMap.containsKey( target ) )
			svt = vbvMap.get( target, vertexRef2 );
		else
			svt = split( target, vertexRef2 );

		for ( final BranchEdge se : svs.outgoingEdges() )
			if ( se.getTarget().equals( svt ) )
			{
				beeMap.removeWithRef( se, eRef );
				super.remove( se );
			}

		releaseRef( vertexRef1 );
		releaseRef( vertexRef2 );
		graph.releaseRef( eRef );
		graph.releaseRef( vRef1 );
		graph.releaseRef( vRef2 );
	}

	/*
	 * Graph listener.
	 */

	@Override
	public void graphRebuilt()
	{
		clear();

		for ( final V v : graph.vertices() )
			vertexAdded( v );

		for ( final E e : graph.edges() )
			edgeAdded( e );
	}

	@Override
	public void vertexAdded( final V vertex )
	{
		final BranchVertex bvRef1 = vertexRef();
		final BranchVertex bvRef2 = vertexRef();
		final V vRef = graph.vertexRef();

		final BranchVertex bv = super.addVertex( bvRef1 ).init( vertex.getTimepoint() );
		vbvMap.put( vertex, bv, bvRef2 );
		bvvMap.put( bv, vertex, vRef );

		releaseRef( bvRef1 );
		releaseRef( bvRef2 );
		graph.releaseRef( vRef );
	}

	@Override
	public void vertexRemoved( final V vertex )
	{
		final BranchVertex bvRef1 = vertexRef();
		final BranchVertex bvRef2 = vertexRef();;
		final BranchEdge beRef = edgeRef();
		final V vRef = graph.vertexRef();

		final BranchVertex w = vbvMap.get( vertex, bvRef1 );
		bvvMap.removeWithRef( w, vRef );
		super.remove( w );

		vbeMap.removeWithRef( vertex, beRef );
		vbvMap.removeWithRef( vertex, bvRef2 );

		releaseRef( bvRef1 );
		releaseRef( bvRef2 );
		releaseRef( beRef );
		graph.releaseRef( vRef );
	}

	@Override
	public void edgeAdded( final E edge )
	{
		final V ref1 = graph.vertexRef();
		final V ref2 = graph.vertexRef();
		final BranchVertex vref1 = vertexRef();
		final BranchVertex vref2 = vertexRef();

		final V source = edge.getSource( ref1 );
		final V target = edge.getTarget( ref2 );
		final BranchVertex sourceBV = vbvMap.get( source, vref1 );
		final BranchVertex targetBV = vbvMap.get( target, vref2 );

		if ( null != sourceBV && null != targetBV )
		{
			final BranchEdge beRef1 = edgeRef();
			final BranchEdge beRef2 = edgeRef();
			final E eRef = graph.edgeRef();

			final BranchEdge be =
					super.addEdge( sourceBV, targetBV, beRef1 ).init();
			ebeMap.put( edge, be, beRef2 );
			beeMap.put( be, edge, eRef );

			checkFuse( sourceBV );
			checkFuse( targetBV );

			releaseRef( beRef1 );
			releaseRef( beRef2 );
			graph.releaseRef( eRef );

		}
		else if ( null == sourceBV && null != targetBV )
		{
			final BranchVertex bvRef = vertexRef();
			final BranchEdge beRef1 = edgeRef();
			final BranchEdge beRef2 = edgeRef();
			final E eRef = graph.edgeRef();

			final BranchVertex newSourceBV = split( source, bvRef );

			final BranchEdge se =
					super.addEdge( newSourceBV, targetBV, beRef1 ).init();
			beeMap.put( se, edge, eRef );
			ebeMap.put( edge, se, beRef2 );

			checkFuse( targetBV );

			graph.releaseRef( eRef );
			releaseRef( beRef1 );
			releaseRef( beRef2 );
			releaseRef( bvRef );
		}
		else if ( null != sourceBV && null == targetBV )
		{
			final BranchVertex vertexRef = vertexRef();
			final BranchVertex newTargetBV = split( target, vertexRef );
			final E edgeRef2 = graph.edgeRef();

			final BranchEdge edgeRef = edgeRef();
			final BranchEdge se =
					super.addEdge( sourceBV, newTargetBV, edgeRef ).init();
			beeMap.put( se, edge, edgeRef2 );

			checkFuse( sourceBV );

			graph.releaseRef( edgeRef2 );
			releaseRef( edgeRef );
			releaseRef( vertexRef );
		}
		else
		{
			final BranchVertex vertexRef1 = vertexRef();
			final BranchVertex vertexRef2 = vertexRef();

			final BranchVertex newSourceBV = split( source, vertexRef1 );
			final BranchVertex newTargetBV = split( target, vertexRef2 );

			final BranchEdge beRef1 = edgeRef();
			final BranchEdge beRef2 = edgeRef();
			final E eRef = graph.edgeRef();

			final BranchEdge se = 
					super.addEdge( newSourceBV, newTargetBV, beRef1 ).init();
			beeMap.put( se, edge, eRef );
			ebeMap.put( edge, se, beRef2 );

			checkFuse( newSourceBV );
			checkFuse( newTargetBV );

			graph.releaseRef( eRef );
			releaseRef( beRef1 );
			releaseRef( beRef2 );
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

		final BranchEdge beRef = edgeRef();
		ebeMap.removeWithRef( edge, beRef );
		releaseRef( beRef );
	}

	/*
	 * Display. Mainly for debug.
	 */

	@Override
	public String toString()
	{
		final V vref = graph.vertexRef();
		final E eref = graph.edgeRef();

		final StringBuffer sb = new StringBuffer( "BranchGraph {\n" );
		sb.append( "  vertices = {\n" );

		for ( final BranchVertex bv : vertices() )
			sb.append( "    " + str( bv, vref ) + "\n" );
		sb.append( "  },\n" );
		sb.append( "  edges = {\n" );

		for ( final BranchEdge be : edges() )
			sb.append( "    " + str( be, eref ) + "\n" );
		sb.append( "  }\n" );
		sb.append( "}" );

		graph.releaseRef( vref );
		graph.releaseRef( eref );

		return sb.toString();
	}

	private String str( final BranchEdge be, final E eref )
	{
		return "be(" + be.getInternalPoolIndex() + ")->" + getLinkedEdge( be, eref );
	}

	private String str( final BranchVertex bv, final V vref )
	{
		return "bv(" + bv.getInternalPoolIndex() + ")->" + getLinkedVertex( bv, vref );
	}
}
