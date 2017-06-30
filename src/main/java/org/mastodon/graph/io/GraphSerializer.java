package org.mastodon.graph.io;

import org.mastodon.graph.Edge;
import org.mastodon.graph.Graph;
import org.mastodon.graph.Vertex;
import org.mastodon.io.AttributeSerializer;

/**
 * Provides serialization of vertex and edge attributes to a byte array, for a
 * specific {@link Graph} class. This serializes only the "payload" of vertex
 * and edge objects, not the graph specific parts describing how vertices and
 * edges link to each other.
 *
 * @param <V>
 *            the vertex type
 * @param <E>
 *            the edge type
 *
 * @author Tobias Pietzsch
 */
public interface GraphSerializer< V extends Vertex< E >, E extends Edge< V > >
{
	public AttributeSerializer< V > getVertexSerializer();

	public AttributeSerializer< E > getEdgeSerializer();
}
