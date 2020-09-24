/*-
 * #%L
 * Mastodon Graphs
 * %%
 * Copyright (C) 2015 - 2020 Tobias Pietzsch, Jean-Yves Tinevez
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

import org.mastodon.graph.Edge;
import org.mastodon.graph.Graph;
import org.mastodon.graph.GraphListener;
import org.mastodon.graph.ListenableReadOnlyGraph;
import org.mastodon.graph.Vertex;
import org.mastodon.pool.MappedElement;

/**
 * Mother class for edge of <b>directed, listenable</b> graphs.
 * <p>
 * Graphs based on this edge class do not have a limitation on the number of
 * edges between a source and target vertices.
 * <p>
 * <em>Important:</em> Derived classes need to define "constructor" methods
 * (preferably called {@code init(...)} and users <em>must</em> call one of
 * these immediately after creating edges. The {@code init(...)} methods
 * <em>must</em> call {@link #initDone()} as their last step.
 * <p>
 * The reason: {@link ListenableReadOnlyGraph} should emit the
 * {@link GraphListener#edgeAdded(Edge)} event only after some basic
 * initialization has happened on the newly created edge. It is therefore not
 * emitted in {@link Graph#addEdge(Vertex, Vertex)} but instead in
 * {@link #initDone()}.
 * <p>
 * Idiomatically, adding an edge to a graph should look like this:<br>
 * {@code MyEdge e = graph.addEdge(...).init(...);}
 * <p>
 * Like a constructor, {@code init(...)} should be called before any other
 * method, and only once. {@link AbstractListenableEdge} tries to do some basic
 * detection of violations of this rule (throwing {@link IllegalStateException}
 * if it finds anything).
 * <p>
 * TODO: It would be nice to be able to enforce this at compile time, but I
 * couldn't find a good solution to achieve that.
 *
 * @param <E>
 *            the concrete type of this edge.
 * @param <V>
 *            the type of vertex in the graph.
 * @param <EP>
 *            the type of the pool on which edges are built
 * @param <T>
 *            the type of mapped element on which the edge pool is built.
 *
 * @author Tobias Pietzsch
 */
public class AbstractListenableEdge<
			E extends AbstractListenableEdge< E, V, EP, T >,
			V extends AbstractVertex< V, ?, ?, ? >,
			EP extends AbstractListenableEdgePool< E, V, T >,
			T extends MappedElement >
		extends AbstractEdge< E, V, EP, T >
{
	protected AbstractListenableEdge( final EP pool )
	{
		super( pool );
		notifyPostInit = pool.notifyPostInit;
	}

	private final NotifyPostInit< ?, E > notifyPostInit;

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
	 * This is called when a new edge is created
	 * ({@link Graph#addEdge(Vertex, Vertex)},
	 * {@link Graph#addEdge(Vertex, Vertex, Edge)}). We use it to set
	 * {@link #pendingInitialize} to {@code true}, which means that
	 * {@link #verifyInitialized()} will throw an exception until
	 * {@link #initDone()} clears {@link #pendingInitialize}.
	 *
	 * @throws IllegalStateException
	 *             if the object is already in a state between being created (
	 *             {@link #setToUninitializedState()}) and initialized (
	 *             {@link #initDone()}) when this method is called.
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
					+ "Please see javadoc of org.mastodon.graph.ref.AbstractListenableEdge for more information." );
		pendingInitialize = false;
		notifyPostInit.notifyEdgeAdded( ( E ) this );
	}
}
