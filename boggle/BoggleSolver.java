/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.TST;

import java.util.TreeSet;

public class BoggleSolver {
    private BoggleBoard bb;
    private boolean[] marked;  // marked[v] = is there an s->v path?
    private final TreeSet<String> valids = new TreeSet<>();
    private final TST<Object>[] rtrie = new TST[26];

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        for (char c = 'A'; c <= 'Z'; ++c) {
            rtrie[c - 65] = new TST<>();
        }
        for (int i = 0; i < dictionary.length; i++) {
            String word = dictionary[i];
            rtrie[word.charAt(0) - 65].put(word, 1);
        }
    }

    private int getV(int row, int col) {
        return (row * bb.rows() + col);
    }

    private Iterable<Integer> getAdjacent(int v) {
        int row = v / bb.rows();
        int col = v % bb.cols();
        Queue<Integer> adj = new Queue<>();
        if (row > 0) {
            if (col > 0) adj.enqueue(getV(row - 1, col - 1));
            adj.enqueue(getV(row - 1, col));
            if (col < bb.cols() - 1) adj.enqueue(getV(row - 1, col + 1));
        }
        if (col > 0) adj.enqueue(getV(row, col - 1));
        if (col < bb.cols() - 1) adj.enqueue(getV(row, col + 1));
        if (row < bb.rows() - 1) {
            if (col > 0) adj.enqueue(getV(row + 1, col - 1));
            adj.enqueue(getV(row + 1, col));
            if (col < bb.cols() - 1) adj.enqueue(getV(row + 1, col + 1));
        }
        return adj;
    }

    private int boardSize() {
        return bb.rows() * bb.cols();
    }

    private String getLetter(int v) {
        char c = bb.getLetter(v / bb.rows(), v % bb.cols());
        if (c == 'Q') return "QU";
        return String.valueOf(c);
    }

    private boolean prefixExists(String next) {
        Queue<String> keys = (Queue<String>) rtrie[next.charAt(0) - 65].keysWithPrefix(next);
        if (keys.size() == 0) {
            return false;
        }
        // StdOut.println("prefix of " + next + ": ");
        // for (String key : keys) {
        //     StdOut.printf(key + ", ");
        // }
        // StdOut.println();
        for (String key : keys) {
            if (key.equals(next) && next.length() >= 3) {
                // StdOut.println("* Add " + next);
                valids.add(next);
            }
        }
        return true;
    }

    private void dfs(int v, String pf) {
        marked[v] = true;
        for (int w : getAdjacent(v)) {
            String adjStr = getLetter(w);
            if (!marked[w]) {
                String next = pf + adjStr;
                if (prefixExists(next)) {
                    // StdOut.printf("%s (%s) -> %s (%s)\n", getLetter(v), v, getLetter(w), w);
                    dfs(w, next);
                    marked[w] = false;
                }
            }
        }
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        // copy board argument to immutable private board
        char[][] bchars = new char[board.rows()][board.cols()];
        for (int i = 0; i < board.rows(); i++) {
            for (int j = 0; j < board.cols(); j++) {
                bchars[i][j] = board.getLetter(i, j);
            }
        }
        bb = new BoggleBoard(bchars);

        for (int s = 0; s < boardSize(); s++) {
            marked = new boolean[boardSize()];
            dfs(s, String.valueOf(getLetter(s)));
        }
        // int s = 6;
        // marked = new boolean[boardSize()];
        // dfs(s, String.valueOf(getLetter(s)));
        return valids;
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (word.length() == 3) return 1;
        else if (word.length() == 4) return 1;
        else if (word.length() == 5) return 2;
        else if (word.length() == 6) return 3;
        else if (word.length() == 7) return 5;
        else if (word.length() >= 8) return 11;
        return 0;
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        StdOut.println(board.toString());
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
}
