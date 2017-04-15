package org.mastodon.pool;

import org.mastodon.revisedundo.attributes.AttributeSerializer;

public class PoolObjectAttributeSerializer< O extends PoolObject< O, ?, ? > > implements AttributeSerializer< O >
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
		obj.access.getBytes( bytes, 0, length, offset );
	}

	@Override
	public void setBytes( final O obj, final byte[] bytes )
	{
		obj.access.putBytes( bytes, 0, length, offset );
	}

	@Override
	public void notifySet( final O obj )
	{}
}
