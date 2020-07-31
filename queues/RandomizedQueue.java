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
            for (int i = 0; i < capacity; ++i) {
                if (i >= s.length) copy[i] = null;
                else copy[i] = s[i];
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
        if (item == null) {
            throw new IllegalArgumentException("Input item cannot be null!");
        }
        if (size == s.length) resize(2 * s.length);
        s[size++] = item;
    }

    // remove and return a random item
    public Item dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException("Queue is empty!");
        }

        int ran = StdRandom.uniform(0, size);
        while (s[ran] == null) {
            ran = StdRandom.uniform(0, size); // keep trying until you find a valid value
        }
        Item item = s[ran];
        s[ran] = null;
        size--;
        if (size > 0 && size == s.length / 4) resize(s.length / 2);
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
            int ran;
            if (n == 0) ran = 0;
            else {
                ran = StdRandom.uniform(0, size);
                while (s[ran] == null) {
                    ran = StdRandom.uniform(0, size); // keep trying until you find a valid value
                }
            }
            Item item = s[ran];
            s[ran] = null;
            n--;
            return item;
        }

        public void remove() {
            throw new UnsupportedOperationException("remove func unsupported!");
        }
    }

    // unit testing (required)
    public static void main(String[] args) {
        RandomizedQueue<String> rqueue = new RandomizedQueue<>();
        rqueue.enqueue("one");
        rqueue.enqueue("two");
        rqueue.enqueue("three");
        System.out.println("sample:" + rqueue.sample());
        System.out.println("sample:" + rqueue.sample());
        rqueue.dequeue();
        rqueue.dequeue();

        Iterator<String> i = rqueue.iterator();
        while (i.hasNext()) {
            String s = i.next();
            System.out.println(s);
        }
        System.out.println("Size: " + rqueue.size());
    }

}
