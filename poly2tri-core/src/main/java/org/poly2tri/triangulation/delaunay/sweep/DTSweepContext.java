/* Poly2Tri
 * Copyright (c) 2009-2010, Poly2Tri Contributors
 * http://code.google.com/p/poly2tri/
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of Poly2Tri nor the names of its contributors may be
 *   used to endorse or promote products derived from this software without specific
 *   prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.poly2tri.triangulation.delaunay.sweep;

import java.util.ArrayList;
import java.util.Collections;

import org.poly2tri.triangulation.TriangulationContext;
import org.poly2tri.triangulation.TriangulationPoint;
import org.poly2tri.triangulation.delaunay.DelaunayTriangle;
import org.poly2tri.triangulation.point.TPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Thomas �hl�n, thahlen@gmail.com
 *
 */
public class DTSweepContext extends TriangulationContext
{
    private final static Logger logger = LoggerFactory.getLogger( DTSweepContext.class );

    // Inital triangle factor, seed triangle will extend 30% of 
    // PointSet width to both left and right.
    private final float ALPHA = 0.3f;

    /*
     * 
     */
    protected ArrayList<DelaunayTriangle> _triList = new ArrayList<DelaunayTriangle>();

//    protected TMesh mesh = new TMesh();
    /** Advancing front **/
    protected AdvancingFront aFront;
    /** head point used with advancing front */
    private TriangulationPoint m_head;
    /** tail point used with advancing front */
    private TriangulationPoint m_tail;
    protected Basin basin = new Basin();
    protected EdgeEvent edgeEvent = new EdgeEvent();
    
    private DTSweepPointComparator _comparator = new DTSweepPointComparator();
    
    public DTSweepContext()
    {
        clear();
    }
        
    public void addToMap( DelaunayTriangle triangle )
    {
        _triList.add( triangle );
        if( isDebugEnabled() )
        {
            getDebugContext().setPrimaryTriangle( triangle );
            suspend("Triangle added to map");
        }
    }

    public void removeFromList( DelaunayTriangle triangle )
    {
        _triList.remove( triangle );
        // TODO: remove all neighbor pointers to this triangle
//        for( int i=0; i<3; i++ )
//        {
//            if( triangle.neighbors[i] != null )
//            {
//                triangle.neighbors[i].clearNeighbor( triangle );
//            }
//        }
//        triangle.clearNeighbors();
        if( isDebugEnabled() )
        {
            getDebugContext().setPrimaryTriangle( null );
            suspend("Triangle removed from map");
        }
    }

    public void meshClean( DelaunayTriangle triangle )
    {
        meshCleanReq( triangle );
    }

    private void meshCleanReq( DelaunayTriangle triangle )
    {
        if( triangle != null && !triangle.isInterior() )
        {
            triangle.isInterior( true );
            _pointSet.addTriangle( triangle );
            for( int i = 0; i < 3; i++ )
            {
                if( !triangle.cEdge[i] )
                {
                    meshCleanReq( triangle.neighbors[i] );
                }
            }
        }
    }

    public void clear()
    {
        super.clear();
        _triList.clear();
    }

    public AdvancingFront getAdvancingFront()
    {
        return aFront;
    }

    public void setHead( TriangulationPoint p1 ) { m_head = p1; }
    public TriangulationPoint getHead() { return m_head; }

    public void setTail( TriangulationPoint p1 ) { m_tail = p1; }
    public TriangulationPoint getTail() { return m_tail; }

    public void addNode( AdvancingFrontNode node )
    {
//        System.out.println( "add:" + node.key + ":" + System.identityHashCode(node.key));
//        m_nodeTree.put( node.getKey(), node );
    }

    public void removeNode( AdvancingFrontNode node )
    {
//        System.out.println( "remove:" + node.key + ":" + System.identityHashCode(node.key));
//        m_nodeTree.delete( node.getKey() );
    }

    public AdvancingFrontNode locateNode( TriangulationPoint point )
    {
        // TODO implement tree
//        System.out.println( m_nodeTree );
//        System.out.println( aFront );
//        AdvancingFrontNode node = m_nodeTree.findSmallerOrEqual( point.getX() );
//        return node;
//        System.out.println( "1-locateNode[p,np]=[" + point.getX() + "," + node.point.getX() + "]" );
//        node = aFront.locate( point );
//        System.out.println( "2-locateNode[p,np]=[" + point.getX() + "," + node.point.getX() + "]" );
//        return node;
//        return m_nodeTree.findSmallerOrEqual( point.getX() );
        return aFront.locate( point.getX() );
    }

    public void createAdvancingFront()
    {
        AdvancingFrontNode middle;
        // Initial triangle
        DelaunayTriangle iTriangle = new DelaunayTriangle( _points.get(0), 
                                                           getTail(), 
                                                           getHead() );
//        DelaunayTriangle iTriangle = new DelaunayTriangle( new TriangulationPoint[] { _pointSet.getPoints().get(0), 
//                                                                                      getTail(), 
//                                                                                      getHead() } );
        addToMap( iTriangle );
        aFront = new AdvancingFront();
        
        aFront.head = new AdvancingFrontNode( iTriangle.points[1] );
        aFront.head.triangle = iTriangle;
        middle = new AdvancingFrontNode( iTriangle.points[0] );
        middle.triangle = iTriangle;
        aFront.tail = new AdvancingFrontNode( iTriangle.points[2] );
        aFront.search = middle;
        
        // TODO: I think it would be more intuitive if head is middles next and not previous
        //       so swap head and tail
        aFront.head.next = middle;
        middle.next = aFront.tail;
        middle.prev = aFront.head;
        aFront.tail.prev = middle;
    }
    
    class Basin
    {
        AdvancingFrontNode leftNode;
        AdvancingFrontNode bottomNode;
        AdvancingFrontNode rightNode;
        public double width;
        public boolean leftHighest;        
    }
    
    class EdgeEvent
    {
        DTSweepConstraint constrainedEdge;
        public boolean right;
    }

    /**
     * Try to map a node to all sides of this triangle that don't have 
     * a neighbor.
     * 
     * @param t
     */
    public void mapTriangleToNodes( DelaunayTriangle t )
    {
        AdvancingFrontNode n;
        for( int i=0; i<3; i++ )
        {
            if( t.neighbors[i] == null )
            {
                n = aFront.locatePoint( t.pointCW( t.points[i] ) );
                if( n != null )
                {
                    n.triangle = t;
                }
            }            
        }        
    }

    @Override
    public void prepareTriangulation()
    {
        double xmax, xmin;
        double ymax, ymin;

        long time = System.nanoTime();

        _pointSet.populate( _points );

        xmax = xmin = _points.get(0).getX();
        ymax = ymin = _points.get(0).getY();
        // Calculate bounds. Should be combined with the sorting
        for( TriangulationPoint p : _points )
        {
            if( p.getX() > xmax )
                xmax = p.getX();
            if( p.getX() < xmin )
                xmin = p.getX();
            if( p.getY() > ymax )
                ymax = p.getY();
            if( p.getY() < ymin )
                ymin = p.getY();
        }

        double deltaX = ALPHA * ( xmax - xmin );
        double deltaY = ALPHA * ( ymax - ymin );
        TPoint p1 = new TPoint( xmax + deltaX, ymin - deltaY );
        TPoint p2 = new TPoint( xmin - deltaX, ymin - deltaY );

        setHead( p1 );
        setTail( p2 );

        // Sort the points along y-axis
        Collections.sort( _points, _comparator );
        logger.info( "Triangulation setup [{}ms]", ( System.nanoTime() - time ) / 1e6 );
    }


    public void finalizeTriangulation()
    {
        _pointSet.getTriangles().addAll( _triList );
        _triList.clear();
    }
}