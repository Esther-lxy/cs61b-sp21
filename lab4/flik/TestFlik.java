package flik;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestFlik {
    @Test
    public void TestEqual(){
        int a = 128;
        int b = 128;
        int c = 5;
        System.out.println((128 == 128));
        assertTrue(Flik.isSameNumber(a, b));
        assertFalse(Flik.isSameNumber(a, c));
    }
}
