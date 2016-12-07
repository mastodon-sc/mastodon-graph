package org.mastodon.graph.branch;

import org.mastodon.graph.Edge;
import org.mastodon.graph.ListenableGraph;
import org.mastodon.graph.Vertex;
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
{

	private final BranchGraphListener< V, E, BranchVertex, BranchEdge > branchGraphListener;

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
		this.branchGraphListener = new BranchGraphListener<>( graph, this );
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
		return branchGraphListener.vbvMap.get( vertex, ref );
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
		return branchGraphListener.vbeMap.get( vertex, ref );
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
		return branchGraphListener.ebeMap.get( edge, ref );
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
		return branchGraphListener.bvvMap.get( bv, ref );
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
		return branchGraphListener.beeMap.get( be, ref );
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
			sb.append( "    " + ( bv ) + "\n" );
		sb.append( "  },\n" );
		sb.append( "  edges = {\n" );

		for ( final BranchEdge be : edges() )
			sb.append( "    " + ( be ) + "\n" );
		sb.append( "  }\n" );
		sb.append( "}" );
		return sb.toString();
	}
}
