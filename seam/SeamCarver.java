/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.IndexMinPQ;
import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;

public class SeamCarver {

    private Picture pic;
    private boolean transpose = false;
    private double[][] ea;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        pic = new Picture(picture);
        ea = new double[pic.width()][pic.height()];
        for (int row = 0; row < pic.height(); row++) {
            for (int col = 0; col < pic.width(); col++) {
                ea[col][row] = calcEnergy(col, row);
            }
        }
    }

    // current picture
    public Picture picture() {
        return pic;
    }

    // width of current picture
    public int width() {
        if (transpose) return pic.height();
        return pic.width();
    }

    // height of current picture
    public int height() {
        if (transpose) return pic.width();
        return pic.height();
    }

    private double colgradient(int rgb1, int rgb2) {
        int r1 = (rgb1 >> 16) & 0xFF;
        int g1 = (rgb1 >> 8) & 0xFF;
        int b1 = (rgb1 >> 0) & 0xFF;
        int r2 = (rgb2 >> 16) & 0xFF;
        int g2 = (rgb2 >> 8) & 0xFF;
        int b2 = (rgb2 >> 0) & 0xFF;
        int Rdiff = r1 - r2;
        int Gdiff = g1 - g2;
        int Bdiff = b1 - b2;
        double Rsq = 0;
        double Gsq = 0;
        double Bsq = 0;
        if (Rdiff != 0) Rsq = Rdiff * Rdiff;
        if (Gdiff != 0) Gsq = Gdiff * Gdiff;
        if (Bdiff != 0) Bsq = Bdiff * Bdiff;
        return Rsq + Gsq + Bsq;
    }

    private double xgradient(int x, int y) {
        return colgradient(pic.getRGB(x - 1, y), pic.getRGB(x + 1, y));
    }

    private double ygradient(int x, int y) {
        return colgradient(pic.getRGB(x, y - 1), pic.getRGB(x, y + 1));
    }

    private double calcEnergy(int x, int y) {
        if (x == 0 || x == width() - 1 || y == 0 || y == height() - 1)
            return 1000.0; // energy at the border
        return Math.sqrt(xgradient(x, y) + ygradient(x, y));
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x < 0 || y < 0 || x > width() - 1 || y > height() - 1)
            throw new IllegalArgumentException("x or y is out of bound x=" + x + " y=" + y);

        if (transpose) {
            int tmpX = x;
            x = y;
            y = tmpX;
        }

        return ea[x][y];
    }

    private void relax(int x, int y, double[][] distTo, int[][] edgeTo) {
        // StdOut.println("relax " + x + ", " + y);
        // down left
        double dist;
        if (x > 0) {
            // relax(x - 1, y + 1);
            dist = distTo[x][y] + energy(x - 1, y + 1);
            // StdOut.println("distTo " + (x - 1) + ", " + (y + 1) + "=" + dist);
            if (distTo[x - 1][y + 1] > dist) {
                distTo[x - 1][y + 1] = dist;
                edgeTo[x - 1][y + 1] = x;
            }
        }
        // down middle
        dist = distTo[x][y] + energy(x, y + 1);
        // StdOut.println("distTo " + (x) + ", " + (y + 1) + "=" + dist);
        if (distTo[x][y + 1] > dist) {
            distTo[x][y + 1] = dist;
            edgeTo[x][y + 1] = x;
        }
        // down right
        if (x < width() - 1) {
            dist = distTo[x][y] + energy(x + 1, y + 1);
            // StdOut.println("distTo " + (x + 1) + ", " + (y + 1) + "=" + dist);
            if (distTo[x + 1][y + 1] > dist) {
                distTo[x + 1][y + 1] = dist;
                edgeTo[x + 1][y + 1] = x;
            }
        }
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        double[][] distTo = new double[width()][height()];
        int[][] edgeTo = new int[width()][height()];
        IndexMinPQ<Double> mpq = new IndexMinPQ<>(width());

        for (int row = 0; row < height(); row++) {
            for (int col = 0; col < width(); col++) {
                distTo[col][row] = Double.POSITIVE_INFINITY;
            }
        }

        // acyclic shortest paths
        for (int y = 0; y < height(); y++) {
            for (int x = 0; x < width(); x++) {
                if (y == 0) distTo[x][0] = 0.0;
                // insert the weight of the last row into min PQ
                if (y >= height() - 1) mpq.insert(x, distTo[x][y]);
                else relax(x, y, distTo, edgeTo);
            }
        }

        // get the x of the min weight from PQ
        int x = mpq.minIndex();
        int[] seam = new int[height()];
        seam[height() - 1] = x;

        for (int y = height() - 2; y >= 0; y--) {
            // StdOut.println("x=" + x + " y=" + (y + 1) + " edgeTo=" + edgeTo[x][y + 1]);
            seam[y] = edgeTo[x][y + 1];
            x = seam[y];
        }
        return seam;
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        transpose = true;
        int[] seam = findVerticalSeam();
        transpose = false;
        return seam;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
    }

    public void printEnergy() {
        StdOut.println("w=" + width() + " h=" + height());
        transpose = true;
        StdOut.println("w=" + width() + " h=" + height());
        for (int row = 0; row < height(); row++) {
            for (int col = 0; col < width(); col++) {
                StdOut.print("| (" + col + ", " + row + ")");
                StdOut.printf("%9.2f ", energy(col, row));
            }
            StdOut.println();
        }
    }

    //  unit testing (optional)
    public static void main(String[] args) {
        Picture picture = new Picture(args[0]);
        SeamCarver sc = new SeamCarver(picture);
        sc.printEnergy();
        // int[] seam = sc.findVerticalSeam();
        // StdOut.println("vertical seam=" + Arrays.toString(seam));
    }
}
