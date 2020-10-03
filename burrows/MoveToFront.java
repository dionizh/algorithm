/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {
    private static final int EXTASCII = 256;

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        char[] asc = new char[EXTASCII];
        for (char i = 0; i < EXTASCII; i++) asc[i] = i;

        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            for (char i = 0; i < EXTASCII; i++) {
                if (c == asc[i]) {
                    // "shift" copy array
                    System.arraycopy(asc, 0, asc, 1, i);
                    asc[0] = c; // move to the front
                    BinaryStdOut.write((int) i, 8); // write it out
                    break;
                }
            }
        }
        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        char[] asc = new char[EXTASCII];
        for (char i = 0; i < EXTASCII; i++) asc[i] = i;

        while (!BinaryStdIn.isEmpty()) {
            int i = BinaryStdIn.readInt(8);
            char c = asc[i];
            BinaryStdOut.write(c);
            // "shift" copy array
            System.arraycopy(asc, 0, asc, 1, i);
            asc[0] = c; // move to the front
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
