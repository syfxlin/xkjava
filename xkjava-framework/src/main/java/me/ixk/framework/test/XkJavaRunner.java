/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.test;

import java.lang.reflect.Method;
import java.util.Arrays;
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
    private final XkJava app = XkJava.create();

    @Override
    public void beforeAll(final ExtensionContext context) throws Exception {
        final Class<?> testClass = context.getRequiredTestClass();
        final MergedAnnotation annotation = AnnotationUtils.getAnnotation(
            testClass
        );
        final XkJavaTest xkJavaTest = annotation.getAnnotation(
            XkJavaTest.class
        );
        String[] args = xkJavaTest.args();
        if (!xkJavaTest.properties().isEmpty()) {
            final String[] copy = new String[args.length + 1];
            boolean in = false;
            for (int i = 0; i < args.length; i++) {
                copy[i] = args[i];
                if (args[i].startsWith(CONFIG_LOCATION_NAME)) {
                    args[i] = CONFIG_LOCATION_NAME + xkJavaTest.properties();
                    in = true;
                }
            }
            if (!in) {
                copy[args.length] =
                    CONFIG_LOCATION_NAME + xkJavaTest.properties();
                args = copy;
            }
        }
        final Class<?>[] classes = Arrays.copyOf(
            xkJavaTest.classes(),
            xkJavaTest.classes().length + 1
        );
        classes[xkJavaTest.classes().length] = testClass;

        this.app.boot(classes, args);

        // HttpClientInjector
        HttpClientInjector injector = new HttpClientInjector();
        this.app.addFirstParameterInjector(injector);
        this.app.addFirstInstanceInjector(injector);
    }

    @Override
    public Object createTestInstance(
        final TestInstanceFactoryContext factoryContext,
        final ExtensionContext extensionContext
    )
        throws TestInstantiationException {
        return this.app.make(factoryContext.getTestClass());
    }

    @Override
    public void interceptTestMethod(
        Invocation<Void> invocation,
        ReflectiveInvocationContext<Method> invocationContext,
        ExtensionContext extensionContext
    )
        throws Throwable {
        this.app.call(invocationContext.getExecutable());
        invocation.skip();
    }

    @Override
    public boolean supportsParameter(
        ParameterContext parameterContext,
        ExtensionContext extensionContext
    )
        throws ParameterResolutionException {
        return true;
    }

    @Override
    public Object resolveParameter(
        ParameterContext parameterContext,
        ExtensionContext extensionContext
    )
        throws ParameterResolutionException {
        return null;
    }
}
