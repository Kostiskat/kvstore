package me.ccute.kvstore.server.storage;

import java.util.concurrent.ConcurrentHashMap;

/**
 * A concurrent map that enforces a maximum size.
 * If the size is exceeded, it uses a high-speed pseudo-random eviction
 * strategy to free memory instantly.
 */
public class BoundedConcurrentMap<K, V> extends ConcurrentHashMap<K, V> {

    private final int maxSize;

    public BoundedConcurrentMap(int maxSize) {
        super();
        this.maxSize = maxSize;
    }

    @Override
    public V compute(K key, java.util.function.BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        if(!this.containsKey(key)) {
            evictIfNecessary();
        }
        return super.compute(key, remappingFunction);
    }

    private void evictIfNecessary() {
        // If we hit the limit, delete the key to free memory instantly.
        // Because ConcurrentHashMap iterators are weakly consistent,
        // grabbing the 'nextElement()' acts as a lightning-fast pseudo-random selector.
        if(this.size() >= maxSize) {
            K randomKey = this.keys().nextElement();
            this.remove(randomKey);
        }
    }


}
