package org.mastodon.graph;

import org.mastodon.features.WithFeatures;

public interface VertexWithFeatures< V extends VertexWithFeatures< V, E >, E extends Edge< ? > > extends Vertex< E >, WithFeatures< V >
{}
