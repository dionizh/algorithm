/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.TST;

public class BoggleSolver {
    private static final int LETTERS = 256;
    private BoggleBoard bb;
    private SET<String> valids;
    private final TST<Integer>[][] rtrie;

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        rtrie = (TST<Integer>[][]) new TST[LETTERS][LETTERS];
        boolean[][] tstExists = new boolean[LETTERS][LETTERS];
        // for (char c = 'A'; c <= 'Z'; ++c) {
        //     for (char d = 'A'; d <= 'Z'; ++d) {
        //         rtrie[c - 65][d - 65] = new TST<>();
        //     }
        // }
        // for (int i = 0; i < 26; i++) {
        //     for (int j = 0; j < 26; j++) {
        //         rtrie[i][j] = null;
        //     }
        // }
        int[] rans = new int[dictionary.length];
        for (int i = 0; i < dictionary.length; i++) rans[i] = i;
        StdRandom.shuffle(rans);

        for (int i = 0; i < dictionary.length; i++) {
            String word = String.copyValueOf(dictionary[rans[i]].toCharArray());
            // put defensive copy of the string
            int length = word.length();
            if (length < 3) continue;
            int c = word.charAt(0);
            int d = word.charAt(1);
            if (!tstExists[c][d]) {
                rtrie[c][d] = new TST<>();
                tstExists[c][d] = true;
            }
            // StdOut.println((count++) + " " + word.substring(2));
            rtrie[c][d].put(word.substring(2), 1);
        }
        // StdOut.println("dict length=" + dictionary.length);
    }

    private int getV(int row, int col) {
        return (row * bb.cols() + col);
    }

    private Iterable<Integer> getAdjacent(int v) {
        int row = v / bb.cols();
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
        char c = bb.getLetter(v / bb.cols(), v % bb.cols());
        if (c == 'Q') return "QU";
        return String.valueOf(c);
    }

    private boolean prefixExists(String next) {
        int length = next.length();
        if (length == 1 && rtrie[next.charAt(0)] != null) return true;
        if (length >= 2) {
            TST<Integer> tst = rtrie[next.charAt(0)][next.charAt(1)];
            if (tst == null) return false;
            if (length == 2) return true; // tst != null

            // StdOut.println("keys with prefix " + next.substring(2));
            Queue<String> keys = (Queue<String>) tst.keysWithPrefix(next.substring(2));
            if (keys.size() == 0) {
                return false;
            }
            for (String key : keys) {
                // StdOut.println(" - key: " + key);
                if (key.equals(next.substring(2))) {
                    // StdOut.println("* ADD " + next);
                    valids.add(next);
                }
            }
        }
        return true;
    }

    private class PrefixNode {
        int v;
        String prefix;
        int[] edges = new int[boardSize()];
        int edgeSize = 0;

        public PrefixNode(int v, String prefix) {
            this.v = v;
            this.prefix = prefix;
            for (int i = 0; i < edges.length; i++) edges[i] = -1;
        }

        public void addEdge(int x) {
            edges[edgeSize++] = x;
        }

        public void printPath() {
            for (int i = 0; i < edges.length; i++) {
                if (edges[i] == -1) break;
                StdOut.printf(edges[i] + "->");
            }
            StdOut.println();
        }
    }

    // dfs with stack and no recursion
    private void dfs(int s) {
        boolean[] marked = new boolean[boardSize()];  // marked[v] = is there an s->v path?
        Stack<PrefixNode> st = new Stack<>();

        PrefixNode n = new PrefixNode(s, getLetter(s));
        st.push(n);
        n.addEdge(s);

        while (!st.isEmpty()) {
            PrefixNode pn = st.pop();
            int v = pn.v;
            if (marked[v]) continue;

            // do work here
            // StdOut.println("* v=" + getLetter(v) + " (" + v + ") | prefix=" + pn.prefix);
            // StdOut.printf("mark: ");
            // mark all vertices in the path (in case unmarked when backtracking)
            for (int x = pn.edgeSize - 1; x >= 0; x--) {
                int e = pn.edges[x];
                // StdOut.printf(getLetter(e) + " (" + e + "), ");
                marked[e] = true;
            }
            // StdOut.println();

            boolean deadend = true;
            Queue<Integer> unmarks = new Queue<>();
            unmarks.enqueue(v);
            for (int w : getAdjacent(v)) {
                if (!marked[w]) {
                    String substr = pn.prefix + getLetter(w);
                    boolean tbc = prefixExists(substr);
                    if (tbc) {
                        // StdOut.println("tbc " + substr + "=" + tbc);
                        PrefixNode node = new PrefixNode(w, substr);
                        for (int i = 0; i < pn.edgeSize; i++) node.addEdge(pn.edges[i]);
                        node.addEdge(w);
                        st.push(node);
                        // node.printPath();
                        deadend = false;
                    }
                    unmarks.enqueue(w);
                }
            }
            if (deadend) {
                // StdOut.printf("UNmark edgeSize " + pn.edgeSize + " :");
                for (int x = pn.edgeSize - 1; x >= 0; x--) {
                    int e = pn.edges[x];
                    // StdOut.printf(getLetter(e) + " (" + e + "), ");
                    marked[e] = false;
                }
                // StdOut.println();
            }
        }
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        valids = new SET<>();
        // copy board argument to immutable private board
        char[][] bchars = new char[board.rows()][board.cols()];
        for (int i = 0; i < board.rows(); i++) {
            for (int j = 0; j < board.cols(); j++) {
                bchars[i][j] = board.getLetter(i, j);
            }
        }
        bb = new BoggleBoard(bchars);
        for (int s = 0; s < boardSize(); s++) {
            dfs(s);
        }
        // int s = 15;
        // dfs(s);
        return valids;
    }

    private boolean wordExists(String w) {
        if (w.length() < 3) return false;
        TST<Integer> tst = rtrie[w.charAt(0)][w.charAt(1)];
        if (tst == null) return false;
        return (((Queue<String>) tst.keysThatMatch(w.substring(2))).size() > 0);
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (!wordExists(word)) return 0;
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
        long startTime = System.nanoTime();
        BoggleSolver solver = new BoggleSolver(dictionary);
        long endTime = System.nanoTime();
        long totalTime = endTime - startTime;
        System.out.println("construction time: " + totalTime / 1000000000.0);

        BoggleBoard board = new BoggleBoard(args[1]);
        // BoggleBoard board = new BoggleBoard(2, 2);
        StdOut.println(board.toString());
        int score = 0;
        int counter = 0;
        startTime = System.nanoTime();
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
            counter++;
        }
        totalTime = System.nanoTime() - startTime;
        System.out.println("getAllValidWords time: " + totalTime / 1000000000.0);
        StdOut.println("Score = " + score);
        StdOut.println("Count = " + counter);

        // String w = "AY";
        // StdOut.println(w + " score= " + solver.scoreOf(w));
    }
}
