package org.mastodon.graph;

import java.util.Iterator;

import org.mastodon.collection.RefSet;
import org.mastodon.graph.algorithm.RootFinder;
import org.mastodon.graph.algorithm.TreeOutputter;
import org.mastodon.graph.ref.GraphImp;
import org.mastodon.pool.ByteMappedElement;

public class TestBranchLinkGraph extends GraphImp< TestBranchLinkVertexPool, TestBranchLinkEdgePool, TestBranchLinkVertex, TestBranchLinkEdge, ByteMappedElement >
{

	private final TestBranchGraph skeletonGraph;

	public TestBranchLinkGraph( final TestBranchLinkVertexPool vertexPool, final TestBranchLinkEdgePool edgePool, final TestBranchVertexPool skeletonVertexPool, final TestBranchEdgePool skeletonEdgePool )
	{
		super( vertexPool, edgePool );
		this.skeletonGraph = new TestBranchGraph( skeletonVertexPool, skeletonEdgePool );
	}

	public TestBranchGraph getBranchGraph()
	{
		return skeletonGraph;
	}

	public static TestBranchLinkGraph create( final int initialCapacity )
	{
		final TestBranchVertexPool skeletonVertexPool = new TestBranchVertexPool( initialCapacity );
		final TestBranchEdgePool skeletonEdgePool = new TestBranchEdgePool( initialCapacity, skeletonVertexPool );
		final TestBranchLinkVertexPool vertexPool = new TestBranchLinkVertexPool( initialCapacity, skeletonVertexPool, skeletonEdgePool );
		final TestBranchLinkEdgePool edgePool = new TestBranchLinkEdgePool( initialCapacity, vertexPool, skeletonEdgePool, skeletonVertexPool );
		return new TestBranchLinkGraph( vertexPool, edgePool, skeletonVertexPool, skeletonEdgePool );
	}

	@Override
	public String toString()
	{
		final StringBuffer sb = new StringBuffer( "TestBranchLinkGraph {\n" );
		sb.append( "  vertices = {\n" );
		final Iterator< TestBranchLinkVertex > vit = vertices().iterator();
		while ( vit.hasNext() )
		{
			sb.append( "    " + vit.next() + "\n" );
		}
		sb.append( "  },\n" );
		sb.append( "  edges = {\n" );
		final Iterator< TestBranchLinkEdge > eit = edges().iterator();
		while ( eit.hasNext() )
		{
			sb.append( "    " + eit.next() + "\n" );
		}
		sb.append( "  }\n" );
		sb.append( "}" );

		sb.append( "\nTestBranchGraph {\n" );
		sb.append( "  vertices = {\n" );
		final Iterator< TestBranchVertex > svit = skeletonGraph.vertices().iterator();
		while ( svit.hasNext() )
		{
			sb.append( "    " + svit.next() + "\n" );
		}
		sb.append( "  },\n" );
		sb.append( "  edges = {\n" );
		final Iterator< TestBranchEdge > seit = skeletonGraph.edges().iterator();
		while ( seit.hasNext() )
		{
			sb.append( "    " + seit.next() + "\n" );
		}
		sb.append( "  }\n" );
		sb.append( "}" );
		return sb.toString();
	}

	public static void main( final String[] args )
	{
		System.out.println( "Creating skeleton graph." );
		final TestBranchLinkGraph graph = create( 10 );

		final TestBranchLinkVertex v0 = graph.addVertex().init( 0, 0 );
		final TestBranchLinkVertex v1 = graph.addVertex().init( 1, 1 );
		final TestBranchLinkVertex v2 = graph.addVertex().init( 2, 2 );
		final TestBranchLinkVertex v3 = graph.addVertex().init( 3, 3 );

		final TestBranchLinkVertex v4 = graph.addVertex().init( 4, 4 );
		final TestBranchLinkVertex v5 = graph.addVertex().init( 5, 5 );

		final TestBranchLinkVertex v6 = graph.addVertex().init( 6, 4 );
		final TestBranchLinkVertex v7 = graph.addVertex().init( 7, 5 );
		final TestBranchLinkVertex v8 = graph.addVertex().init( 8, 6 );

		graph.addEdge( v0, v1 );
		graph.addEdge( v1, v2 );

		graph.addEdge( v2, v3 );
		graph.addEdge( v3, v4 );
		graph.addEdge( v3, v6 );

		graph.addEdge( v4, v5 );

		graph.addEdge( v6, v7 );
		graph.addEdge( v7, v8 );

		System.out.println( "Before removal:\n" );
		System.out.println( TreeOutputter.output( graph ) );
		System.out.println( TreeOutputter.output( graph.getBranchGraph() ) );

		System.out.println( "_______________________________________" );
		System.out.print( "\nRemoving... " );
		graph.remove( v3 );

		System.out.println( "Done.\n" );

		System.out.println( "_______________________________________" );

		System.out.println( "Graph:" );
		final RefSet< TestBranchLinkVertex > roots = RootFinder.getRoots( graph );
		for ( final TestBranchLinkVertex root : roots )
		{
			System.out.println( "\nFor root: " + root );
			System.out.println( TreeOutputter.output( graph, root ) );

		}

		System.out.println( "_______________________________________" );
		System.out.println( "Branch graph:" );
		final RefSet< TestBranchVertex > broots = RootFinder.getRoots( graph.getBranchGraph() );
		for ( final TestBranchVertex root : broots )
		{
			System.out.println( "\nFor root: " + root );
			System.out.println( TreeOutputter.output( graph.getBranchGraph(), root ) );

		}
	}

}