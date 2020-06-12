package me.ixk.framework.utils;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ClassUtil;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.stream.Collectors;

public abstract class ClassUtils extends ClassUtil {
    public static final String FILE_PROTOCOL = "file";

    public static final String JAR_PROTOCOL = "jar";

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

    public static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    public static Class<?> loadClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Set<Class<?>> getPackageClass(String basePackage) {
        URL url = getClassLoader().getResource(basePackage.replace(".", "/"));
        if (null == url) {
            throw new RuntimeException("无法获取项目路径文件");
        }
        try {
            if (url.getProtocol().equalsIgnoreCase(FILE_PROTOCOL)) {
                // 若为普通文件夹，则遍历
                File file = new File(url.getFile());
                Path basePath = file.toPath();
                return Files
                    .walk(basePath)
                    .filter(path -> path.toFile().getName().endsWith(".class"))
                    .map(path -> getClassByPath(path, basePath, basePackage))
                    .collect(Collectors.toSet());
            } else if (url.getProtocol().equalsIgnoreCase(JAR_PROTOCOL)) {
                // 若在 jar 包中，则解析 jar 包中的 entry
                JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
                return jarURLConnection
                    .getJarFile()
                    .stream()
                    .filter(jarEntry -> jarEntry.getName().endsWith(".class"))
                    .map(ClassUtils::getClassByJar)
                    .collect(Collectors.toSet());
            }
            return Collections.emptySet();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Class<?> getClassByPath(
        Path classPath,
        Path basePath,
        String basePackage
    ) {
        String packageName = classPath
            .toString()
            .replace(basePath.toString(), "");
        String className =
            (basePackage + packageName).replace("/", ".")
                .replace("\\", ".")
                .replace(".class", "");
        return loadClass(className);
    }

    private static Class<?> getClassByJar(JarEntry jarEntry) {
        String jarEntryName = jarEntry.getName();
        // 获取类名
        String className = jarEntryName
            .substring(0, jarEntryName.lastIndexOf("."))
            .replaceAll("/", ".");
        return loadClass(className);
    }

    public static Class<?> getUserClass(Object instance) {
        if (instance == null) {
            throw new RuntimeException("Instance must not be null");
        }
        return getUserClass(instance.getClass());
    }

    public static Class<?> getUserClass(Class<?> clazz) {
        if (clazz.getName().contains("$$")) {
            Class<?> superclass = clazz.getSuperclass();
            if (superclass != null && superclass != Object.class) {
                return superclass;
            }
        }
        return clazz;
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
            (ParameterizedType) _class.getGenericSuperclass()
        ).getActualTypeArguments()[index];
    }
}
