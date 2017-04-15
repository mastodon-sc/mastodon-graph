package org.mastodon.undo.attributes;

public interface AttributeChangeListener< O >
{
	public void attributeChanged( final Attribute< O > attribute, final O object );
}
