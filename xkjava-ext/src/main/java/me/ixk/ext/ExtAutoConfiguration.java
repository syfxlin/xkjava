package me.ixk.ext;

import me.ixk.framework.annotation.Bean;
import me.ixk.framework.annotation.Configuration;

/**
 * 自动配置测试类
 *
 * @author Otstar Lin
 * @date 2021/1/31 下午 2:24
 */
@Configuration
public class ExtAutoConfiguration {

    @Bean
    public String extName() {
        return "This is a test data";
    }
}
