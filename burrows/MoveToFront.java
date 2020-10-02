/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;
import edu.princeton.cs.algs4.Queue;

public class MoveToFront {
    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        int EXTASCII = 256;
        char[] asc = new char[EXTASCII];
        for (char i = 0; i < EXTASCII; i++) asc[i] = i;
        Queue<Integer> out = new Queue<>();

        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            // StdOut.println(String.valueOf(c));
            for (char i = 0; i < EXTASCII; i++) {
                if (c == asc[i]) {
                    // "shift" copy array
                    System.arraycopy(asc, 0, asc, 1, i);
                    asc[0] = c; // move to the front
                    out.enqueue((int) i);
                    break;
                }
            }
        }
        while (!out.isEmpty()) {
            // StdOut.printf(out.dequeue() + " ");
            BinaryStdOut.write(out.dequeue(), 8);
        }
        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {

    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        String in = args[0];
        if (in.equals("-")) {
            MoveToFront.encode();
        }
        else if (in.equals("+")) {
            MoveToFront.decode();
        }
    }
}
