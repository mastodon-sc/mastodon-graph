/*-
 * #%L
 * Mastodon Graphs
 * %%
 * Copyright (C) 2015 - 2022 Tobias Pietzsch, Jean-Yves Tinevez
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
package org.mastodon.graph.algorithm;

import org.mastodon.collection.RefSet;
import org.mastodon.graph.TestSimpleEdge;
import org.mastodon.graph.TestSimpleGraph;
import org.mastodon.graph.TestSimpleVertex;
import org.mastodon.graph.object.ObjectEdge;
import org.mastodon.graph.object.ObjectGraph;
import org.mastodon.graph.object.ObjectVertex;

public class TreeOutputterExample
{
	private static TestSimpleGraph tsg;

	private static TestSimpleVertex A;

	public static void main( final String[] args )
	{
		final ObjectGraph< String > graph = createCElegansLineage();

		final RefSet< ObjectVertex< String >> roots = RootFinder.getRoots( graph );
		final TreeOutputter< ObjectVertex< String >, ObjectEdge< String > > treeOutputter = new TreeOutputter< >( graph );

		for ( final ObjectVertex< String > root : roots )
		{
			System.out.println( treeOutputter.get( root ) );
		}

		System.out.println();
		System.out.println();

		createTrackMateGraph();
		final TreeOutputter< TestSimpleVertex, TestSimpleEdge > tsto = new TreeOutputter< >( tsg );
		System.out.println( tsto.get( A ) );

	}

	public static final TestSimpleGraph createTrackMateGraph()
	{
		tsg = new TestSimpleGraph();

		A = tsg.addVertex().init( 0 );

		final TestSimpleVertex B = tsg.addVertex().init( 1 );
		tsg.addEdge( A, B );

		final TestSimpleVertex C = tsg.addVertex().init( 2 );
		tsg.addEdge( A, C );

		final TestSimpleVertex E = tsg.addVertex().init( 3 );
		tsg.addEdge( A, E );

		final TestSimpleVertex D = tsg.addVertex().init( 4 );
		tsg.addEdge( B, D );

		final TestSimpleVertex F = tsg.addVertex().init( 5 );
		tsg.addEdge( B, F );

		final TestSimpleVertex G = tsg.addVertex().init( 6 );
		tsg.addEdge( C, G );

		// For fun, let's make it NOT a tree
		tsg.addEdge( G, A );
		tsg.addEdge( E, G );

		return tsg;
	}

	public static ObjectGraph< String > createCElegansLineage()
	{
		final ObjectGraph< String > graph = new ObjectGraph< >();

		// AB lineage

		final ObjectVertex< String > AB = graph.addVertex().init( "AB" );
		final ObjectVertex< String > ABa = graph.addVertex().init( "AB.a" );
		final ObjectVertex< String > ABp = graph.addVertex().init( "AB.p" );
		graph.addEdge( AB, ABa );
		graph.addEdge( AB, ABp );

		final ObjectVertex< String > ABal = graph.addVertex().init( "AB.al" );
		final ObjectVertex< String > ABar = graph.addVertex().init( "AB.ar" );
		graph.addEdge( ABa, ABal );
		graph.addEdge( ABa, ABar );

		final ObjectVertex< String > ABala = graph.addVertex().init( "AB.ala" );
		final ObjectVertex< String > ABalp = graph.addVertex().init( "AB.alp" );
		graph.addEdge( ABal, ABala );
		graph.addEdge( ABal, ABalp );

		final ObjectVertex< String > ABara = graph.addVertex().init( "AB.ara" );
		final ObjectVertex< String > ABarp = graph.addVertex().init( "AB.arp" );
		graph.addEdge( ABar, ABara );
		graph.addEdge( ABar, ABarp );

		final ObjectVertex< String > ABpl = graph.addVertex().init( "AB.pl" );
		final ObjectVertex< String > ABpr = graph.addVertex().init( "AB.pr" );
		graph.addEdge( ABp, ABpl );
		graph.addEdge( ABp, ABpr );

		final ObjectVertex< String > ABpla = graph.addVertex().init( "AB.pla" );
		final ObjectVertex< String > ABplp = graph.addVertex().init( "AB.plp" );
		graph.addEdge( ABpl, ABpla );
		graph.addEdge( ABpl, ABplp );

		final ObjectVertex< String > ABpra = graph.addVertex().init( "AB.pra" );
		final ObjectVertex< String > ABprp = graph.addVertex().init( "AB.prp" );
		graph.addEdge( ABpr, ABpra );
		graph.addEdge( ABpr, ABprp );

		// P1 lineage

		final ObjectVertex< String > P1 = graph.addVertex().init( "P1" );
		final ObjectVertex< String > P2 = graph.addVertex().init( "P2" );
		final ObjectVertex< String > EMS = graph.addVertex().init( "EMS" );
		graph.addEdge( P1, P2 );
		graph.addEdge( P1, EMS );

		final ObjectVertex< String > P3 = graph.addVertex().init( "P3" );
		graph.addEdge( P2, P3 );

		final ObjectVertex< String > C = graph.addVertex().init( "C" );
		graph.addEdge( P2, C );

		// C

		final ObjectVertex< String > Ca = graph.addVertex().init( "C.a" );
		final ObjectVertex< String > Cp = graph.addVertex().init( "C.p" );
		graph.addEdge( C, Ca );
		graph.addEdge( C, Cp );

		final ObjectVertex< String > Caa = graph.addVertex().init( "C.aa" );
		final ObjectVertex< String > Cap = graph.addVertex().init( "C.ap" );
		graph.addEdge( Ca, Caa );
		graph.addEdge( Ca, Cap );

		final ObjectVertex< String > Cpa = graph.addVertex().init( "C.pa" );
		final ObjectVertex< String > Cpp = graph.addVertex().init( "C.pp" );
		graph.addEdge( Cp, Cpa );
		graph.addEdge( Cp, Cpp );

		// E

		final ObjectVertex< String > E = graph.addVertex().init( "E" );
		graph.addEdge( EMS, E );

		final ObjectVertex< String > Ea = graph.addVertex().init( "E.a" );
		final ObjectVertex< String > Ep = graph.addVertex().init( "E.p" );
		graph.addEdge( E, Ea );
		graph.addEdge( E, Ep );

		final ObjectVertex< String > Eal = graph.addVertex().init( "E.al" );
		final ObjectVertex< String > Ear = graph.addVertex().init( "E.ar" );
		graph.addEdge( Ea, Eal );
		graph.addEdge( Ea, Ear );

		final ObjectVertex< String > Epl = graph.addVertex().init( "E.pl" );
		final ObjectVertex< String > Epr = graph.addVertex().init( "E.pr" );
		graph.addEdge( Ep, Epl );
		graph.addEdge( Ep, Epr );

		// MS

		final ObjectVertex< String > MS = graph.addVertex().init( "MS" );
		graph.addEdge( EMS, MS );
		final ObjectVertex< String > MSa = graph.addVertex().init( "MS.a" );
		final ObjectVertex< String > MSp = graph.addVertex().init( "MS.p" );
		graph.addEdge( MS, MSa );
		graph.addEdge( MS, MSp );

		// P3


		final ObjectVertex< String > D = graph.addVertex().init( "D" );
		graph.addEdge( P3, D );
		final ObjectVertex< String > P4 = graph.addVertex().init( "P4" );
		graph.addEdge( P3, P4 );

		final ObjectVertex< String > Z2 = graph.addVertex().init( "Z2" );
		final ObjectVertex< String > Z3 = graph.addVertex().init( "Z3" );
		graph.addEdge( P4, Z2 );
		graph.addEdge( P4, Z3 );

		// Zygote

		final ObjectVertex< String > zygote = graph.addVertex().init( "Zygote" );
		graph.addEdge( zygote, AB );
		graph.addEdge( zygote, P1 );

		// Extras, to get a long branch

		final ObjectVertex< String > ABplaa = graph.addVertex().init( "AB.plaa" );
		graph.addEdge( ABpla, ABplaa );

		final ObjectVertex< String > ABplaaa = graph.addVertex().init( "AB.plaaa" );
		graph.addEdge( ABplaa, ABplaaa );

		final ObjectVertex< String > ABplaaaa = graph.addVertex().init( "AB.plaaaa" );
		graph.addEdge( ABplaaa, ABplaaaa );

		return graph;

	}
}
