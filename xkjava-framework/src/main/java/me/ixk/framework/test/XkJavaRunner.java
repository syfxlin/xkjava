/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.test;

import me.ixk.framework.ioc.XkJava;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * XkJava JUnit 调试回调
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 4:55
 */
public class XkJavaRunner implements BeforeAllCallback {

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        XkJava.create().boot(new Class[] { context.getRequiredTestClass() });
    }
}
