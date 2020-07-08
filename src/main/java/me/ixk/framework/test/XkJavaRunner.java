/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.test;

import me.ixk.framework.ioc.Application;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class XkJavaRunner implements BeforeAllCallback {

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        Application
            .create()
            .bootNoServer(new Class[] { context.getRequiredTestClass() });
    }
}
