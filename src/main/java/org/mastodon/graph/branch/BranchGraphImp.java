/*-
 * #%L
 * Mastodon Graphs
 * %%
 * Copyright (C) 2015 - 2021 Tobias Pietzsch, Jean-Yves Tinevez
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

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.mastodon.collection.RefCollections;
import org.mastodon.collection.RefList;
import org.mastodon.collection.RefMaps;
import org.mastodon.collection.RefRefMap;
import org.mastodon.collection.RefSet;
import org.mastodon.collection.RefStack;
import org.mastodon.graph.Edge;
import org.mastodon.graph.Edges;
import org.mastodon.graph.GraphIdBimap;
import org.mastodon.graph.GraphListener;
import org.mastodon.graph.ListenableGraph;
import org.mastodon.graph.Vertex;
import org.mastodon.graph.algorithm.Assigner;
import org.mastodon.graph.algorithm.RootFinder;
import org.mastodon.graph.algorithm.ShortestPath;
import org.mastodon.graph.algorithm.traversal.DepthFirstIterator;
import org.mastodon.graph.algorithm.traversal.GraphSearch.SearchDirection;
import org.mastodon.graph.ref.AbstractListenableEdge;
import org.mastodon.graph.ref.AbstractListenableEdgePool;
import org.mastodon.graph.ref.AbstractListenableVertex;
import org.mastodon.graph.ref.AbstractListenableVertexPool;
import org.mastodon.graph.ref.ListenableGraphImp;
import org.mastodon.pool.MappedElement;

/**
 * A branch graph implementation for {@link ListenableGraph}s.
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
 * @param <BV>
 *            the type of the branch vertices.
 * @param <BE>
 *            the type of the branch edges.
 * @param <BVP>
 *            the type of the branch vertex pool.
 * @param <BEP>
 *            the type of the branch edge pool.
 * @param <T>
 *            the type of {@link MappedElement} used for the vertex and edge
 *            pool.
 */
public abstract class BranchGraphImp< 
	V extends Vertex< E >, 
	E extends Edge< V >, 
	BV extends AbstractListenableVertex< BV, BE, BVP, T >, 
	BE extends AbstractListenableEdge< BE, BV, BEP, T >, 
	BVP extends AbstractListenableVertexPool< BV, BE, T >, 
	BEP extends AbstractListenableEdgePool< BE, BV, T >, 
	T extends MappedElement >
		extends ListenableGraphImp< BVP, BEP, BV, BE, T >
		implements GraphListener< V, E >, BranchGraph< BV, BE, V, E >
{

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

	private final ListenableGraph< V, E > graph;

	private final GraphIdBimap< BV, BE > idmap;

	private final ShortestPath< V, E > shortestPath;

	/**
	 * Instantiates a branch graph linked to the specified graph. This instance
	 * registers itself as a listener of the linked graph.
	 *
	 * @param graph
	 *            the graph to link to.
	 * @param branchEdgePool
	 *            the branch edge pool used for graph creation.
	 */
	public BranchGraphImp(
			final ListenableGraph< V, E > graph,
			final BEP branchEdgePool )
	{
		super( branchEdgePool );
		this.graph = graph;
		this.idmap = new GraphIdBimap<>( vertexPool, edgePool );
		this.vbvMap = RefMaps.createRefRefMap( graph.vertices(), vertices() );
		this.vbeMap = RefMaps.createRefRefMap( graph.vertices(), edges() );
		this.ebeMap = RefMaps.createRefRefMap( graph.edges(), edges() );
		this.bvvMap = RefMaps.createRefRefMap( vertices(), graph.vertices() );
		this.beeMap = RefMaps.createRefRefMap( edges(), graph.edges() );
		final V vertexRef = graph.vertexRef();
		this.assigner = Assigner.getFor( vertexRef );
		this.shortestPath = new ShortestPath<>( graph, SearchDirection.DIRECTED );

		graph.releaseRef( vertexRef );
		graphRebuilt();
	}

	@Override
	public GraphIdBimap< BV, BE > getGraphIdBimap()
	{
		return idmap;
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
	public BE addEdge( final BV source, final BV target )
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
	public BE addEdge( final BV source, final BV target, final BE ref )
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
	public BE insertEdge( final BV source, final int sourceOutIndex, final BV target, final int targetInIndex )
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
	public BE insertEdge( final BV source, final int sourceOutIndex, final BV target, final int targetInIndex, final BE ref )
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
	public BV addVertex()
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
	public BV addVertex( final BV ref )
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
	public void remove( final BE edge )
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
	public void remove( final BV vertex )
	{
		throw new UnsupportedOperationException( "Cannot modify a branch graph." );
	}

	/*
	 * Query branch graph.
	 */

	@Override
	public BV getBranchVertex( final V vertex, final BV ref )
	{
		return vbvMap.get( vertex, ref );
	}

	@Override
	public BE getBranchEdge( final V vertex, final BE ref )
	{
		return vbeMap.get( vertex, ref );
	}

	@Override
	public BE getBranchEdge( final E edge, final BE ref )
	{
		return ebeMap.get( edge, ref );
	}

	@Override
	public V getLinkedVertex( final BV bv, final V ref )
	{
		return bvvMap.get( bv, ref );
	}

	@Override
	public E getLinkedEdge( final BE be, final E ref )
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
	private void checkFuse( final BV bv )
	{
		if ( bv.incomingEdges().size() == 1 && bv.outgoingEdges().size() == 1 )
		{
			final BE refBE1 = edgeRef();
			final BE refBE2 = edgeRef();
			final BE refBE3 = edgeRef();
			final BE refBE4 = edgeRef();
			final BE refBE5 = edgeRef();
			final BV refBV1 = vertexRef();
			final BV refBV2 = vertexRef();
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
				/*
				 * We will remove bv from the graph. So we also need to be
				 * cautious and unmap the links TO its edges, as well as the
				 * mapping FROM it.
				 */
				beeMap.removeWithRef( beIn, refLE3 );
				beeMap.removeWithRef( beOut, refLE4 );
				bvvMap.removeWithRef( bv, refLV3 );
				beeMap.removeWithRef( beIn, refLE3 );
				beeMap.removeWithRef( beOut, refLE4 );
				super.remove( bv );

				// beNew := new branch edge between bvSource and bvTarget.
				final BE beNew = init( super.addEdge( bvSource, bvTarget, refBE3 ), le );

				// reference f3 from every source graph vertex on the path
				linkBranchEdge( lv1, lv2, beNew );

				// link from f3 to source edge that was previously linked from f1
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
			releaseRef( refBE4 );
			releaseRef( refBE5 );
		}
	}

	/**
	 * Creates a new branch vertex linked to the specified vertex, and inserts
	 * it in the branch graph.
	 * 
	 * @param v
	 *            the vertex.
	 * @param ref
	 *            a branch vertex ref used to return the new branch vertex.
	 * @return the new branch vertex.
	 */
	private BV split( final V v, final BV ref )
	{
		final E refE2 = graph.edgeRef();
		final E refE3 = graph.edgeRef();
		final E refE4 = graph.edgeRef();
		final E refE5 = graph.edgeRef();
		final V refV1 = graph.vertexRef();
		final V refV2 = graph.vertexRef();
		final BE refBE0 = edgeRef();
		final BV refBV1 = vertexRef();
		final BV refBV2 = vertexRef();
		final BE refBE1 = edgeRef();
		final BE refBE2 = edgeRef();
		final BE refBE3 = edgeRef();

		// Initially, the specified vertex (v) was mapped to this branch edge:
		final BE initialBE = vbeMap.get( v, refBE0 );
		// (It was mapped to a branch *edge* because it was part of a branch.
		// Hence there were no branch vertex mapped to it, and this method
		// will create it for it.)

		// The initial branch started with this edge.
		final E branchStartingEdge = beeMap.get( initialBE, refE2 );

		// The source branch vertex of this branch:
		final BV beSource = initialBE.getSource( refBV1 );
		// The target branch vertex of this branch: (The new branch vertex will
		// be inserted between these 2.)
		final BV beTarget = initialBE.getTarget( refBV2 );

		// The last vertex of the branch.
		final V branchLastVertex = bvvMap.get( beTarget, refV2 );

		// We now need to find the edge going out of v, but that originates
		// backward from the branch last vertex (branchLastVertex). We have to
		// do that in case v has more that one outgoing edge. The true outgoing
		// edge is the one that is linked to the initial branch edge (that we
		// will remove later).
		E outgoingEdge = null;
		for ( final E e : v.outgoingEdges() )
		{
			final BE be = ebeMap.get( e );
			if ( be != null && be.equals( initialBE ) )
			{
				outgoingEdge = e;
				break;
			}
		}

		// Unmap the initial branch edge.
		beeMap.removeWithRef( initialBE, refE5 );
		// Remove the initial branch edge.
		super.remove( initialBE );
		// Now beSource and beTarget are dangling alone.

		/*
		 * The splitting point.
		 */

		// Create a new branch vertex, linked to v.
		final BV newVertex = init( super.addVertex( ref ), v );

		/*
		 * Make branch edge before the splitting point.
		 */

		// Link the branch source to this new branch vertex. And map it to the
		// branch starting edge.
		final BE newEdge1 = init( super.addEdge( beSource, newVertex, refBE1 ), branchStartingEdge );
		beeMap.put( newEdge1, branchStartingEdge, refE3 );
		// The second vertex of the branch:
		final V branchSecondVertex = branchStartingEdge.getTarget( refV1 );
		linkBranchEdge( branchSecondVertex, v, newEdge1 );

		/*
		 * Make branch edge after the splitting point. (It works now because we
		 * were cautious to search the right outgoing edge).
		 */

		final BE newEdge2 = init( super.addEdge( newVertex, beTarget, refBE2 ), outgoingEdge );
		beeMap.put( newEdge2, outgoingEdge, refE4 );
		linkBranchEdge( v, branchLastVertex, newEdge2 );

		bvvMap.put( newVertex, v );
		vbvMap.put( v, newVertex );
		vbeMap.removeWithRef( v, refBE3 );

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
	private void linkBranchEdge( final V begin, final V end, final BE branchEdge )
	{
		final E eRef = graph.edgeRef();
		final V vRef1 = graph.vertexRef();
		final V vRef2 = graph.vertexRef();
		final BV bvRef = vertexRef();
		final BE beRef = edgeRef();

		final RefList< V > path = shortestPath.findPath( begin, end );
		// From end to begin.
		final V previous = vRef1;
		final Iterator< V > it = path.iterator();
		assigner.assign( previous, it.next() );
		while ( it.hasNext() )
		{
			final V next = it.next();

			final Edges< E > edges = next.outgoingEdges();
			// Look for the right edge.
			E edge = null;
			if ( edges.size() == 1 )
			{
				edge = edges.get( 0, eRef );
			}
			else
			{
				for ( int i = 0; i < edges.size(); i++ )
				{
					edge = edges.get( i, eRef );
					if ( edge.getTarget( vRef2 ).equals( previous ) )
						break;
				}
			}
			ebeMap.put( edge, branchEdge, beRef );
			vbeMap.put( next, branchEdge, beRef );
			vbvMap.removeWithRef( next, bvRef );

			assigner.assign( previous, next );
		}

		graph.releaseRef( eRef );
		graph.releaseRef( vRef1 );
		graph.releaseRef( vRef2 );
		releaseRef( beRef );
		releaseRef( bvRef );
	}

	private void releaseBranchEdgeFor( final E edge )
	{
		final V vRef1 = graph.vertexRef();
		final V source = edge.getSource( vRef1 );
		final BV vertexRef1 = vertexRef();
		final E eRef = graph.edgeRef();

		final BV svs;
		if ( vbvMap.containsKey( source ) )
			svs = vbvMap.get( source, vertexRef1 );
		else
			svs = split( source, vertexRef1 );

		final V vRef2 = graph.vertexRef();
		final V target = edge.getTarget( vRef2 );

		final BV vertexRef2 = vertexRef();
		final BV svt;
		if ( vbvMap.containsKey( target ) )
			svt = vbvMap.get( target, vertexRef2 );
		else
			svt = split( target, vertexRef2 );

		// Store source & target branch vertices.
		final BV vref1 = vertexRef();
		final BV vref2 = vertexRef();
		final BE beRef = edgeRef();
		final BE be = ebeMap.get( edge, beRef );
		final BV bs = be.getSource( vref1 );
		final BV bt = be.getTarget( vref2 );

		for ( final BE se : svs.outgoingEdges() )
			if ( se.getTarget().equals( svt ) )
			{
				beeMap.removeWithRef( se, eRef );
				super.remove( se );
			}

		// Check whether branch vertices should be fused.
		checkFuse( bs );
		checkFuse( bt );

		releaseRef( vertexRef1 );
		releaseRef( vertexRef2 );
		releaseRef( beRef );
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

		final BV bvRef1 = vertexRef(); // -> bv of a root.
		final BV bvRef2 = vertexRef(); // -> previous mapping of root.
		final BV bvRef3 = vertexRef(); // -> to create a new bv.
		final BE beRef1 = edgeRef(); // -> new be of a branch.
		final BE beRef2 = edgeRef(); // -> previous mapping of first edge.
		final V vRef1 = graph.vertexRef(); // -> root of a branch.
		final V vRef2 = graph.vertexRef(); // -> previous mapping of bv root.
		final V vRef3 = graph.vertexRef(); // -> second vertex of a branch.
		final E eRef = graph.edgeRef(); // -> previous mapping of be.

		final DepthFirstIterator< V, E > it = new DepthFirstIterator<>( graph );

		// Keep track of what we should iterate from.
		final RefSet< V > roots = RootFinder.getRoots( graph );
		final RefStack< V > queue = RefCollections.createRefStack( graph.vertices() );
		queue.addAll( roots );

		// To avoid iterating several times the same branch when we have graphs
		// with merge events.
		final RefSet< V > visited = RefCollections.createRefSet( graph.vertices() );

		while ( !queue.isEmpty() )
		{
			final V root = queue.pop( vRef1 );

			// Create or get first node.
			BV bvBegin = vbvMap.get( root, bvRef1 );
			if ( bvBegin == null )
			{
				bvBegin = init( super.addVertex( bvRef1 ), root );
				vbvMap.put( root, bvBegin, bvRef2 );
				bvvMap.put( bvBegin, root, vRef2 );
			}

			// Successors of the node.
			for ( final E firstEdge : root.outgoingEdges() )
			{
				final V firstTarget = firstEdge.getTarget( vRef3 );
				if ( visited.contains( firstTarget ) )
					continue;
				visited.add( firstTarget );

				it.reset( firstTarget );
				while ( it.hasNext() )
				{
					final V v = it.next();
					if ( v.incomingEdges().size() != 1 || v.outgoingEdges().size() != 1 )
					{
						// Merge point, we have to create a branch.
						// Does a BV already exists for this vertex?
						BV bvEnd = vbvMap.get( v, bvRef1 );
						if ( bvEnd == null )
						{
							// Create node.
							bvEnd = init( super.addVertex( bvRef3 ), v );
							vbvMap.put( v, bvEnd, bvRef2 );
							bvvMap.put( bvEnd, v, vRef2 );
						}

						// Edge that connect to previous node.
						final BE be = init( super.addEdge( bvBegin, bvEnd, beRef1 ), firstEdge );
						ebeMap.put( firstEdge, be, beRef2 );
						beeMap.put( be, firstEdge, eRef );

						// Walk back the branch to map it properly.
						linkBranchEdge( firstTarget, v, be );

						// Reset iteration.
						queue.add( v );
						break;
					}
				}
			}
		}

		releaseRef( bvRef1 );
		releaseRef( bvRef2 );
		releaseRef( bvRef3 );
		releaseRef( beRef1 );
		releaseRef( beRef2 );
		graph.releaseRef( vRef1 );
		graph.releaseRef( vRef2 );
		graph.releaseRef( vRef3 );
		graph.releaseRef( eRef );

		notifyGraphChanged();
	}

	@Override
	public void vertexAdded( final V vertex )
	{
		final BV bvRef1 = vertexRef();
		final BV bvRef2 = vertexRef();
		final V vRef = graph.vertexRef();

		final BV bv = init( super.addVertex( bvRef1 ), vertex );
		vbvMap.put( vertex, bv, bvRef2 );
		bvvMap.put( bv, vertex, vRef );

		releaseRef( bvRef1 );
		releaseRef( bvRef2 );
		graph.releaseRef( vRef );

		notifyGraphChanged();
	}

	@Override
	public void vertexRemoved( final V vertex )
	{
		final BV bvRef1 = vertexRef();
		final BV bvRef2 = vertexRef();;
		final BE beRef = edgeRef();
		final V vRef = graph.vertexRef();

		final BV w = vbvMap.get( vertex, bvRef1 );
		bvvMap.removeWithRef( w, vRef );
		super.remove( w );

		vbeMap.removeWithRef( vertex, beRef );
		vbvMap.removeWithRef( vertex, bvRef2 );

		releaseRef( bvRef1 );
		releaseRef( bvRef2 );
		releaseRef( beRef );
		graph.releaseRef( vRef );

		notifyGraphChanged();
	}

	@Override
	public void edgeAdded( final E edge )
	{
		final V ref1 = graph.vertexRef();
		final V ref2 = graph.vertexRef();
		final BV vref1 = vertexRef();
		final BV vref2 = vertexRef();

		final V source = edge.getSource( ref1 );
		final V target = edge.getTarget( ref2 );
		final BV sourceBV = vbvMap.get( source, vref1 );
		final BV targetBV = vbvMap.get( target, vref2 );

		if ( null != sourceBV && null != targetBV )
		{
			final BE beRef1 = edgeRef();
			final BE beRef2 = edgeRef();
			final E eRef = graph.edgeRef();

			final BE be = init( super.addEdge( sourceBV, targetBV, beRef1 ), edge );
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
			final BV bvRef = vertexRef();
			final BE beRef1 = edgeRef();
			final BE beRef2 = edgeRef();
			final E eRef = graph.edgeRef();

			final BV newSourceBV = split( source, bvRef );

			final BE se = init( super.addEdge( newSourceBV, targetBV, beRef1 ), edge );
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
			final BV vertexRef = vertexRef();
			final BV newTargetBV = split( target, vertexRef );
			final E edgeRef2 = graph.edgeRef();

			final BE edgeRef = edgeRef();
			final BE se = init( super.addEdge( sourceBV, newTargetBV, edgeRef ), edge );

			beeMap.put( se, edge, edgeRef2 );

			checkFuse( sourceBV );

			graph.releaseRef( edgeRef2 );
			releaseRef( edgeRef );
			releaseRef( vertexRef );
		}
		else
		{
			final BV vertexRef1 = vertexRef();
			final BV vertexRef2 = vertexRef();

			final BV newSourceBV = split( source, vertexRef1 );
			final BV newTargetBV = split( target, vertexRef2 );

			final BE beRef1 = edgeRef();
			final BE beRef2 = edgeRef();
			final E eRef = graph.edgeRef();

			final BE se = init( super.addEdge( newSourceBV, newTargetBV, beRef1 ), edge );

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

		notifyGraphChanged();
	}

	@Override
	public void edgeRemoved( final E edge )
	{
		// Possibly add branch vertices to make up for removed link.
		releaseBranchEdgeFor( edge );

		// Remove edge from map.
		final BE beRef = edgeRef();
		ebeMap.removeWithRef( edge, beRef );
		releaseRef( beRef );

		notifyGraphChanged();
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

		for ( final BV bv : vertices() )
			sb.append( "    " + str( bv, vref ) + "\n" );
		sb.append( "  },\n" );
		sb.append( "  edges = {\n" );

		for ( final BE be : edges() )
			sb.append( "    " + str( be, eref ) + "\n" );
		sb.append( "  }\n" );

		sb.append( "  mapping v->bv = {\n" );
		for ( final V v : vbvMap.keySet() )
			sb.append( "    " + v + " -> " + vbvMap.get( v ) + "\n" );
		sb.append( "  },\n" );

		sb.append( "  mapping bv->v = {\n" );
		for ( final BV bv : bvvMap.keySet() )
			sb.append( "    " + bv + " -> " + bvvMap.get( bv ) + "\n" );
		sb.append( "  },\n" );

		sb.append( "  mapping v->be = {\n" );
		for ( final V v : vbeMap.keySet() )
			sb.append( "    " + v + " -> " + vbeMap.get( v ) + "\n" );
		sb.append( "  },\n" );

		sb.append( "  mapping e->be = {\n" );
		for ( final E e : ebeMap.keySet() )
			sb.append( "    " + e + " -> " + ebeMap.get( e ) + "\n" );
		sb.append( "  },\n" );
		sb.append( "  mapping be->e = {\n" );
		for ( final BE be : beeMap.keySet() )
			sb.append( "    " + be + " -> " + beeMap.get( be ) + "\n" );
		sb.append( "  },\n" );

		sb.append( "}" );

		graph.releaseRef( vref );
		graph.releaseRef( eref );

		return sb.toString();
	}

	private String str( final BE be, final E eref )
	{
		return "be(" + be.getInternalPoolIndex() + ")->" + getLinkedEdge( be, eref );
	}

	private String str( final BV bv, final V vref )
	{
		return "bv(" + bv.getInternalPoolIndex() + ")->" + getLinkedVertex( bv, vref );
	}

	@Override
	public Iterator< V > vertexBranchIterator( final BE edge )
	{
		return new VertexBranchIterator( edge );
	}

	@Override
	public Iterator< E > edgeBranchIterator( final BE edge )
	{
		return new EdgeBranchIterator( edge );
	}

	/**
	 * Performs initialization tasks of the specified branch vertex, just after
	 * it has been added to the branch graph. This method should at least ensure
	 * that the <code>init()</code> "constructor" method of the vertex is
	 * called, using fields from the specified linked vertex.
	 *
	 * @param bv
	 *            the branch vertex to initialize.
	 * @param v
	 *            the linked vertex corresponding to the branch vertex in the
	 *            source graph. Used for reading fields, not modified.
	 * @return the branch vertex instance, properly initialized.
	 */
	public abstract BV init( final BV bv, final V v );

	/**
	 * Performs initialization tasks of the specified branch edge, just after it
	 * has been added to the branch graph. This method should at least ensure
	 * that the <code>init()</code> "constructor" method of the edge is called,
	 * using fields from the specified linked edge.
	 *
	 * @param be
	 *            the branch edge or edge to initialize.
	 * @param e
	 *            the linked edge corresponding to the branch edge in the source
	 *            graph. Used for reading fields, not modified.
	 * @return the branch edge instance, properly initialized.
	 */
	public abstract BE init( final BE be, final E e );

	/*
	 * INNER CLASSES
	 */

	private final class VertexBranchIterator implements Iterator< V >
	{

		private V next;

		private E e;

		private final V vref;

		private final V branchEnd;

		private final E eref;

		public VertexBranchIterator( final BE edge )
		{
			this.vref = graph.vertexRef();
			this.eref = graph.edgeRef();
			e = getLinkedEdge( edge, eref );
			next = e.getSource( vref );

			final BV bvref1 = vertexRef();
			final BV bv1 = edge.getTarget( bvref1 );
			final V vref1 = graph.vertexRef();
			branchEnd = getLinkedVertex( bv1, vref1 );
			releaseRef( bvref1 );
		}

		@Override
		public boolean hasNext()
		{
			return e != null && !next.equals( branchEnd );
		}

		@Override
		public V next()
		{
			if ( !hasNext() )
				throw new NoSuchElementException();
			next = e.getTarget( vref );
			e = next.outgoingEdges().isEmpty() ? null : next.outgoingEdges().get( 0, eref );
			return next;
		}
	}

	private final class EdgeBranchIterator implements Iterator< E >
	{

		private final V vref;

		private final E eref;

		private final BV bvref;

		private E next;

		private V target;

		public EdgeBranchIterator( final BE edge )
		{
			this.vref = graph.vertexRef();
			this.eref = graph.edgeRef();
			this.bvref = vertexRef();
			next = getLinkedEdge( edge, eref );
			target = next.getTarget( vref );
		}

		@Override
		public boolean hasNext()
		{
			return getBranchVertex( target, bvref ) == null;
		}

		@Override
		public E next()
		{
			if ( !hasNext() )
				throw new NoSuchElementException();
			next = target.outgoingEdges().get( 0, eref );
			target = next.getTarget( vref );
			return next;
		}

	}
}
