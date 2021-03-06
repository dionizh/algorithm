import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;
import java.util.InputMismatchException;

public class FastCollinearPoints {
    private LineSegment[] ls;
    private int segcount = 0;

    // finds all line segments containing 4 or more points
    public FastCollinearPoints(Point[] points) {
        if (points == null) {
            throw new IllegalArgumentException("Points are null");
        }
        // check null
        for (int i = 0; i < points.length; i++) {
            if (points[i] == null)
                throw new IllegalArgumentException("Point " + i + " is null");
        }

        // check duplicates
        for (int p = 0; p < points.length; p++) {
            if (p > 0) {
                for (int d = 0; d <= p - 1; d++) {
                    // the same points
                    if (points[d].slopeTo(points[p]) == Double.NEGATIVE_INFINITY) {
                        throw new IllegalArgumentException(
                                "Duplicate point: " + points[p].toString());
                    }
                }
            }
        }

        if (points.length < 4) return;

        // worst case is quadratic according to FAQ!
        ls = new LineSegment[points.length * points.length];

        // For each other point q, determine the slope it makes with p.
        // Sort the points according to the slopes they makes with p.
        for (int p = 0; p < points.length; p++) {
            Point refPoint = points[p];
            // System.out.println("\nREF point " + refPoint.toString());

            // initialize sortedPoints without refPoint
            int sortsize = points.length - 1; // points.length - (p + 1);
            Point[] sortPoints = new Point[sortsize];
            int count = 0;
            for (int i = 0; i < points.length; i++) {
                if (i != p) {
                    sortPoints[count++] = points[i];
                }
            }

            Arrays.sort(sortPoints, refPoint.slopeOrder());
            // System.out.println("Sorted points:");
            // for (int i = 0; i < sortPoints.length; i++) {
            //     System.out.print(sortPoints[i].toString());
            // }
            // System.out.println("");

            // Check if any 3 (or more) adjacent points in the sorted order have equal slopes
            // with respect to p. If so, these points, together with p, are collinear.
            int q = 0;
            double slope1;
            for (q = 0; q < sortPoints.length - 2; q++) {
                Point p1 = sortPoints[q];
                Point p2 = sortPoints[q + 1];
                Point p3 = sortPoints[q + 2];
                slope1 = refPoint.slopeTo(p1);
                double slope2 = refPoint.slopeTo(p2);
                double slope3 = refPoint.slopeTo(p3);
                // System.out.printf("check slope idx %s %s | %s %s | %s %s\n",
                //                   q, slope1, q + 1, slope2, q + 2, slope3);
                if (slope1 == slope2 && slope2 == slope3) {
                    // System.out.printf("COLLINEAR %s %s %s %s\n", refPoint.toString(),
                    //                   p1.toString(), p2.toString(), p3.toString());
                    // System.out.printf("SLOPE %s %s %s\n", slope, refPoint.slopeTo(sortPoints[q + 1]), refPoint.slopeTo(sortPoints[q + 2]));

                    // extra points
                    int memcount = 4; // at least 4 points to become a segment
                    int r = q + 3;
                    while (r < sortPoints.length) {
                        if (refPoint.slopeTo(sortPoints[r]) != slope1) {
                            break;
                        }
                        // System.out.println("Extra point " + sortPoints[r].toString());
                        r++;
                        memcount++;
                    }

                    Point[] segmems = new Point[memcount]; // +1 for the refPoint
                    segmems[0] = refPoint;
                    int memidx = 1;
                    for (int i = q; i < r; i++) {
                        segmems[memidx++] = sortPoints[i];
                    }

                    addSegmentMems(segmems, memcount, refPoint);

                    // once a collinear is found, we can skip ahead
                    q = r - 1;
                }
            }
        }
    }

    private void addSegmentMems(Point[] segmems, int memcount, Point refPoint) {
        // POTENTIAL OPTIMIZATION
        Arrays.sort(segmems);
        // System.out.println("Sorted segmems:");
        // for (int i = 0; i < segmems.length; i++) {
        //     System.out.print(segmems[i].toString());
        // }
        // System.out.println("");

        // only add if ref point is the min the line
        // else ignore it
        Point endPoint = segmems[memcount - 1];
        if (refPoint.slopeTo(segmems[0]) == Double.NEGATIVE_INFINITY) {
            // add the line segment
            ls[segcount++] = new LineSegment(refPoint, endPoint);
            // System.out.printf("LINE SEGMENT %s --> %s segcount %s\n",
            //                   segmems[0].toString(), endPoint.toString(), segcount);
        }
    }

    // the number of line segments
    public int numberOfSegments() {
        return segcount;
    }

    // the line segments
    public LineSegment[] segments() {
        LineSegment[] copy = new LineSegment[segcount];
        for (int i = 0; i < segcount; i++) {
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
        StdDraw.setPenRadius(0.005);
        StdDraw.setPenColor(StdDraw.PRINCETON_ORANGE);
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            if (segment != null) {
                StdOut.println(segment);
                segment.draw();
            }
        }
        StdDraw.show();

        // draw the points
        StdDraw.setPenRadius(0.01);
        StdDraw.setPenColor(StdDraw.BOOK_RED);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        System.out.println("Total segments:" + collinear.numberOfSegments());
    }
}
