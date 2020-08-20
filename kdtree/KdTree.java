/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

public class KdTree {

    private Node root = null;
    private int size = 0;

    private double mindist;
    private Point2D nearest;

    private class Node {
        Point2D p;
        Node left = null;
        Node right = null;
        Node parent = null;
        boolean isVertical = true; // either vertical or horizontal
        RectHV rect;

        public Node(Point2D p) {
            this.p = p;
        }
    }

    // construct an empty set of points
    public KdTree() {
    }

    // is the set empty?
    public boolean isEmpty() {
        return root == null;
    }

    // number of points in the set
    public int size() {
        return size;
    }

    private Node insertNode(Node n, Point2D p, boolean isVertical) {
        if (n == null) return new Node(p);

        Point2D parentp = n.p;
        if (p.equals(parentp)) return n;

        if (isVertical) {
            if (p.x() < parentp.x()) {
                n.left = insertNode(n.left, p, !isVertical);
                n.left.rect = new RectHV(n.rect.xmin(), n.rect.ymin(), n.p.x(), n.rect.ymax());
            }
            else {
                n.right = insertNode(n.right, p, !isVertical);
                n.right.rect = new RectHV(n.p.x(), n.rect.ymin(), n.rect.xmax(), n.rect.ymax());
            }
        }
        else { // horizontal
            if (p.y() < parentp.y()) {
                n.left = insertNode(n.left, p, !isVertical);
                n.left.rect = new RectHV(n.rect.xmin(), n.rect.ymin(), n.rect.xmax(), n.p.y());
            }
            else {
                n.right = insertNode(n.right, p, !isVertical);
                n.right.rect = new RectHV(n.rect.xmin(), n.p.y(), n.rect.xmax(), n.rect.ymax());
            }
        }
        return n;
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException("insert: Point2D is null");
        if (root == null) {
            root = new Node(p);
            root.rect = new RectHV(0.0, 0.0, 1.0, 1.0); // the whole unit
        }
        else root = insertNode(root, p, true);
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException("contains: Point2D is null");
        // return ps.contains(p);
        return false;
    }

    private void drawNode(Node n, boolean isVertical) {
        if (n == null) return;

        if (isVertical) {
            Point2D p1 = new Point2D(n.p.x(), n.rect.ymin());
            Point2D p2 = new Point2D(n.p.x(), n.rect.ymax());
            drawLine(p1, p2, isVertical);
        }
        else {
            Point2D p1 = new Point2D(n.rect.xmin(), n.p.y());
            Point2D p2 = new Point2D(n.rect.xmax(), n.p.y());
            drawLine(p1, p2, isVertical);
        }

        StdDraw.setPenRadius(0.01);
        StdDraw.setPenColor(StdDraw.BLACK);
        n.p.draw();
        drawNode(n.left, !isVertical);
        drawNode(n.right, !isVertical);
    }

    private void drawLine(Point2D p1, Point2D p2, boolean isVertical) {
        StdDraw.setPenRadius(0.005);
        if (isVertical) StdDraw.setPenColor(StdDraw.RED);
        else StdDraw.setPenColor(StdDraw.BLUE);
        p1.drawTo(p2);
    }

    // draw all points to standard draw
    public void draw() {
        drawNode(root, true);
    }

    private void rangeSearch(Node n, RectHV rect, Queue<Point2D> q) {
        if (n == null) return;
        if (!rect.intersects(n.rect)) return; // no need to explore the node further
        if (rect.contains(n.p)) q.enqueue(n.p);
        rangeSearch(n.left, rect, q);
        rangeSearch(n.right, rect, q);
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException("range: RectHV is null");
        Queue<Point2D> q = new Queue<>();
        rangeSearch(root, rect, q);
        return q;
    }

    private void nearestSearch(Point2D p, Node n) {
        if (n == null) return;
        if (mindist < n.rect.distanceSquaredTo(p)) return; // no need to explore the node further

        double dist = n.p.distanceSquaredTo(p);
        if (dist < mindist) {
            mindist = dist;
            nearest = n.p;
        }

        // if there are 2 subtrees to go through
        if (n.left != null && n.right != null) {
            // select the one on the same side first
            if (n.left.rect.contains(p)) {
                nearestSearch(p, n.left);
                nearestSearch(p, n.right);
            }
            else {
                nearestSearch(p, n.right);
                nearestSearch(p, n.left);
            }
        }
        else {
            // does not matter which order
            nearestSearch(p, n.left);
            nearestSearch(p, n.right);
        }
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException("nearest: Point2D is null");
        mindist = Double.MAX_VALUE;
        nearestSearch(p, root);
        return nearest;
    }

    public static void main(String[] args) {

    }
}
