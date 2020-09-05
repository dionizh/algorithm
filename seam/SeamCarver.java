/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Picture;

public class SeamCarver {

    private Picture pic;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        pic = new Picture(picture);
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

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x < 0 || x > width() - 1 || y < 0 || y > height())
            throw new IllegalArgumentException("x or y is out of bound");
        if (x == 0 || x == width() - 1 || y == 0 || y == height() - 1)
            return 1000.0; // energy at the border
        return Math.sqrt(xgradient(x, y) + ygradient(x, y));
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        return new int[1];
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        return new int[1];
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
    }

    //  unit testing (optional)
    public static void main(String[] args) {
    }
}
