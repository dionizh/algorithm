/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {
    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output
    public static void transform() {
        String s = BinaryStdIn.readString();
        CircularSuffixArray a = new CircularSuffixArray(s);
        char[] last = new char[s.length()];
        int first = -1;
        for (int i = 0; i < s.length(); i++) {
            int idx = a.index(i);
            int pos = idx - 1;
            if (pos < 0) pos += s.length();
            last[i] = s.charAt(pos);
            // StdOut.println(String.valueOf(c) + " " + idx);
            // StdOut.printf("%02x", c & 0xff);
            // StdOut.print(" ");
            if (idx == 0) first = i;
        }

        // StdOut.printf("%08x", first & 0xff);
        // StdOut.println();

        BinaryStdOut.write(first);
        for (int i = 0; i < last.length; i++) BinaryStdOut.write(last[i]);
        BinaryStdOut.close();
    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {
        int first = BinaryStdIn.readInt();
        String s = BinaryStdIn.readString();
        int[] next = new int[s.length()];
        char[] t = s.toCharArray();
        char[] f = new char[t.length];

        // sort first column with key-indexed counting
        final int R = 256;
        int[] count = new int[R + 1];
        for (int i = 0; i < t.length; i++)
            count[t[i] + 1]++;
        for (int r = 0; r < R; r++)
            count[r + 1] += count[r];
        for (int i = 0; i < t.length; i++)
            f[count[t[i]]++] = t[i];

        int i = 0;
        while (i < s.length()) {
            for (int j = 0; j < s.length(); j++) {
                if (f[i] == t[j]) {
                    next[i] = j;
                    if (i < s.length() - 1 && f[i] != f[i + 1]) break;
                    i++;
                    if (i >= s.length()) break;
                }
            }
            i++;
        }
        char[] ori = new char[s.length()];
        int k = 0;
        for (int j = first; k < s.length(); j = next[j]) {
            ori[k++] = f[j];
        }
        BinaryStdOut.write(new String(ori));
        BinaryStdOut.close();
    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        String in = args[0];
        if (in.equals("-")) {
            BurrowsWheeler.transform();
        }
        else if (in.equals("+")) {
            BurrowsWheeler.inverseTransform();
        }
    }
}
