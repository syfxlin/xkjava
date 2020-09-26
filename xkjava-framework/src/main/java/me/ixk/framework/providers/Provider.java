/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.providers;

public interface Provider {
    void register();
    void boot();
    boolean isBooted();
    void setBooted(boolean booted);
}
