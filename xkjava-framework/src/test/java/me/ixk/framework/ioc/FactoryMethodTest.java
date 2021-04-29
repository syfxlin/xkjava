package me.ixk.framework.ioc;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import me.ixk.framework.annotation.core.Bean;
import me.ixk.framework.test.XkJavaTest;
import org.junit.jupiter.api.Test;

/**
 * @author Otstar Lin
 * @date 2021/4/29 下午 2:52
 */
@XkJavaTest
class FactoryMethodTest {

    public static class Config {

        @Bean
        public static Config newInstance() {
            return new Config();
        }
    }

    @Test
    void create() {
        final Config config = XkJava.of().make(Config.class);
        assertNotNull(config);
    }
}
