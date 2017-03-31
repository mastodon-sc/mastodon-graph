package org.mastodon.graph.ref;

import java.util.Map;

import org.mastodon.collection.UniqueHashcodeArrayMap;
import org.mastodon.features.Feature;
import org.mastodon.features.FeatureValue;
import org.mastodon.features.Features;
import org.mastodon.graph.VertexWithFeatures;
import org.mastodon.pool.MappedElement;

/**
 * TODO: javadoc
 *
 * @param <V>
 * @param <E>
 * @param <T>
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public class AbstractVertexWithFeatures<
			V extends AbstractVertexWithFeatures< V, E, VP, T >,
			E extends AbstractEdge< E, ?, ?, ? >,
			VP extends AbstractVertexWithFeaturesPool< V, ?, T >,
			T extends MappedElement >
		extends AbstractVertex< V, E, VP, T >
		implements VertexWithFeatures< V, E >
{
	protected AbstractVertexWithFeatures( final VP pool )
	{
		super( pool );
		features = pool.features;
		featureValues = new UniqueHashcodeArrayMap<>();
	}

	final Features< V > features;

	private final Map< Feature< ?, V, ? >, FeatureValue< ? > > featureValues;

	@SuppressWarnings( "unchecked" )
	@Override
	public < F extends FeatureValue< ? >, M > F feature( final Feature< M, V, F > feature )
	{
		F fv = ( F ) featureValues.get( feature );
		if ( fv == null )
		{
			fv = feature.createFeatureValue( ( V ) this, features );
			featureValues.put( feature, fv );
		}
		return fv;
	}
}
