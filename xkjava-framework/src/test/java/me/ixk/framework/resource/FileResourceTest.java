package me.ixk.framework.resource;

import static org.junit.jupiter.api.Assertions.assertTrue;

import cn.hutool.core.io.IoUtil;
import org.junit.jupiter.api.Test;

/**
 * @author Otstar Lin
 * @date 2021/4/18 下午 4:11
 */
class FileResourceTest {

    @Test
    void getStream() {
        final FileResource resource = new FileResource(
            "F:\\workspace\\project\\xkjava\\xkjava-framework\\src\\main\\resources\\application.properties"
        );
        final String s = IoUtil.readUtf8(resource.getStream());
        assertTrue(s.length() > 0);
    }
}
