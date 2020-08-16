/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

public class Solver {
    private final Stack<Board> solutions = new Stack<>();
    private final boolean isSolveable;

    private class SearchNode implements Comparable<SearchNode> {
        Board board;
        int levels = 0;
        int manhattan = 0;
        SearchNode previous = null;

        public SearchNode(Board board) {
            this.board = board;
            this.manhattan = board.manhattan();
        }

        public int compareTo(SearchNode that) {
            if (that == null) {
                throw new NullPointerException("compareTo SearchNode argument is null");
            }
            int thispriority = this.manhattan + this.levels;
            int thatpriority = that.manhattan + that.levels;
            if (thispriority < thatpriority) return -1;
            if (thispriority > thatpriority) return 1;
            // if the same priority value
            if (this.manhattan < that.manhattan) return -1;
            if (this.manhattan > that.manhattan) return 1;
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

        boolean initReachGoal = false;
        boolean twinReachGoal = false;

        // int i = 0;
        SearchNode mininit = null;
        SearchNode mintwin = null;
        while (true) {
            // System.out.println("\nSTEP " + i++);
            mininit = pqinit.delMin();
            mintwin = pqtwin.delMin();

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
                neigh.levels = mininit.levels + 1;
                neigh.previous = mininit;
                pqinit.insert(neigh);
            }

            for (Board b : mintwin.board.neighbors()) {
                // don’t enqueue a neighbor if its board is the same as the board
                // of the previous search node
                if (mintwin.previous != null && b.equals(mintwin.previous.board)) continue;

                SearchNode neigh = new SearchNode(b);
                neigh.levels = mintwin.levels + 1;
                neigh.previous = mintwin;
                pqtwin.insert(neigh);
            }
        }

        // follow the previous pointers to compose the solution
        if (initReachGoal) {
            SearchNode sn = mininit;
            while (sn != null) {
                solutions.push(sn.board);
                sn = sn.previous;
            }
        }

        // if the twin is the one reaching its goal, then initial board is unsolvable
        isSolveable = (initReachGoal && !twinReachGoal);
    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        return isSolveable;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        if (solutions.size() >= 0) return solutions.size() - 1;
        return -1;
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        if (solutions.size() == 0) return null;
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
        if (!isSolveable) {
            StdOut.println("No solution possible");
            StdOut.println(solver.solution());
        }
        else {
            for (Board board : solver.solution())
                StdOut.println(board);
            StdOut.println("Minimum number of moves = " + solver.moves());
        }
    }

}
