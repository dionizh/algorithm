/* *****************************************************************************
 *  Name:              Ada Lovelace
 *  Coursera User ID:  123456
 *  Last modified:     October 16, 1842
 **************************************************************************** */

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private final WeightedQuickUnionUF wquf;
    private final int n;
    private final boolean[] openSites;
    private int topSiteID;
    private int bottomSiteID;
    private int openSiteCount = 0;

    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int startN) {
        n = startN;
        if (n <= 0) {
            throw new IllegalArgumentException("n value must be > 0 (" + n + ")");
        }
        // the 2 extra elements are for the top and bottom "virtual" sites
        wquf = new WeightedQuickUnionUF(n * n + 2);
        // initial ID is its own element index
        topSiteID = n * n;
        bottomSiteID = n * n + 1;
        openSites = new boolean[n * n + 2];
    }

    private void validateRowCol(int row, int col) {
        if (row < 1 || row > n) {
            throw new IllegalArgumentException("Row value out of range (" + row + ")" + " n=" + n);
        }
        if (col < 1 || col > n) {
            throw new IllegalArgumentException("Col value out of range (" + col + ")" + " n=" + n);
        }
    }

    private int findElement(int row, int col) {
        return ((row - 1) * n + (col - 1));
    }

    // if any adjacent is open, do a union
    private void unionAdjacent(int row, int col, int el) {
        // check left
        if (col - 1 > 0) {
            if (isOpen(row, col - 1)) {
                wquf.union(el, el - 1);
            }
        }
        // check right
        if (col + 1 <= n) {
            if (isOpen(row, col + 1)) {
                wquf.union(el, el + 1);
            }
        }
        // check top
        if (row - 1 > 0) {
            if (isOpen(row - 1, col)) {
                wquf.union(el, el - n);
            }
        }
        // check bottom
        if (row + 1 <= n) {
            if (isOpen(row + 1, col)) {
                wquf.union(el, el + n);
            }
        }
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        validateRowCol(row, col);
        int el = findElement(row, col);
        // System.out.println("Open " + row + ", " + col + " | el=" + el);
        int elID = wquf.find(el);
        if (!openSites[elID]) {
            openSites[elID] = true;
            openSiteCount++;
            unionAdjacent(row, col, el);

            // if it's a top row element, union it to the virtual topSite
            if (row == 1 && !openSites[topSiteID]) {
                wquf.union(el, (n * n));
                // to handle data type that sets root nondeterministically (can change)
                //topSiteID = wquf.find(n * n);
            }
            // if it's a bottom row element, union it to the virtual bottomSite
            if (row == n && !openSites[bottomSiteID]) {
                wquf.union(el, (n * n + 1));
                // to handle data type that sets root nondeterministically
                //bottomSiteID = wquf.find(n * n + 1);
            }
        }
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        validateRowCol(row, col);
        int el = findElement(row, col);
        return openSites[wquf.find(el)];
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        if (!isOpen(row, col)) {
            return false;
        }
        validateRowCol(row, col);
        int el = findElement(row, col);
        // full when it's in the same set with the virtual topSite
        if (wquf.find(el) == wquf.find(n * n)) {
            return true;
        }
        return false;
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return openSiteCount;
    }

    // does the system percolate?
    public boolean percolates() {
        // percolates if the virtual topSite is in the same set as the bottomSite
        if (wquf.find(n * n) == wquf.find(n * n + 1)) {
            //if (topSiteID == bottomSiteID) {
            return true;
        }
        return false;
    }

    // test client (optional)
    // public static void main(String[] args) {
    //     In in = new In(args[0]);      // input file
    //     int n = in.readInt();         // n-by-n percolation system
    //
    //     // repeatedly read in sites to open and draw resulting system
    //     Percolation perc = new Percolation(n);
    //     while (!in.isEmpty()) {
    //         int i = in.readInt();
    //         int j = in.readInt();
    //         System.out.println("open(" + i + ", " + j + ")");
    //         perc.open(i, j);
    //         System.out.println("numberOfOpenSites()=" + perc.numberOfOpenSites());
    //     }
    // }
}
