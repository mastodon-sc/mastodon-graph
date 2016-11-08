package org.mastodon.graph.ref;

import java.util.Map;

import org.mastodon.collection.UniqueHashcodeArrayMap;
import org.mastodon.features.Feature;
import org.mastodon.features.FeatureValue;
import org.mastodon.features.Features;
import org.mastodon.graph.EdgeWithFeatures;
import org.mastodon.pool.MappedElement;

/**
 * Base class for edge with features implementations.
 * <p>
 * Build on {@link AbstractEdge} and adds the capacity to return
 * {@link FeatureValue}s.
 *
 * @param <E>
 *            the edge type.
 * @param <V>
 *            the vertex type.
 * @param <T>
 *            the {@code MappedElement} type, for example
 *            {@code ByteMappedElement}, used for internal representation of the
 *            vertex.
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public class AbstractEdgeWithFeatures< E extends AbstractEdgeWithFeatures< E, V, T >, V extends AbstractVertex< V, ?, ? >, T extends MappedElement >
		extends AbstractEdge< E, V, T >
		implements EdgeWithFeatures< E, V >
{

	protected AbstractEdgeWithFeatures( final AbstractEdgePool< E, V, T > pool )
	{
		super( pool );
		featureValues = new UniqueHashcodeArrayMap<>();
	}

	Features< E > features;

	private final Map< Feature< ?, E, ? >, FeatureValue< ? > > featureValues;

	@SuppressWarnings( "unchecked" )
	@Override
	public < F extends FeatureValue< ? >, M > F feature( final Feature< M, E, F > feature )
	{
		F fv = ( F ) featureValues.get( feature );
		if ( fv == null )
		{
			fv = feature.createFeatureValue( ( E ) this, features );
			featureValues.put( feature, fv );
		}
		return fv;
	}
}
