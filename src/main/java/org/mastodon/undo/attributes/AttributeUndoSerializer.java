package org.mastodon.undo.attributes;

/**
 * Provides serialization of (parts of) an object of type {@code O} to a byte
 * array.
 *
 * @param <O>
 *            type of object.
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public interface AttributeUndoSerializer< O >
{
	/**
	 * How many bytes are needed for storage. (This is the expected size of the
	 * {@code bytes} array passed to {@link #getBytes(Object, byte[])},
	 * {@link #setBytes(Object, byte[])}.
	 *
	 * @return number of bytes are needed for storage.
	 */
	public int getNumBytes();

	/**
	 * Stores data from {@code obj} into {@code bytes}.
	 * <p>
	 * The required array size can be obtained by {@link #getNumBytes()}.
	 *
	 * @param obj
	 *            the object to store.
	 * @param bytes
	 *            the byte array in which to write.
	 */
	public void getBytes( final O obj, final byte[] bytes );

	/**
	 * Restores data from {@code bytes} into {@code obj}.
	 *
	 * @param obj
	 *            the object to restore.
	 * @param bytes
	 *            the byte array to read.
	 */
	public void setBytes( final O obj, final byte[] bytes );

	/**
	 * Notifies that bytes have been written ({@link #setBytes(Object, byte[])})
	 * to {@code obj}.
	 * <p>
	 * Note: Currently nothing is ever done in between {@code setBytes()} and
	 * {@code notifySet()}, so maybe this will be removed later and
	 * notifications directly linked to {@code setBytes()}. For now, we keep it
	 * explicit.
	 *
	 * @param obj
	 *            the object that has been modified.
	 */
	public void notifySet( final O obj );
}
