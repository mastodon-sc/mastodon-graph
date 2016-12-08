package org.mastodon.collection;

import java.util.HashMap;
import java.util.Map;

import org.mastodon.RefPool;
import org.mastodon.collection.ref.RefObjectHashMap;
import org.mastodon.collection.ref.RefPoolBackedRefCollection;
import org.mastodon.collection.ref.RefRefHashMap;
import org.mastodon.collection.wrap.RefMapWrapper;

/**
 * TODO merge with {@link RefCollections}.
 */
public class RefMaps
{

	public static < K, V > RefRefMap< K, V > createRefRefMap( final RefCollection< K > keyCollection, final RefCollection< V > valueCollection )
	{
		final RefPool< K > keyPool = tryGetRefPool( keyCollection );
		final RefPool< V > valuePool = tryGetRefPool( valueCollection );
		if ( keyPool != null && valuePool != null )
		{
			return new RefRefHashMap<>( keyPool, valuePool );
		}
		else if ( keyPool != null && valuePool == null )
		{
			return wrap( new RefObjectHashMap<>( keyPool ) );
		}
		else if ( keyPool == null && valuePool != null )
		{
			// TODO create the ObjectRefMap in mastodon-collection.
			return null;
		}
		else
		{
			return wrap( new HashMap< K, V >() );
		}
	}

	public static < K, V > RefRefMap< K, V > createRefRefMap( final RefCollection< K > keyCollection, final RefCollection< V > valueCollection, final int initialCapacity )
	{
		final RefPool< K > keyPool = tryGetRefPool( keyCollection );
		final RefPool< V > valuePool = tryGetRefPool( valueCollection );
		if ( keyPool != null && valuePool != null )
		{
			return new RefRefHashMap<>( keyPool, valuePool, initialCapacity );
		}
		else if ( keyPool != null && valuePool == null )
		{
			return wrap( new RefObjectHashMap<>( keyPool, initialCapacity ) );
		}
		else if ( keyPool == null && valuePool != null )
		{
			// TODO create the ObjectRefMap in mastodon-collection.
			return null;
		}
		else
		{
			return wrap( new HashMap< K, V >( initialCapacity ) );
		}
	}


	private static < O > RefPool< O > tryGetRefPool( final RefCollection< O > collection )
	{
		return ( collection instanceof RefPoolBackedRefCollection )
				? ( ( org.mastodon.collection.ref.RefPoolBackedRefCollection< O > ) collection ).getRefPool()
				: null;
	}

	private static < K, O > RefRefMap< K, O > wrap( final Map< K, O > map )
	{
		return new RefMapWrapper<>( map );
	}

	private RefMaps()
	{}

}
