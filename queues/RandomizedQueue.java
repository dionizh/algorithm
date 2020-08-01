import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RandomizedQueue<Item> implements Iterable<Item> {

    private static final int INIT_SIZE = 1;

    private Item[] s;
    private int size = 0;

    // construct an empty randomized queue
    public RandomizedQueue() {
        s = (Item[]) new Object[INIT_SIZE];
    }

    private void resize(int capacity) {
        Item[] copy = (Item[]) new Object[capacity];

        if (capacity > s.length) {
            // System.out.println("BIGGER RESIZE! size:" + s.length + " capacity:" + capacity);
            int[] indexes = new int[s.length];
            for (int i = 0; i < s.length; ++i) {
                indexes[i] = i;
            }
            StdRandom.shuffle(indexes);
            for (int i = 0; i < capacity; ++i) {
                if (i >= s.length) copy[i] = null;
                else copy[i] = s[indexes[i]];
            }
        }
        else {
            // System.out.println("SMALLER RESIZE! size:" + s.length + " capacity:" + capacity);
            int count = 0;
            for (int i = 0; i < s.length; ++i) {
                if (s[i] != null) copy[count] = s[i];
            }
        }
        s = copy;
        // System.out.println("AFTER RESIZE");
        // for (int i = 0; i < capacity; ++i) {
        //     System.out.print(s[i] + ", ");
        // }
        // System.out.println();
    }

    // is the randomized queue empty?
    public boolean isEmpty() {
        return size == 0;
    }

    // return the number of items on the randomized queue
    public int size() {
        return size;
    }

    // add the item
    public void enqueue(Item item) {
        // System.out.println("Enqueue " + item);
        if (item == null) {
            throw new IllegalArgumentException("Input item cannot be null!");
        }
        s[size++] = item;
        if (size == s.length) resize(2 * s.length);
    }

    // remove and return a random item
    public Item dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty!");
        }

        // System.out.println("dequeue element " + size);
        size--;
        Item item = s[size];
        s[size] = null;
        if (size > 0 && size == s.length / 4) resize(s.length / 2);
        // System.out.println("Dequeue " + item);
        return item;
    }

    // return a random item (but do not remove it)
    public Item sample() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty!");
        }
        return s[StdRandom.uniform(0, size)];
    }

    // return an independent iterator over items in random order
    public Iterator<Item> iterator() {
        MeeIterator it = new MeeIterator();
        it.s = s;
        return it;
    }

    private class MeeIterator implements Iterator<Item> {
        private int n = size;
        private Item[] s;

        public boolean hasNext() {
            return (n > 0);
        }

        public Item next() {
            if (n <= 0) {
                throw new NoSuchElementException("No more item to return!");
            }

            n--;
            Item item = s[n];
            s[n] = null;
            return item;
        }

        public void remove() {
            throw new UnsupportedOperationException("remove func unsupported!");
        }
    }

    // unit testing (required)
    public static void main(String[] args) {

        // RandomizedQueue<Integer> rq = new RandomizedQueue<Integer>();
        // rq.enqueue(347);
        // rq.enqueue(33);
        // rq.enqueue(212);
        // rq.enqueue(212);
        // rq.enqueue(262);
        // rq.enqueue(390);
        // System.out.println("SAMPLE " + rq.sample()); //     ==> null
        //
        // Iterator<Integer> i = rq.iterator();
        // while (i.hasNext()) {
        //     Integer s = i.next();
        //     System.out.println(s);
        // }
        // System.out.println("size: " + rq.size());

        // RandomizedQueue<String> rqueue = new RandomizedQueue<>();
        // rqueue.enqueue("one");
        // rqueue.enqueue("two");
        // rqueue.enqueue("three");
        // System.out.println("sample:" + rqueue.sample());
        // System.out.println("sample:" + rqueue.sample());
        // rqueue.dequeue();
        // rqueue.dequeue();
        //
        // Iterator<String> i = rqueue.iterator();
        // while (i.hasNext()) {
        //     String s = i.next();
        //     System.out.println(s);
        // }
        // System.out.println("Size: " + rqueue.size());
    }

}