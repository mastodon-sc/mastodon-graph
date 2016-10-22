package org.mastodon.graph.algorithm.traversal;

import org.mastodon.graph.Edge;
import org.mastodon.graph.Vertex;

/**
 * Interface for classes interacting with {@link GraphSearch}es.
 * <p>
 * The {@link GraphSearch} hierarchy does not implement the iterator interface
 * and upon calling {@link GraphSearch#start(Vertex)}, the search processes
 * until complete without stopping. The {@link SearchListener} interface allows
 * interacting with the search using notifications. Specific methods of this
 * interface are called when a vertex is discovered, is done being processed,
 * when an edge is traversed or when a connected component has been iterated
 * completely.
 * <p>
 * These methods have the actual graph search instance as parameter, so that,
 * <ul>
 * <li>the search can be stopped via {@link GraphSearch#abort()},
 * <li>the class of an edge can be queried with
 * {@link GraphSearch#edgeClass(Vertex, Vertex)}
 * <li>the parent of a vertex in the search tree can be queried with
 * {@link GraphSearch#parent(Vertex)}
 * </ul>
 * Searches inheriting from {@link GraphSearch} might add some extra useful
 * methods, specific to the search strategy they implement.
 * <p>
 * The method names follow the framework of
 * <a href="http://www.algorist.com/">Skiena</a>.
 *
 * @author Jean=Yves Tinevez
 *
 * @param <V>
 *            the type of the graph vertices.
 * @param <E>
 *            the type of the graph edges.
 * @param <G>
 *            the type of the graph search.
 */
public interface SearchListener< V extends Vertex< E >, E extends Edge< V >, G extends GraphSearch< G, V, E > >
{
	/**
	 * Called when a vertex has been processed during a search.
	 * <p>
	 * The meaning of "processed" differs according to the search class.
	 * <ul>
	 * <li>For breadth-first searches, a vertex is processed when all its edges
	 * have been crossed. Vertices are therefore processed in the same order
	 * that of their discovery.
	 * <li>For depth-first searches, a vertex is processed when all its
	 * descendants in the search tree have been processed or when it is a leaf
	 * in the search tree. Vertices are therefore processed in the reverse order
	 * that of their discovery.
	 * </ul>
	 *
	 * @param vertex
	 *            the vertex processed.
	 * @param search
	 *            the graph search used to iterate over the graph.
	 */
	public void processVertexLate( final V vertex, G search );

	/**
	 * Called when a vertex is discovered during the search.
	 * <p>
	 * A vertex is discovered when the search crosses an edge in the graph that
	 * leads to it.
	 *
	 * @param vertex
	 *            the vertex discovered.
	 * @param search
	 *            the graph search used to iterate over the graph.
	 */
	public void processVertexEarly( final V vertex, G search );

	/**
	 * Called when an edge is crossed during the search.
	 *
	 * @param edge
	 *            the edge in the graph.
	 * @param from
	 *            the vertex the search crossed the edge from.
	 * @param to
	 *            the vertex the search crossed the edge to.
	 * @param search
	 *            the graph search used to iterate over the graph.
	 */
	public void processEdge( final E edge, final V from, final V to, G search );

	/**
	 * Called for cross-component searches when the search finished iterating
	 * over a connected component and jump to another one in the graph. Such
	 * jumps do not cross over an edge in the search graph.
	 *
	 * @param from
	 *            the last vertex of the first connected component we jump from.
	 * @param to
	 *            a vertex in the new connected component we jump to.
	 * @param search
	 *            the graph search used to iterate over the graph.
	 */
	public void crossComponent( V from, V to, G search );
}
