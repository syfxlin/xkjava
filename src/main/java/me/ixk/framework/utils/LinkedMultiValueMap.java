/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.utils;

import java.util.LinkedHashMap;
import java.util.List;

public class LinkedMultiValueMap<K, V>
    extends LinkedHashMap<K, List<V>>
    implements MultiValueMap<K, V> {}
