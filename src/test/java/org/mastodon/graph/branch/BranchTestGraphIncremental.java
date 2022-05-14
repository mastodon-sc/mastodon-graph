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
package org.mastodon.graph.branch;

import org.mastodon.graph.ListenableGraph;
import org.mastodon.graph.ListenableTestEdge;
import org.mastodon.graph.ListenableTestVertex;
import org.mastodon.pool.ByteMappedElement;

/**
 * Version of the BranchTestGraph that listens to incremental changes.
 * 
 * @author Jean-Yves Tinevez
 */
public class BranchTestGraphIncremental extends BranchGraphImpIncremental<
	ListenableTestVertex,
	ListenableTestEdge,
	BranchTestVertex,
	BranchTestEdge,
	BranchTestVertexPool,
	BranchTestEdgePool,
	ByteMappedElement >
{

	public BranchTestGraphIncremental( final ListenableGraph< ListenableTestVertex, ListenableTestEdge > graph, final BranchTestEdgePool branchEdgePool )
	{
		super( graph, branchEdgePool );
		// Listen to incremental changes.
		graph.addGraphListener( this );
	}

	@Override
	public BranchTestVertex init( final BranchTestVertex bv, final ListenableTestVertex v )
	{
		return bv.init( v.getId(), v.getTimepoint() );
	}

	@Override
	public BranchTestEdge init( final BranchTestEdge be, final ListenableTestEdge e )
	{
		return be.init();
	}
}
