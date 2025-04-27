package deque;

public class ArrayDeque<T> implements Deque<T> {
    T[] items;
    int size;
    int first;
    int last;

    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
    }

    private void resize(int capacity) {
        T[] right = (T[]) new Object[capacity];
        if (first < last) {
            System.arraycopy(items, first, right, 0, size);
            first = 0;
            last = size - 1;
        } else {
            int newFirst = (capacity - items.length + first);
            System.arraycopy(items, first, right, newFirst, size - last - 1);
            System.arraycopy(items, 0, right, 0, last + 1);
            first = newFirst;
        }
        items = right;
    }

    public void addFirst(T item) {
        if (size == items.length) {
            resize(size * 2);
        }

        if (size == 0) {
            items[0] = item;
            first = 0;
            last = 0;
        } else {
            first = (first - 1 + items.length) % items.length;
            items[first] = item;
        }
        size++;
    }

    public void addLast(T item) {
        if (size == items.length) {
            resize(size * 2);
        }

        if (size == 0) {
            items[0] = item;
            first = 0;
            last = 0;
        } else {
            last = (last + 1) % items.length;
            items[last] = item;
        }
        size ++;
    }

    public boolean isEmpty() {
        if (size == 0) {
            return true;
        } else {
            return false;
        }
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        int count = 0;
        while (count < size) {
            System.out.print((first + count) % items.length);
            count++;
        }
        System.out.println();
    }

    public T removeFirst() {
        if (size == 0) {
            return null;
        }

        if (items.length >= 16 && size - 1 < 0.25 * items.length) {
            resize(items.length / 2);
        }

        T temp = items[first];
        items[first] = null;
        first = (first + 1) % items.length;
        size--;
        return temp;
    }

    public T removeLast() {
        if (size == 0) {
            return null;
        }

        if (items.length >= 16 && size - 1 < 0.25 * items.length) {
            resize(items.length / 2);
        }

        T temp = items[last];
        items[last] = null;
        last = (last - 1 + items.length) % items.length;
        size--;
        return temp;
    }

    public T get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return items[(first + index) % items.length];
    }

    /*
    public Iterator<T> iterator() {
        pass;
    }
    */

    /*
    public boolean equals(Object o) {
        if (!(o instanceof Deque)) {
            return false;
        } else if (o.size != size) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (!(o.get(i)).equals(this.get(i))) {
                return false;
            }
        }
        return true;
    }
     */
}

