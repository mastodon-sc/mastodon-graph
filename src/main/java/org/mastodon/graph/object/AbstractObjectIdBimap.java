package org.mastodon.graph.object;

import java.util.NoSuchElementException;

import org.mastodon.RefPool;

import gnu.trove.map.TIntObjectArrayMap;
import gnu.trove.map.TIntObjectMap;

/**
 * A {@link RefPool} implementation for object graphs that maintains a mapping
 * between objects and {@code int} IDs.
 * <p>
 * IDs are assigned to objects when first requested ({@link #getId(Object)}).
 *
 * @param <O>
 *            the type of objects in this map.
 *
 * @author Tobias Pietzsch
 */
public abstract class AbstractObjectIdBimap< O > implements RefPool< O >
{
	private final Class< O > klass;

	private final TIntObjectMap< O > idToObj;

	private int idgen;

	public AbstractObjectIdBimap( final Class< O > klass )
	{
		this.klass = klass;
		idToObj = new TIntObjectArrayMap<>();
		idgen = 0;
	}

	@Override
	public O getObject( final int id, final O obj )
	{
		final O o = idToObj.get( id );
		if ( o == null )
			throw new NoSuchElementException();
		return o;
	}

	@Override
	public O getObjectIfExists( final int id, final O ref )
	{
		return idToObj.get( id );
	}

	@Override
	public O createRef()
	{
		return null;
	}

	@Override
	public void releaseRef( final O obj )
	{}

	@Override
	public Class< O > getRefClass()
	{
		return klass;
	}

	protected int createId( final O o )
	{
		final int id = idgen++;
		idToObj.put( id, o );
		return id;
	}
}
