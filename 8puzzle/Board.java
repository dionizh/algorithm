/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdRandom;

import java.util.Arrays;

public class Board {
    private final int[][] tiles;
    private final int n;
    private int zeroidx;

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        // assume that the constructor receives an n-by-n array containing the
        // n2 integers between 0 and n2 − 1, where 0 represents the blank square.
        // You may also assume that 2 ≤ n < 128.
        n = tiles.length;
        this.tiles = new int[n][n];
        // clone performs a shallow copy, you need the nested loop
        for (int row = 0; row < n; row++) {
            this.tiles[row] = tiles[row].clone();
            for (int col = 0; col < n; col++) {
                if (tiles[row][col] == 0) {
                    zeroidx = (row * n) + col;
                }
            }
        }
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
        int i = 1;
        int count = 0;
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                int tile = tiles[row][col];
                if (tile != i && tile != 0) {
                    return false;
                }
                i++;
            }
        }
        return true;
    }

    // does this board equal y?
    public boolean equals(Object y) {
        if (this == y) return true;
        if (y == null) return false;
        if (getClass() != y.getClass()) return false;
        Board that = (Board) y;
        return (this.n == that.n && Arrays.deepEquals(this.tiles, that.tiles));
    }

    private int[][] getCopy() {
        int[][] copy = new int[n][n];
        for (int row = 0; row < n; row++) {
            copy[row] = tiles[row].clone();
        }
        return copy;
    }

    private Board swap(String direction, int row, int col) {
        // System.out.println("SWAP " + direction + " " + row + " " + col);
        int[][] copy = getCopy();
        if (direction.equals("left")) {
            int tmp = copy[row][col - 1];
            copy[row][col - 1] = copy[row][col];
            copy[row][col] = tmp;
        }
        else if (direction.equals("right")) {
            int tmp = copy[row][col + 1];
            copy[row][col + 1] = copy[row][col];
            copy[row][col] = tmp;
        }
        else if (direction.equals("up")) {
            int tmp = copy[row - 1][col];
            copy[row - 1][col] = copy[row][col];
            copy[row][col] = tmp;
        }
        else if (direction.equals("down")) {
            int tmp = copy[row + 1][col];
            copy[row + 1][col] = copy[row][col];
            copy[row][col] = tmp;
        }
        return new Board(copy);
    }

    // all neighboring boards
    // The neighbors() method returns an iterable containing the neighbors of the board.
    // Depending on the location of the blank square, a board can have 2, 3, or 4 neighbors.
    public Iterable<Board> neighbors() {
        Queue<Board> qboards = new Queue<>();
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                // first find where the empty tile is (0)
                if (tiles[row][col] == 0) {
                    if (col > 0) qboards.enqueue(swap("left", row, col));
                    if (col < n - 1) qboards.enqueue(swap("right", row, col));
                    if (row > 0) qboards.enqueue(swap("up", row, col));
                    if (row < n - 1) qboards.enqueue(swap("down", row, col));
                }
            }
        }
        return qboards;
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        // System.out.println("zeroidx=" + zeroidx);
        int size = n * n;
        int[] indexes = new int[size - 1];
        int count = 0;
        for (int i = 0; i < size; i++) {
            if (i != zeroidx) {
                indexes[count++] = i;
            }
        }
        StdRandom.shuffle(indexes);
        // just take 2 indexes from the shuffled list
        int ran = indexes[indexes.length - 1];
        int ran2 = indexes[0];

        int[][] mytiles = getCopy();
        int tmp = mytiles[ran / n][ran % n];
        mytiles[ran / n][ran % n] = mytiles[ran2 / n][ran2 % n];
        mytiles[ran2 / n][ran2 % n] = tmp;

        return new Board(mytiles);
    }

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
        System.out.println(b);
        System.out.println("hamming=" + b.hamming());
        System.out.println("manhattan=" + b.manhattan());
        System.out.println("isGoal=" + b.isGoal());

        // Iterable<Board> neighbours = b.neighbors();
        // Iterator<Board> it = neighbours.iterator();
        // while (it.hasNext()) {
        //     System.out.println("Neighbour:");
        //     System.out.println(it.next());
        // }

        // Board b2 = new Board(tiles);
        // System.out.println(b.equals(b2));

        Board t = b.twin();
        System.out.println("TWIN:");
        System.out.println(t);
    }
}
