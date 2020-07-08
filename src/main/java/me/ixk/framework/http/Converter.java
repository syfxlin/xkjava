/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.http;

public interface Converter<T> {
    default T after(T object) {
        return object;
    }

    default T before(T object) {
        return object;
    }
}
