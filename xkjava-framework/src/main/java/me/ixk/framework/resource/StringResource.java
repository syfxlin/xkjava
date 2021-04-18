package me.ixk.framework.resource;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.jetbrains.annotations.Nullable;

/**
 * @author Otstar Lin
 * @date 2021/4/18 下午 3:14
 */
public class StringResource extends AbstractResource {

    private final CharSequence sequence;
    private final Charset charset;

    public StringResource(final CharSequence sequence) {
        this(sequence, null);
    }

    public StringResource(final CharSequence sequence, final String name) {
        this(sequence, name, StandardCharsets.UTF_8);
    }

    public StringResource(
        final CharSequence sequence,
        final String name,
        final Charset charset
    ) {
        super(name);
        this.sequence = sequence;
        this.charset = charset;
    }

    @Override
    public InputStream getStream() {
        return new ByteArrayInputStream(
            this.sequence.toString().getBytes(this.charset)
        );
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
