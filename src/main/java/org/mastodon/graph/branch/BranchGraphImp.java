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
import org.mastodon.graph.GraphIdBimap;
import org.mastodon.graph.GraphListener;
import org.mastodon.graph.ListenableGraph;
import org.mastodon.graph.Vertex;
import org.mastodon.graph.algorithm.RootFinder;
import org.mastodon.graph.algorithm.traversal.DepthFirstIterator;
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
 * <p>
 * IMPORTANT - This version of the branch-graph does not implement incremental
 * changes. The branch-graph is only rebuilt upon calls of the
 * {@link #graphRebuilt()} method. The methods {@link #edgeAdded(Edge)},
 * {@link #edgeRemoved(Edge)}, {@link #vertexAdded(Vertex)} and
 * {@link #vertexRemoved(Vertex)} do not do anything.
 * <p>
 * As a side effect, this implementation does not support 'rings'. If a
 * connected component in the core-graph looks like this:
 * 
 * <pre>
 * A -&gt; B -&gt; C -&gt; D -&gt; A
 * </pre>
 * 
 * (edge direction is important), it won't be discovered in the branch-graph,
 * which requires 'roots' to be built. Roots are vertices with no incoming
 * edges. 'Diamonds' connected components:
 * 
 * <pre>
 * A -&gt; B -&gt; D
 * </pre>
 * 
 * and
 * 
 * <pre>
 * A -&gt; C -&gt; D
 * </pre>
 * 
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
	 * Maps from linked graph vertex to branch edge. Only contains mappings for
	 * vertices that are actually linked to a branch edge. If a key (a source
	 * graph vertex) is not present in this map, it means that it is linked to a
	 * branch vertex.
	 */
	private final RefRefMap< V, BE > vbeMap;

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
	private final RefRefMap< BV, V > bvvMap;

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
		this.vbeMap = RefMaps.createRefRefMap( graph.vertices(), edges() );
		this.ebeMap = RefMaps.createRefRefMap( graph.edges(), edges() );
		this.bvvMap = RefMaps.createRefRefMap( vertices(), graph.vertices() );
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

	@Override
	protected void clear()
	{
		vbvMap.clear();
		vbeMap.clear();
		ebeMap.clear();
		bvvMap.clear();
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
		final BV bvRef1 = vertexRef(); // -> bv of a root.
		final BV bvRef2 = vertexRef(); // -> previous mapping of root.
		final BV bvRef3 = vertexRef(); // -> to create a new bv.
		final BE beRef1 = edgeRef(); // -> new be of a branch.
		final BE beRef2 = edgeRef(); // -> previous mapping of first edge.
		final BE beRef3 = edgeRef(); // -> used to map e to be.
		final BE beRef4 = edgeRef(); // -> used to map v to be.
		final V vRef1 = graph.vertexRef(); // -> root of a branch.
		final V vRef2 = graph.vertexRef(); // -> previous mapping of bv root.
		final V vRef3 = graph.vertexRef(); // -> second vertex of a branch.
		final E eRef1 = graph.edgeRef(); // -> previous mapping of be.
		final E eRef2 = graph.edgeRef(); // -> used to add to the list to tag

		try
		{
			final DepthFirstIterator< V, E > it = new DepthFirstIterator<>( graph );
			final RefList< V > vToTag = RefCollections.createRefList( graph.vertices() );
			final RefList< E > eToTag = RefCollections.createRefList( graph.edges() );

			// Keep track of what we should iterate from.
			final RefSet< V > roots = RootFinder.getRoots( graph );
			final RefStack< V > queue = RefCollections.createRefStack( graph.vertices() );
			queue.addAll( roots );

			// To avoid iterating several times the same branch when we have
			// graphs with merge events.
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

					// Prepare list of edges and vertices of the branch.
					vToTag.clear();
					eToTag.clear();
					eToTag.add( firstEdge );

					it.reset( firstTarget );
					while ( it.hasNext() )
					{
						final V v = it.next();
						if ( v.incomingEdges().size() != 1 || v.outgoingEdges().size() != 1 )
						{
							// Merge point, we have to create a branch.
							// Does a BV already exists for this vertex?
							BV bvEnd = vbvMap.get( v, bvRef3 );
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
							beeMap.put( be, firstEdge, eRef1 );

							// Walk back the branch to map it properly.
							for ( final E e : eToTag )
								ebeMap.put( e, be, beRef3 );
							for ( final V v2 : vToTag )
								vbeMap.put( v2, be, beRef4 );

							// Reset iteration.
							queue.add( v );
							break;
						}
						else
						{
							// Store vertex and edge to map them later to the branch.
							vToTag.add( v );
							eToTag.add( v.outgoingEdges().get( 0, eRef1 ) );
						}
					}
				}
			}
			notifyGraphChanged();
		}
		finally
		{
			releaseRef( bvRef1 );
			releaseRef( bvRef2 );
			releaseRef( bvRef3 );
			releaseRef( beRef1 );
			releaseRef( beRef2 );
			releaseRef( beRef3 );
			releaseRef( beRef4 );
			graph.releaseRef( vRef1 );
			graph.releaseRef( vRef2 );
			graph.releaseRef( vRef3 );
			graph.releaseRef( eRef1 );
			graph.releaseRef( eRef2 );
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
