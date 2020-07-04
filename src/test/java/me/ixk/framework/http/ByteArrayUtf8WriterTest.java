/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.http;

import me.ixk.framework.utils.ByteArrayUtf8Writer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class ByteArrayUtf8WriterTest {

    @Test
    void writeAndRead() throws IOException {
        ByteArrayUtf8Writer writer = new ByteArrayUtf8Writer();
        writer.write("123456");
        Assertions.assertEquals("123456", writer.toString());
    }
}
