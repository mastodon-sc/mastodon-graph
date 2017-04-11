package org.mastodon.revisedundo.attributes;

import java.util.ArrayList;

public class Attribute< O >
{
	private final AttributeSerializer< O > serializer;

	private final String name;

	private final ArrayList< BeforeAttributeChangeListener< O > > beforeChangeListeners;

	private final ArrayList< AttributeChangeListener< O > > changeListeners;

	private boolean emitEvents;

	public Attribute( final AttributeSerializer< O > serializer )
	{
		this( serializer, "unnamed" );
	}

	public Attribute( final AttributeSerializer< O > serializer, final String name )
	{
		this.serializer = serializer;
		this.name = name;
		beforeChangeListeners = new ArrayList<>();
		changeListeners = new ArrayList<>();
		emitEvents = true;
	}

	public AttributeSerializer< O > getUndoSerializer()
	{
		return serializer;
	}

	@Override
	public String toString()
	{
		return "Attribute(\"" + name + "\")";
	}

	/**
	 * Register a {@link BeforeAttributeChangeListener} that will be notified
	 * before the value of this attribute is changed.
	 *
	 * @param listener
	 *            the listener to register.
	 * @return {@code true} if the listener was successfully registered.
	 *         {@code false} if it was already registered.
	 */
	public boolean addBeforeAttributeChangeListener( final BeforeAttributeChangeListener< O > listener )
	{
		if ( !beforeChangeListeners.contains( listener ) )
		{
			beforeChangeListeners.add( listener );
			return true;
		}
		return false;
	}

	/**
	 * Removes the specified {@link BeforeAttributeChangeListener} from the set
	 * of listeners.
	 *
	 * @param listener
	 *            the listener to remove.
	 * @return {@code true} if the listener was present in the listeners of this
	 *         model and was successfully removed.
	 */
	public boolean removeBeforeAttributeChangeListener( final BeforeAttributeChangeListener< O > listener )
	{
		return beforeChangeListeners.remove( listener );
	}

	/**
	 * Register a {@link AttributeChangeListener} that will be notified when the
	 * value of this attribute has changed.
	 *
	 * @param listener
	 *            the listener to register.
	 * @return {@code true} if the listener was successfully registered.
	 *         {@code false} if it was already registered.
	 */
	public boolean addAttributeChangeListener( final AttributeChangeListener< O > listener )
	{
		if ( !changeListeners.contains( listener ) )
		{
			changeListeners.add( listener );
			return true;
		}
		return false;
	}

	/**
	 * Removes the specified {@link AttributeChangeListener} from the set of
	 * listeners.
	 *
	 * @param listener
	 *            the listener to remove.
	 * @return {@code true} if the listener was present in the listeners of this
	 *         model and was successfully removed.
	 */
	public boolean removeAttributeChangeListener( final AttributeChangeListener< O > listener )
	{
		return changeListeners.remove( listener );
	}

	/**
	 * Resume sending events to {@link BeforeAttributeChangeListener}s.
	 */
	public void pauseListeners()
	{
		emitEvents = false;
	}

	/**
	 * Resume sending events to {@link BeforeAttributeChangeListener}s.
	 */
	public void resumeListeners()
	{
		emitEvents = true;
	}

	/**
	 * Notify listeners that the value of this attribute for {@code object} is
	 * about to change.
	 *
	 * @param object
	 *            the object for which the attribute value is about to change.
	 */
	public void notifyBeforeAttributeChange( final O object )
	{
		if ( emitEvents )
			for ( final BeforeAttributeChangeListener< O > l : beforeChangeListeners )
				l.beforeAttributeChange( this, object );
	}

	/**
	 * Notify listeners that the value of this attribute for {@code object} has
	 * changed.
	 *
	 * @param object
	 *            the object for which the attribute value has changed.
	 */
	public void notifyAttributeChanged( final O object )
	{
		if ( emitEvents )
			for ( final AttributeChangeListener< O > l : changeListeners )
				l.attributeChanged( this, object );
	}
}
