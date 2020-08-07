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

    private class SegmentInfo {
        int min;
        int max;
        double slope;
    }

    // finds all line segments containing 4 points
    public BruteCollinearPoints(Point[] points) {
        if (points == null) {
            throw new IllegalArgumentException("Points are null");
        }
        ls = new LineSegment[points.length];
        sinfo = new SegmentInfo[points.length];

        Arrays.sort(points, Point.BY_AXIS);

        System.out.println("Sorted points:");
        for (int i = 0; i < points.length; i++) {
            System.out.println(points[i].toString());
        }
        this.points = points;

        for (int i = 0; i < points.length - 3; i++) {
            if (points[i] == null) throw new IllegalArgumentException("Point " + i + " is null");
            // and if contains a repeated point

            for (int j = i + 1; j < points.length - 2; j++) {
                if (j == i) continue;
                double slope1 = points[i].slopeTo(points[j]);

                for (int k = i + 2; k < points.length - 1; k++) {
                    if (k == j) continue;
                    double slope2 = points[i].slopeTo(points[k]);

                    for (int l = i + 3; l < points.length; l++) {
                        if (l == k) continue;
                        double slope3 = points[i].slopeTo(points[l]);

                        // System.out.printf(
                        //         "i: %s point1: %s point2: %s point3: %s point4: %s\n",
                        //         i, points[i].toString(), points[j].toString(),
                        //         points[k].toString(), points[l].toString());
                        // System.out
                        //         .printf("i: %s slope1: %s slope2: %s slope3: %s\n",
                        //                 i, slope1, slope2, slope3);
                        if (slope1 == slope2 && slope2 == slope3) {
                            // System.out.printf("Potential segment i %s j %s k %s l %s\n",
                            //                   i, j, k, l);
                            addSegment(i, l, slope1);
                        }
                    }
                }
            }
        }

        // add the actual line segments
        for (int i = 0; i < sinfoCount; i++) {
            int a = sinfo[i].min;
            int b = sinfo[i].max;
            ls[count++] = new LineSegment(this.points[a], this.points[b]);
            System.out.printf("ADD SEGMENT a %s b %s count %s\n", a, b, count);
        }
    }

    private void addSegment(int min, int max, double slope) {
        boolean exists = false;
        for (int i = 0; i < sinfoCount; i++) {
            if (sinfo[i].max == max && sinfo[i].min == min) {
                exists = true;
                continue;
            }
            if (sinfo[i].min == min) {
                if (sinfo[i].max < max) {
                    System.out
                            .printf("Update sinfo min: %s max: %s -> %s\n", min, sinfo[i].max, max);
                    sinfo[i].max = max;
                }
                exists = true;
            }
            if (sinfo[i].max == max) {
                if (sinfo[i].min > min) {
                    System.out
                            .printf("Update sinfo min: %s -> %s max: %s\n", sinfo[i].min, min, max);
                    sinfo[i].min = min;
                }
                exists = true;
            }
            if (slope == sinfo[i].slope) exists = true;
        }
        if (!exists) {
            System.out.printf("Add to sinfo: %s %s\n", min, max);
            SegmentInfo s = new SegmentInfo();
            s.min = min;
            s.max = max;
            s.slope = slope;
            sinfo[sinfoCount++] = s;
        }
    }

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
