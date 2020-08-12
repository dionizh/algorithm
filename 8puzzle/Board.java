/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;

public class Board {
    private final int[][] tiles;
    private final int n;

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        // assume that the constructor receives an n-by-n array containing the
        // n2 integers between 0 and n2 − 1, where 0 represents the blank square.
        // You may also assume that 2 ≤ n < 128.
        this.tiles = tiles.clone();
        this.n = tiles.length;
        // System.out.println("n=" + this.n + " tiles.length=" + tiles.length);
    }

    // string representation of this board
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(n + "\n");
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                str.append(String.format(" %2d", tiles[row][col]));
            }
            str.append("\n");
        }
        return str.toString();
    }

    // board dimension n
    public int dimension() {
        return n;
    }

    // number of tiles out of place
    public int hamming() {
        int i = 1;
        int count = 0;
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                int tile = tiles[row][col];
                if (tile != i && tile != 0) {
                    count++;
                }
                i++;
            }
        }
        return count;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        int mdist = 0;
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                int tile = tiles[row][col];
                if (tile == 0) continue;
                int targetrow = (tile - 1) / n;
                int targetcol = (tile - 1) % n;

                int dist = Math.abs(targetrow - row);
                dist += Math.abs(targetcol - col);
                mdist += dist;
                // System.out.printf("tile %s dist %s\n", tile, dist);
            }
        }
        return mdist;
    }

    // is this board the goal board?
    public boolean isGoal() {
        return false;
    }

    // does this board equal y?
    public boolean equals(Object y) {
        return false;
    }

    // all neighboring boards
    // public Iterable<Board> neighbors() {
    // }
    //
    // // a board that is obtained by exchanging any pair of tiles
    // public Board twin() {
    // }

    // unit testing (not graded)
    public static void main(String[] args) {
        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();

        int[][] tiles = new int[n][n];
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                tiles[row][col] = in.readInt();
            }
        }
        Board b = new Board(tiles);
        System.out.println(b.toString());
        System.out.println("hamming=" + b.hamming());
        System.out.println("manhattan=" + b.manhattan());
    }
}
