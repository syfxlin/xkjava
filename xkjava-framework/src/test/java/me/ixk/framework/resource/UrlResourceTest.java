package me.ixk.framework.resource;

import static org.junit.jupiter.api.Assertions.assertTrue;

import cn.hutool.core.io.IoUtil;
import org.junit.jupiter.api.Test;

/**
 * @author Otstar Lin
 * @date 2021/4/18 下午 4:21
 */
class UrlResourceTest {

    @Test
    void getStream() {
        final UrlResource resource = new UrlResource("https://ixk.me");
        final String s = IoUtil.readUtf8(resource.getStream());
        assertTrue(s.length() > 0);
    }
}
