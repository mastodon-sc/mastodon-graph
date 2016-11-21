package org.mastodon.graph;

import org.mastodon.graph.ref.AbstractEdge;
import org.mastodon.graph.ref.AbstractEdgePool;
import org.mastodon.graph.ref.AbstractVertex;
import org.mastodon.graph.ref.AbstractVertexPool;
import org.mastodon.graph.ref.AllEdges;
import org.mastodon.pool.MappedElement;
import org.mastodon.pool.PoolObject;

public class AbstractBranchLinkEdgePool< 
E extends AbstractEdge< E, V, T >, 
V extends AbstractBranchLinkVertex< V, SV, SE, E, T >, 
SV extends AbstractBranchVertex< SV, SE, V, T >, 
SE extends AbstractBranchEdge< SE, SV, E, T >, 
T extends MappedElement >
		extends AbstractEdgePool< E, V, T >
{
	protected final AbstractEdgePool< SE, SV, ? > skeletonEdgePool;

	private final AbstractVertexPool< SV, SE, ? > skeletonVertexPool;

	private final AbstractVertexPool< V, ?, ? > vertexPool;

	public AbstractBranchLinkEdgePool(
			final int initialCapacity,
			final PoolObject.Factory< E, T > edgeFactory,
			final AbstractVertexPool< V, ?, ? > vertexPool,
			final AbstractEdgePool< SE, SV, ? > skeletonEdgePool,
			final AbstractVertexPool< SV, SE, ? > skeletonVertexPool )
	{
		super( initialCapacity, edgeFactory, vertexPool );
		this.vertexPool = vertexPool;
		this.skeletonEdgePool = skeletonEdgePool;
		this.skeletonVertexPool = skeletonVertexPool;
	}

	@Override
	public void deleteAllLinkedEdges( final AbstractVertex< ?, ?, ? > vertex )
	{
		@SuppressWarnings( "unchecked" )
		final AllEdges< E > edges = ( AllEdges< E > ) vertex.edges();
		for ( final E e : edges )
			releaseBranchEdgeFor( e );

		super.deleteAllLinkedEdges( vertex );
	}

	@Override
	public void delete( final E edge )
	{
		releaseBranchEdgeFor( edge );
		super.delete( edge );
	}

	private void releaseBranchEdgeFor( final E edge )
	{
		final V source = edge.getSource();
		final V target = edge.getTarget();
		final SV svs;
		final SV svt;

		if ( source.isBranchGraphVertex() )
		{
			svs = source.getBranchGraphVertex();
		}
		else
		{
			svs = split( source );
		}

		if ( target.isBranchGraphVertex() )
		{
			svt = target.getBranchGraphVertex();
		}
		else
		{
			svt = split( target );
		}

		for ( final SE se : svs.outgoingEdges() )
		{
			if ( se.getTarget().equals( svt ) )
			{
				skeletonEdgePool.delete( se );
			}
		}
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public E addEdge( final AbstractVertex< ?, ?, ? > source, final AbstractVertex< ?, ?, ? > target, E edge )
	{
		edge = super.addEdge( source, target, edge );
		final V s = ( V ) source;
		final V t = ( V ) target;

		if ( s.isBranchGraphVertex() && t.isBranchGraphVertex() )
		{
			final int ids = s.getBranchGraphIndex();
			final int idt = t.getBranchGraphIndex();
			final SV svs = skeletonVertexPool.createRef();
			final SV svt = skeletonVertexPool.createRef();
			skeletonVertexPool.getObject( ids, svs );
			skeletonVertexPool.getObject( idt, svt );

			SE se = skeletonEdgePool.createRef();
			se = skeletonEdgePool.addEdge( svs, svt, se );
			se.setLinkedEdgeIndex( edge.getInternalPoolIndex() );

			checkFuse( svs );
			checkFuse( svt );

			skeletonEdgePool.releaseRef( se );
			skeletonVertexPool.releaseRef( svt );
			skeletonVertexPool.releaseRef( svs );

		}
		else if ( !s.isBranchGraphVertex() && t.isBranchGraphVertex() )
		{
			final SV svs = split( s );

			final int idt = t.getBranchGraphIndex();
			final SV svt = skeletonVertexPool.createRef();
			skeletonVertexPool.getObject( idt, svt );

			SE se = skeletonEdgePool.createRef();
			se = skeletonEdgePool.addEdge( svs, svt, se );
			se.setLinkedEdgeIndex( edge.getInternalPoolIndex() );

			checkFuse( svt );

			skeletonEdgePool.releaseRef( se );
			skeletonVertexPool.releaseRef( svt );
			skeletonVertexPool.releaseRef( svs );
		}
		else if ( s.isBranchGraphVertex() && !t.isBranchGraphVertex() )
		{
			final SV svt = split( t );

			final int ids = s.getBranchGraphIndex();
			final SV svs = skeletonVertexPool.createRef();
			skeletonVertexPool.getObject( ids, svs );

			SE se = skeletonEdgePool.createRef();
			se = skeletonEdgePool.addEdge( svs, svt, se );
			se.setLinkedEdgeIndex( edge.getInternalPoolIndex() );

			checkFuse( svs );

			skeletonEdgePool.releaseRef( se );
			skeletonVertexPool.releaseRef( svs );
			skeletonVertexPool.releaseRef( svt );
		}
		else
		{
			final SV svs = split( s );
			final SV svt = split( t );

			SE se = skeletonEdgePool.createRef();
			se = skeletonEdgePool.addEdge( svs, svt, se );
			se.setLinkedEdgeIndex( edge.getInternalPoolIndex() );

			checkFuse( svs );
			checkFuse( svt );

			skeletonEdgePool.releaseRef( se );
		}

		return edge;
	}

	protected SV split( final V v )
	{
		assert !v.isBranchGraphVertex();

		final SE f = skeletonEdgePool.createRef();
		final SE f1 = skeletonEdgePool.createRef();
		final SE f2 = skeletonEdgePool.createRef();
		final E e = createRef();
		final E fe = createRef();
		final SV w = skeletonVertexPool.createRef();
		final SV ws = skeletonVertexPool.createRef();
		final SV wt = skeletonVertexPool.createRef();
		final V v1 = vertexPool.createRef();
		final V v2 = vertexPool.createRef();

		v.getBranchGraphEdge( f );
		v.outgoingEdges().get( 0, e );
		f.getSourceGraphEdge( fe );
		f.getSource( ws );
		f.getTarget( wt );
		fe.getTarget( v1 );
		wt.getSourceGraphVertex( v2 );

		skeletonEdgePool.delete( f );

		skeletonVertexPool.create( w );
		w.setLinkedVertexIndex( v.getInternalPoolIndex() );

		skeletonEdgePool.addEdge( ws, w, f1 );
		f1.setLinkedEdgeIndex( fe.getInternalPoolIndex() );
		linkBranchEdge( v1, v, f1 );

		skeletonEdgePool.addEdge( w, wt, f2 );
		f2.setLinkedEdgeIndex( e.getInternalPoolIndex() );
		linkBranchEdge( v, v2, f2 );

		vertexPool.releaseRef( v2 );
		vertexPool.releaseRef( v1 );
		skeletonVertexPool.releaseRef( wt );
		skeletonVertexPool.releaseRef( ws );
//			skeletonVertexPool.releaseRef( w );
		releaseRef( fe );
		releaseRef( e );
		skeletonEdgePool.releaseRef( f2 );
		skeletonEdgePool.releaseRef( f1 );
		skeletonEdgePool.releaseRef( f );

		v.setIsBranchGraphVertex( true );
		v.setBranchGraphIndex( w.getInternalPoolIndex() );

		return w;
	}

	/**
	 * If skeleton graph vertex {@code w} has exactly one incoming and one
	 * outgoing edge, remove it and merge the incoming and outgoing edges.
	 * Update links between source graph and skeleton graph accordingly.
	 *
	 * @param w
	 *            the skeleton vertex.
	 */
	private void checkFuse( final SV w )
	{
		if ( w.incomingEdges().size() == 1 && w.outgoingEdges().size() == 1 )
		{
			final SE f1 = skeletonEdgePool.createRef();
			final SE f2 = skeletonEdgePool.createRef();
			final SE f3 = skeletonEdgePool.createRef();
			final SV f1s = skeletonVertexPool.createRef();
			final SV f2t = skeletonVertexPool.createRef();
			final V v1 = vertexPool.createRef();
			final V v2 = vertexPool.createRef();
			final E f1e = createRef();

			// f1 := skeleton edge to w
			w.incomingEdges().get( 0, f1 );
			// f2 := skeleton edge from w
			w.outgoingEdges().get( 0, f2 );

			// f1s := source skeleton vertex of f1
			f1.getSource( f1s );
			// f2t := target skeleton vertex of f2
			f2.getTarget( f2t );

			// f1e := source edge corresponding to f1
			f1.getSourceGraphEdge( f1e );
			// v1 := target vertex of f1e ==> first source vertex on new
			// skeleton edge
			f1e.getTarget( v1 );

			// v2 := source vertex corresponding to f2t ==> terminates new
			// skeleton edge
			f2t.getSourceGraphVertex( v2 );

			// remember source edge linked from f1
			final int f1linkedEdgeIndex = f1.getLinkedEdgeIndex();

			// remove w, f1, f2 from skeleton graph
			skeletonVertexPool.delete( w );

			// f3 := new skeleton edge between f1s and f2t
			skeletonEdgePool.addEdge( f1s, f2t, f3 );

			// reference f3 from every source graph vertex on the path
			linkBranchEdge( v1, v2, f3 );

			// link from f3 to source edge that was previously linked from f1
			f3.setLinkedEdgeIndex( f1linkedEdgeIndex );

			releaseRef( f1e );
			vertexPool.releaseRef( v2 );
			vertexPool.releaseRef( v1 );
			skeletonVertexPool.releaseRef( f2t );
			skeletonVertexPool.releaseRef( f1s );
			skeletonEdgePool.releaseRef( f3 );
			skeletonEdgePool.releaseRef( f2 );
			skeletonEdgePool.releaseRef( f1 );
		}
	}

	/**
	 * Link source graph vertices from {@code begin} (inclusive) to {@code end}
	 * (exclusive) to the {@code skeletonEdge}.
	 *
	 * @param begin
	 * @param end
	 * @param skeletonEdge
	 */
	private void linkBranchEdge( final V begin, final V end, final SE skeletonEdge )
	{
		final int i = skeletonEdge.getInternalPoolIndex();
		final V v = vertexPool.createRef();
		final E e = createRef();
		v.refTo( begin );
		while ( !v.equals( end ) )
		{
			v.setIsBranchGraphVertex( false );
			v.setBranchGraphIndex( i );
			v.outgoingEdges().get( 0, e ).getTarget( v );
		}
		releaseRef( e );
		vertexPool.releaseRef( v );
	}
}