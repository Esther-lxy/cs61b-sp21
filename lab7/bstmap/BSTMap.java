package bstmap;


import java.util.Iterator;
import java.util.Set;


public class BSTMap<K extends Comparable, V> implements Map61B<K, V>{
    int size;
    BSTNode root;

    private class BSTNode {
        K key;
        V value;
        BSTMap<K, V> left;
        BSTMap<K, V> right;

        BSTNode(K k, V v) {
            key = k;
            value = v;
            left = new BSTMap<K, V>();
            right = new BSTMap<K, V>();
        }
    }

    BSTMap() {
        size = 0;
        root = null;
    }

    /** Removes all of the mappings from this map. */
    @Override
    public void clear() {
        root = null;
        size = 0;
    };

    /* Returns true if this map contains a mapping for the specified key. */
    @Override
    public boolean containsKey(K key) {
        if (root == null) {
            return false;
        } else if(key.compareTo(root.key) == 0) {
            return true;
        } else if(key.compareTo(root.key) < 0) {
            return root.left.containsKey(key);
        } else {
            return root.right.containsKey(key);
        }
    }

    /* Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    @Override
    public V get(K key) {
        if (root == null) {
            return null;
        } else if(key.compareTo(root.key) == 0) {
            return root.value;
        } else if(key.compareTo(root.key) < 0) {
            return root.left.get(key);
        } else {
            return root.right.get(key);
        }
    };

    /* Returns the number of key-value mappings in this map. */
    @Override
    public int size() {
        return size;
    };

    /* Associates the specified value with the specified key in this map. */
    @Override
    public void put(K key, V value) {
        if (containsKey(key)) {
            PurePut(key, value);
        } else {
            PutPlus(key, value);
        }
    }

    private void PutPlus(K key, V value) {
        size++;
        PurePut(key, value);
    }

    private void PurePut(K key, V value) {
        if (root == null) {
            root = new BSTNode(key, value);
        } else if(key.compareTo(root.key) < 0) {
            root.left.PurePut(key, value);
        } else if (key.compareTo(root.key) > 0){
            root.right.PurePut(key, value);
        } else {
            root.value = value;
        }
    }

    /* prints out BSTMap in order of increasing Key */
    public void printInOrder() {
        root.left.printInOrder();
        System.out.println(root.key);
        root.right.printInOrder();
    }

    /* Returns a Set view of the keys contained in this map. Not required for Lab 7.
     * If you don't implement this, throw an UnsupportedOperationException. */
    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    };

    /* Removes the mapping for the specified key from this map if present.
     * Not required for Lab 7. If you don't implement this, throw an
     * UnsupportedOperationException. */
    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    };

    /* Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 7. If you don't implement this,
     * throw an UnsupportedOperationException.*/
    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    };

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }
}
