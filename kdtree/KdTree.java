/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

public class KdTree {

    private Node root = null;
    private int size = 0;

    private double mindist;
    private Point2D nearest;

    private static class Node {
        Point2D p;
        Node left = null;
        Node right = null;
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
        if (n == null) {
            size++;
            return new Node(p);
        }

        Point2D parentp = n.p;
        if (p.equals(parentp)) return n;

        double parentx = n.p.x();
        double parenty = n.p.y();

        if (isVertical) {
            if (p.x() < parentx) {
                Node tmp = n.left;
                n.left = insertNode(n.left, p, !isVertical);
                if (n.left != tmp)
                    n.left.rect = new RectHV(n.rect.xmin(), n.rect.ymin(), parentx, n.rect.ymax());
            }
            else {
                Node tmp = n.right;
                n.right = insertNode(n.right, p, !isVertical);
                if (n.right != tmp)
                    n.right.rect = new RectHV(parentx, n.rect.ymin(), n.rect.xmax(), n.rect.ymax());
            }
        }
        else { // horizontal
            if (p.y() < parenty) {
                Node tmp = n.left;
                n.left = insertNode(n.left, p, !isVertical);
                if (n.left != tmp)
                    n.left.rect = new RectHV(n.rect.xmin(), n.rect.ymin(), n.rect.xmax(), parenty);
            }
            else {
                Node tmp = n.right;
                n.right = insertNode(n.right, p, !isVertical);
                if (n.right != tmp)
                    n.right.rect = new RectHV(n.rect.xmin(), parenty, n.rect.xmax(), n.rect.ymax());
            }
        }
        return n;
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException("insert: Point2D is null");
        if (root == null) {
            root = new Node(p);
            size++;
            root.rect = new RectHV(0.0, 0.0, 1.0, 1.0); // the whole unit
        }
        else root = insertNode(root, p, true);
    }

    private boolean containSearch(Node n, Point2D qp, boolean isVertical) {
        if (n == null) return false;
        // System.out.println("check " + n.p.toString() + ": " + n.p.equals(qp));
        if (n.p.equals(qp)) return true;

        if (isVertical) {
            if (qp.x() < n.p.x()) return containSearch(n.left, qp, !isVertical);
            else return containSearch(n.right, qp, !isVertical);
        }
        else {
            if (qp.y() < n.p.y()) return containSearch(n.left, qp, !isVertical);
            else return containSearch(n.right, qp, !isVertical);
        }
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException("contains: Point2D is null");
        return containSearch(root, p, true);
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

        // StdDraw.setPenRadius(0.01);
        StdDraw.setPenColor(StdDraw.BLACK);
        n.p.draw();
        drawNode(n.left, !isVertical);
        drawNode(n.right, !isVertical);
    }

    private void drawLine(Point2D p1, Point2D p2, boolean isVertical) {
        // StdDraw.setPenRadius(0.005);
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
        // System.out.println("dist to rect " + n.p.toString() + ":" + n.rect.distanceSquaredTo(p));
        if (mindist < n.rect.distanceSquaredTo(p)) return; // no need to explore the node further

        // System.out.println("***** CHECK NODE " + n.p.toString() + " mindist:" + mindist);
        // System.out.println("dist to pnt " + n.p.toString());
        double dist = n.p.distanceSquaredTo(p);
        if (dist < mindist) {
            mindist = dist;
            nearest = n.p;
        }

        // if there are 2 subtrees to go through
        if (n.left != null && n.right != null) {
            // System.out.println(
            //         "left rect contains " + p.toString() + ":" + n.left.rect.contains(p));
            // System.out.println(
            //         "right rect contains " + p.toString() + ":" + n.right.rect.contains(p));
            // select the one on the same side first
            if (n.left.rect.contains(p)) {
                nearestSearch(p, n.left);
                nearestSearch(p, n.right);
            }
            else if (n.right.rect.contains(p)) {
                nearestSearch(p, n.right);
                nearestSearch(p, n.left);
            }
            else {
                // if neither contains the point, check the distance to the rect
                // and check the closest one first
                double leftrectdist = n.left.rect.distanceSquaredTo(p);
                double rightrectdist = n.right.rect.distanceSquaredTo(p);

                if (leftrectdist < rightrectdist) {
                    nearestSearch(p, n.left);
                    nearestSearch(p, n.right);
                }
                else {
                    nearestSearch(p, n.right);
                    nearestSearch(p, n.left);
                }
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
        mindist = Double.POSITIVE_INFINITY;
        nearestSearch(p, root);
        return nearest;
    }

    public static void main(String[] args) {
        // initialize the two data structures with point from file
        String filename = args[0];
        In in = new In(filename);
        KdTree kdtree = new KdTree();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            kdtree.insert(p);
        }

        Point2D p = new Point2D(0.73, 0.43);
        System.out.println("nearest " + kdtree.nearest(p));

        System.out.println("size: " + kdtree.size());

        // StdDraw.enableDoubleBuffering();
        // while (true) {
        //     kdtree.draw();
        //     StdDraw.show();
        //     StdDraw.pause(40);
        // }
    }
}
