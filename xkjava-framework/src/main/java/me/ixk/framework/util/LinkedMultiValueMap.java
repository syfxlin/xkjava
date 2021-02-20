/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.util;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * LinkedMultiValueMap
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 5:04
 */
public class LinkedMultiValueMap<K, V>
    extends LinkedHashMap<K, List<V>>
    implements MultiValueMap<K, V> {}
