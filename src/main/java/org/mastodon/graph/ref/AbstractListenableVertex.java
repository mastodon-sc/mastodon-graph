/*-
 * #%L
 * Mastodon Graphs
 * %%
 * Copyright (C) 2015 - 2021 Tobias Pietzsch, Jean-Yves Tinevez
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package org.mastodon.graph.ref;

import org.mastodon.graph.Graph;
import org.mastodon.graph.GraphListener;
import org.mastodon.graph.ListenableReadOnlyGraph;
import org.mastodon.graph.Vertex;
import org.mastodon.pool.MappedElement;

/**
 * TODO: javadoc
 *
 * <p>
 * <em>Important:</em> Derived classes need to define "constructor" methods
 * (preferably called {@code init(...)} and users <em>must</em> call one of
 * these immediately after creating vertices. The {@code init(...)} methods
 * <em>must</em> call {@link #initDone()} as their last step.
 * <p>
 * The reason: {@link ListenableReadOnlyGraph} should emit the
 * {@link GraphListener#vertexAdded(Vertex)} event only after some basic
 * initialization has happened on the newly created vertex. It is therefore not
 * emitted in {@link Graph#addVertex()} but instead in {@link #initDone()}.
 * <p>
 * Idiomatically, adding a vertex to a graph should look like this:<br>
 * {@code MyVertex v = graph.addVertex().init(...);}<br>
 * respectively:<br>
 * {@code MyVertex v = graph.addVertex(ref).init(...);}
 * <p>
 * Like a constructor, {@code init(...)} should be called before any other
 * method, and only once. {@link AbstractListenableVertex} tries to do some
 * basic detection of violations of this rule (throwing
 * {@link IllegalStateException} if it finds anything).
 * <p>
 * TODO: It would be nice to be able to enforce this at compile time, but I
 * couldn't find a good solution to achieve that.
 *
 *
 * @param <V>
 *            the concrete type of this vertex.
 * @param <E>
 *            the type of edges in the graph..
 * @param <VP>
 *            the type of the pool on which vertices are built
 * @param <T>
 *            the type of mapped element on which the vertex pool is built.
 * 
 * @author Tobias Pietzsch
 */
public class AbstractListenableVertex<
			V extends AbstractListenableVertex< V, E, VP, T >,
			E extends AbstractEdge< E, ?, ?, ? >,
			VP extends AbstractListenableVertexPool< V, ?, T >,
			T extends MappedElement >
		extends AbstractVertex< V, E, VP, T >
{
	protected AbstractListenableVertex( final VP pool )
	{
		super( pool );
		notifyPostInit = pool.notifyPostInit;
		pendingInitialize = false;
	}

	final NotifyPostInit< V, ? > notifyPostInit;

	/**
	 * Flag to detect missing or duplicate initialization. Is set to
	 * {@code true} in {@link #setToUninitializedState()}, is set to
	 * {@code false} in {@link #initDone()}.
	 */
	private boolean pendingInitialize;

	/**
	 * Verify that the object has been initialized.
	 *
	 * @throws IllegalStateException
	 *             if the object is in a state between beeing created (
	 *             {@link #setToUninitializedState()} and initialized (
	 *             {@link #initDone()}).
	 */
	protected void verifyInitialized() throws IllegalStateException
	{
		if ( pendingInitialize )
			throw new IllegalStateException( "TODO" );
	}

	/**
	 * This is called when a new vertex is created ({@link Graph#addVertex()},
	 * {@link Graph#addVertex(Vertex)}). We use it to set
	 * {@link #pendingInitialize} to {@code true}, which means that
	 * {@link #verifyInitialized()} will throw an exception until
	 * {@link #initDone()} clears {@link #pendingInitialize}.
	 *
	 * @throws IllegalStateException
	 *             if the object is already in a state between being created
	 *             ({@link #setToUninitializedState()}) and initialized
	 *             ({@link #initDone()}) when this method is called.
	 */
	@Override
	protected void setToUninitializedState() throws IllegalStateException
	{
		super.setToUninitializedState();
		verifyInitialized();
		pendingInitialize = true;
	}

	/**
	 * Deriving classes need to have {@code init(...)} methods, which should
	 * call this as the final step.
	 *
	 * @throws IllegalStateException
	 *             if {@link #initDone()} was already called on the object.
	 */
	@SuppressWarnings( "unchecked" )
	protected void initDone() throws IllegalStateException
	{
		if ( !pendingInitialize )
			throw new IllegalStateException( this.getClass().getSimpleName() + " already initialized! "
					+ "Please see javadoc of org.mastodon.graph.ref.AbstractListenableVertex for more information." );
		pendingInitialize = false;
		notifyPostInit.notifyVertexAdded( ( V ) this );
	}
}
