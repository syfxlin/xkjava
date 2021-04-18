package me.ixk.framework.resource;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import me.ixk.framework.util.ResourceUtils;
import org.jetbrains.annotations.Nullable;

/**
 * 统一资源接口
 *
 * @author Otstar Lin
 * @date 2021/4/18 下午 3:03
 */
public interface Resource {
    public static final String CLASSPATH_URL_PREFIX = "classpath:";

    /**
     * 获取输入流
     *
     * @return 输入流
     */
    InputStream getStream();

    /**
     * 获取 URL
     *
     * @return URL
     */
    @Nullable
    URL getUrl();

    /**
     * 获取文件名称
     *
     * @return 文件名
     */
    @Nullable
    String getName();

    /**
     * 获取 File 对象，有些资源不支持会返回 null
     *
     * @return File 对象
     */
    @Nullable
    File getFile();

    /**
     * 获取 Resource
     *
     * @param location 位置
     * @return Resource
     */
    static Resource create(String location) {
        if (location.startsWith("/")) {
            return new FileResource(location);
        } else if (location.startsWith(CLASSPATH_URL_PREFIX)) {
            return new ClassPathResource(
                location.substring(CLASSPATH_URL_PREFIX.length())
            );
        } else {
            try {
                // Try to parse the location as a URL...
                URL url = new URL(location);
                return (
                    ResourceUtils.isFileUrl(url)
                        ? new FileResource(url.getFile())
                        : new UrlResource(url)
                );
            } catch (MalformedURLException ex) {
                // No URL -> resolve as resource path.
                return new ClassPathResource(location);
            }
        }
    }

    static Resource create(URL url) {
        return create(url.toString());
    }

    static Resource create(URI uri) {
        return create(uri.toString());
    }

    static Resource create(File file) {
        return new FileResource(file);
    }

    static Resource create(Path path) {
        return new FileResource(path);
    }
}
