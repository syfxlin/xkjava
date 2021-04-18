package me.ixk.framework.resource;

import cn.hutool.core.util.URLUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import me.ixk.framework.exception.ResourceException;
import org.jetbrains.annotations.Nullable;

/**
 * @author Otstar Lin
 * @date 2021/4/18 下午 4:16
 */
public class UrlResource extends AbstractResource {

    private final URL url;

    public UrlResource(final String url) {
        this(URLUtil.url(url));
    }

    public UrlResource(final URL url) {
        this(url, null);
    }

    public UrlResource(final URL url, @Nullable final String name) {
        super(name);
        this.url = url;
    }

    @Override
    public InputStream getStream() {
        try {
            final URLConnection con = this.url.openConnection();
            try {
                return con.getInputStream();
            } catch (final IOException ex) {
                if (con instanceof HttpURLConnection) {
                    ((HttpURLConnection) con).disconnect();
                }
                throw new ResourceException(ex);
            }
        } catch (final IOException e) {
            throw new ResourceException(e);
        }
    }

    @Override
    public @Nullable URL getUrl() {
        return this.url;
    }

    @Override
    public @Nullable File getFile() {
        return new File(URLUtil.toURI(this.url));
    }
}
