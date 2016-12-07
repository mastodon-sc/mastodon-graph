package org.mastodon.graph.branch;

import org.mastodon.collection.RefMaps;
import org.mastodon.collection.RefRefMap;
import org.mastodon.graph.Edge;
import org.mastodon.graph.Graph;
import org.mastodon.graph.GraphListener;
import org.mastodon.graph.ListenableGraph;
import org.mastodon.graph.Vertex;
import org.mastodon.graph.algorithm.Assigner;

class BranchGraphListener< 
	V extends Vertex< E >, 
	E extends Edge< V >,
	BV extends Vertex< BE >, 
	BE extends Edge< BV > > 
implements GraphListener< V, E >
{

	private final ListenableGraph< V, E > graph;

	private final Graph< BV, BE > branchGraph;

	/**
	 * Maps from linked graph vertex to a branch vertex. Only contains mappings
	 * for vertices that are actually linked to a branch vertex. If a key (a
	 * source graph vertex) is not present in this map, it means that it is
	 * linked to a branch edge.
	 */
	final RefRefMap< V, BV > vbvMap;

	/**
	 * Maps from linked graph vertex to branch edge. Only contains mappings for
	 * vertices that are actually linked to a branch edge. If a key (a source
	 * graph vertex) is not present in this map, it means that it is linked to a
	 * branch vertex.
	 */
	final RefRefMap< V, BE > vbeMap;

	/**
	 * Maps from linked graph edge to a branch edge.
	 */
	final RefRefMap< E, BE > ebeMap;

	/**
	 * Maps from branch graph edge to a linked graph edge.
	 */
	final RefRefMap< BE, E > beeMap;

	/**
	 * Maps from branch graph vertex to a linked graph vertex.
	 */
	final RefRefMap< BV, V > bvvMap;

	private final Assigner< V > assigner;

	public BranchGraphListener( final ListenableGraph< V, E > graph, final Graph< BV, BE > branchGraph )
	{
		this.graph = graph;
		this.branchGraph = branchGraph;
		this.vbvMap = RefMaps.createRefRefMap( graph.vertices(), branchGraph.vertices() );
		this.vbeMap = RefMaps.createRefRefMap( graph.vertices(), branchGraph.edges() );
		this.ebeMap = RefMaps.createRefRefMap( graph.edges(), branchGraph.edges() );
		this.bvvMap = RefMaps.createRefRefMap( branchGraph.vertices(), graph.vertices() );
		this.beeMap = RefMaps.createRefRefMap( branchGraph.edges(), graph.edges() );
		final V vertexRef = graph.vertexRef();
		this.assigner = Assigner.getFor( vertexRef );
		graph.releaseRef( vertexRef );

		graph.addGraphListener( this );
		graphRebuilt();
	}

	/**
	 * If branch graph vertex {@code w} has exactly one incoming and one
	 * outgoing edge, remove it and merge the incoming and outgoing edges.
	 *
	 * @param bv
	 *            the branch vertex.
	 */
	private void checkFuse( final BV bv )
	{
		if ( bv.incomingEdges().size() == 1 && bv.outgoingEdges().size() == 1 )
		{
			final BE refBE1 = branchGraph.edgeRef();
			final BE refBE2 = branchGraph.edgeRef();
			final BE refBE3 = branchGraph.edgeRef();
			final BV refBV1 = branchGraph.vertexRef();
			final BV refBV2 = branchGraph.vertexRef();
			final V refLV1 = graph.vertexRef();
			final V refLV2 = graph.vertexRef();
			final V refLV3 = graph.vertexRef();
			final E refLE1 = graph.edgeRef();
			final E refLE2 = graph.edgeRef();
			final E refLE3 = graph.edgeRef();
			final E refLE4 = graph.edgeRef();

			// beIn := branch edge to bv.
			final BE beIn = bv.incomingEdges().get( 0, refBE1 );
			// beOut := branch edge from bv.
			final BE beOut = bv.outgoingEdges().get( 0, refBE2 );

			/*
			 * Only fuse the branch vertex if we do not have a loop. Loop are
			 * edges that have the same vertex as source and target. They
			 * represent a loop in the linked graph.
			 */
			if ( !beIn.equals( beOut ) )
			{

				// bvSource := source branch vertex of beIn.
				final BV bvSource = beIn.getSource( refBV1 );
				// bvTarget := target branch vertex of beOut
				final BV bvTarget = beOut.getTarget( refBV2 );

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
				bvvMap.removeWithRef( bv, refLV3 );
				beeMap.removeWithRef( beIn, refLE3 );
				beeMap.removeWithRef( beOut, refLE4 );
				branchGraph.remove( bv );

				// beNew := new branch edge between bvSource and bvTarget.
				final BE beNew = branchGraph.addEdge( bvSource, bvTarget, refBE3 );

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
			branchGraph.releaseRef( refBV1 );
			branchGraph.releaseRef( refBV2 );
			branchGraph.releaseRef( refBE1 );
			branchGraph.releaseRef( refBE2 );
			branchGraph.releaseRef( refBE3 );
		}
	}

	private BV split( final V v, final BV ref )
	{
		final E refE1 = graph.edgeRef();
		final E refE2 = graph.edgeRef();
		final E refE3 = graph.edgeRef();
		final E refE4 = graph.edgeRef();
		final E refE5 = graph.edgeRef();
		final V refV1 = graph.vertexRef();
		final V refV2 = graph.vertexRef();
		final BE refBE0 = branchGraph.edgeRef();
		final BV refBV1 = branchGraph.vertexRef();
		final BV refBV2 = branchGraph.vertexRef();
		final BE refBE1 = branchGraph.edgeRef();
		final BE refBE2 = branchGraph.edgeRef();
		final BE refBE3 = branchGraph.edgeRef();

		final BE initialBE = vbeMap.get( v, refBE0 );
		final E outgoingEdge = v.outgoingEdges().get( 0, refE1 );

		final E branchStartingEdge = beeMap.get( initialBE, refE2 );

		final BV beSource = initialBE.getSource( refBV1 );
		final BV beTarget = initialBE.getTarget( refBV2 );
		final V branchSecondVertex = branchStartingEdge.getTarget( refV1 );
		final V branchLastVertex = bvvMap.get( beTarget, refV2 );

		beeMap.removeWithRef( initialBE, refE5 );
		branchGraph.remove( initialBE );

		final BV newVertex = branchGraph.addVertex( ref );

		final BE newEdge1 = branchGraph.addEdge( beSource, newVertex, refBE1 );
		beeMap.put( newEdge1, branchStartingEdge, refE3 );
		linkBranchEdge( branchSecondVertex, v, newEdge1 );

		final BE newEdge2 = branchGraph.addEdge( newVertex, beTarget, refBE2 );
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
		branchGraph.releaseRef( refBV1 );
		branchGraph.releaseRef( refBV2 );
		branchGraph.releaseRef( refBE3 );
		branchGraph.releaseRef( refBE0 );
		branchGraph.releaseRef( refBE1 );
		branchGraph.releaseRef( refBE2 );

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
	private void linkBranchEdge( final V begin, final V end, final BE branchEdge )
	{
		final E eRef = graph.edgeRef();
		final V vRef = graph.vertexRef();
		final BV bvRef = branchGraph.vertexRef();
		final BE beRef = branchGraph.edgeRef();
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
		branchGraph.releaseRef( beRef );
		branchGraph.releaseRef( bvRef );
	}

	private void releaseBranchEdgeFor( final E edge )
	{
		final V vRef1 = graph.vertexRef();
		final V source = edge.getSource( vRef1 );
		final BV vertexRef1 = branchGraph.vertexRef();
		final E eRef = graph.edgeRef();

		final BV svs;
		if ( vbvMap.containsKey( source ) )
			svs = vbvMap.get( source, vertexRef1 );
		else
			svs = split( source, vertexRef1 );

		final V vRef2 = graph.vertexRef();
		final V target = edge.getTarget( vRef2 );

		final BV vertexRef2 = branchGraph.vertexRef();
		final BV svt;
		if ( vbvMap.containsKey( target ) )
			svt = vbvMap.get( target, vertexRef2 );
		else
			svt = split( target, vertexRef2 );

		for ( final BE se : svs.outgoingEdges() )
			if ( se.getTarget().equals( svt ) )
			{
				branchGraph.remove( se );
				beeMap.removeWithRef( se, eRef );
			}

		branchGraph.releaseRef( vertexRef1 );
		branchGraph.releaseRef( vertexRef2 );
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
		// TODO clear the branch graph!

		for ( final V v : graph.vertices() )
			vertexAdded( v );

		for ( final E e : graph.edges() )
			edgeAdded( e );
	}

	@Override
	public void vertexAdded( final V vertex )
	{
		final BV bvRef1 = branchGraph.vertexRef();
		final BV bvRef2 = branchGraph.vertexRef();
		final V vRef = graph.vertexRef();

		final BV bv = branchGraph.addVertex( bvRef1 );
		vbvMap.put( vertex, bv, bvRef2 );
		bvvMap.put( bv, vertex, vRef );

		branchGraph.releaseRef( bvRef1 );
		branchGraph.releaseRef( bvRef2 );
		graph.releaseRef( vRef );
	}

	@Override
	public void vertexRemoved( final V vertex )
	{
		final BV bvRef1 = branchGraph.vertexRef();
		final BV bvRef2 = branchGraph.vertexRef();;
		final BE beRef = branchGraph.edgeRef();
		final V vRef = graph.vertexRef();

		final BV w = vbvMap.get( vertex, bvRef1 );
		bvvMap.removeWithRef( w, vRef );
		branchGraph.remove( w );

		vbeMap.removeWithRef( vertex, beRef );
		vbvMap.removeWithRef( vertex, bvRef2 );

		branchGraph.releaseRef( bvRef1 );
		branchGraph.releaseRef( bvRef2 );
		branchGraph.releaseRef( beRef );
		graph.releaseRef( vRef );
	}

	@Override
	public void edgeAdded( final E edge )
	{
		final V ref1 = graph.vertexRef();
		final V ref2 = graph.vertexRef();
		final BV vref1 = branchGraph.vertexRef();
		final BV vref2 = branchGraph.vertexRef();

		final V source = edge.getSource( ref1 );
		final V target = edge.getTarget( ref2 );
		final BV sourceBV = vbvMap.get( source, vref1 );
		final BV targetBV = vbvMap.get( target, vref2 );

		if ( null != sourceBV && null != targetBV )
		{
			final BE beRef1 = branchGraph.edgeRef();
			final BE beRef2 = branchGraph.edgeRef();
			final E eRef = graph.edgeRef();

			final BE be = branchGraph.addEdge( sourceBV, targetBV, beRef1 );
			ebeMap.put( edge, be, beRef2 );
			beeMap.put( be, edge, eRef );

			checkFuse( sourceBV );
			checkFuse( targetBV );

			branchGraph.releaseRef( beRef1 );
			branchGraph.releaseRef( beRef2 );
			graph.releaseRef( eRef );

		}
		else if ( null == sourceBV && null != targetBV )
		{
			final BV bvRef = branchGraph.vertexRef();
			final BE beRef1 = branchGraph.edgeRef();
			final BE beRef2 = branchGraph.edgeRef();
			final E eRef = graph.edgeRef();

			final BV newSourceBV = split( source, bvRef );

			final BE se = branchGraph.addEdge( newSourceBV, targetBV, beRef1 );
			beeMap.put( se, edge, eRef );
			ebeMap.put( edge, se, beRef2 );

			checkFuse( targetBV );

			graph.releaseRef( eRef );
			branchGraph.releaseRef( beRef1 );
			branchGraph.releaseRef( beRef2 );
			branchGraph.releaseRef( bvRef );
		}
		else if ( null != sourceBV && null == targetBV )
		{
			final BV vertexRef = branchGraph.vertexRef();
			final BV newTargetBV = split( target, vertexRef );
			final E edgeRef2 = graph.edgeRef();

			final BE edgeRef = branchGraph.edgeRef();
			final BE se = branchGraph.addEdge( sourceBV, newTargetBV, edgeRef );
			beeMap.put( se, edge, edgeRef2 );

			checkFuse( sourceBV );

			graph.releaseRef( edgeRef2 );
			branchGraph.releaseRef( edgeRef );
			branchGraph.releaseRef( vertexRef );
		}
		else
		{
			final BV vertexRef1 = branchGraph.vertexRef();
			final BV vertexRef2 = branchGraph.vertexRef();

			final BV newSourceBV = split( source, vertexRef1 );
			final BV newTargetBV = split( target, vertexRef2 );

			final BE beRef1 = branchGraph.edgeRef();
			final BE beRef2 = branchGraph.edgeRef();
			final E eRef = graph.edgeRef();

			final BE se = branchGraph.addEdge( newSourceBV, newTargetBV, beRef1 );
			beeMap.put( se, edge, eRef );
			ebeMap.put( edge, se, beRef2 );

			checkFuse( newSourceBV );
			checkFuse( newTargetBV );

			graph.releaseRef( eRef );
			branchGraph.releaseRef( beRef1 );
			branchGraph.releaseRef( beRef2 );
			branchGraph.releaseRef( vertexRef1 );
			branchGraph.releaseRef( vertexRef2 );
		}

		graph.releaseRef( ref1 );
		graph.releaseRef( ref2 );
		branchGraph.releaseRef( vref1 );
		branchGraph.releaseRef( vref2 );
	}

	@Override
	public void edgeRemoved( final E edge )
	{
		releaseBranchEdgeFor( edge );

		final BE beRef = branchGraph.edgeRef();
		ebeMap.removeWithRef( edge, beRef );
		branchGraph.releaseRef( beRef );
	}
}
