import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.InputMismatchException;

public class BruteCollinearPoints {
    private final LineSegment[] ls;
    private int count = 0;

    // finds all line segments containing 4 points
    public BruteCollinearPoints(Point[] points) {
        if (points == null) {
            throw new IllegalArgumentException("Points are null");
        }

        ls = new LineSegment[points.length * 2];

        // check null
        for (int i = 0; i < points.length; i++) {
            if (points[i] == null)
                throw new IllegalArgumentException("Point " + i + " is null");
            // System.out.println(points[i].toString());
        }

        for (int i = 0; i < points.length; i++) {
            // check duplicates
            if (i > 0) {
                for (int d = 0; d <= i - 1; d++) {
                    if (points[d].slopeTo(points[i]) == Double.NEGATIVE_INFINITY) {
                        throw new IllegalArgumentException(
                                "Duplicate point: " + points[i].toString());
                    }
                }
            }

            Point refPoint = points[i];
            // System.out.println("\nREF point " + refPoint.toString());

            for (int j = 0; j < points.length; j++) {
                if (j == i) continue;
                Point p1 = points[j];
                double slope1 = refPoint.slopeTo(p1);

                for (int k = 0; k < points.length; k++) {
                    if (k == i || k == j) continue;
                    Point p2 = points[k];
                    double slope2 = refPoint.slopeTo(p2);

                    for (int lp = 0; lp < points.length; lp++) {
                        if (lp == k || lp == j || lp == i) continue;
                        Point p3 = points[lp];
                        double slope3 = refPoint.slopeTo(p3);

                        if (slope1 == slope2 && slope2 == slope3) {
                            // System.out.printf(" COLLINEAR %s %s %s %s\n", refPoint.toString(),
                            //                   p1.toString(), p2.toString(), p3.toString());

                            Point[] segmems = { refPoint, p1, p2, p3 };
                            addSegmentMems(segmems);
                        }
                    }
                }
            }
        }
    }

    private void addSegmentMems(Point[] segmems) {
        for (int i = 0; i < segmems.length - 1; i++) {
            if (segmems[i].compareTo(segmems[i + 1]) > 0) {
                // System.out.println("Not in ascending order, skip!");
                return;
            }
        }

        // System.out.printf("UNSorted segmems: ");
        // for (int i = 0; i < segmems.length; i++) {
        //     System.out.print(segmems[i].toString());
        // }
        // System.out.println("");

        Point refPoint = segmems[0];
        // Arrays.sort(segmems);

        // only consider if refpoint is the outermost
        if (refPoint == segmems[0]) {
            Point endPoint = segmems[segmems.length - 1];
            ls[count++] = new LineSegment(refPoint, endPoint);
            // System.out.printf("ADD SEGMENT a %s b %s count %s\n", refPoint, endPoint, count);
        }
    }

    // the number of line segments
    public int numberOfSegments() {
        return count;
    }

    // the line segments
    public LineSegment[] segments() {
        LineSegment[] copy = new LineSegment[count];
        for (int i = 0; i < count; i++) {
            if (ls[i] != null) copy[i] = ls[i];
        }
        return copy;
        // return ls.clone();
    }

    public static void main(String[] args) {

        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            try {
                int x = in.readInt();
                int y = in.readInt();
                points[i] = new Point(x, y);
            }
            catch (IllegalArgumentException | InputMismatchException e) {
                points[i] = null;
            }
        }

        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);

        // print and draw the line segments
        StdDraw.setPenRadius(0.01);
        StdDraw.setPenColor(StdDraw.MAGENTA);
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        // int i = 0;
        for (LineSegment segment : collinear.segments()) {
            // System.out.println("SEGMENT " + i + ": " + segment);
            if (segment != null) {
                StdOut.println(segment);
                segment.draw();
            }
            // i++;
        }
        StdDraw.show();

        // draw the points
        StdDraw.setPenRadius(0.02);
        StdDraw.setPenColor(StdDraw.BLUE);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();
    }
}
