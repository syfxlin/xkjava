package me.ixk.framework.resource;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import org.jetbrains.annotations.Nullable;

/**
 * 字节资源
 *
 * @author Otstar Lin
 * @date 2021/4/18 下午 3:07
 */
public class BytesResource extends AbstractResource {

    private final byte[] bytes;

    public BytesResource(final byte[] bytes) {
        this(bytes, null);
    }

    public BytesResource(final byte[] bytes, final String name) {
        super(name);
        this.bytes = bytes;
    }

    @Override
    public InputStream getStream() {
        return new ByteArrayInputStream(this.bytes);
    }

    @Override
    public @Nullable URL getUrl() {
        return null;
    }

    @Override
    public @Nullable File getFile() {
        return null;
    }
}
