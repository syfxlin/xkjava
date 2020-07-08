/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.http;

public interface Converter {
    default <T> T after(T object) {
        return object;
    }

    default <T> T before(T object) {
        return object;
    }
}
