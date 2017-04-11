package org.mastodon.revisedundo.attributes;

public interface AttributeChangeListener< O >
{
	public void attributeChanged( final Attribute< O > attribute, final O object );
}
