package org.mastodon.pool;

import org.mastodon.undo.attributes.Attribute;

public class IntAttribute< O extends PoolObject< O, ?, ? > >
	extends Attribute< O >
{
	private final int offset;

	public IntAttribute( final int offset )
	{
		super( new PoolObjectAttributeSerializer<>( offset, ByteUtils.INT_SIZE ) );
		this.offset = offset;
	}

	public void setQuiet( final O key, final int value )
	{
		key.access.putInt( value, offset );
	}

	public void set( final O key, final int value )
	{
		notifyBeforeAttributeChange( key );
		key.access.putInt( value, offset );
		notifyAttributeChanged( key );
	}

	public int get( final O key )
	{
		return key.access.getInt( offset );
	}
}
