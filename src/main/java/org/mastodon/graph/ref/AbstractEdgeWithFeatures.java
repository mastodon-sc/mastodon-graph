package org.mastodon.graph.ref;

import java.util.Map;

import org.mastodon.collection.UniqueHashcodeArrayMap;
import org.mastodon.features.Feature;
import org.mastodon.features.FeatureValue;
import org.mastodon.features.Features;
import org.mastodon.graph.EdgeWithFeatures;
import org.mastodon.pool.MappedElement;

public class AbstractEdgeWithFeatures<
			E extends AbstractEdgeWithFeatures< E, V, EP, T >,
			V extends AbstractVertex< V, ?, ?, ? >,
			EP extends AbstractEdgeWithFeaturesPool< E, V, T >,
			T extends MappedElement >
		extends AbstractEdge< E, V, EP, T >
		implements EdgeWithFeatures< E, V >
{

	protected AbstractEdgeWithFeatures( final EP pool )
	{
		super( pool );
		features = pool.features;
		featureValues = new UniqueHashcodeArrayMap<>();
	}

	final Features< E > features;

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
