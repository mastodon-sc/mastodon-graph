package org.mastodon.graph.ref;

import java.util.Map;

import org.mastodon.collection.UniqueHashcodeArrayMap;
import org.mastodon.features.Feature;
import org.mastodon.features.FeatureValue;
import org.mastodon.features.Features;
import org.mastodon.graph.VertexWithFeatures;
import org.mastodon.pool.MappedElement;

/**
 * Base class for vertex with features implementations.
 * <p>
 * Build on {@link AbstractVertex} and adds the capacity to return
 * {@link FeatureValue}s.
 *
 * @param <V>
 *            the vertex type.
 * @param <E>
 *            the edge type.
 * @param <T>
 *            the {@code MappedElement} type, for example
 *            {@code ByteMappedElement}, used for internal representation of the
 *            vertex.
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public class AbstractVertexWithFeatures< V extends AbstractVertexWithFeatures< V, E, T >, E extends AbstractEdge< E, ?, ? >, T extends MappedElement >
		extends AbstractVertex< V, E, T >
		implements VertexWithFeatures< V, E >
{
	protected AbstractVertexWithFeatures( final AbstractVertexPool< V, ?, T > pool )
	{
		super( pool );
		featureValues = new UniqueHashcodeArrayMap<>();
	}

	Features< V > features;

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
