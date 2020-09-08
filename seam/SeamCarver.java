/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.IndexMinPQ;
import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public class SeamCarver {

    private Picture pic;
    private boolean transpose = false;
    private double[][] ea;
    private int[][] colors;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) throw new IllegalArgumentException("picture is null");

        pic = new Picture(picture);
        colors = new int[pic.width()][pic.height()];
        ea = new double[pic.width()][pic.height()];

        for (int x = 0; x < pic.width(); x++) {
            for (int y = 0; y < pic.height(); y++) {
                colors[x][y] = pic.getRGB(x, y);
            }
        }
        for (int x = 0; x < pic.width(); x++) {
            for (int y = 0; y < pic.height(); y++) {
                ea[x][y] = calcEnergy(x, y);
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
        int rdiff = r1 - r2;
        int gdiff = g1 - g2;
        int bdiff = b1 - b2;
        double rsq = 0;
        double gsq = 0;
        double bsq = 0;
        if (rdiff != 0) rsq = rdiff * rdiff;
        if (gdiff != 0) gsq = gdiff * gdiff;
        if (bdiff != 0) bsq = bdiff * bdiff;
        return rsq + gsq + bsq;
    }

    private double calcEnergy(int x, int y) {
        // because energy is calculated from color table
        int colorWidth = colors.length;
        int colorHeight = colors[0].length;
        if (transpose) {
            colorWidth = colors[0].length;
            colorHeight = colors.length;
        }
        // StdOut.println("CALC ENERGY " + x + ", " + y + " colWidth=" + colorWidth + " colHeight=" + colorHeight);
        if (x == 0 || x == colorWidth - 1 || y == 0 || y == colorHeight - 1)
            return 1000.0; // energy at the border

        // StdOut.print(" XG=" + color(x - 1, y) + "+" + color(x + 1, y));
        // StdOut.print(" YG=" + color(x, y - 1) + "+" + color(x, y + 1));
        double xgradient = colgradient(color(x - 1, y), color(x + 1, y));
        double ygradient = colgradient(color(x, y - 1), color(x, y + 1));
        return Math.sqrt(xgradient + ygradient);
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

    private int color(int x, int y) {
        if (transpose) {
            int tmpX = x;
            x = y;
            y = tmpX;
        }
        // StdOut.println(" col " + x + ", " + y);
        return colors[x][y];
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

    // private void printColors(int[][] cols) {
    //     if (transpose) {
    //         StdOut.println("* COLORS " + cols[0].length + "x" + cols.length);
    //         for (int y = 0; y < cols.length; y++) {
    //             for (int x = 0; x < cols[0].length; x++) {
    //                 StdOut.printf("%9d ", cols[y][x]);
    //             }
    //             StdOut.println();
    //         }
    //     }
    //     else {
    //         StdOut.println("* COLORS " + cols.length + "x" + cols[0].length);
    //         for (int y = 0; y < cols[0].length; y++) {
    //             for (int x = 0; x < cols.length; x++) {
    //                 StdOut.printf("%9d ", cols[x][y]);
    //             }
    //             StdOut.println();
    //         }
    //     }
    // }

    private void updatePic() {
        // StdOut.println("NEW PIC: " + colors.length + "x" + colors[0].length);
        Picture newpic = new Picture(colors.length, colors[0].length);
        for (int x = 0; x < colors.length; x++) {
            for (int y = 0; y < colors[x].length; y++) {
                newpic.setRGB(x, y, colors[x][y]);
            }
        }
        pic = newpic;
        // StdOut.println("* NEW PIC w=" + width() + " h=" + height());
    }

    private int[][] removeSeamColors(int[] seam) {
        int newWidth = width() - 1;
        int[][] newcols = new int[newWidth][height()];
        // printColors(colors);
        // remove the seam from the colors array
        for (int y = 0; y < height(); y++) {
            int px = 0; // x pointer
            for (int x = 0; x < width(); x++) {
                if (seam[y] != x) newcols[px++][y] = color(x, y);
            }
        }
        return newcols;
    }

    private double[][] removeSeamEnergy(int[] seam) {
        int newWidth = width() - 1;
        double[][] newea = new double[newWidth][height()];
        // StdOut.println("NEW energy " + newWidth + "x" + height());
        // recalculate and resize energy table
        for (int y = 0; y < height(); y++) {
            int px = 0; // x pointer
            for (int x = 0; x < width(); x++) {
                if (x == seam[y] - 1 || x == seam[y] + 1) {
                    // StdOut.printf("| RC (" + x + ", " + y + ")");
                    double energy = calcEnergy(px, y);
                    newea[px][y] = energy;
                    px++;
                    // StdOut.printf("=%9.2f", energy);
                }
                else if (x == seam[y]) {
                    continue;
                }
                else {
                    // StdOut.printf("| CP (" + x + ", " + y + ")=%9.2f", energy(x, y));
                    newea[px++][y] = energy(x, y);
                }
            }
            // StdOut.println();
        }
        return newea;
    }

    private void validateSeam(int[] seam, int width, int height) {
        // StdOut.println("validateSeam image w=" + width + " h=" + height);
        if (width <= 1)
            throw new IllegalArgumentException("Image length is less than or equal to 1");
        if (seam.length != height)
            throw new IllegalArgumentException("Seam length is outside of range");
        for (int i = 0; i < seam.length - 1; i++) {
            if (Math.abs(seam[i] - seam[i + 1]) > 1)
                throw new IllegalArgumentException(
                        "Seam two adjacent entries differ by more than 1");
        }
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (seam == null) throw new IllegalArgumentException("seam is null");
        validateSeam(seam, height(), width());

        transpose = true;
        // printColors(colors);
        int[][] newcols = removeSeamColors(seam);
        // transpose back before copying the results into the real arrays
        transpose = false;
        colors = new int[width()][height() - 1]; // reset arrays
        for (int y = 0; y < height() - 1; y++) {
            for (int x = 0; x < width(); x++) {
                colors[x][y] = newcols[y][x];
            }
        }

        transpose = true;
        // printColors(colors);
        // printEnergy(ea);
        double[][] newea = removeSeamEnergy(seam);
        // transpose back before copying the results into the real arrays
        transpose = false;
        ea = new double[width()][height() - 1]; // reset arrays
        for (int y = 0; y < height() - 1; y++) {
            for (int x = 0; x < width(); x++) {
                ea[x][y] = newea[y][x];
            }
        }

        updatePic();
        // printEnergy(ea);
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (seam == null) throw new IllegalArgumentException("seam is null");
        validateSeam(seam, width(), height());

        int[][] newcols = removeSeamColors(seam);
        colors = new int[width() - 1][height()]; // reset arrays
        for (int x = 0; x < width() - 1; x++) {
            colors[x] = newcols[x].clone();
        }

        double[][] newea = removeSeamEnergy(seam);
        ea = new double[width() - 1][height()]; // reset arrays
        for (int x = 0; x < width() - 1; x++) {
            ea[x] = newea[x].clone();
        }

        updatePic(); // called only after colors updates
        // printColors(colors);
        // printEnergy(ea);
    }

    private void printEnergy(double[][] enarr) {
        StdOut.println("* ENERGY " + enarr.length + "x" + enarr[0].length);
        for (int y = 0; y < enarr[0].length; y++) {
            for (int x = 0; x < enarr.length; x++) {
                StdOut.print("| (" + x + ", " + y + ")");
                StdOut.printf("%9.2f ", enarr[x][y]);
            }
            StdOut.println();
        }
    }

    //  unit testing (optional)
    public static void main(String[] args) {
        Picture picture = new Picture(args[0]);
        SeamCarver sc = new SeamCarver(picture);

        // int[] verticalSeam = new int[] { 1, 2, 3, 4, 3 };
        int[] verticalSeam = sc.findVerticalSeam();
        StdOut.println("vertical seam: " + Arrays.toString(verticalSeam));
        sc.removeVerticalSeam(verticalSeam);

        // int[] horizontalSeam = sc.findHorizontalSeam();
        // StdOut.println("\nhorizontal seam: " + Arrays.toString(horizontalSeam));
        // sc.removeHorizontalSeam(horizontalSeam);
    }
}
