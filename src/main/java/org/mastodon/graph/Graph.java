package org.mastodon.graph;

/**
 * A graph consisting of vertices of type {@code V} and edges of type {@code E}.
 *
 * @param <V>
 *            the {@link Vertex} type of the {@link Graph}.
 * @param <E>
 *            the {@link Edge} type of the {@link Graph}.
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public interface Graph< V extends Vertex< E >, E extends Edge< V > > extends ReadOnlyGraph< V, E >
{

	/**
	 * Creates and adds a new vertex to this graph.
	 *
	 * @return the vertex created.
	 */
	public V addVertex();

	/**
	 * Creates and adds a new vertex to this graph.
	 *
	 * @param ref
	 *            an vertex reference that can be used for returning the newly
	 *            created vertex. Depending on concrete implementation, this
	 *            object can be cleared, ignored or re-used.
	 * @return the newly created vertex. The object actually returned might be
	 *         the specified {@code ref}, depending on concrete implementation.
	 */
	public V addVertex( final V ref );

	/**
	 * Adds a new directed {@link Edge} from {@code source} to {@code target}.
	 *
	 * @param source
	 *            the source vertex.
	 * @param target
	 *            the target vertex.
	 * @return the newly created edge.
	 */
	public E addEdge( final V source, final V target );

	/**
	 * Adds a new directed {@link Edge} from {@code source} to {@code target}.
	 *
	 * <p>
	 * This method is a (potentially) allocation-free version of
	 * {@link #addEdge(Vertex, Vertex)}.
	 *
	 * @param source
	 *            the source vertex.
	 * @param target
	 *            the target vertex.
	 * @param ref
	 *            an edge reference that can be used for returning the newly
	 *            created edge. Depending on concrete implementation, this
	 *            object can be cleared, ignored or re-used.
	 * @return the newly created edge. The object actually returned might be the
	 *         specified {@code ref}, depending on concrete implementation.
	 */
	public E addEdge( final V source, final V target, final E ref );

	/**
	 * Adds a new {@link Edge} between {@code source} and {@code target}. The
	 * new edge is inserted in the source and target edge lists such that
	 * {@link Edge#getSourceOutIndex()}{@code == sourceOutIndex} and
	 * {@link Edge#getTargetInIndex()}{@code == targetInIndex}.
	 *
	 * <p>
	 * Optional operation implemented by graphs that maintain edge order.
	 *
	 * @param source
	 *            the source vertex.
	 * @param sourceOutIndex
	 *            the index in the source-out list to insert the source vertex
	 *            at.
	 * @param target
	 *            the target vertex.
	 * @param targetInIndex
	 *            the index in the target-in list to insert the target vertex
	 *            at.
	 * @return the newly created edge.
	 */
	public E insertEdge( final V source, final int sourceOutIndex, final V target, final int targetInIndex );

	/**
	 * Adds a new {@link Edge} between {@code source} and {@code target}. The
	 * new edge is inserted in the source and target edge lists such that
	 * {@link Edge#getSourceOutIndex()}{@code == sourceOutIndex} and
	 * {@link Edge#getTargetInIndex()}{@code == targetInIndex}.
	 *
	 * <p>
	 * Optional operation implemented by graphs that maintain edge order.
	 *
	 * <p>
	 * This method is a (potentially) allocation-free version of
	 * {@link #insertEdge(Vertex, int, Vertex, int)}.
	 *
	 * @param source
	 *            the source vertex.
	 * @param sourceOutIndex
	 *            the index in the source-out list to insert the source vertex
	 *            at.
	 * @param target
	 *            the target vertex.
	 * @param targetInIndex
	 *            the index in the target-in list to insert the target vertex
	 *            at.
	 * @param ref
	 *            an edge reference that can be used for returning the newly
	 *            created edge. Depending on concrete implementation, this
	 *            object can be cleared, ignored or re-used.
	 * @return the newly created edge. The object actually returned might be the
	 *         specified {@code ref}, depending on concrete implementation.
	 */
	public E insertEdge( final V source, final int sourceOutIndex, final V target, final int targetInIndex, final E ref );

	/**
	 * Removes the specified vertex from this graph.
	 *
	 * @param vertex
	 *            the vertex to remove.
	 */
	public void remove( final V vertex );

	/**
	 * Removes the specified edge from this graph.
	 *
	 * @param edge
	 *            the edge to remove.
	 */
	public void remove( final E edge );

	/**
	 * Removes all the edges linked to the specified vertex from this graph.
	 *
	 * @param vertex
	 *            the vertex whose edges are to be removed.
	 */
	public void removeAllLinkedEdges( final V vertex );
}
