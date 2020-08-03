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

        int count = 0;
        String in;
        while (!StdIn.isEmpty() && k > 0) {
            // deque.addLast(in);
            in = StdIn.readString();
            rqueue.enqueue(in);
            count++;
        }

        while (count > k) {
            rqueue.dequeue();
            count--;
        }

        Iterator<String> i = rqueue.iterator();

        while (i.hasNext()) {
            String s = i.next();
            System.out.println(s);
        }
        System.out.println("size:" + rqueue.size());
    }
}
