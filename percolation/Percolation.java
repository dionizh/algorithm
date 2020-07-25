/* *****************************************************************************
 *  Name:              Ada Lovelace
 *  Coursera User ID:  123456
 *  Last modified:     October 16, 1842
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private final WeightedQuickUnionUF wquf;
    private final int n;
    private final boolean[] openSites;
    private int openSiteCount = 0;

    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int startN) {
        n = startN;
        if (n <= 0) {
            throw new IllegalArgumentException("n value must be > 0 (" + n + ")");
        }
        wquf = new WeightedQuickUnionUF(n * n);
        openSites = new boolean[n * n];
        for (int i = 0; i < (n * n); ++i) {
            openSites[i] = false;
        }
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

    // private void printElementIDs() {
    //     for (int i = 0; i < (n * n); ++i) {
    //         System.out.print(wquf.find(i) + " ");
    //     }
    //     System.out.println();
    // }

    private boolean adjustOpenSites() {
        // for all elements
        // if any adjacent element is open but with different ID, union
        // System.out.println("Adjust open sites..");
        boolean adjusting = false;
        for (int row = 1; row <= n; ++row) {
            for (int col = 1; col <= n; ++col) {
                int el1 = findElement(row, col);
                int el1ID = wquf.find(el1);
                // check the right side
                if (col < n) {
                    if (isOpen(row, col) && isOpen(row, col + 1)) {
                        int el2 = findElement(row, col + 1);
                        if (el1ID != wquf.find(el2)) {
                            wquf.union(el1, el2);
                            adjusting = true;
                        }
                    }
                }
                // check the bottom
                if (row < n) {
                    if (isOpen(row, col) && isOpen(row + 1, col)) {
                        int el2 = findElement(row + 1, col);
                        if (el1ID != wquf.find(el2)) {
                            wquf.union(el1, el2);
                            adjusting = true;
                        }
                    }
                }
            }
        }
        return adjusting;
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        validateRowCol(row, col);
        int el = findElement(row, col);
        // System.out.println("Open " + row + ", " + col + " | el=" + el);

        openSites[wquf.find(el)] = true;
        openSiteCount++;

        boolean adjustment = false;
        do {
            adjustment = adjustOpenSites();
        } while (adjustment);
        // printElementIDs();
        // System.out.println(
        //         "Number of sets: " + wquf.count() + " " + Arrays.toString(openSites.toArray()));
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        validateRowCol(row, col);
        int el = findElement(row, col);
        return (isElementOpen(el));
    }

    private boolean isElementOpen(int elidx) {
        return openSites[wquf.find(elidx)];
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        if (!isOpen(row, col)) {
            return false;
        }
        validateRowCol(row, col);
        int el = findElement(row, col);
        // full when it's in the same set with any top row element
        for (int i = 0; i < n; ++i) {
            if (wquf.find(i) == wquf.find(el)) {
                return true;
            }
        }
        return false;
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return openSiteCount;
    }

    // does the system percolate?
    public boolean percolates() {
        // percolates if any element in the top row is in the same set as any
        // element in the bottom row
        for (int i = 0; i < n; ++i) {
            int topid = wquf.find(i);
            // only bother to check if top element is open
            if (isElementOpen(i)) {
                int lastel = n * n - 1;
                for (int j = lastel; j > lastel - n; --j) {
                    // System.out.println(wquf.find(i) + " " + wquf.find(j));
                    if (topid == wquf.find(j)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // test client (optional)
    public static void main(String[] args) {
        In in = new In(args[0]);      // input file
        int n = in.readInt();         // n-by-n percolation system

        // repeatedly read in sites to open and draw resulting system
        Percolation perc = new Percolation(n);
        while (!in.isEmpty()) {
            int i = in.readInt();
            int j = in.readInt();
            System.out.println("open(" + i + ", " + j + ")");
            perc.open(i, j);
            System.out.println("numberOfOpenSites()=" + perc.numberOfOpenSites());
        }
    }
}
