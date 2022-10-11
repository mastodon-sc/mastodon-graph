package org.mastodon.graph.branch;

import org.junit.Test;
import org.mastodon.graph.Edge;
import org.mastodon.graph.ListenableTestEdge;
import org.mastodon.graph.ListenableTestGraph;
import org.mastodon.graph.ListenableTestVertex;
import org.mastodon.graph.ReadOnlyGraph;
import org.mastodon.graph.Vertex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

/**
 * Tests the following methods of {@link BranchGraphImp}:
 * <ul>
 *     <li>{@link BranchGraph#getBranchVertex(Edge, Vertex)}</li>
 *     <li>{@link BranchGraph#getBranchVertex(Vertex, Vertex)}</li>
 *     <li>{@link BranchGraph#getFirstLinkedVertex}</li>
 *     <li>{@link BranchGraph#getLastLinkedVertex}</li>
 *     <li>{@link BranchGraph#getBranchEdge}</li>
 *     <li>{@link BranchGraph#getLinkedEdge}</li>
 * </ul>
 */
public class BranchGraphMapsTest
{
	private final ListenableTestGraph graph = TestGraphBuilder.build( "1->2->3->4, 1->5->4" );

	private final BranchTestGraph branchGraph = new BranchTestGraph( graph );

	private final BranchTestVertex bvRef = branchGraph.vertexRef();

	private ListenableTestVertex vRef = graph.vertexRef();

	@Test
	public void testGetBranchVertex() {
		String result = mapAsString(
				graph.vertices(),
				v -> branchGraph.getBranchVertex( v, bvRef ));
		assertEquals( "1->b1, 2->b2, 3->b2, 4->b4, 5->b5", result );
	}

	@Test
	public void testGetFirstLinkedVertex() {
		String result = mapAsString(
				branchGraph.vertices(),
				bv -> branchGraph.getFirstLinkedVertex( bv, vRef ));
		assertEquals( "b1->1, b2->2, b4->4, b5->5", result );
	}

	@Test
	public void testGetLastLinkedVertex() {
		String result = mapAsString(
				branchGraph.vertices(),
				bv -> branchGraph.getLastLinkedVertex( bv, vRef ));
		assertEquals( "b1->1, b2->3, b4->4, b5->5", result );
	}

	@Test
	public void testGetBranchVertexFromEdge() {
		String result = mapAsString(
				graph.edges(),
				e -> branchGraph.getBranchVertex( e, bvRef ));
		assertEquals( "(2->3)->b2", result );
	}

	@Test
	public void testGetBranchEdge() {
		BranchTestEdge eRef = branchGraph.edgeRef();
		String result = mapAsString(
				graph.edges(),
				e -> branchGraph.getBranchEdge( e, eRef ));
		assertEquals( "(1->2)->(b1->b2), (1->5)->(b1->b5), (3->4)->(b2->b4), (5->4)->(b5->b4)", result );
		branchGraph.releaseRef( eRef );
	}

	@Test
	public void testGetLinkedEdge() {
		ListenableTestEdge eRef = graph.edgeRef();
		String result = mapAsString(
				branchGraph.edges(),
				be -> branchGraph.getLinkedEdge( be, eRef ));
		assertEquals( "(b1->b2)->(1->2), (b1->b5)->(1->5), (b2->b4)->(3->4), (b5->b4)->(5->4)", result );
		graph.releaseRef( eRef );
	}

	private <K, V> String mapAsString( Collection<K> keys, Function<K, V> map )
	{
		List<String> strings = new ArrayList<>();
		for(K key : keys )
		{
			V value = map.apply( key );
			if(value != null)
				strings.add(toString( key ) + "->" + toString( value ));
		}
		return strings.stream().sorted().collect( Collectors.joining( ", " ) );
	}

	private String toString( Object o ) {
		if( o == null )
			return null;
		if( o instanceof BranchTestVertex )
			return toString( (BranchTestVertex ) o );
		if( o instanceof ListenableTestVertex )
			return toString( (ListenableTestVertex ) o );
		if( o instanceof ListenableTestEdge )
			return toString( (ListenableTestEdge) o, graph );
		if( o instanceof BranchTestEdge )
			return toString( (BranchTestEdge) o, branchGraph );
		return o.toString();
	}


	private String toString( BranchTestVertex bv )
	{
		ListenableTestVertex ref = graph.vertexRef();
		try {
			return "b" + branchGraph.getFirstLinkedVertex( bv, ref ).getId();
		}
		finally {
			graph.releaseRef( ref );
		}
	}

	private String toString( ListenableTestVertex bv )
	{
		return "" + bv.getId();
	}

	private <V extends Vertex<E>, E extends Edge<V>> String toString( E edge, ReadOnlyGraph<V, E> graph )
	{
		V ref1 = graph.vertexRef();
		V ref2 = graph.vertexRef();
		try {
			return "(" + toString(edge.getSource(ref1)) + "->" +
					toString(edge.getTarget(ref2)) + ")";
		}
		finally {
			graph.releaseRef( ref1 );
			graph.releaseRef( ref2 );
		}
	}
}
