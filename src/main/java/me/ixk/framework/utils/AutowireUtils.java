package me.ixk.framework.utils;

import java.io.Serializable;
import java.lang.reflect.*;
import java.util.Arrays;
import me.ixk.framework.factory.ObjectFactory;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public abstract class AutowireUtils {

    public static void sortConstructors(Constructor<?>[] constructors) {
        sortMethods(constructors);
    }

    public static void sortFactoryMethods(Method[] factoryMethods) {
        sortMethods(factoryMethods);
    }

    private static void sortMethods(Executable[] executable) {
        Arrays.sort(
            executable,
            (fm1, fm2) -> {
                boolean p1 = Modifier.isPublic(fm1.getModifiers());
                boolean p2 = Modifier.isPublic(fm2.getModifiers());
                if (p1 != p2) {
                    return (p1 ? -1 : 1);
                }
                int c1pl = fm1.getParameterTypes().length;
                int c2pl = fm2.getParameterTypes().length;
                return (Integer.compare(c2pl, c1pl));
            }
        );
    }

    public static Object proxyObjectFactory(
        Object target,
        Class<?> requiredType
    ) {
        return resolveAutowiringValue(target, requiredType);
    }

    public static Object resolveAutowiringValue(
        Object autowiringValue,
        Class<?> requiredType
    ) {
        if (autowiringValue instanceof ObjectFactory) {
            ObjectFactory<?> factory = (ObjectFactory<?>) autowiringValue;
            if (
                autowiringValue instanceof Serializable &&
                requiredType.isInterface()
            ) {
                autowiringValue =
                    Proxy.newProxyInstance(
                        requiredType.getClassLoader(),
                        new Class<?>[] { requiredType },
                        new ObjectFactoryDelegatingInterceptor(factory)
                    );
            } else {
                autowiringValue =
                    Enhancer.create(
                        requiredType,
                        new ObjectFactoryDelegatingInterceptor(factory)
                    );
            }
        }
        return autowiringValue;
    }

    private static class ObjectFactoryDelegatingInterceptor
        implements MethodInterceptor, InvocationHandler, Serializable {
        private final ObjectFactory<?> objectFactory;

        public ObjectFactoryDelegatingInterceptor(
            ObjectFactory<?> objectFactory
        ) {
            this.objectFactory = objectFactory;
        }

        @Override
        public Object intercept(
            Object obj,
            Method method,
            Object[] args,
            MethodProxy proxy
        )
            throws Throwable {
            switch (method.getName()) {
                case "equals":
                    // Only consider equal when proxies are identical.
                    return (proxy == args[0]);
                case "hashCode":
                    // Use hashCode of proxy.
                    return System.identityHashCode(proxy);
                case "toString":
                    return this.objectFactory.toString();
            }
            return proxy.invoke(this.objectFactory.getObject(), args);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
            switch (method.getName()) {
                case "equals":
                    // Only consider equal when proxies are identical.
                    return (proxy == args[0]);
                case "hashCode":
                    // Use hashCode of proxy.
                    return System.identityHashCode(proxy);
                case "toString":
                    return this.objectFactory.toString();
            }
            try {
                return method.invoke(this.objectFactory.getObject(), args);
            } catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            }
        }
    }
}
