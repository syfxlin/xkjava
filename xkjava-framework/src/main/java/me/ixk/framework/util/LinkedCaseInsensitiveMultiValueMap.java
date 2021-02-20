/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.util;

import java.util.List;
import java.util.Map;

/**
 * LinkedCaseInsensitiveMultiValueMap
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 5:04
 */
public class LinkedCaseInsensitiveMultiValueMap<V>
    extends LinkedCaseInsensitiveHashMap<List<V>>
    implements MultiValueMap<String, V> {

    public LinkedCaseInsensitiveMultiValueMap(
        int initialCapacity,
        float loadFactor
    ) {
        super(initialCapacity, loadFactor);
    }

    public LinkedCaseInsensitiveMultiValueMap(int initialCapacity) {
        super(initialCapacity);
    }

    public LinkedCaseInsensitiveMultiValueMap() {
        super();
    }

    public LinkedCaseInsensitiveMultiValueMap(
        int initialCapacity,
        float loadFactor,
        boolean accessOrder
    ) {
        super(initialCapacity, loadFactor, accessOrder);
    }

    public LinkedCaseInsensitiveMultiValueMap(
        Map<? extends String, ? extends List<V>> m
    ) {
        super(m);
    }
}
