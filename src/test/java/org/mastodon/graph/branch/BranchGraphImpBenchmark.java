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

import org.mastodon.graph.ListenableTestEdge;
import org.mastodon.graph.ListenableTestGraph;
import org.mastodon.graph.ListenableTestVertex;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * Measures the runtime of {@link BranchGraphImp#vertexBranchIterator}.
 */
@BenchmarkMode( Mode.AverageTime )
@OutputTimeUnit( TimeUnit.MILLISECONDS )
@Fork( 1 )
@Warmup( iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS )
@Measurement( iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS )
@State( Scope.Benchmark )
public class BranchGraphImpBenchmark
{
	private final ListenableTestGraph graph = createArtificialGraphForDepthAndBranchLength( 18, 10 );

	private BranchTestGraph branchGraphV2;

	@Benchmark
	public long benchmarkBranchGraphV2()
	{
		lazyCreateBranchGraphV2();
		return sumIdsNew( branchGraphV2 );
	}

	@Benchmark
	public BranchTestGraph benchmarkBranchGraphV2Rebuilt()
	{
		return new BranchTestGraph( graph );
	}

	private void lazyCreateBranchGraphV2()
	{
		if ( branchGraphV2 == null )
			branchGraphV2 = new BranchTestGraph( graph );
	}

	private static long sumIdsNew( BranchTestGraph branchGraph )
	{
		long sum = 0;
		for ( BranchTestVertex bv : branchGraph.vertices() )
		{
			Iterator<ListenableTestVertex> vIterator = branchGraph.vertexBranchIterator( bv );
			while ( vIterator.hasNext() )
			{
				ListenableTestVertex vertex = vIterator.next();
				sum += vertex.getId();
			}
			branchGraph.releaseIterator( vIterator );
		}
		return sum;
	}

	private static ListenableTestGraph createArtificialGraphForDepthAndBranchLength( int depth, int length )
	{
		int[] counter = { 0 };
		ListenableTestGraph graph = new ListenableTestGraph();
		ListenableTestVertex root = graph.vertexRef();
		createArtificialGraph( graph, 0, depth, length, counter, root );
		graph.releaseRef( root );
		return graph;
	}

	private static void createArtificialGraph( ListenableTestGraph graph, int startTime, int depth, int length, int[] counter, ListenableTestVertex rootRef )
	{
		final ListenableTestVertex branchEnd = graph.vertexRef();
		final ListenableTestVertex childA = graph.vertexRef();
		final ListenableTestVertex childB = graph.vertexRef();
		final ListenableTestEdge eRef = graph.edgeRef();
		try
		{
			createArtificialBranch( graph, startTime, length, counter, rootRef, branchEnd );
			if ( depth > 0 )
			{
				createArtificialGraph( graph, startTime + length, depth - 1, length, counter, childA );
				createArtificialGraph( graph, startTime + length, depth - 1, length, counter, childB );
				graph.addEdge( branchEnd, childA, eRef ).init();
				graph.addEdge( branchEnd, childB, eRef ).init();
			}
		}
		finally
		{
			graph.releaseRef( branchEnd );
			graph.releaseRef( childA );
			graph.releaseRef( childB );
			graph.releaseRef( eRef );
		}
	}

	private static void createArtificialBranch( ListenableTestGraph graph, int startTime, int length, int[] counter, ListenableTestVertex start, ListenableTestVertex end )
	{
		final ListenableTestVertex vertex = graph.vertexRef();
		final ListenableTestEdge eRef = graph.edgeRef();
		try
		{
			addVertex( graph, startTime, counter, start );
			end.refTo( start );
			for ( int t = startTime + 1; t < startTime + length; t++ )
			{
				addVertex( graph, t, counter, vertex );
				graph.addEdge( end, vertex, eRef ).init();
				end.refTo( vertex );
			}
		}
		finally
		{
			graph.releaseRef( vertex );
			graph.releaseRef( eRef );
		}
	}

	private static void addVertex( ListenableTestGraph graph, int timepoint, int[] counter, ListenableTestVertex ref )
	{
		graph.addVertex( ref ).init( counter[ 0 ]++, timepoint );
	}

	public static void main( String... args ) throws RunnerException
	{
		Options options = new OptionsBuilder().include( BranchGraphImpBenchmark.class.getName() ).build();
		new Runner( options ).run();
	}

}
