/* *****************************************************************************
 *  Name:              Alan Turing
 *  Coursera User ID:  123456
 *  Last modified:     1/1/2019
 **************************************************************************** */

import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {
    private final double[] percThreshold;
    private final int trials;

    // perform independent trials on an n-by-n grid
    public PercolationStats(int n, int startTrials) {
        // Throw an IllegalArgumentException in the constructor if either n ≤ 0 or trials ≤ 0.
        if (n <= 0) {
            throw new IllegalArgumentException("n value out of range: " + n);
        }
        if (startTrials <= 0) {
            throw new IllegalArgumentException("trials value out of range: " + startTrials);
        }

        trials = startTrials;
        percThreshold = new double[trials];

        for (int i = 0; i < trials; i++) {
            int count = doPercolation(n);
            // System.out.println("trial " + i + " count=" + count);
            percThreshold[i] = (double) count / (n * n);
            // System.out.println("Percolation count: " + count + " threshold: " + percThreshold[i]);
        }
    }

    // returns the number open sites when it percolates
    private int doPercolation(int n) {
        Percolation perc = new Percolation(n);

        int count = 0;
        do {
            int row = StdRandom.uniform(1, n + 1);
            int col = StdRandom.uniform(1, n + 1);
            if (!perc.isOpen(row, col)) {
                // System.out.println("Open " + row + " " + col);
                perc.open(row, col);
                count++;
                if (count >= n * n) {
                    System.out.println("ALL OPEN!");
                    break;
                }
            }
        } while (!perc.percolates());
        return count;
    }

    // sample mean of percolation threshold
    public double mean() {
        return StdStats.mean(percThreshold);
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return StdStats.stddev(percThreshold);
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        return mean() - 1.96 / Math.sqrt(trials);
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        return mean() + 1.96 / Math.sqrt(trials);
    }

    public static void main(String[] args) {
        String n = args[0];
        String t = args[1];

        PercolationStats ps = new PercolationStats(Integer.parseInt(n), Integer.parseInt(t));
        System.out.println("mean                    = " + ps.mean());
        System.out.println("stddev                  = " + ps.stddev());
        System.out.println(
                "95% confidence interval = [" + ps.confidenceLo() + ", " + ps.confidenceHi() + "]");

    }
}
