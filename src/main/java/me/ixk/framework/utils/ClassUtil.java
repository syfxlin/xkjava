package me.ixk.framework.utils;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.stream.Collectors;

public class ClassUtil {
    public static final String FILE_PROTOCOL = "file";

    public static final String JAR_PROTOCOL = "jar";

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
                    .map(ClassUtil::getClassByJar)
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
}
