package org.mastodon.undo;

import org.mastodon.graph.Edge;
import org.mastodon.graph.Graph;
import org.mastodon.graph.Vertex;

/**
 * TODO: Consider splitting into two objects, generic {@code UndoSerializer< O >}
 */
/**
 * Provides serialization of vertices and edges to a byte array, for a specific
 * {@link Graph} class.
 *
 * @param <V>
 *            the vertex type.
 * @param <E>
 *            the edge type.
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public interface GraphUndoSerializer< V extends Vertex< E >, E extends Edge< V > >
{

	/**
	 * Returns the number of bytes required to serialize a vertex.
	 *
	 * @return the number of bytes.
	 */
	public int getVertexNumBytes();

	/**
	 * Stores data from {@code vertex} into {@code bytes}.
	 *
	 * @param vertex
	 *            the vertex to store.
	 * @param bytes
	 *            the byte array in which to write.
	 * @see #getVertexNumBytes()
	 */
	public void getBytes( final V vertex, final byte[] bytes );

	/**
	 * Restores data from {@code bytes} into {@code vertex}.
	 *
	 * @param vertex
	 *            the vertex to restore.
	 * @param bytes
	 *            the byte array to read.
	 */
	public void setBytes( final V vertex, final byte[] bytes );

	/**
	 * Notifies that bytes have been written ({@link #setBytes(Vertex, byte[])})
	 * to {@code vertex}.
	 *
	 * @param vertex
	 *            the vertex that has been modified.
	 */
	public void notifyVertexAdded( final V vertex );

	public int getEdgeNumBytes();

	/**
	 * Stores data from {@code edge} into {@code bytes}.
	 *
	 * @param edge
	 *            the edge to store.
	 * @param bytes
	 *            the byte array in which to write.
	 * @see #getEdgeNumBytes()
	 */
	public void getBytes( final E edge, final byte[] bytes );

	/**
	 * Restores data from {@code bytes} into {@code edge}.
	 *
	 * @param edge
	 *            the edge to restore.
	 * @param bytes
	 *            the byte array to read.
	 */
	public void setBytes( final E edge, final byte[] bytes );

	/**
	 * Notifies that bytes have been written ({@link #setBytes(Edge, byte[])})
	 * to {@code edge}.
	 *
	 * @param edge
	 *            the edge that has been modified.
	 */
	public void notifyEdgeAdded( final E edge );
}
