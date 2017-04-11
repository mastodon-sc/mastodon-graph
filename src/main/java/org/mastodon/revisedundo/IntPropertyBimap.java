package org.mastodon.revisedundo;

import org.mastodon.RefPool;
import org.mastodon.collection.IntRefMap;
import org.mastodon.collection.RefCollection;
import org.mastodon.collection.RefCollections;
import org.mastodon.collection.RefIntMap;
import org.mastodon.collection.ref.IntRefHashMap;
import org.mastodon.collection.ref.RefIntHashMap;
import org.mastodon.properties.AbstractPropertyMap;

/**
 * Property map ({@code O} keys to {@code int} values) that also keeps a reverse
 * map ({@code int} keys to {@code O} values). This is used to implements
 * {@link UndoIdBimap}.
 *
 * @param <O>
 *            the object type
 *
 * @author Tobias Pietzsch
 */
class IntPropertyBimap< O > extends AbstractPropertyMap< O, Integer >
{
	private final int noEntryValue;

	private final RefIntMap< O > map;

	private final IntRefMap< O > rmap;

	public IntPropertyBimap( final RefCollection< O > pool, final int noEntryValue )
	{
		this.noEntryValue = noEntryValue;
		map = RefCollections.createRefIntMap( pool, noEntryValue );
		rmap = RefCollections.createIntRefMap( pool, noEntryValue );
		tryRegisterPropertyMap( pool );
	}

	public IntPropertyBimap( final RefCollection< O > pool, final int noEntryValue, final int initialCapacity )
	{
		this.noEntryValue = noEntryValue;
		map = RefCollections.createRefIntMap( pool, noEntryValue, initialCapacity );
		rmap = RefCollections.createIntRefMap( pool, noEntryValue, initialCapacity );
		tryRegisterPropertyMap( pool );
	}

	public IntPropertyBimap( final RefPool< O > pool, final int noEntryValue )
	{
		this.noEntryValue = noEntryValue;
		map = new RefIntHashMap<>( pool, noEntryValue );
		rmap = new IntRefHashMap<>( pool, noEntryValue );
		tryRegisterPropertyMap( pool );
	}

	public IntPropertyBimap( final RefPool< O > pool, final int noEntryValue, final int initialCapacity )
	{
		this.noEntryValue = noEntryValue;
		map = new RefIntHashMap<>( pool, noEntryValue, initialCapacity );
		rmap = new IntRefHashMap<>( pool, noEntryValue, initialCapacity );
		tryRegisterPropertyMap( pool );
	}

	/*
	 * methods used by UndoIdBimap
	 */

	public void set( final O key, final int value )
	{
//		notifyBeforePropertyChange( key );
		map.put( key, value );
		rmap.put( value, key );
//		notifyPropertyChanged( key );
	}

	public int getValue( final O key )
	{
		return map.get( key );
	}

	public O getKey( final int value, final O ref )
	{
		return rmap.get( value, ref );
	}

	/*
	 * methods below are to satisfy PropertyMap interface
	 */

	@Override
	public Integer set( final O key, final Integer value )
	{
//		notifyBeforePropertyChange( key );
		final int old = map.put( key, value );
		rmap.put( value, key );
//		notifyPropertyChanged( key );
		return ( old == noEntryValue ) ? null : Integer.valueOf( old );
	}

	@Override
	public void remove( final O key )
	{
//		notifyBeforePropertyChange( key );
		final int value = map.remove( key );
		if ( value != noEntryValue )
			rmap.remove( value );
//		notifyPropertyChanged( key );
	}

	@Override
	public Integer get( final O key )
	{
		return map.get( key );
	}

	@Override
	public boolean isSet( final O key )
	{
		return map.containsKey( key );
	}

	public void release()
	{
		map.clear();
		rmap.clear();
		tryUnregisterPropertyMap();
	}

	@Override
	public void beforeDeleteObject( final O key )
	{
		final int value = map.remove( key );
		if ( value != noEntryValue )
			rmap.remove( value );
	}
}
