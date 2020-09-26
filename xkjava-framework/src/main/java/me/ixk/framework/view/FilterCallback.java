/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.view;

@FunctionalInterface
public interface FilterCallback {
    String filter(String result);
}
