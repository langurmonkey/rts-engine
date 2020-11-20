package arties.datastructure;

/**
 * Simple implementation of Pair
 *
 * @param <T>
 * @param <K>
 * @author Toni Sagrista
 */
public class Pair<T, K> {

    public T first;
    public K second;

    public Pair(T t, K k) {
        first = t;
        second = k;
    }
}
