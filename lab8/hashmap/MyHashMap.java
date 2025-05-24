package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!
    private int NumberOfBuckets = 16;
    private int size = 0;
    private double loadFactor = 0.75;
    private Set<K> keySet = new HashSet<>();


    /** Constructors */
    public MyHashMap() {
        buckets = createTable(NumberOfBuckets);
        FillBuckets();
    }

    public MyHashMap(int initialSize) {
        NumberOfBuckets = initialSize;
        buckets = createTable(NumberOfBuckets);
        FillBuckets();
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        NumberOfBuckets = initialSize;
        loadFactor = maxLoad;
        buckets = createTable(NumberOfBuckets);
        FillBuckets();
    }

    private void FillBuckets() {
        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = createBucket();
        }
    }
    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        return new Collection[tableSize];
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!

    private int BucketIndex(K key) {
        return Math.floorMod(key.hashCode(), NumberOfBuckets);
    }

    /** Removes all of the mappings from this map. */
    public void clear() {
        for(int i = 0; i < buckets.length; i++) {
            buckets[i] = createBucket();
        }
        size = 0;
    }

    /** Returns true if this map contains a mapping for the specified key. */
    public boolean containsKey(K key) {
        int index = BucketIndex(key);
        for (Node N: buckets[index]) {
            if (N.key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    public V get(K key) {
        int index = BucketIndex(key);
        for (Node N: buckets[index]) {
            if (N.key.equals(key)) {
                return N.value;
            }
        }
        return null;
    };

    /** Returns the number of key-value mappings in this map. */
    public int size() {
        return size;
    };

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key,
     * the old value is replaced.
     */
    public void put(K key, V value) {
        int index = BucketIndex(key);
        for (Node N: buckets[index]) {
            if (N.key.equals(key)) {
                N.value = value;
                return;
            }
        }
        buckets[index].add(createNode(key, value));
        size++;
        keySet.add(key);
        if (size / NumberOfBuckets >= loadFactor) {
            ResizeBuckets();
        }
    };

    private void ResizeBuckets() {
        MyHashMap<K,V> NewHashMap = new MyHashMap<>(NumberOfBuckets * 2, loadFactor);
        Set<K> oldKeys = keySet();
        for(K key : oldKeys) {
            NewHashMap.put(key, this.get(key));
        }
        this.buckets = NewHashMap.buckets;
        this.NumberOfBuckets = NumberOfBuckets * 2;
    }

    /** Returns a Set view of the keys contained in this map. */
    public Set<K> keySet() {
        return keySet;
    };

    /**
     * Removes the mapping for the specified key from this map if present.
     * Not required for Lab 8. If you don't implement this, throw an
     * UnsupportedOperationException.
     */
    public V remove(K key) {
        int index = BucketIndex(key);
        for (Node N : buckets[index]) {
            if (N.key.equals(key)) {
                buckets[index].remove(N);
                size--;
                keySet.remove(key);
                return N.value;
            }
        }
        return null;
    };

    /**
     * Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 8. If you don't implement this,
     * throw an UnsupportedOperationException.
     */
    public V remove(K key, V value) {
        int index = BucketIndex(key);
        for (Node N : buckets[index]) {
            if (N.key.equals(key) && N.value.equals(value)) {
                buckets[index].remove(N);
                size--;
                keySet.remove(key);
                return N.value;
            }
        }
        return null;
    }

    @Override
    public Iterator<K> iterator() {
        return keySet.iterator();
    }
}
