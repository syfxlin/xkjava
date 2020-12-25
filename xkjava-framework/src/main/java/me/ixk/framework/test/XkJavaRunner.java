/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.utils.AnnotationUtils;
import me.ixk.framework.utils.MergedAnnotation;
import org.junit.jupiter.api.extension.BeforeAllCallback;
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
    public void beforeAll(final ExtensionContext context) throws Exception {
        final Class<?> testClass = context.getRequiredTestClass();
        final MergedAnnotation annotation = AnnotationUtils.getAnnotation(
            testClass
        );
        final XkJavaTest xkJavaTest = annotation.getAnnotation(
            XkJavaTest.class
        );
        List<String> args = new ArrayList<>(Arrays.asList(xkJavaTest.args()));
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

        this.app = XkJava.boot(classes, args.toArray(String[]::new));

        // HttpClientInjector
        HttpClientInjector injector = new HttpClientInjector();
        this.app.addFirstParameterInjector(injector);
        this.app.addFirstInstanceInjector(injector);
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
        Invocation<Void> invocation,
        ReflectiveInvocationContext<Method> invocationContext,
        ExtensionContext extensionContext
    ) throws Throwable {
        this.app.call(invocationContext.getExecutable());
        invocation.skip();
    }

    @Override
    public boolean supportsParameter(
        ParameterContext parameterContext,
        ExtensionContext extensionContext
    ) throws ParameterResolutionException {
        return true;
    }

    @Override
    public Object resolveParameter(
        ParameterContext parameterContext,
        ExtensionContext extensionContext
    ) throws ParameterResolutionException {
        return null;
    }
}
