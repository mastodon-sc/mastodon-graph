package org.mastodon.undo;

import org.mastodon.RefPool;

import gnu.trove.impl.Constants;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;

/**
 * Bidirectional map that links objects in an undoable edit stack and some undo
 * IDs.
 *
 * @param <O>
 *            the object type
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public class UndoIdBimap< O > implements RefPool< O >
{
	/** Value used to declare that the requested value is not in the map. */
	public static final int NO_ENTRY_VALUE = -1;

	private final TIntIntMap undoIdToObjectId;

	private final TIntIntMap objectIdToUndoId;

	private final RefPool< O > idmap;

	private int idgen;

	/**
	 * Create a bidirectional undo IDs - objects map for the specified object
	 * pool.
	 *
	 * @param idmap
	 *            the object pool.
	 */
	public UndoIdBimap( final RefPool< O > idmap )
	{
		this.idmap = idmap;
		undoIdToObjectId = new TIntIntHashMap( Constants.DEFAULT_CAPACITY, Constants.DEFAULT_LOAD_FACTOR, NO_ENTRY_VALUE, NO_ENTRY_VALUE );
		objectIdToUndoId = new TIntIntHashMap( Constants.DEFAULT_CAPACITY, Constants.DEFAULT_LOAD_FACTOR, NO_ENTRY_VALUE, NO_ENTRY_VALUE );
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
		final int objectId = idmap.getId( o );
		int undoId = objectIdToUndoId.get( objectId );
		if ( undoId == NO_ENTRY_VALUE )
		{
			undoId = idgen++;
			objectIdToUndoId.put( objectId, undoId );
			undoIdToObjectId.put( undoId, objectId );
		}
		return undoId;
	}

	/**
	 * Stores the specified undo ID for the specified object.
	 *
	 * @param undoId
	 *            the undo ID.
	 * @param o
	 *            the object.
	 */
	public synchronized void put( final int undoId, final O o )
	{
		final int objectId = idmap.getId( o );
		objectIdToUndoId.put( objectId, undoId );
		undoIdToObjectId.put( undoId, objectId );
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
		final int objectId = undoIdToObjectId.get( undoId );
		if ( objectId == NO_ENTRY_VALUE )
			return null;
		else
			return idmap.getObject( objectId, ref );
	}

	@Override
	public O createRef()
	{
		return idmap.createRef();
	}

	@Override
	public void releaseRef( final O ref )
	{
		idmap.releaseRef( ref );
	}

	@Override
	public Class< O > getRefClass()
	{
		return idmap.getRefClass();
	}
}
