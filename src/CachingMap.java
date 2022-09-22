import java.util.LinkedHashMap;
import java.util.Map;

/**
 * CachingMap: A LinkedHashMap that keeps only the most recently accessed elements
 * @param <K> Key
 * @param <V> Value
 */
class CachingMap<K, V> extends LinkedHashMap<K, V> {
    private final int capacity;

    /**
     * CachingMap constructor
     * @param capacity Maximum capacity before removing least accessed entries
     */
    public CachingMap(int capacity) {
        super(capacity, 0.75F, true);
        this.capacity = capacity;
    }

    /**
     * removeEldestEntry: Removes the least accessed entry upon inserting a new element
     * @param eldest Entry to remove
     * @return Returns boolean.
     */
    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;
    }

    @Override
    public V get(Object key) {
        return super.get(key);
    }

    @Override
    public V put(K key, V value) {
        return super.put(key, value);
    }
}
