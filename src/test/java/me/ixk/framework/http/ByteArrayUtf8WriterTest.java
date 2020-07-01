package me.ixk.framework.http;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ByteArrayUtf8WriterTest {

    @Test
    void writeAndRead() throws IOException {
        ByteArrayUtf8Writer writer = new ByteArrayUtf8Writer();
        writer.write("123456");
        Assertions.assertEquals("123456", writer.toString());
    }
}
