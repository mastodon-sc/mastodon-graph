/*-
 * #%L
 * Mastodon Graphs
 * %%
 * Copyright (C) 2015 - 2020 Tobias Pietzsch, Jean-Yves Tinevez
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

import org.mastodon.graph.Edge;
import org.mastodon.graph.GraphIdBimap;
import org.mastodon.graph.ListenableGraph;
import org.mastodon.graph.ListenableReadOnlyGraph;
import org.mastodon.graph.Vertex;

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
 *
 * @param <BV>
 *            the type of the branch vertices.
 * @param <BE>
 *            the type of the branch edges.
 * @param <V>
 *            the type of linked vertices.
 * @param <E>
 *            the type of linked edges.
 */
public interface BranchGraph<
	BV extends Vertex< BE >,
	BE extends Edge< BV >,
	V extends Vertex< E >,
	E extends Edge< V > >
		extends ListenableReadOnlyGraph< BV, BE >
{

	/**
	 * Returns the edge linked to the specified branch edge. The linked edge is
	 * the first edge of the branch in the linked graph.
	 * <p>
	 * For instance, in
	 * <pre>
	 *     LINKED-GRAPH                  BRANCH-GRAPH
	 *
	 *           v0                           bv0
	 *           |  e0                         |
	 *           v1                            |  be0
	 *           |  e1                         |
	 *           v2                           bv1
	 *       e2 / \  e4                       / \
	 *         /   \                    be1  /   \  be2
	 *        v3    v5                      /     \
	 *     e3 |     | e5                   /       \
	 *        v4    v6                    bv2      bv3
	 * </pre>
	 * the edge linked to <code>be1</code> is <code>e2</code>. The edge linked
	 * to <code>be2</code> is <code>e4</code>.
	 *
	 *
	 * @param be
	 *            the branch edge.
	 * @param ref
	 *            a reference to a linked graph edge used for retrieval.
	 *            Depending on concrete implementation of the linked graph, this
	 *            object can be cleared, ignored or re-used.
	 * @return the linked edge.
	 */
	public E getLinkedEdge( BE be, E ref );

	/**
	 * Returns the vertex linked to the specified branch vertex. The linked
	 * vertex is a branch extremity.
	 * <p>
	 * For instance, in
	 *
	 * <pre>
	 *     LINKED-GRAPH                  BRANCH-GRAPH
	 *
	 *           v0                           bv0
	 *           |  e0                         |
	 *           v1                            |  be0
	 *           |  e1                         |
	 *           v2                           bv1
	 *       e2 / \  e4                       / \
	 *         /   \                    be1  /   \  be2
	 *        v3    v5                      /     \
	 *     e3 |     | e5                   /       \
	 *        v4    v6                    bv2      bv3
	 * </pre>
	 *
	 * the vertex linked to <code>bv0</code> is <code>v0</code>. The vertex
	 * linked to <code>bv1</code> is <code>v2</code>.
	 *
	 * @param bv
	 *            the branch vertex.
	 * @param ref
	 *            a reference to a linked graph vertex used for retrieval.
	 *            Depending on concrete implementation of the linked graph, this
	 *            object can be cleared, ignored or re-used.
	 * @return the linked vertex.
	 */
	public V getLinkedVertex( BV bv, V ref );

	/**
	 * Returns the branch edge linked to the specified edge.
	 * <p>
	 * For instance, in
	 *
	 * <pre>
	 *     LINKED-GRAPH                  BRANCH-GRAPH
	 *
	 *           v0                           bv0
	 *           |  e0                         |
	 *           v1                            |  be0
	 *           |  e1                         |
	 *           v2                           bv1
	 *       e2 / \  e4                       / \
	 *         /   \                    be1  /   \  be2
	 *        v3    v5                      /     \
	 *     e3 |     | e5                   /       \
	 *        v4    v6                    bv2      bv3
	 * </pre>
	 *
	 * <code>e0</code> and <code>e1</code> link to <code>be0</code>.
	 * <code>e2</code> and <code>e3</code> link to <code>be1</code>.
	 * <code>e4</code> and <code>e5</code> link to <code>be2</code>.
	 *
	 * @param edge
	 *            the linked edge.
	 * @param ref
	 *            a reference object to the branch edge used for retrieval.
	 * @return a branch edge.
	 */
	public BE getBranchEdge( E edge, BE ref );

	/**
	 * Returns the branch edge linked to the specified vertex if it belongs to a
	 * branch. Returns <code>null</code> if the specified vertex is a branch
	 * extremity.
	 * <p>
	 * For instance, in
	 *
	 * <pre>
	 *     LINKED-GRAPH                  BRANCH-GRAPH
	 *
	 *           v0                           bv0
	 *           |  e0                         |
	 *           v1                            |  be0
	 *           |  e1                         |
	 *           v2                           bv1
	 *       e2 / \  e4                       / \
	 *         /   \                    be1  /   \  be2
	 *        v3    v5                      /     \
	 *     e3 |     | e5                   /       \
	 *        v4    v6                    bv2      bv3
	 * </pre>
	 *
	 * <code>v1</code> links to <code>be0</code>. <code>v3</code> links to
	 * <code>be1</code>. <code>v0</code> links to <code>null</code> because this
	 * vertex is a branch extremity.
	 *
	 * @param vertex
	 *            the linked vertex.
	 * @param ref
	 *            a reference object to the branch edge used for retrieval.
	 * @return a branch edge or <code>null</code>.
	 */
	public BE getBranchEdge( V vertex, BE ref );

	/**
	 * Returns the branch vertex linked to the specified vertex if it is a
	 * branch extremity. Returns <code>null</code> if the specified vertex
	 * belongs to inside a branch.
	 * <p>
	 * For instance, in
	 *
	 * <pre>
	 *     LINKED-GRAPH                  BRANCH-GRAPH
	 *
	 *           v0                           bv0
	 *           |  e0                         |
	 *           v1                            |  be0
	 *           |  e1                         |
	 *           v2                           bv1
	 *       e2 / \  e4                       / \
	 *         /   \                    be1  /   \  be2
	 *        v3    v5                      /     \
	 *     e3 |     | e5                   /       \
	 *        v4    v6                    bv2      bv3
	 * </pre>
	 *
	 * <code>v1</code> and <code>v3</code> link to <code>null</code> because
	 * they belong inside a branch. <code>v0</code> links to <code>bv0</code>.
	 *
	 * @param vertex
	 *            the linked vertex.
	 * @param ref
	 *            a reference object to the branch vertex used for retrieval.
	 * @return a branch vertex or <code>null</code>.
	 */
	public BV getBranchVertex( V vertex, BV ref );

	/**
	 * Returns a graph id map for the branch graph.
	 *
	 * @return a graph id map.
	 */
	public GraphIdBimap< BV, BE > getGraphIdBimap();

	/**
	 * Returns an iterator that iterates in order over the linked vertices in a
	 * branch, specified by its branch edge. The first and last vertex iterated
	 * are the branch extremities.
	 *
	 * @param edge
	 *            the branch edge.
	 * @return a new iterator.
	 */
	public Iterator< V > vertexBranchIterator( BE edge );

	/**
	 * Returns an iterator that iterates in order over the linked edges of a
	 * branch, specified by its branch edge.
	 *
	 * @param edge
	 *            the branch edge.
	 * @return a new iterator.
	 */
	public Iterator< E > edgeBranchIterator( BE edge );

}
