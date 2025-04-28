package deque;
import java.util.Comparator;

public class Bigger<T extends Comparable<T>> implements Comparator<T>{
    public int compare(T a, T b) {
        int r = a.compareTo(b);
        if (r > 1) {
            return 1;
        } else if (r < 0) {
            return -1;
        } else {
            return 0;
        }
    }
}
