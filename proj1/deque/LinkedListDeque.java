package deque;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class LinkedListDeque<T> implements Deque<T> {
    public class Node {
        public Node prev;
        public T item;
        public Node next;

        public Node(Node p, T i, Node n) {
            prev = p;
            item = i;
            next = n;
        }

        public Node getPrev() {
            return prev;
        }

        public Node getNext() {
            return next;
        }

        public T get(int i) {
            if (i == 0) {
                return item;
            }
            return next.get(i - 1);
        }
    }

    public Node sentinel;
    public int size;

    public LinkedListDeque() {
        sentinel = new Node(null, null, null);
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        size = 0;
    }

    public LinkedListDeque(T x) {
        sentinel = new Node(null, null, null);
        sentinel.next = new Node(sentinel, x, sentinel);
        sentinel.prev = sentinel.next;
        size = 1;
    }

    public void addFirst(T item) {
        Node temp = sentinel.next;
        sentinel.next = new Node(sentinel, item, temp);
        temp.prev = sentinel.next;
        size ++;
    }

    public void addLast(T item) {
        Node temp = sentinel.prev;
        sentinel.prev = new Node(temp, item, sentinel);
        temp.next = sentinel.prev;
        size++;
    }

    public boolean isEmpty() {
        if (size == 0) {
            return true;
        }
        return false;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        Node temp = sentinel.next;
        while (temp != sentinel) {
            System.out.print(temp.item);
            temp = temp.next;
        }
        System.out.println();
    }

    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        Node temp = sentinel.next;
        sentinel.next = temp.next;
        sentinel.next.prev = sentinel;
        size--;
        return temp.item;
    }

    public T removeLast() {
        if (size == 0) {
            return null;
        }
        Node temp = sentinel.prev;
        sentinel.prev = temp.prev;
        sentinel.prev.next = sentinel;
        size--;
        return temp.item;
    }

    public T get(int index) {
        if (index >= size || index < 0) {
            return null;
        }
        int i = 0;
        Node t = sentinel.next;
        while ( i != index) {
            t = t.next;
            i++;
        }
        return t.item;
    }

    public T getRecursive(int index) {
        if (index >= size || index < 0) {
            return null;
        }
        return sentinel.next.get(index);
    }


    public Iterator<T> iterator() {
        return new LLIterator();
    }

    private class LLIterator implements Iterator<T> {
        Node wizpos = sentinel.next;

        public boolean hasNext() {
            if (wizpos == sentinel) {
                return false;
            }
            return true;
        }

        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException("You've iterated all elements!");
            }
            T re = wizpos.item;
            wizpos = wizpos.next;
            return re;
        }

    }

    public boolean equals(Object o) {
        if (!(o instanceof Deque uddao )) {
            return false;
        } else if (uddao.size() != size()) {
            return false;
        }
        for (int i = 0; i < size(); i++) {
            if (!(uddao.get(i)).equals(this.get(i))) {
                return false;
            }
        }
        return true;
    }





}
