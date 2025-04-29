package deque;
import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T>{
    private Comparator<T> def;

    public MaxArrayDeque(Comparator<T> c) {
        def = c;
    }

    public T max() {
        return max(def);
    }

    public T max(Comparator<T> c) {
        int maxIndex = 0;
        T max = get(maxIndex);
        for (int i = 0; i < size(); i++) {
            T curr = get(i);
            if (c.compare(curr, max) > 0) {
                maxIndex = i;
                max = get(maxIndex);
            }
        }
        return max;
    }
}
