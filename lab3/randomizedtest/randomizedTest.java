package randomizedtest;
import edu.princeton.cs.introcs.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

public class randomizedTest {
    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> B = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 3);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                B.addLast(randVal);
            } else if (operationNumber == 1) {
                // size
                if(L.size() > 0){
                    int LLast = L.getLast();
                    int BLast = B.getLast();
                    assertEquals(LLast, BLast);
                }
            } else if (operationNumber == 2) {
                if(L.size() > 0){
                    int LLast = L.removeLast();
                    int BLast = B.removeLast();
                    assertEquals(LLast, BLast);
                }
            }
        }
    }
}
