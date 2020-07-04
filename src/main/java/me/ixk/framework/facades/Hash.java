/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.facades;

public class Hash extends AbstractFacade {

    protected static me.ixk.framework.utils.Hash make() {
        return app.make(me.ixk.framework.utils.Hash.class);
    }

    public static String make(String value) {
        return make().make(value);
    }

    public static boolean check(String value, String hashedValue) {
        return make().check(value, hashedValue);
    }
}
