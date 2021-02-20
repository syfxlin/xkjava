package me.ixk.framework.kernel;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import me.ixk.framework.util.Convert;
import me.ixk.framework.util.LinkedMultiValueMap;
import me.ixk.framework.util.MultiValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 框架元信息读取器
 *
 * @author Otstar Lin
 * @date 2021/1/30 下午 11:24
 */
public class XkJavaMetadataLoader {

    public static final String METADATA_RESOURCE_LOCATION =
        "META-INF/xkjava.properties";

    private static final Logger log = LoggerFactory.getLogger(
        XkJavaMetadataLoader.class
    );

    public static Map<String, List<String>> loadMetadata() {
        MultiValueMap<String, String> result = new LinkedMultiValueMap<>();
        try {
            final Enumeration<URL> resources =
                XkJavaMetadataLoader.class.getClassLoader()
                    .getResources(METADATA_RESOURCE_LOCATION);
            while (resources.hasMoreElements()) {
                final URL url = resources.nextElement();
                if (log.isDebugEnabled()) {
                    log.debug("Load metadata [{}]", url);
                }
                Properties properties = new Properties();
                properties.load(url.openConnection().getInputStream());
                for (Map.Entry<?, ?> entry : properties.entrySet()) {
                    String name = ((String) entry.getKey()).trim();
                    for (String itemName : Convert.convert(
                        String[].class,
                        entry.getValue()
                    )) {
                        result.add(name, itemName.trim());
                    }
                }
            }
            return result;
        } catch (final IOException e) {
            throw new IllegalArgumentException(
                "Unable to load factories from location [" +
                METADATA_RESOURCE_LOCATION +
                "]",
                e
            );
        }
    }
}
