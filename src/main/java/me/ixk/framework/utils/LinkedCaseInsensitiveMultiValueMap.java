package me.ixk.framework.utils;

import java.util.List;
import java.util.Map;

public class LinkedCaseInsensitiveMultiValueMap<V>
    extends LinkedCaseInsensitiveHashMap<List<V>>
    implements MultiValueMap<String, V> {

    public LinkedCaseInsensitiveMultiValueMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public LinkedCaseInsensitiveMultiValueMap(int initialCapacity) {
        super(initialCapacity);
    }

    public LinkedCaseInsensitiveMultiValueMap() {
        super();
    }

    public LinkedCaseInsensitiveMultiValueMap(int initialCapacity, float loadFactor, boolean accessOrder) {
        super(initialCapacity, loadFactor, accessOrder);
    }

    public LinkedCaseInsensitiveMultiValueMap(
        Map<? extends String, ? extends List<V>> m
    ) {
        super(m);
    }
}
