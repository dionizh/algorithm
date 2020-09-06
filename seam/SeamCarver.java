/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.IndexMinPQ;
import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

public class SeamCarver {

    private Picture pic;
    private double[][] ea;

    Stack<Integer> reversePost;
    boolean[][] marked;

    double[][] distTo;
    int[][] edgeTo;

    IndexMinPQ<Double> mpq;

    // private class Pixel {
    //     int x;
    //     int y;
    //
    //     public Pixel(int x, int y) {
    //         this.x = x;
    //         this.y = y;
    //     }
    // }

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
        return pic.width();
    }

    // height of current picture
    public int height() {
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
        if (x < 0 || x > width() - 1 || y < 0 || y > height())
            throw new IllegalArgumentException("x or y is out of bound");
        return ea[x][y];
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        return new int[1];
    }

    private int v(int x, int y) {
        return y * width() + x;
    }

    private void dfs(int x, int y) {
        // StdOut.println("mark " + x + " and " + y);
        marked[x][y] = true;
        // check down left, middle and right
        if (y < height() - 1) {
            if (x > 0 && !marked[x - 1][y + 1]) dfs(x - 1, y + 1);
            if (!marked[x][y + 1]) dfs(x, y + 1);
            if (x < width() - 1 && !marked[x + 1][y + 1]) dfs(x + 1, y + 1);
        }
        int v = y * width() + x;
        reversePost.push(v);
    }

    private void topologicalOrder() {
        reversePost = new Stack<Integer>();
        marked = new boolean[width()][height()];
        for (int y = 0; y < height(); y++) {
            for (int x = 0; x < width(); x++) {
                if (!marked[x][y]) dfs(x, y);
            }
        }
        // while (!reversePost.isEmpty()) {
        //     int v = reversePost.pop();
        //     StdOut.println("v=" + v + " x=" + (v / width()) + " y=" + (v % width()));
        // }
    }

    private void relax(int x, int y) {
        if (y >= height() - 1) return;
        // StdOut.println("relax " + x + ", " + y);
        // down left
        double dist;
        if (x > 0) {
            // relax(x - 1, y + 1);
            dist = distTo[x][y] + ea[x - 1][y + 1];
            // StdOut.println("distTo " + (x - 1) + ", " + (y + 1) + "=" + dist);
            if (distTo[x - 1][y + 1] > dist) {
                distTo[x - 1][y + 1] = dist;
                edgeTo[x - 1][y + 1] = x;
            }
        }
        // down middle
        dist = distTo[x][y] + ea[x][y + 1];
        // StdOut.println("distTo " + (x) + ", " + (y + 1) + "=" + dist);
        if (distTo[x][y + 1] > dist) {
            distTo[x][y + 1] = dist;
            edgeTo[x][y + 1] = x;
        }
        // down right
        if (x < width() - 1) {
            dist = distTo[x][y] + ea[x + 1][y + 1];
            // StdOut.println("distTo " + (x + 1) + ", " + (y + 1) + "=" + dist);
            if (distTo[x + 1][y + 1] > dist) {
                distTo[x + 1][y + 1] = dist;
                edgeTo[x + 1][y + 1] = x;
            }
        }
    }

    private void acyclicSP() {
        mpq = new IndexMinPQ<>(width());
        distTo = new double[width()][height()];
        edgeTo = new int[width()][height()];

        for (int row = 0; row < pic.height(); row++) {
            for (int col = 0; col < pic.width(); col++) {
                distTo[col][row] = Double.POSITIVE_INFINITY;
            }
        }

        for (int y = 0; y < height(); y++) {
            for (int x = 0; x < width(); x++) {
                if (y == 0) distTo[x][0] = 0.0;
                if (y >= height() - 1) mpq.insert(x, distTo[x][y]);
                relax(x, y);
            }
        }

        // topologicalOrder();
        // StdOut.println("Topological order:");
        // for (int v : reversePost) {
        //     StdOut.printf("%d ", v);
        //     int x = v % width();
        //     int y = v / width();
        //     relax(x, y);
        // }
        // StdOut.println();
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        acyclicSP();
        // StdOut.println("* vertical seam");
        // for (int row = 0; row < height(); row++) {
        //     for (int col = 0; col < width(); col++) {
        //         StdOut.printf("%9.2f ", distTo[col][row]);
        //     }
        //     StdOut.println();
        // }
        // double shortest = Double.POSITIVE_INFINITY;
        // int spx = 0;
        // for (int x = 0; x < width(); x++) {
        //     if (distTo[x][height() - 1] < shortest) {
        //         shortest = distTo[x][height() - 1];
        //         spx = x;
        //     }
        // }
        // StdOut.println(
        //         "min idx=" + mpq.minIndex() + " key=" + mpq.minKey() + " size=" + mpq.size());

        int[] seam = new int[height()];
        int x = mpq.minIndex();
        seam[height() - 1] = x;

        for (int y = height() - 2; y >= 0; y--) {
            // StdOut.println("x=" + x + " y=" + (y + 1) + " edgeTo=" + edgeTo[x][y + 1]);
            seam[y] = edgeTo[x][y + 1];
            x = seam[y];
        }
        return seam;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
    }

    public void printEnergy() {
        for (int row = 0; row < height(); row++) {
            for (int col = 0; col < width(); col++)
                StdOut.printf("%9.2f ", ea[col][row]);
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
