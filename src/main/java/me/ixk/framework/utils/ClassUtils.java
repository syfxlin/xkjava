/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.utils;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ClassUtil;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.util.*;

public abstract class ClassUtils extends ClassUtil {
    private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new IdentityHashMap<>(
        8
    );

    private static final Map<Class<?>, Class<?>> primitiveTypeToWrapperMap = new IdentityHashMap<>(
        8
    );

    static {
        primitiveWrapperTypeMap.put(Boolean.class, boolean.class);
        primitiveWrapperTypeMap.put(Byte.class, byte.class);
        primitiveWrapperTypeMap.put(Character.class, char.class);
        primitiveWrapperTypeMap.put(Double.class, double.class);
        primitiveWrapperTypeMap.put(Float.class, float.class);
        primitiveWrapperTypeMap.put(Integer.class, int.class);
        primitiveWrapperTypeMap.put(Long.class, long.class);
        primitiveWrapperTypeMap.put(Short.class, short.class);
        primitiveWrapperTypeMap.put(Void.class, void.class);

        for (Map.Entry<Class<?>, Class<?>> entry : primitiveWrapperTypeMap.entrySet()) {
            primitiveTypeToWrapperMap.put(entry.getValue(), entry.getKey());
        }
    }

    public static boolean isCglibProxy(Object object) {
        return isCglibProxyClass(object.getClass());
    }

    public static boolean isCglibProxyClass(Class<?> _class) {
        return (_class != null && isCglibProxyClassName(_class.getName()));
    }

    public static boolean isCglibProxyClassName(String className) {
        return (className != null && className.contains("$$"));
    }

    public static boolean isJdkProxy(Object object) {
        return isJdkProxy(object.getClass());
    }

    public static boolean isJdkProxy(Class<?> _class) {
        return Proxy.isProxyClass(_class);
    }

    public static boolean isProxy(Object object) {
        return isProxy(object.getClass());
    }

    public static boolean isProxy(Class<?> _class) {
        return isJdkProxy(_class) || isCglibProxyClass(_class);
    }

    public static Class<?> getUserClass(Object instance) {
        if (instance == null) {
            throw new RuntimeException("Instance must not be null");
        }
        return getUserClass(instance.getClass());
    }

    public static Class<?> getUserClass(Class<?> _class) {
        if (_class.getName().contains("$$")) {
            Class<?> superClass = _class.getSuperclass();
            if (superClass != null && superClass != Object.class) {
                return superClass;
            }
        }
        return _class;
    }

    public static boolean isAssignable(Class<?> lhsType, Class<?> rhsType) {
        cn.hutool.core.lang.Assert.notNull(
            lhsType,
            "Left-hand side type must not be null"
        );
        Assert.notNull(rhsType, "Right-hand side type must not be null");
        if (lhsType.isAssignableFrom(rhsType)) {
            return true;
        }
        if (lhsType.isPrimitive()) {
            Class<?> resolvedPrimitive = primitiveWrapperTypeMap.get(rhsType);
            return (lhsType == resolvedPrimitive);
        } else {
            Class<?> resolvedWrapper = primitiveTypeToWrapperMap.get(rhsType);
            return (
                resolvedWrapper != null &&
                lhsType.isAssignableFrom(resolvedWrapper)
            );
        }
    }

    public static <T> Class<?> getGenericClass(Class<T> _class) {
        return getGenericClass(_class, 0);
    }

    public static <T> Class<?> getGenericClass(Class<T> _class, int index) {
        return (Class<?>) (
            (ParameterizedType) getUserClass(_class).getGenericSuperclass()
        ).getActualTypeArguments()[index];
    }

    public static Set<Class<?>> getInterfaces(Object instance) {
        return getInterfaces(instance.getClass());
    }

    public static Set<Class<?>> getInterfaces(Class<?> _class) {
        Set<Class<?>> set = new HashSet<>();
        getInterfaces(_class, set);
        return set;
    }

    private static void getInterfaces(Class<?> _class, Set<Class<?>> set) {
        _class = getUserClass(_class);
        Class<?>[] interfaces = _class.getInterfaces();
        set.addAll(Arrays.asList(interfaces));
        Class<?> superClass = _class.getSuperclass();
        if (superClass != null && superClass != Object.class) {
            getInterfaces(superClass, set);
        }
    }

    public static Set<Method> getMethods(Object instance) {
        return getMethods(instance.getClass());
    }

    public static Set<Method> getMethods(Class<?> _class) {
        _class = getUserClass(_class);
        Set<Method> methods = new HashSet<>();
        for (Method method : _class.getMethods()) {
            switch (method.getName()) {
                case "getClass":
                case "hashCode":
                case "equals":
                case "clone":
                case "toString":
                case "notify":
                case "notifyAll":
                case "wait":
                case "finalize":
                    continue;
                default:
                    methods.add(method);
            }
        }
        return methods;
    }

    public static Class<?> primitiveTypeToWrapper(Class<?> type) {
        return primitiveTypeToWrapperMap.get(type);
    }

    public static Class<?> primitiveWrapperToType(Class<?> type) {
        return primitiveWrapperTypeMap.get(type);
    }

    public static Class<?> primitiveTypeToWrapper(String type) {
        for (Map.Entry<Class<?>, Class<?>> entry : primitiveTypeToWrapperMap.entrySet()) {
            if (entry.getKey().getName().equals(type)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public static Class<?> primitiveWrapperToType(String type) {
        for (Map.Entry<Class<?>, Class<?>> entry : primitiveWrapperTypeMap.entrySet()) {
            if (entry.getKey().getName().equals(type)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public static Class<?> forName(String name) {
        switch (name) {
            case "long":
                return long.class;
            case "int":
                return int.class;
            case "short":
                return short.class;
            case "char":
                return char.class;
            case "byte":
                return byte.class;
            case "double":
                return double.class;
            case "float":
                return float.class;
            case "boolean":
                return boolean.class;
            default:
                try {
                    return Class.forName(name);
                } catch (ClassNotFoundException e) {
                    return null;
                }
        }
    }
}
