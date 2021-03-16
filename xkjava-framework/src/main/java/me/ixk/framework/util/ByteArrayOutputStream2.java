/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.util;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;

/**
 * ByteArrayOutputStream2
 *
 * @author Otstar Lin
 * @date 2020/10/24 下午 2:31
 */
public class ByteArrayOutputStream2 extends ByteArrayOutputStream {

    public ByteArrayOutputStream2() {
        super();
    }

    public ByteArrayOutputStream2(final int size) {
        super(size);
    }

    public byte[] getBuf() {
        return buf;
    }

    public int getCount() {
        return count;
    }

    public void setCount(final int count) {
        this.count = count;
    }

    public void reset(final int minSize) {
        reset();
        if (buf.length < minSize) {
            buf = new byte[minSize];
        }
    }

    public void writeUnchecked(final int b) {
        buf[count++] = (byte) b;
    }

    @Override
    public synchronized String toString(final Charset charset) {
        return new String(buf, 0, count, charset);
    }
}
