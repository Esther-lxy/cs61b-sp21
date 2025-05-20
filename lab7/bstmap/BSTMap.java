package bstmap;


import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;


public class BSTMap<K extends Comparable, V> implements Map61B<K, V>{
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

    public BSTMap() {
        root = null;
    }

    /** Removes all of the mappings from this map. */
    @Override
    public void clear() {
        root = null;
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
        if (root == null) {
            return 0;
        } else {
            return 1 + root.left.size() + root.right.size();
        }
    };

    /* Associates the specified value with the specified key in this map. */
    @Override
    public void put(K key, V value) {
        if (root == null) {
            root = new BSTNode(key, value);
        } else if(key.compareTo(root.key) < 0) {
            root.left.put(key, value);
        } else if (key.compareTo(root.key) > 0){
            root.right.put(key, value);
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
        TreeSet<K> keys = new TreeSet<K>();
        if (root == null) {
        } else {
            keys.addAll(root.left.keySet());
            keys.add(root.key);
            keys.addAll(root.right.keySet());
        }
        return keys;
    };

    @Override
    /*return an iterator over the keys */
    public Iterator<K> iterator() {
        return keySet().iterator();
    }

    /*
    private class KeysIterator implements Iterator<K> {
        K current ;
        int rest = size;
        KeysIterator() {
            current = root.key;
        }
        @Override
        public boolean hasNext() {
            if (size == 0) {
                return false;
            } else {
                return true;
            }
        }

        @Override
        public K next() {
            return null;
        }
    } */

    /* Removes the mapping for the specified key from this map if present.
     * Not required for Lab 7. If you don't implement this, throw an
     * UnsupportedOperationException. */
    @Override
    public V remove(K key) {
        if (root == null) {
            return null;
        }
        if (root.key == key) {
            V ReturnValue = root.value;
            if (root.left.root == null && root.right.root == null) {
                root = null;
            } else if (root.left.root == null) {
                root = root.right.root;
            } else if (root.right.root == null) {
                root = root.left.root;
            } else {
                K largest = root.left.FindLargest();
                V value = root.left.get(largest);
                root.left.remove(largest);
                root.key = largest;
                root.value = value;
            }
            return ReturnValue;
        } else if (key.compareTo(root.key) < 0){
            return root.left.remove(key);
        } else {
            return root.right.remove(key);
        }
    };

    private K FindLargest() {
        if (root == null) {
            return null;
        }
        BSTMap<K, V> current = this;
        K largest = current.root.key;
        while (current.root != null) {
            largest = current.root.key;
            current = current.root.right;
        }
        return largest;
    }

    /* Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 7. If you don't implement this,
     * throw an UnsupportedOperationException.*/
    @Override
    public V remove(K key, V value) {
        if (get(key) == value) {
            return remove(key);
        } else {
            return null;
        }
    };
}
