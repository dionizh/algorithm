import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public class BruteCollinearPoints {
    private LineSegment[] ls;
    private int count = 0;
    private Point[] points;
    private SegmentInfo[] sinfo;
    private int sinfoCount = 0;
    private int segcount = 0;

    private class SegmentInfo {
        Point ref = null;
        Point end = null;
        double slope;
    }

    // finds all line segments containing 4 points
    public BruteCollinearPoints(Point[] points) {
        if (points == null) {
            throw new IllegalArgumentException("Points are null");
        }
        ls = new LineSegment[points.length];
        sinfo = new SegmentInfo[points.length];

        // Arrays.sort(points, Point.BY_AXIS);

        // System.out.println("Sorted points:");
        // for (int i = 0; i < points.length; i++) {
        //     System.out.println(points[i].toString());
        // }
        this.points = points;

        for (int i = 0; i < points.length; i++) {
            if (points[i] == null) throw new IllegalArgumentException("Point " + i + " is null");
            // and if contains a repeated point
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

                    for (int l = 0; l < points.length; l++) {
                        if (l == k || l == j || l == i) continue;
                        Point p3 = points[l];
                        double slope3 = refPoint.slopeTo(p3);

                        if (slope1 == slope2 && slope2 == slope3) {
                            // System.out.printf(" COLLINEAR %s %s %s %s\n", refPoint.toString(),
                            //                   p1.toString(), p2.toString(), p3.toString());

                            int memcount = 4; // at least 4 points to become a segment
                            // extra point, just check one more as max is 5
                            Point p4 = null;
                            for (int m = 0; m < points.length; m++) {
                                if (m == l || m == k || m == j || m == i) continue;
                                if (refPoint.slopeTo(points[m]) == slope1) {
                                    p4 = points[m];
                                    memcount++;
                                    // System.out.println("Extra point: " + p4.toString());
                                    break;
                                }
                            }

                            Point[] segmems = new Point[memcount];
                            segmems[0] = refPoint;
                            segmems[1] = p1;
                            segmems[2] = p2;
                            segmems[3] = p3;
                            if (memcount == 5) segmems[4] = p4;

                            addSegmentMems(segmems);
                        }
                    }
                }
            }
        }

        // add the actual line segments
        for (int i = 0; i < sinfoCount; i++) {
            Point ref = sinfo[i].ref;
            Point end = sinfo[i].end;
            ls[count++] = new LineSegment(ref, end);
            // System.out.printf("ADD SEGMENT a %s b %s count %s\n", ref, end, count);
        }
    }

    private void addSegmentMems(Point[] segmems) {
        Point refPoint = segmems[0];
        Arrays.sort(segmems, Point.BY_AXIS);

        // only consider if refpoint is the outermost
        if (refPoint == segmems[0]) {
            boolean exists = false;
            Point endPoint = segmems[segmems.length - 1];
            for (int i = 0; i < sinfoCount; ++i) {
                if (sinfo[i].ref.toString().equals(refPoint.toString()) &&
                        sinfo[i].end.toString().equals(endPoint.toString())) {
                    exists = true;
                    break;
                }
            }
            // only add if not already added
            if (!exists) {
                SegmentInfo si = new SegmentInfo();
                si.ref = refPoint;
                si.end = endPoint;
                sinfo[sinfoCount++] = si;

                // System.out.println("Sorted segmems:");
                // for (int i = 0; i < segmems.length; i++) {
                //     System.out.print(segmems[i].toString());
                // }
                // System.out.println("");
                //
                // System.out.println("* ADD to sinfo " + refPoint.toString() + endPoint.toString());
            }
        }
    }

    // private void addSegment(int min, int max, double slope) {
    //     boolean exists = false;
    //     for (int i = 0; i < sinfoCount; i++) {
    //         if (sinfo[i].max == max && sinfo[i].min == min) {
    //             exists = true;
    //             continue;
    //         }
    //         if (sinfo[i].min == min) {
    //             if (sinfo[i].max < max) {
    //                 System.out
    //                         .printf("Update sinfo min: %s max: %s -> %s\n", min, sinfo[i].max, max);
    //                 sinfo[i].max = max;
    //             }
    //             exists = true;
    //         }
    //         if (sinfo[i].max == max) {
    //             if (sinfo[i].min > min) {
    //                 System.out
    //                         .printf("Update sinfo min: %s -> %s max: %s\n", sinfo[i].min, min, max);
    //                 sinfo[i].min = min;
    //             }
    //             exists = true;
    //         }
    //         if (slope == sinfo[i].slope) exists = true;
    //     }
    //     if (!exists) {
    //         System.out.printf("Add to sinfo: %s %s\n", min, max);
    //         SegmentInfo s = new SegmentInfo();
    //         s.min = min;
    //         s.max = max;
    //         s.slope = slope;
    //         sinfo[sinfoCount++] = s;
    //     }
    // }

    // the number of line segments
    public int numberOfSegments() {
        return count;
    }

    // the line segments
    public LineSegment[] segments() {
        return ls;
    }

    public static void main(String[] args) {

        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);

        // print and draw the line segments
        StdDraw.setPenRadius(0.01);
        StdDraw.setPenColor(StdDraw.MAGENTA);
        // FastCollinearPoints collinear = new FastCollinearPoints(points);
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            if (segment != null) {
                StdOut.println(segment);
                segment.draw();
            }
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
