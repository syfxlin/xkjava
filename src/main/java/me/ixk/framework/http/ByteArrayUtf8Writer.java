package me.ixk.framework.http;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.eclipse.jetty.util.ByteArrayOutputStream2;

public class ByteArrayUtf8Writer extends Writer {
    private byte[] _buf;
    private int _size;
    private ByteArrayOutputStream2 _bout = null;
    private OutputStreamWriter _writer = null;
    private boolean _fixed = false;

    public ByteArrayUtf8Writer() {
        _buf = new byte[2048];
    }

    public ByteArrayUtf8Writer(int capacity) {
        _buf = new byte[capacity];
    }

    public ByteArrayUtf8Writer(byte[] buf) {
        _buf = buf;
        _fixed = true;
    }

    public Object getLock() {
        return lock;
    }

    public int size() {
        return _size;
    }

    public int capacity() {
        return _buf.length;
    }

    public int spareCapacity() {
        return _buf.length - _size;
    }

    public void setLength(int l) {
        _size = l;
    }

    public byte[] getBuf() {
        return _buf;
    }

    public void writeTo(OutputStream out) throws IOException {
        out.write(_buf, 0, _size);
    }

    public void write(char c) throws IOException {
        ensureSpareCapacity(1);
        if (c <= 0x7f) _buf[_size++] = (byte) c; else {
            char[] ca = { c };
            writeEncoded(ca, 0, 1);
        }
    }

    @Override
    public void write(char[] ca) throws IOException {
        ensureSpareCapacity(ca.length);
        for (int i = 0; i < ca.length; i++) {
            char c = ca[i];
            if (c <= 0x7f) _buf[_size++] = (byte) c; else {
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
            if (c <= 0x7f) _buf[_size++] = (byte) c; else {
                writeEncoded(ca, offset + i, length - i);
                break;
            }
        }
    }

    @Override
    public void write(String s) throws IOException {
        if (s == null) {
            write("null", 0, 4);
            return;
        }

        int length = s.length();
        ensureSpareCapacity(length);
        for (int i = 0; i < length; i++) {
            char c = s.charAt(i);
            if (c <= 0x7f) _buf[_size++] = (byte) c; else {
                writeEncoded(s.toCharArray(), i, length - i);
                break;
            }
        }
    }

    @Override
    public void write(String s, int offset, int length) throws IOException {
        ensureSpareCapacity(length);
        for (int i = 0; i < length; i++) {
            char c = s.charAt(offset + i);
            if (c <= 0x7f) _buf[_size++] = (byte) c; else {
                writeEncoded(s.toCharArray(), offset + i, length - i);
                break;
            }
        }
    }

    private void writeEncoded(char[] ca, int offset, int length)
        throws IOException {
        if (_bout == null) {
            _bout = new ByteArrayOutputStream2(2 * length);
            _writer = new OutputStreamWriter(_bout, StandardCharsets.UTF_8);
        } else _bout.reset();
        _writer.write(ca, offset, length);
        _writer.flush();
        ensureSpareCapacity(_bout.getCount());
        System.arraycopy(_bout.getBuf(), 0, _buf, _size, _bout.getCount());
        _size += _bout.getCount();
    }

    @Override
    public void flush() {}

    public void resetWriter() {
        _size = 0;
    }

    @Override
    public void close() {}

    public void destroy() {
        _buf = null;
    }

    public void ensureSpareCapacity(int n) throws IOException {
        if (_size + n > _buf.length) {
            if (_fixed) throw new IOException(
                "Buffer overflow: " + _buf.length
            );
            _buf = Arrays.copyOf(_buf, (_buf.length + n) * 4 / 3);
        }
    }

    public byte[] getByteArray() {
        return Arrays.copyOf(_buf, _size);
    }
}
