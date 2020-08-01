/* *****************************************************************************
 *  Name:              Ada Lovelace
 *  Coursera User ID:  123456
 *  Last modified:     October 16, 1842
 **************************************************************************** */

import edu.princeton.cs.algs4.StdIn;

import java.util.Iterator;

public class Permutation {
    public static void main(String[] args) {
        int k = Integer.parseInt(args[0]);

        // Deque<String> deque = new Deque<>();
        RandomizedQueue<String> rqueue = new RandomizedQueue<>();

        String in;
        while (!StdIn.isEmpty()) {
            // deque.addLast(in);
            in = StdIn.readString();
            // System.out.println("IN:" + in);
            rqueue.enqueue(in);
        }

        Iterator<String> i = rqueue.iterator();
        int count = 0;
        while (i.hasNext() && count < k) {
            String s = i.next();
            System.out.println(s);
            count++;
        }
    }
}
