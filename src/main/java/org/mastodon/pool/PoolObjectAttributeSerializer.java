package org.mastodon.pool;

import org.mastodon.undo.attributes.AttributeUndoSerializer;

public class PoolObjectAttributeSerializer< O extends PoolObject< O, ?, ? > > implements AttributeUndoSerializer< O >
{
	private final int offset;

	private final int length;

	public PoolObjectAttributeSerializer(
			final int offset,
			final int length )
	{
		this.offset = offset;
		this.length = length;
	}

	@Override
	public int getNumBytes()
	{
		return length;
	}

	@Override
	public void getBytes( final O obj, final byte[] bytes )
	{
		for ( int i = 0, j = offset; i < length; ++i, ++j )
			bytes[ i ] = obj.access.getByte( j );
	}

	@Override
	public void setBytes( final O obj, final byte[] bytes )
	{
		for ( int i = 0, j = offset; i < length; ++i, ++j )
			obj.access.putByte( bytes[ i ], j );
	}

	@Override
	public void notifySet( final O obj )
	{}
}
