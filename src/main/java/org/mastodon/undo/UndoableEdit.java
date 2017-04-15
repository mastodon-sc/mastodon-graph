package org.mastodon.undo;

public interface UndoableEdit
{
	public void undo();

	public void redo();
}
