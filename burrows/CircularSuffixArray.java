/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 * Implement the CircularSuffixArray. Be sure not to create new String objects
 * when you sort the suffixes. That would take quadratic space. A natural
 * approach is to define a nested class CircularSuffix that represents a circular
 * suffix implicitly (via a reference to the input string and a pointer to the
 * first character in the circular suffix). The constructor of CircularSuffix
 * should take constant time and use constant space. You might also consider
 * making CircularSuffix implement the Comparable<CircularSuffix> interface.
 * Note, that while this is, perhaps, the cleanest solution, it is not the fastest.
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;

public class CircularSuffixArray {

    private final String s;
    private final CircularSuffix[] suffixes;

    private class CircularSuffix {
        private final String s;
        private final int start;
        private final int idx; // the original index

        public CircularSuffix(String s, int start) {
            this.s = s;
            this.start = start;
            this.idx = start;
        }

        public int charAt(int d) {
            int pos = start + d;
            if (pos >= s.length()) pos -= s.length();
            return s.charAt(pos);
        }

        // public void printStr() {
        //     int p = start;
        //     for (int i = 0; i < s.length(); i++) {
        //         StdOut.printf(String.valueOf(s.charAt(p++)) + " ");
        //         if (p >= s.length()) p = 0;
        //     }
        //     StdOut.printf(" " + idx);
        //     StdOut.println();
        // }
    }

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) throw new IllegalArgumentException("Constructor param is null");

        this.s = s;
        suffixes = new CircularSuffix[s.length()];

        for (int i = 0; i < s.length(); i++) {
            suffixes[i] = new CircularSuffix(this.s, i);
            // suffixes[i].printStr();
        }

        sort(suffixes, 0, suffixes.length - 1, 0);
        // StdOut.println("*** POST SORTING ***");
        // for (int i = 0; i < s.length(); i++) {
        //     suffixes[i].printStr();
        // }
    }

    private static void exch(CircularSuffix[] suffixes, int a, int b) {
        CircularSuffix tmp = suffixes[a];
        suffixes[a] = suffixes[b];
        suffixes[b] = tmp;
    }

    // 3-way string quicksort
    private static void sort(CircularSuffix[] suffixes, int lo, int hi, int d) {
        if (hi <= lo) return;
        int lt = lo, gt = hi;
        // StdOut.println("char " + suffixes[lo].charAt(d));
        int v = suffixes[lo].charAt(d);
        int i = lo + 1;
        while (i <= gt) {
            int t = suffixes[i].charAt(d);
            if (t < v) exch(suffixes, lt++, i++);
            else if (t > v) exch(suffixes, i, gt--);
            else i++;
        }

        sort(suffixes, lo, lt - 1, d);
        if (v >= 0) sort(suffixes, lt, gt, d + 1);
        sort(suffixes, gt + 1, hi, d);
    }

    // length of s
    public int length() {
        return s.length();
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        // Throw an IllegalArgumentException in the method index() if i is outside its prescribed range (between 0 and n − 1).
        return suffixes[i].idx;
    }

    // unit testing (required)
    public static void main(String[] args) {
        String s = "ABRACADABRA!";
        CircularSuffixArray a = new CircularSuffixArray(s);
        StdOut.println("index[11]=" + a.index(11));
    }
}
