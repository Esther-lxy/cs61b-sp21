package deque;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Comparator;

public class MaxArrayDexTest {
    public class IntBigger<T extends Comparable<T>> implements Comparator<T> {
        public int compare(T a, T b) {
            int r = a.compareTo(b);
            if (r > 0) {
                return 1;
            } else if (r < 0) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    @Test
    public void testIntBigger() {
        Comparator<Integer> IntCom = new IntBigger<Integer>();
        MaxArrayDeque<Integer> IntmaxA = new MaxArrayDeque<>(IntCom);
        IntmaxA.addLast(3);
        IntmaxA.addLast(5);
        IntmaxA.addFirst(6);
        IntmaxA.addFirst(8);
        IntmaxA.addFirst(4);
        int re = IntmaxA.max();
        assertEquals(re, 8);
    }

    public class IntSmaller <T extends Comparable<T>> implements Comparator<T> {
        public int compare(T a, T b) {
            int r = a.compareTo(b);
            if (r > 0) {
                return -1;
            } else if (r < 0) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    @Test
    public void testIntSmaller() {
        Comparator<Integer> IntComS = new IntSmaller<Integer>();
        MaxArrayDeque<Integer> IntmaxA = new MaxArrayDeque<>(IntComS);
        IntmaxA.addLast(3);
        IntmaxA.addLast(5);
        IntmaxA.addFirst(6);
        IntmaxA.addFirst(8);
        IntmaxA.addFirst(4);
        Comparator<Integer> IntComB = new IntBigger<Integer>();
        int re = IntmaxA.max(IntComB);
        assertEquals(re, 8);
    }
}





