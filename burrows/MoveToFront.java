/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;
import edu.princeton.cs.algs4.Queue;

public class MoveToFront {
    private final static int EXTASCII = 256;

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        char[] asc = new char[EXTASCII];
        for (char i = 0; i < EXTASCII; i++) asc[i] = i;
        Queue<Integer> out = new Queue<>();

        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
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
            BinaryStdOut.write(out.dequeue(), 8);
        }
        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        char[] asc = new char[EXTASCII];
        for (char i = 0; i < EXTASCII; i++) asc[i] = i;
        Queue<Character> out = new Queue<>();

        while (!BinaryStdIn.isEmpty()) {
            int i = BinaryStdIn.readInt(8);
            char c = asc[i];
            out.enqueue(c);

            // "shift" copy array
            System.arraycopy(asc, 0, asc, 1, i);
            asc[0] = c; // move to the front
        }
        while (!out.isEmpty()) {
            BinaryStdOut.write(out.dequeue());
        }
        BinaryStdOut.close();
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
