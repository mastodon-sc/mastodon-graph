package org.mastodon.graph;

import org.mastodon.features.WithFeatures;

public interface EdgeWithFeatures< E extends EdgeWithFeatures< E, V >, V extends Vertex< ? > > extends Edge< V >, WithFeatures< E >
{}
