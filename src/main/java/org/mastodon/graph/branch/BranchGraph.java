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

import org.mastodon.graph.Edge;
import org.mastodon.graph.GraphIdBimap;
import org.mastodon.graph.ListenableReadOnlyGraph;
import org.mastodon.graph.Vertex;

import java.util.Iterator;

/**
 * Branch graph is a simplified view of a source graph.
 * <p>
 * This specific instance supports branch-edges and vertices with features.
 * <p>
 * This class implements a view of a source {@link org.mastodon.graph.ListenableGraph} (called here
 * <i>linked graph</i>) that offers a coarser level of details by representing
 * branches of the linked graph as a single vertex in the branch graph.
 * <p>
 * A branch in the linked graph is defined as sequence of connected vertices,
 * where all except the last vertex have exactly one outgoing edge, and all
 * except the first vertex have exactly one incoming edge.
 * <p>
 * If in the linked graph has a branch like this:
 *
 * <pre>
 * v0 &#8594; v1 &#8594; v2 &#8594; v3
 * </pre>
 *
 * It's represented in the branch graph as a single vertex: <i>bv0</i><br>
 * Where bv0 links to all the nodes, and edges: v0 &#8594; v1 &#8594; v2 &#8594; v3.
 * <p>
 * A small graph like this:
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
 *       bv1
 *     /
 * bv0
 *     \
 *       bv2
 * </pre>
 *
 * The vertices and edge of the branch graph are linked to the vertices and
 * edges of the original graph as follows:
 * <p>
 * <ul>
 *     <li>bv0 is linked to v0 &#8594; v1 &#8594; v2</li>
 *     <li>bv1 is linked to v3 &#8594; v4 &#8594; v5</li>
 *     <li>bv2 is linked to v6 &#8594; v7 &#8594; v8</li>
 *     <li>The edge bv0 &#8594; bv1 is linked to the edge v2 &#8594; v3 </li>
 *     <li>The edge bv0 &#8594; bv2 is linked to the edge v2 &#8594; v6 </li>
 * </ul>
 * <p>
 *
 * Let's look at a diamond shaped graph like this:
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
 *     __ bv1 __
 *    /         \
 * bv0           bv1
 *    \__ bv2 __/
 * </pre>
 *
 * @param <BV>
 *            the type of the branch-vertices.
 * @param <BE>
 *            the type of the branch-edges.
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
	 * Returns the edge linked to the specified branch-edge.
	 * <p>
	 * For instance, in
	 * <pre>
	 *     LINKED-GRAPH                  BRANCH-GRAPH
	 *
	 *           v0
	 *           |  e0
	 *           v1                           bv0
	 *           |  e1                        / \
	 *           v2                     be1  /   \  be2
	 *       e2 / \  e4                     /     \
	 *         /   \                       /       \
	 *        v3    v5                    bv1      bv2
	 *     e3 |     | e5
	 *        v4    v6
	 * </pre>
	 * The edge linked to <code>be1</code> is <code>e2</code>. The edge linked
	 * to <code>be2</code> is <code>e4</code>.
	 *
	 * @param be
	 *            the branch-edge.
	 * @param ref
	 *            a reference to a linked graph edge used for retrieval.
	 *            Depending on concrete implementation of the linked graph, this
	 *            object can be cleared, ignored or re-used.
	 * @return the linked edge.
	 */
	public E getLinkedEdge( BE be, E ref );

	/**
	 * Returns the first vertex linked to the specified branch-vertex.
	 * <p>
	 * For instance, in
	 * <pre>
	 *     LINKED-GRAPH                  BRANCH-GRAPH
	 *
	 *           v0
	 *           |  e0
	 *           v1                           bv0
	 *           |  e1                        / \
	 *           v2                     be1  /   \  be2
	 *       e2 / \  e4                     /     \
	 *         /   \                       /       \
	 *        v3    v5                    bv1      bv2
	 *     e3 |     | e5
	 *        v4    v6
	 * </pre>
	 * The first vertex linked to <code>bv0</code> is <code>v0</code>.
	 * The first vertex linked to <code>bv1</code> is <code>v3</code>.
	 * The first vertex linked to <code>bv2</code> is <code>v5</code>.
	 *
	 * @param bv
	 *            the branch-vertex.
	 * @param ref
	 *            a reference to a linked graph vertex used for retrieval.
	 *            Depending on concrete implementation of the linked graph, this
	 *            object can be cleared, ignored or re-used.
	 * @return the first vertex in the branch linked to the branch-vertex.
	 */
	public V getFirstLinkedVertex( BV bv, V ref );

	/**
	 * Returns the last vertex linked to the specified branch-vertex.
	 * <p>
	 * For instance, in
	 * <pre>
	 *     LINKED-GRAPH                  BRANCH-GRAPH
	 *
	 *           v0
	 *           |  e0
	 *           v1                           bv0
	 *           |  e1                        / \
	 *           v2                     be1  /   \  be2
	 *       e2 / \  e4                     /     \
	 *         /   \                       /       \
	 *        v3    v5                    bv1      bv2
	 *     e3 |     | e5
	 *        v4    v6
	 * </pre>
	 * The last vertex linked to <code>bv0</code> is <code>v2</code>.
	 * The last vertex linked to <code>bv1</code> is <code>v4</code>.
	 * The last vertex linked to <code>bv2</code> is <code>v6</code>.
	 *
	 * @param bv
	 *            the branch-vertex.
	 * @param ref
	 *            a reference to a linked graph vertex used for retrieval.
	 *            Depending on concrete implementation of the linked graph, this
	 *            object can be cleared, ignored or re-used.
	 * @return the last vertex in the branch linked to the branch-vertex.
	 */
	public V getLastLinkedVertex( BV bv, V ref );

	/**
	 * Returns the branch-edge linked to the specified edge.
	 * <p>
	 * For instance, in
	 * <pre>
	 *     LINKED-GRAPH                  BRANCH-GRAPH
	 *
	 *           v0
	 *           |  e0
	 *           v1                           bv0
	 *           |  e1                        / \
	 *           v2                     be1  /   \  be2
	 *       e2 / \  e4                     /     \
	 *         /   \                       /       \
	 *        v3    v5                    bv1      bv2
	 *     e3 |     | e5
	 *        v4    v6
	 * </pre>
	 * The branch-edge linked to <code>e2</code> is <code>be1</code>.
	 * The branch-edge linked to <code>e4</code> is <code>be2</code>.
	 * In this example only these two edges are linked to a corresponding
	 * branch-edge.
	 * <p>
	 * All other edges are part of branches and therefore linked to the
	 * respective branch-vertex (see {@link #getBranchVertex(Edge, Vertex)} for
	 * details).
	 *
	 * @param edge
	 *            the branch-edge.
	 * @param ref
	 *            a reference to a branch graph edge used for retrieval.
	 *            Depending on concrete implementation of the linked graph, this
	 *            object can be cleared, ignored or re-used.
	 * @return the linked branch-edge. Or null if the edge is not linked to a
	 *         branch-edge.
	 */
	public BE getBranchEdge( E edge, BE ref );

	/**
	 * Returns the branch-vertex linked to the specified vertex.
	 * <p>
	 * For instance, in
	 * <pre>
	 *     LINKED-GRAPH                  BRANCH-GRAPH
	 *
	 *           v0
	 *           |  e0
	 *           v1                           bv0
	 *           |  e1                        / \
	 *           v2                     be1  /   \  be2
	 *       e2 / \  e4                     /     \
	 *         /   \                       /       \
	 *        v3    v5                    bv1      bv2
	 *     e3 |     | e5
	 *        v4    v6
	 * </pre>
	 * The branch-vertex linked to <code>v0</code> is <code>bv0</code>.
	 * The branch-vertex linked to <code>v1</code> and <code>v2</code> is also
	 * <code>bv0</code>.
	 * The branch-vertex linked to <code>v3</code> and <code>v4</code> is
	 * <code>bv1</code>.
	 * The branch-vertex linked to <code>v5</code> and <code>v6</code> is
	 * <code>bv2</code>.
	 *
	 * @param vertex
	 *            the vertex.
	 * @param ref
	 *            a reference to a branch graph vertex used for retrieval.
	 *            Depending on concrete implementation of the linked graph, this
	 *            object can be cleared, ignored or re-used.
	 * @return the branch-vertex linked to vertex.
	 */
	public BV getBranchVertex( V vertex, BV ref );

	/**
	 * Returns the branch-vertex linked to the specified edge.
	 * <p>
	 * For instance, in
	 * <pre>
	 *     LINKED-GRAPH                  BRANCH-GRAPH
	 *
	 *           v0
	 *           |  e0
	 *           v1                           bv0
	 *           |  e1                        / \
	 *           v2                     be1  /   \  be2
	 *       e2 / \  e4                     /     \
	 *         /   \                       /       \
	 *        v3    v5                    bv1      bv2
	 *     e3 |     | e5
	 *        v4    v6
	 * </pre>
	 * The branch-vertex linked to <code>e0</code> is <code>bv0</code>.
	 * The branch-vertex linked to <code>e1</code> is also <code>bv0</code>.
	 * The branch-vertex linked to <code>e3</code> is <code>bv1</code>.
	 * The branch-vertex linked to <code>e3</code> is <code>bv2</code>.
	 * <p>
	 * The edges <code>e2</code> and <code>e4</code> are instead linked to
	 * branch edges. (see {@link #getBranchEdge(Edge, Edge)})
	 *
	 * @param edge
	 *            the edge.
	 * @param ref
	 *            a reference to a linked graph edge used for retrieval.
	 *            Depending on concrete implementation of the linked graph, this
	 *            object can be cleared, ignored or re-used.
	 * @return the linked branch-vertex. Or null if the edge is not linked to a
	 *         branch-vertex.
	 */
	public BV getBranchVertex( E edge, BV ref );


	/**
	 * @return a graph id map for the branch graph.
	 */
	public GraphIdBimap< BV, BE > getGraphIdBimap();

	/**
	 * Returns an iterator that iterates in order over the linked vertices in a
	 * branch, specified by its branch-vertex.
	 * <p>
	 * The iterator is recycled when released with
	 * {@link #releaseIterator(Iterator)}.
	 *
	 * @param vertex
	 *            the branch-vertex.
	 * @return a iterator.
	 */
	public Iterator< V > vertexBranchIterator( BV vertex );

	/**
	 * Returns an iterator that iterates in order over the linked edges of a
	 * branch, specified by its branch-vertex.
	 * <p>
	 * The iterator is recycled when released with
	 * {@link #releaseIterator(Iterator)}.
	 *
	 * @param vertex
	 *            the branch-vertex.
	 * @return a iterator.
	 */
	public Iterator< E > edgeBranchIterator( BV vertex );


	/**
	 * The iterators returned by {@link #vertexBranchIterator(Vertex)}
	 * and {@link #edgeBranchIterator(Vertex)} should be released after they
	 * have been used, by calling this method. Iterators that have been released
	 * will by recycled. This can significantly improve garbage collection
	 * performance.
	 *
	 * @param iterator
	 *            the iterator that is no longer being used.
	 */
	public void releaseIterator( Iterator<?> iterator );
}
