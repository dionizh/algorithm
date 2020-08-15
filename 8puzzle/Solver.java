/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;

public class Solver {

    private final int moves;
    private final Queue<Board> solutions = new Queue<>();
    private final boolean isSolveable;

    private class SearchNode implements Comparable<SearchNode> {
        Board board;
        int moves = 0;
        int manhattan;
        SearchNode previous = null;

        public SearchNode(Board board) {
            this.board = board;
            this.manhattan = board.manhattan();
        }

        public int compareTo(SearchNode that) {
            if (that == null) {
                throw new NullPointerException("compareTo SearchNode argument is null");
            }
            if (this.manhattan + moves < that.manhattan + that.moves) return -1;
            if (this.manhattan + moves > that.manhattan + that.moves) return 1;
            return 0;
        }
    }

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null) throw new IllegalArgumentException("initial Board is null");

        MinPQ<SearchNode> pqinit = new MinPQ<>();
        MinPQ<SearchNode> pqtwin = new MinPQ<>();

        Board twin = initial.twin();
        pqinit.insert(new SearchNode(initial));
        pqtwin.insert(new SearchNode(twin));
        // System.out.println("TWIN");
        // System.out.println(twin);

        boolean initReachGoal = false;
        boolean twinReachGoal = false;

        // int i = 0;
        SearchNode mininit;
        while (true) {
            // System.out.println("i=" + i++);
            mininit = pqinit.delMin();
            SearchNode mintwin = pqtwin.delMin();
            // System.out.println(mintwin.board);

            solutions.enqueue(mininit.board);

            if (mininit.board.isGoal()) {
                initReachGoal = true;
                break;
            }
            if (mintwin.board.isGoal()) {
                twinReachGoal = true;
                break;
            }

            for (Board b : mininit.board.neighbors()) {
                // don’t enqueue a neighbor if its board is the same as the board
                // of the previous search node
                if (mininit.previous != null && b.equals(mininit.previous.board)) continue;

                SearchNode neigh = new SearchNode(b);
                neigh.moves = mininit.moves + 1;
                neigh.previous = mininit;
                pqinit.insert(neigh);
            }

            for (Board b : mintwin.board.neighbors()) {
                // don’t enqueue a neighbor if its board is the same as the board
                // of the previous search node
                if (mintwin.previous != null && b.equals(mintwin.previous.board)) continue;

                SearchNode neigh = new SearchNode(b);
                neigh.moves = mintwin.moves + 1;
                neigh.previous = mintwin;
                pqtwin.insert(neigh);
            }
        }

        moves = mininit.moves;
        // if the twin is the one reaching its goal, then initial board is unsolvable
        isSolveable = (initReachGoal && !twinReachGoal);
    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        return isSolveable;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        return moves;
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        return solutions;
    }

    // test client (see below)
    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        // solve the puzzle
        Solver solver = new Solver(initial);

        boolean isSolveable = solver.isSolvable();
        System.out.println("isSolvable=" + isSolveable);
        // print solution to standard output
        if (!isSolveable)
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            // for (Board board : solver.solution())
            //     StdOut.println(board);
        }
    }

}
