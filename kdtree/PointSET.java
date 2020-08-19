/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;

public class PointSET {
    private SET<Point2D> ps;

    // construct an empty set of points
    public PointSET() {
        ps = new SET<>();
    }

    // is the set empty?
    public boolean isEmpty() {
        return ps.isEmpty();
    }

    // number of points in the set
    public int size() {
        return ps.size();
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException("insert: Point2D is null");
        ps.add(p);
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException("contains: Point2D is null");
        return ps.contains(p);
    }

    // draw all points to standard draw
    public void draw() {
        // draw the points
        // StdDraw.setPenRadius(0.02);
        // StdDraw.setPenColor(StdDraw.BOOK_LIGHT_BLUE);
        for (Point2D p : ps) {
            p.draw();
        }
        // StdDraw.show();
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException("range: RectHV is null");
        // check all Point2D whether it's inside rect
        Queue<Point2D> q = new Queue<>();
        for (Point2D p : ps) {
            if (p.x() >= rect.xmin() && p.x() <= rect.xmax() &&
                    p.y() >= rect.ymin() && p.y() <= rect.ymax())
                q.enqueue(p);
        }
        return q;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException("nearest: Point2D is null");
        double mindist = Double.MAX_VALUE;
        Point2D nearest = null;
        Queue<Point2D> q = new Queue<>();
        for (Point2D itp : ps) {
            double sqdist = p.distanceSquaredTo(itp);
            if (sqdist < mindist) {
                mindist = sqdist;
                nearest = itp;
            }
        }
        return nearest;
    }

    public static void main(String[] args) {

    }
}
