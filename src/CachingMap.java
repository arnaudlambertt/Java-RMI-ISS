import java.util.LinkedHashMap;
import java.util.Map;

class CachingMap<K, V> extends LinkedHashMap<K, V> {
    private final int capacity;

    public CachingMap(int capacity) {
        super(capacity, 0.75F, true);
        this.capacity = capacity;
    }

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
