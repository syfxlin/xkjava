/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import me.ixk.framework.annotations.Autowired;
import me.ixk.framework.test.XkJavaTest;
import org.junit.jupiter.api.Test;

/**
 * @author Otstar Lin
 * @date 2020/12/13 下午 2:52
 */
@XkJavaTest
class CryptTest {

    @Autowired
    Crypt crypt;

    @Test
    void crypt() {
        final String encrypt = crypt.encrypt("123");
        assertNotNull(encrypt);
        assertEquals("123", crypt.decrypt(encrypt));
    }
}
