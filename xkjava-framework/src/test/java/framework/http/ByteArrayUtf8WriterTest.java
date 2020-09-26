/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package framework.http;

import java.io.IOException;
import me.ixk.framework.utils.ByteArrayUtf8Writer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ByteArrayUtf8WriterTest {

    @Test
    void writeAndRead() throws IOException {
        final ByteArrayUtf8Writer writer = new ByteArrayUtf8Writer();
        writer.write("123456");
        Assertions.assertEquals("123456", writer.toString());
    }
}