/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.util;

import at.favre.lib.crypto.bcrypt.BCrypt;

/**
 * Hash 工具类
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 5:03
 */
public class Hash {

    public String make(final String value) {
        return BCrypt.withDefaults().hashToString(10, value.toCharArray());
    }

    public boolean check(final String value, final String hashedValue) {
        return BCrypt
            .verifyer()
            .verify(value.toCharArray(), hashedValue.toCharArray())
            .verified;
    }
}
