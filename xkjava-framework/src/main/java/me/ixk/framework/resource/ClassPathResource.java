package me.ixk.framework.resource;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import org.jetbrains.annotations.Nullable;

/**
 * @author Otstar Lin
 * @date 2021/4/18 下午 3:23
 */
public class ClassPathResource extends AbstractResource {

    private final String path;

    public ClassPathResource(final String path) {
        super(FileUtil.getName(normalizePath(path)));
        this.path = normalizePath(path);
    }

    @Override
    public InputStream getStream() {
        final File file = this.getFile();
        if (file == null) {
            return InputStream.nullInputStream();
        }
        return IoUtil.toStream(file);
    }

    @Override
    public @Nullable URL getUrl() {
        return ResourceUtil.getResourceObj(this.path).getUrl();
    }

    @Override
    public @Nullable File getFile() {
        final URL url = this.getUrl();
        if (url == null) {
            return null;
        }
        return new File(URLUtil.toURI(url));
    }

    private static String normalizePath(String path) {
        // 标准化路径
        path = FileUtil.normalize(path);
        path = StrUtil.removePrefix(path, StrUtil.SLASH);

        Assert.isFalse(
            FileUtil.isAbsolutePath(path),
            "Path [{}] must be a relative path !",
            path
        );
        return path;
    }
}
