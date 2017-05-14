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
public class AbstractSimpleEdge<
			E extends AbstractSimpleEdge< E, V, EP, T >,
			V extends AbstractVertex< V, ?, ?, ? >,
			EP extends AbstractSimpleEdgePool< E, V, T >,
			T extends MappedElement >
		extends AbstractEdge< E, V, EP, T >
{
	protected AbstractSimpleEdge( final EP pool )
	{
		super( pool );
	}
}
