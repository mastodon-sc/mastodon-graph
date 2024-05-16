/*-
 * #%L
 * Mastodon Graphs
 * %%
 * Copyright (C) 2015 - 2024 Tobias Pietzsch, Jean-Yves Tinevez
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

import org.junit.Test;
import org.mastodon.graph.ListenableTestEdge;
import org.mastodon.graph.ListenableTestGraph;
import org.mastodon.graph.ListenableTestVertex;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.StringJoiner;

import static org.junit.Assert.assertEquals;

public class BranchGraphTest
{
	@Test
	public void testOneNode() {
		ListenableTestGraph graph = TestGraphBuilder.build( "4" );
		BranchTestGraph branchGraph = new BranchTestGraph( graph );
		assertEquals("bv(4)", toString(branchGraph ));
	}

	@Test
	public void testFourNodes() {
		ListenableTestGraph graph = TestGraphBuilder.build( "1->2->3, 4" );
		BranchTestGraph branchGraph = new BranchTestGraph( graph );
		assertEquals( "bv(1), bv(4)",  toString(branchGraph ) );
	}

	@Test
	public void testDiamond() {
		ListenableTestGraph graph = TestGraphBuilder.build( "1->2->3, 1->4->3" );
		BranchTestGraph branchGraph = new BranchTestGraph( graph );
		assertEquals( "bv(1)->bv(2), bv(1)->bv(4), bv(2)->bv(3), bv(4)->bv(3)",  toString(branchGraph ) );
	}

	@Test
	public void testLoop() {
		// NB: The branch graph doesn't show a loop at all. But that's ok.
		// We won't have loops in the case of Mastodon anyway.
		ListenableTestGraph graph = TestGraphBuilder.build( "1->2->3->1" );
		BranchTestGraph branchGraph = new BranchTestGraph( graph );
		assertEquals( "",  toString(branchGraph ) );
	}

	@Test
	public void testLoop2() {
		ListenableTestGraph graph = TestGraphBuilder.build( "1->2->3, 2->4->5->6->2" );
		BranchTestGraph branchGraph = new BranchTestGraph( graph );
		assertEquals( "bv(1)->bv(2), bv(2)->bv(3), bv(2)->bv(4), bv(4)->bv(2)",  toString(branchGraph ) );
	}

	@Test
	public void testBranchVertexIterator() {
		// setup
		ListenableTestGraph graph = TestGraphBuilder.build( "1->2->3->4->5, 2->6->7->8->4" );
		ListenableTestVertex v6 = getVertexFromId(graph, 6);
		BranchTestGraph branchGraph = new BranchTestGraph( graph );
		BranchTestVertex bv6 = branchGraph.getBranchVertex( v6, branchGraph.vertexRef() );
		// process
		Iterator<ListenableTestVertex> iterator = branchGraph.vertexBranchIterator( bv6 );
		//test
		assertEquals( "lv(6), lv(7), lv(8)", toString( iterator ) );
	}

	@Test
	public void testBranchEdgeIterator() {
		// setup
		ListenableTestGraph graph = TestGraphBuilder.build( "1->2->3->4->5, 2->6->7->8->4" );
		ListenableTestVertex v6 = getVertexFromId(graph, 6);
		BranchTestGraph branchGraph = new BranchTestGraph( graph );
		BranchTestVertex bv6 = branchGraph.getBranchVertex( v6, branchGraph.vertexRef() );
		// process
		Iterator<ListenableTestEdge> iterator = branchGraph.edgeBranchIterator( bv6 );
		// test
		assertEquals( "le(6 -> 7), le(7 -> 8)", toString( iterator ) );
	}

	private ListenableTestVertex getVertexFromId( ListenableTestGraph graph, int id )
	{
		for(ListenableTestVertex vertex : graph.vertices())
			if(vertex.getId() == id)
				return vertex;
		throw new NoSuchElementException("No vertex with id: " + id);
	}

	private String toString( BranchTestGraph graph ) {
		return GraphToString.toString( graph, Objects::toString );
	}

	private String toString( Iterator<?> iterator )
	{
		StringJoiner joiner = new StringJoiner( ", " );
		while( iterator.hasNext())
			joiner.add(iterator.next().toString());
		return joiner.toString();
	}
}
