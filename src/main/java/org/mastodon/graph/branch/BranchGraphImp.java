/*-
 * #%L
 * Mastodon Graphs
 * %%
 * Copyright (C) 2015 - 2022 Tobias Pietzsch, Jean-Yves Tinevez
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

import net.imglib2.util.Cast;
import org.mastodon.collection.RefMaps;
import org.mastodon.collection.RefRefMap;
import org.mastodon.graph.Edge;
import org.mastodon.graph.Edges;
import org.mastodon.graph.GraphIdBimap;
import org.mastodon.graph.GraphListener;
import org.mastodon.graph.ListenableGraph;
import org.mastodon.graph.Vertex;
import org.mastodon.graph.ref.AbstractListenableEdge;
import org.mastodon.graph.ref.AbstractListenableEdgePool;
import org.mastodon.graph.ref.AbstractListenableVertex;
import org.mastodon.graph.ref.AbstractListenableVertexPool;
import org.mastodon.graph.ref.ListenableGraphImp;
import org.mastodon.pool.MappedElement;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A branch graph implementation for {@link ListenableGraph}s.
 * <p>
 * The linked graph must be listenable. The branch graph registers as a listener
 * to it, and reflect changes in the linked graph properly. The branch graph
 * itself is read-only. Trying to call its {@link #addVertex()} and other graph
 * modification methods will result in an exception to be thrown.
 * <p>
 * IMPORTANT - This version of the branch-graph does not implement incremental
 * changes. The branch-graph is only rebuilt upon calls of the
 * {@link #graphRebuilt()} method. The methods {@link #edgeAdded(Edge)},
 * {@link #edgeRemoved(Edge)}, {@link #vertexAdded(Vertex)} and
 * {@link #vertexRemoved(Vertex)} do not do anything.
 * <p>
 * This implementation ignores 'rings'. A connected component in the linked
 * graph, that looks like this:
 *
 * <pre>
 * {@code A -> B -> C -> D -> A}
 * </pre>
 *
 * (edge direction is important) won't be visible in the BranchGraph.
 * <p>
 * 'Diamonds' connected components:
 * <pre>
 * {@code  A -> B -> C
 * and A -> D -> C}
 * </pre>
 * are supported.
 *
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
	private final RefRefMap< V, BV > vbvMap;

	/**
	 * TODO
	 * Maps from linked graph vertex to branch edge. Only contains mappings for
	 * vertices that are actually linked to a branch edge. If a key (a source
	 * graph vertex) is not present in this map, it means that it is linked to a
	 * branch vertex.
	 */
	private final RefRefMap< E, BV > ebvMap;

	/**
	 * Maps from linked graph edge to a branch edge.
	 */
	private final RefRefMap< E, BE > ebeMap;

	/**
	 * Maps from branch graph edge to a linked graph edge.
	 */
	private final RefRefMap< BE, E > beeMap;

	/**
	 * Maps from branch graph vertex to a linked graph vertex.
	 */
	private final RefRefMap< BV, V > bvvMapFirst;

	/**
	 * Maps from branch graph vertex to a linked graph vertex.
	 */
	private final RefRefMap< BV, V > bvvMapLast;

	private final ListenableGraph< V, E > graph;

	private final GraphIdBimap< BV, BE > idmap;


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
		this.ebvMap = RefMaps.createRefRefMap( graph.edges(), vertices() );
		this.ebeMap = RefMaps.createRefRefMap( graph.edges(), edges() );
		this.bvvMapFirst = RefMaps.createRefRefMap( vertices(), graph.vertices() );
		this.bvvMapLast = RefMaps.createRefRefMap( vertices(), graph.vertices() );
		this.beeMap = RefMaps.createRefRefMap( edges(), graph.edges() );
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
	public BV getBranchVertex( final E edge, final BV ref )
	{
		return ebvMap.get( edge, ref );
	}

	@Override
	public BE getBranchEdge( final E edge, final BE ref )
	{
		return ebeMap.get( edge, ref );
	}

	@Override
	public V getFirstLinkedVertex( final BV bv, final V ref )
	{
		return bvvMapFirst.get( bv, ref );
	}

	@Override
	public V getLastLinkedVertex( final BV bv, final V ref )
	{
		return bvvMapLast.get( bv, ref );
	}

	@Override
	public E getLinkedEdge( final BE be, final E ref )
	{
		return beeMap.get( be, ref );
	}

	@Override
	protected void clear()
	{
		vbvMap.clear();
		ebvMap.clear();
		ebeMap.clear();
		bvvMapFirst.clear();
		bvvMapLast.clear();
		beeMap.clear();
		super.clear();
	}

	/*
	 * Graph listener.
	 */

	@Override
	public void graphRebuilt()
	{
		clear();

		// A branch starts at a node that has a number of incoming edges
		// other than one, or as a child of a node with number of outgoing
		// edges more than one.
		for( V vertex : graph.vertices() ) {
			if(!sizeEqualsOne( vertex.incomingEdges() ))
				builtBranch(vertex);
			if( sizeIsGreaterThanOne( vertex.outgoingEdges() ))
				builtChildBranches(vertex);
		}
		for( V vertex : graph.vertices() ) {
			if( sizeIsGreaterThanOne( vertex.incomingEdges() ))
				builtGraphAddEdges(vertex.incomingEdges());
			if( sizeIsGreaterThanOne( vertex.outgoingEdges() ))
				builtGraphAddEdges(vertex.outgoingEdges());
		}
		notifyGraphChanged();
	}

	public void builtBranch( V vertex )
	{
		if(vbvMap.containsKey( vertex ))
			return;
		V vRef = graph.vertexRef();
		V vRef2 = graph.vertexRef();
		BV bvRef = vertexRef();
		BV bvRef2 = vertexRef();
		try {
			BV branchVertex = super.addVertex( bvRef );
			bvvMapFirst.put( branchVertex, vertex, vRef2 );
			vbvMap.put( vertex, branchVertex, bvRef2 );
			V v = vertex;
			while( sizeEqualsOne( v.outgoingEdges() )) {
				E e = v.outgoingEdges().iterator().next();
				v = e.getTarget(vRef);
				Edges<E> edges = v.incomingEdges();
				if( !sizeEqualsOne( edges ) )
				{
					v = e.getSource(vRef);
					break;
				}
				vbvMap.put( v, branchVertex, bvRef2 );
				ebvMap.put( e, branchVertex, bvRef2 );
			}
			bvvMapLast.put( branchVertex, v, vRef2 );
			init(branchVertex, vertex, v);
		}
		finally
		{
			graph.releaseRef( vRef );
			graph.releaseRef( vRef2 );
			releaseRef( bvRef );
			releaseRef( bvRef2 );
		}
	}

	private void builtChildBranches( V vertex )
	{
		V vRef = graph.vertexRef();
		try {
			for ( E edge : vertex.outgoingEdges() ) {
				V child = edge.getTarget( vRef );
				builtBranch( child );
			}
		}
		finally
		{
			graph.releaseRef( vRef );
		}
	}

	private void builtGraphAddEdges( Edges<E> edges )
	{
		for( E edge : edges )
			builtGraphAddEdge( edge );
	}

	private void builtGraphAddEdge( E edge )
	{
		if(ebeMap.containsKey( edge ))
			return;
		V vRef = graph.vertexRef();
		BV bvRef1 = vertexRef();
		BV bvRef2 = vertexRef();
		E eRef = graph.edgeRef();
		BE beRef1 = edgeRef();
		BE beRef2 = edgeRef();
		try {
			BV source = vbvMap.get( edge.getSource( vRef ), bvRef1 );
			Objects.requireNonNull(source);
			BV target = vbvMap.get( edge.getTarget( vRef ), bvRef2 );
			Objects.requireNonNull(target);
			BE branchEdge = init( super.addEdge( source, target, beRef1 ), edge );
			ebeMap.put( edge, branchEdge, beRef2 );
			beeMap.put( branchEdge, edge, eRef );
		}
		finally {
			graph.releaseRef( vRef );
			releaseRef( bvRef1 );
			releaseRef( bvRef2 );
			graph.releaseRef( eRef );
			releaseRef( beRef1 );
			releaseRef( beRef2 );
		}
	}

	@Override
	public void edgeAdded( final E edge )
	{}

	@Override
	public void edgeRemoved( final E edge )
	{}

	@Override
	public void vertexAdded( final V vertex )
	{}

	@Override
	public void vertexRemoved( final V vertex )
	{}

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
		for ( final BV bv : bvvMapFirst.keySet() )
			sb.append( "    " + bv + " -> " + bvvMapFirst.get( bv ) + "\n" );
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
		return "bv(" + bv.getInternalPoolIndex() + ")->" + getFirstLinkedVertex( bv, vref );
	}

	private final ConcurrentLinkedQueue<VertexBranchIterator> vertexBranchIteratorQueue =
			new ConcurrentLinkedQueue<>();

	private final ConcurrentLinkedQueue<EdgeBranchIterator> edgeBranchIteratorQueue =
			new ConcurrentLinkedQueue<>();

	@Override
	public Iterator< V > vertexBranchIterator( final BV branchVertex )
	{
		VertexBranchIterator iterator = vertexBranchIteratorQueue.poll();
		if(iterator == null)
			iterator = new VertexBranchIterator();
		iterator.reset(branchVertex);
		return iterator;
	}

	@Override
	public Iterator< E > edgeBranchIterator( final BV branchVertex )
	{
		EdgeBranchIterator iterator = edgeBranchIteratorQueue.poll();
		if(iterator == null)
			iterator = new EdgeBranchIterator();
		iterator.reset(branchVertex);
		return iterator;
	}

	@Override
	public void releaseIterator( final Iterator<?> iterator )
	{
		Class<?> iteratorClass = iterator.getClass();
		if(VertexBranchIterator.class.equals(iteratorClass))
			vertexBranchIteratorQueue.add(Cast.unchecked(iterator));
		else if(EdgeBranchIterator.class.equals(iteratorClass))
			edgeBranchIteratorQueue.add(Cast.unchecked(iterator));
	}

	/**
	 * Performs initialization tasks of the specified branch vertex, just after
	 * it has been added to the branch graph. This method should at least ensure
	 * that the <code>init()</code> "constructor" method of the vertex is
	 * called, using fields from the specified linked vertex.
	 *
	 * @param branchVertex
	 *            the branch vertex to initialize.
	 * @param branchStart
	 *            the first linked vertex, corresponding to the branch vertex.
	 * @param branchEnd
	 *            the last linked vertex, corresponding to the branch vertex.
	 * @return the branch vertex instance, properly initialized.
	 */
	public abstract BV init( final BV branchVertex, final V branchStart, final V branchEnd );

	/**
	 * Performs initialization tasks of the specified branch edge, just after it
	 * has been added to the branch graph. This method should at least ensure
	 * that the <code>init()</code> "constructor" method of the edge is called,
	 * using fields from the specified linked edge.
	 *
	 * @param branchEdge
	 *            the branch edge or edge to initialize.
	 * @param edge
	 *            the linked edge corresponding to the branch edge in the source
	 *            graph. Used for reading fields, not modified.
	 * @return the branch edge instance, properly initialized.
	 */
	public abstract BE init( final BE branchEdge, final E edge );

	/*
	 * INNER CLASSES
	 */

	private final class VertexBranchIterator implements Iterator< V >
	{

		private BV branchVertex;

		private V next;

		boolean hasNext;

		private E edge;

		private final V vref;

		private final BV bvRef;

		public VertexBranchIterator()
		{
			vref = graph.vertexRef();
			bvRef = vertexRef();
			hasNext = false;
		}

		public void reset( BV branchVertex )
		{

			this.branchVertex = branchVertex;
			next = getFirstLinkedVertex( branchVertex, vref );
			edge = null;
			hasNext = (next != null);
		}

		@Override
		public boolean hasNext()
		{
			return hasNext;
		}

		@Override
		public V next()
		{
			if ( ! hasNext )
				throw new NoSuchElementException();
			if( edge != null )
			{
				next = edge.getTarget(vref);
			}
			if(next.outgoingEdges().isEmpty())
				hasNext = false;
			else
			{
				edge = next.outgoingEdges().iterator().next();
				hasNext = branchVertex.equals( getBranchVertex( edge, bvRef ) );
			}
			return next;
		}
	}

	private final class EdgeBranchIterator implements Iterator< E >
	{

		private BV branchVertex;

		private E next;

		private V v;

		private V end;

		boolean hasNext;

		private final V vref1;

		private final V vref2;

		private final BV bvRef;

		public EdgeBranchIterator()
		{
			vref1 = graph.vertexRef();
			vref2 = graph.vertexRef();
			bvRef = vertexRef();
		}

		public void reset( final BV branchVertex )
		{

			this.branchVertex = branchVertex;
			next = null;
			v = getFirstLinkedVertex( branchVertex, vref1 );
			end = getLastLinkedVertex( branchVertex, vref2 );
			hasNext = (v != null) && (end != null) && ! end.equals( v );
		}

		@Override
		public boolean hasNext()
		{
			return hasNext;
		}

		@Override
		public E next()
		{
			next = v.outgoingEdges().iterator().next();
			v = next.getTarget(vref1);
			hasNext = ! end.equals( v ) && branchVertex.equals( getBranchVertex( v, bvRef ) );
			return next;
		}

	}

	private static boolean sizeEqualsOne(Edges<?> edges) {
		if(edges.isEmpty())
			return false;
		Iterator<?> iterator = edges.iterator();
		iterator.next();
		return ! iterator.hasNext();
	}

	private static boolean sizeIsGreaterThanOne(Edges<?> edges) {
		if(edges.isEmpty())
			return false;
		Iterator<?> iterator = edges.iterator();
		iterator.next();
		return iterator.hasNext();
	}
}
