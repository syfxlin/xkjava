/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.utils;

import cn.hutool.core.lang.SimpleCache;

/**
 * 软引用缓存
 *
 * @author Otstar Lin
 * @date 2020/12/2 下午 12:14
 */
public class SoftSimpleCache<K, V> extends SimpleCache<K, V> {

    public SoftSimpleCache() {
        super(new SoftHashMap<>());
    }
}
