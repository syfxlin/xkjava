/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.test;

import me.ixk.framework.ioc.XkJava;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstanceFactory;
import org.junit.jupiter.api.extension.TestInstanceFactoryContext;
import org.junit.jupiter.api.extension.TestInstantiationException;

/**
 * XkJava JUnit 调试回调
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 4:55
 */
public class XkJavaRunner implements BeforeAllCallback, TestInstanceFactory {

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        XkJava.create().boot(new Class[] { context.getRequiredTestClass() });
    }

    @Override
    public Object createTestInstance(
        TestInstanceFactoryContext factoryContext,
        ExtensionContext extensionContext
    )
        throws TestInstantiationException {
        final XkJava app = XkJava.of();
        final Class<?> testType = factoryContext.getTestClass();
        app.bind(testType);
        return app.make(testType);
    }
}
