/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.jetbrains.annotations.NotNull;

/**
 * ByteArrayUtf8Writer
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 5:01
 */
public class ByteArrayUtf8Writer extends Writer {

    private byte[] buf;
    private int size;
    private ByteArrayOutputStream2 bout = null;
    private OutputStreamWriter writer = null;
    private boolean fixed = false;

    public ByteArrayUtf8Writer() {
        buf = new byte[2048];
    }

    public ByteArrayUtf8Writer(int capacity) {
        buf = new byte[capacity];
    }

    public ByteArrayUtf8Writer(byte[] buf) {
        this.buf = buf;
        fixed = true;
    }

    public Object getLock() {
        return lock;
    }

    public int size() {
        return size;
    }

    public int capacity() {
        return buf.length;
    }

    public int spareCapacity() {
        return buf.length - size;
    }

    public void setLength(int l) {
        size = l;
    }

    public byte[] getBuf() {
        return buf;
    }

    public void writeTo(OutputStream out) throws IOException {
        out.write(buf, 0, size);
    }

    public void write(char c) throws IOException {
        ensureSpareCapacity(1);
        if (c <= 0x7f) {
            buf[size++] = (byte) c;
        } else {
            char[] ca = { c };
            writeEncoded(ca, 0, 1);
        }
    }

    @Override
    public void write(char[] ca) throws IOException {
        ensureSpareCapacity(ca.length);
        for (int i = 0; i < ca.length; i++) {
            char c = ca[i];
            if (c <= 0x7f) {
                buf[size++] = (byte) c;
            } else {
                writeEncoded(ca, i, ca.length - i);
                break;
            }
        }
    }

    @Override
    public void write(char[] ca, int offset, int length) throws IOException {
        ensureSpareCapacity(length);
        for (int i = 0; i < length; i++) {
            char c = ca[offset + i];
            if (c <= 0x7f) {
                buf[size++] = (byte) c;
            } else {
                writeEncoded(ca, offset + i, length - i);
                break;
            }
        }
    }

    @Override
    public void write(@NotNull String s) throws IOException {
        if (s == null) {
            write("null", 0, 4);
            return;
        }

        int length = s.length();
        ensureSpareCapacity(length);
        for (int i = 0; i < length; i++) {
            char c = s.charAt(i);
            if (c <= 0x7f) {
                buf[size++] = (byte) c;
            } else {
                writeEncoded(s.toCharArray(), i, length - i);
                break;
            }
        }
    }

    @Override
    public void write(@NotNull String s, int offset, int length)
        throws IOException {
        ensureSpareCapacity(length);
        for (int i = 0; i < length; i++) {
            char c = s.charAt(offset + i);
            if (c <= 0x7f) {
                buf[size++] = (byte) c;
            } else {
                writeEncoded(s.toCharArray(), offset + i, length - i);
                break;
            }
        }
    }

    private void writeEncoded(char[] ca, int offset, int length)
        throws IOException {
        if (bout == null) {
            bout = new ByteArrayOutputStream2(2 * length);
            writer = new OutputStreamWriter(bout, StandardCharsets.UTF_8);
        } else {
            bout.reset();
        }
        writer.write(ca, offset, length);
        writer.flush();
        ensureSpareCapacity(bout.getCount());
        System.arraycopy(bout.getBuf(), 0, buf, size, bout.getCount());
        size += bout.getCount();
    }

    @Override
    public void flush() {}

    public void resetWriter() {
        size = 0;
    }

    @Override
    public void close() {}

    public void destroy() {
        buf = null;
    }

    public void ensureSpareCapacity(int n) throws IOException {
        if (size + n > buf.length) {
            if (fixed) {
                throw new IOException("Buffer overflow: " + buf.length);
            }
            buf = Arrays.copyOf(buf, (buf.length + n) * 4 / 3);
        }
    }

    public byte[] getByteArray() {
        return Arrays.copyOf(buf, size);
    }

    @Override
    public String toString() {
        return new String(buf, 0, size);
    }
}
