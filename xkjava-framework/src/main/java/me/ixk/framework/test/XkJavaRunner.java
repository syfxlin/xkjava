/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.test.event.AfterTestAllEvent;
import me.ixk.framework.test.event.AfterTestEachEvent;
import me.ixk.framework.test.event.AfterTestExecutionEvent;
import me.ixk.framework.test.event.BeforeTestAllEvent;
import me.ixk.framework.test.event.BeforeTestEachEvent;
import me.ixk.framework.test.event.BeforeTestExecutionEvent;
import me.ixk.framework.util.MergedAnnotation;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import org.junit.jupiter.api.extension.TestInstanceFactory;
import org.junit.jupiter.api.extension.TestInstanceFactoryContext;
import org.junit.jupiter.api.extension.TestInstantiationException;

/**
 * XkJava JUnit 调试回调
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 4:55
 */
public class XkJavaRunner
    implements
        BeforeAllCallback,
        BeforeTestExecutionCallback,
        BeforeEachCallback,
        AfterAllCallback,
        AfterTestExecutionCallback,
        AfterEachCallback,
        TestInstanceFactory,
        ParameterResolver,
        InvocationInterceptor {

    private static final String CONFIG_LOCATION_NAME =
        "--xkjava.config.location=";
    private static final String CONFIG_NAME_NAME = "--xkjava.config.name=";
    private static final String CONFIG_ACTIVE_NAME = "--xkjava.config.active=";
    private static final String CONFIG_IMPORT_NAME = "--xkjava.config.import=";
    private XkJava app;

    @Override
    public void beforeAll(final ExtensionContext context) {
        final Class<?> testClass = context.getRequiredTestClass();
        final MergedAnnotation annotation = MergedAnnotation.from(testClass);
        final XkJavaTest xkJavaTest = annotation.getAnnotation(
            XkJavaTest.class
        );
        final List<String> args = new ArrayList<>(
            Arrays.asList(xkJavaTest.args())
        );
        if (!xkJavaTest.location().isEmpty()) {
            args.add(CONFIG_LOCATION_NAME + xkJavaTest.location());
        }
        if (!xkJavaTest.name().isEmpty()) {
            args.add(CONFIG_NAME_NAME + xkJavaTest.name());
        }
        if (!xkJavaTest.active().isEmpty()) {
            args.add(CONFIG_ACTIVE_NAME + xkJavaTest.active());
        }
        if (xkJavaTest.imports().length > 0) {
            args.add(
                CONFIG_IMPORT_NAME + String.join(",", xkJavaTest.imports())
            );
        }
        final Class<?>[] classes = Arrays.copyOf(
            xkJavaTest.classes(),
            xkJavaTest.classes().length + 1
        );
        classes[xkJavaTest.classes().length] = testClass;

        // Create
        this.app = XkJava.of();

        this.app.booting(
                event -> {
                    // Before test class event
                    this.app.event()
                        .publishEvent(new BeforeTestAllEvent(this.app));
                }
            );

        // Boot
        this.app.bootInner(classes, args.toArray(String[]::new));

        // HttpClientInjector
        final HttpClientInjector injector = new HttpClientInjector();
        this.app.addFirstParameterInjector(injector);
        this.app.addFirstInstanceInjector(injector);

        // Other
        this.app.instance("extensionContext", context);
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        this.app.event().publishEvent(new BeforeTestEachEvent(this.app));
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        this.app.event().publishEvent(new BeforeTestExecutionEvent(this.app));
    }

    @Override
    public Object createTestInstance(
        final TestInstanceFactoryContext factoryContext,
        final ExtensionContext extensionContext
    ) throws TestInstantiationException {
        return this.app.make(factoryContext.getTestClass());
    }

    @Override
    public void interceptTestMethod(
        final Invocation<Void> invocation,
        final ReflectiveInvocationContext<Method> invocationContext,
        final ExtensionContext extensionContext
    ) {
        this.app.call(invocationContext.getExecutable());
        invocation.skip();
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        this.app.event().publishEvent(new AfterTestExecutionEvent(this.app));
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        this.app.event().publishEvent(new AfterTestEachEvent(this.app));
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        this.app.event().publishEvent(new AfterTestAllEvent(this.app));
    }

    @Override
    public boolean supportsParameter(
        final ParameterContext parameterContext,
        final ExtensionContext extensionContext
    ) throws ParameterResolutionException {
        return true;
    }

    @Override
    public Object resolveParameter(
        final ParameterContext parameterContext,
        final ExtensionContext extensionContext
    ) throws ParameterResolutionException {
        return null;
    }
}
