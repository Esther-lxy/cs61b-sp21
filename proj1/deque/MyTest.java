package deque;

import edu.princeton.cs.introcs.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

public class MyTest {
    @Test
    public void randomizedTest() {
        LinkedListDeque<Integer> L = new LinkedListDeque<>();
        ArrayDeque<Integer> B = new ArrayDeque<>();

        int N = 10000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                B.addLast(randVal);
            } else if (operationNumber == 1) {
                // addFirst
                int randVal = StdRandom.uniform(0, 100);
                L.addFirst(randVal);
                B.addFirst(randVal);
            } else if (operationNumber == 2) {
                // size
                if (L.size() > 0) {
                    int LLast = L.removeFirst();
                    int BLast = B.removeFirst();
                    assertEquals(LLast, BLast);
                }
            } else if (operationNumber == 3) {
                if (L.size() > 0) {
                    int LLast = L.removeLast();
                    int BLast = B.removeLast();
                    assertEquals(LLast, BLast);
                }
            }
        }
    }

    @Test
    public void GetTest() {
        LinkedListDeque<String> L = new LinkedListDeque<>();
        ArrayDeque<String> A = new ArrayDeque<>();

        A.addFirst("love");
        A.addLast("coding");
        A.addFirst("i");
        A.addFirst("and");
        A.addFirst("Emily");
        A.addLast("but");
        A.addLast("we");
        A.addLast("do");
        A.addLast("not");
        A.addLast("like");
        A.addLast("testing");
        String f = "i";
        String v = A.get(2);
        assertEquals(f, v);

    }
}
