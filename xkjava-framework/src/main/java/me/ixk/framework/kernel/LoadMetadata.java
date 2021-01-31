package me.ixk.framework.kernel;

import java.util.List;
import java.util.Map;
import me.ixk.framework.ioc.BeanScanner;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.property.Metadata;

/**
 * 读取元数据
 *
 * @author Otstar Lin
 * @date 2021/1/31 下午 2:12
 */
public class LoadMetadata {

    private static final String AUTO_CONFIGURATION_NAME = "auto-configuration";

    private final XkJava app;

    public LoadMetadata(final XkJava app) {
        this.app = app;
    }

    public void load() {
        final Map<String, List<String>> metadataMap = XkJavaMetadataLoader.loadMetadata();
        final Metadata metadata = new Metadata("metadata", metadataMap);
        this.app.instance("metadata", metadata);

        // 读取自动配置元信息
        this.loadAutoConfiguration(metadata);
    }

    private void loadAutoConfiguration(final Metadata metadata) {
        final BeanScanner scanner = this.app.beanScanner();
        final String[] configs = metadata.get(
            AUTO_CONFIGURATION_NAME,
            String[].class
        );
        scanner.addDefinition(configs);
    }
}
