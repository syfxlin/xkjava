package me.ixk.framework.resource;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.URLUtil;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import org.jetbrains.annotations.Nullable;

/**
 * @author Otstar Lin
 * @date 2021/4/18 下午 4:07
 */
public class FileResource extends AbstractResource {

    private final File file;

    public FileResource(final String path) {
        this(FileUtil.file(path));
    }

    public FileResource(final Path path) {
        this(path.toFile());
    }

    public FileResource(final File file) {
        super(FileUtil.getName(file));
        this.file = file;
    }

    @Override
    public InputStream getStream() {
        return IoUtil.toStream(this.file);
    }

    @Override
    public @Nullable URL getUrl() {
        return URLUtil.getURL(this.file);
    }

    @Override
    public @Nullable File getFile() {
        return this.file;
    }
}
