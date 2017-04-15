package org.mastodon.undo;

import org.mastodon.RefPool;

/**
 * Bidirectional map that links objects and undo IDs.
 *
 * @param <O>
 *            the object type
 *
 * @author Tobias Pietzsch
 */
public class UndoIdBimap< O > implements RefPool< O >
{
	/** Value used to declare that the requested value is not in the map. */
	public static final int NO_ENTRY_VALUE = -1;

	private final RefPool< O > pool;

	private final IntPropertyBimap< O > objUndoIdBimap;

	private int idgen;

	/**
	 * Create a bidirectional object - int map that links objects of the
	 * specified object pool to undo IDs.
	 *
	 * @param pool
	 *            the object pool.
	 */
	public UndoIdBimap( final RefPool< O > pool )
	{
		this.pool = pool;
		objUndoIdBimap = new IntPropertyBimap<>( pool, NO_ENTRY_VALUE );
		idgen = 0;
	}

	/**
	 * Returns the undo ID for the specified object,
	 * <p>
	 * Creates new undo ID if {@code o} is not in map yet.
	 *
	 * @param o
	 *            the object.
	 * @return its undo ID.
	 */
	@Override
	public synchronized int getId( final O o )
	{
		int id = objUndoIdBimap.getValue( o );
		if ( id == NO_ENTRY_VALUE )
		{
			id = idgen++;
			objUndoIdBimap.set( o, id );
		}
		return id;
	}

	/**
	 * Stores the specified undo ID for the specified object.
	 *
	 * @param o
	 *            the object.
	 * @param id
	 *            the undo ID.
	 */
	public synchronized void put( final O o, final int id )
	{
		objUndoIdBimap.set( o, id );
	}

	/**
	 * Returns the object mapped to the specified undo ID.
	 *
	 * @param undoId
	 *            the undo ID.
	 * @param ref
	 *            a pool reference that might be used for object retrieval.
	 * @return the object mapped to the specified undo ID, or <code>null</code>
	 *         is there are no such undo ID stored in this map.
	 */
	@Override
	public O getObject( final int undoId, final O ref )
	{
		return objUndoIdBimap.getKey( undoId, ref );
	}

	@Override
	public O createRef()
	{
		return pool.createRef();
	}

	@Override
	public void releaseRef( final O ref )
	{
		pool.releaseRef( ref );
	}

	@Override
	public Class< O > getRefClass()
	{
		return pool.getRefClass();
	}
}
