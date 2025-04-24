package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */

public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testBuggyAList(){
        AListNoResizing<Integer> n = new AListNoResizing<>();
        BuggyAList<Integer> b = new BuggyAList<>();
        n.addLast(4);
        n.addLast(5);
        n.addLast(6);
        b.addLast(4);
        b.addLast(5);
        b.addLast(6);
        int last_n = n.removeLast();
        int last_b = b.removeLast();
        assertEquals(last_n, last_b);
        int second_n = n.removeLast();
        int second_b = b.removeLast();
        assertEquals(second_n, second_b);
        int first_n = n.removeLast();
        int first_b = b.removeLast();
        assertEquals(first_n, first_b);
    }

}
