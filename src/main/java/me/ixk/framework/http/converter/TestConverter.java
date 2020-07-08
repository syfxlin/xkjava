/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.http.converter;

import me.ixk.framework.http.Converter;

public class TestConverter implements Converter<String> {

    @Override
    public String after(String object) {
        return "testConverter:" + object;
    }
}
