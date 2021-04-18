package me.ixk.framework.resource;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import org.jetbrains.annotations.Nullable;

/**
 * @author Otstar Lin
 * @date 2021/4/18 下午 4:15
 */
public class InputStreamResource extends AbstractResource {

    private final InputStream in;

    public InputStreamResource(final InputStream in) {
        this(in, null);
    }

    public InputStreamResource(
        final InputStream in,
        @Nullable final String name
    ) {
        super(name);
        this.in = in;
    }

    @Override
    public InputStream getStream() {
        return this.in;
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
