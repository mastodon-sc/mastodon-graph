package org.mastodon.graph;

import org.mastodon.graph.ref.GraphImp;
import org.mastodon.pool.ByteMappedElement;

public class TestBranchGraph extends GraphImp< TestBranchVertexPool, TestBranchEdgePool, TestBranchVertex, TestBranchEdge, ByteMappedElement >
{

	public TestBranchGraph( final TestBranchVertexPool vertexPool, final TestBranchEdgePool edgePool )
	{
		super( vertexPool, edgePool );
	}

}