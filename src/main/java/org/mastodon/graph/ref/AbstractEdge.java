package org.mastodon.graph.ref;

import org.mastodon.pool.MappedElement;

/**
 * TODO: javadoc
 *
 * @param <E>
 * @param <V>
 * @param <EP>
 * @param <T>
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public class AbstractEdge<
			E extends AbstractEdge< E, V, EP, T >,
			V extends AbstractVertex< V, ?, ?, ? >,
			EP extends AbstractEdgePool< E, V, T >,
			T extends MappedElement >
		extends AbstractNonSimpleEdge< E, V, EP, T >
{
	protected AbstractEdge( final EP pool )
	{
		super( pool );
	}
}
