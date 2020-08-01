/* *****************************************************************************
 *  Name:              Ada Lovelace
 *  Coursera User ID:  123456
 *  Last modified:     October 16, 1842
 **************************************************************************** */

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {

    private Node first = null;
    private Node last = null;
    private int size = 0;

    private class Node {
        Item item;
        Node next;
        Node parent;
    }

    // construct an empty deque
    public Deque() {
    }

    // is the deque empty?
    public boolean isEmpty() {
        return first == null;
    }

    // return the number of items on the deque
    public int size() {
        return size;
    }

    // add the item to the front
    public void addFirst(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Input item cannot be null!");
        }
        Node newfirst = new Node();
        newfirst.item = item;
        newfirst.next = first;
        newfirst.parent = null;
        if (first != null) {
            first.parent = newfirst;
        }
        first = newfirst;
        size++;
        if (last == null) {
            last = first;
        }
    }

    // add the item to the back
    public void addLast(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Input item cannot be null!");
        }
        Node newlast = new Node();
        newlast.item = item;
        newlast.next = null;
        newlast.parent = last;
        size++;
        if (first == null) {
            first = newlast;
        }
        if (last != null) {
            last.next = newlast;
        }
        last = newlast;
    }

    // remove and return the item from the front
    public Item removeFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException("Deque is empty!");
        }
        Node remfirst = first;
        first = remfirst.next;
        if (first == null) {
            // means this was the first and last element
            last = null;
        }
        else {
            first.parent = null;
        }
        size--;
        return remfirst.item;
    }

    // remove and return the item from the back
    public Item removeLast() {
        if (isEmpty()) {
            throw new NoSuchElementException("Deque is empty!");
        }
        Node remlast = last;
        if (remlast.parent != null) {
            last = remlast.parent;
            last.next = null;
        }
        else {
            // no parent, so this element is the first and last
            first = null;
            last = null;
        }
        size--;
        return remlast.item;
    }

    // return an iterator over items in order from front to back
    public Iterator<Item> iterator() {
        MeeIterator it = new MeeIterator();
        it.current = first;
        return it;
    }

    private class MeeIterator implements Iterator<Item> {
        public Node current;

        public boolean hasNext() {
            return (current != null);
        }

        public Item next() {
            if (current == null) {
                throw new NoSuchElementException("No more item to return!");
            }
            Item item = current.item;
            current = current.next;
            return item;
        }

        public void remove() {
            throw new UnsupportedOperationException("remove func unsupported!");
        }
    }

    // unit testing (required)
    public static void main(String[] args) {
        // Deque<Integer> deque = new Deque<Integer>();
        // deque.addLast(1);
        // deque.addFirst(2);
        // deque.removeLast(); //     ==> 1
        // deque.addLast(4);
        // deque.removeFirst(); //     ==> 2
        // deque.removeLast(); //     ==> 4
        // deque.size(); //            ==> 0
        // deque.addLast(8);
        // deque.addLast(9);
        // deque.addLast(10);
        // System.out.println(deque.removeFirst()); //     ==> 4
        //
        // Iterator<Integer> i = deque.iterator();
        // while (i.hasNext()) {
        //     Integer s = i.next();
        //     System.out.print(s + ", ");
        // }
        // System.out.println("size: " + deque.size());


        // Deque<String> deque = new Deque<>();
        // deque.addFirst("one");
        // deque.addLast("two");
        // deque.addLast("three");
        // System.out.println("Size: " + deque.size());
        // deque.removeLast();
        // deque.removeFirst();
        //
        // Iterator<String> i = deque.iterator();
        //
        // while (i.hasNext()) {
        //     String s = i.next();
        //     System.out.println(s);
        // }
        // System.out.println("Size: " + deque.size());
    }
}
