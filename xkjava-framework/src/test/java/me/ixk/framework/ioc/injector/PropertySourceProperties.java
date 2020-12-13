/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.injector;

import me.ixk.framework.annotations.ConfigurationProperties;
import me.ixk.framework.annotations.PropertySource;
import me.ixk.framework.annotations.PropertySources;
import me.ixk.framework.annotations.PropertyValue;
import me.ixk.framework.annotations.Value;
import me.ixk.framework.config.ClassProperty;
import me.ixk.framework.ioc.PropertyResolver;

/**
 * @author Otstar Lin
 * @date 2020/11/29 下午 1:17
 */
@PropertySources(
    @PropertySource(
        location = "classpath:/test.properties",
        value = { "test1=true", "skip=true" }
    )
)
@ConfigurationProperties
public class PropertySourceProperties {

    private boolean test;

    @Value("${test}")
    private boolean value;

    @Value("#{#root['test']}")
    private boolean valueEl;

    @PropertyValue(name = "test1")
    private boolean test2;

    @PropertyValue(resolver = Test3Resolver.class)
    private String test3;

    @PropertyValue(skip = true)
    private boolean skip;

    public boolean isTest() {
        return test;
    }

    public void setTest(final boolean test) {
        this.test = test;
    }

    public boolean isValue() {
        return value;
    }

    public void setValue(final boolean value) {
        this.value = value;
    }

    public boolean isValueEl() {
        return valueEl;
    }

    public void setValueEl(final boolean valueEl) {
        this.valueEl = valueEl;
    }

    public boolean isTest1() {
        return test2;
    }

    public void setTest1(boolean test1) {
        this.test2 = test1;
    }

    public String getTest3() {
        return test3;
    }

    public void setTest3(String test3) {
        this.test3 = test3;
    }

    public boolean isSkip() {
        return skip;
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }

    public static class Test3Resolver implements PropertyResolver {

        @Override
        public boolean supportsProperty(String value, ClassProperty property) {
            return true;
        }

        @Override
        public Object resolveProperty(String value, ClassProperty property) {
            return "test3";
        }
    }
}
