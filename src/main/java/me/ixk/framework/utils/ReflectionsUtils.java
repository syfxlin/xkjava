package me.ixk.framework.utils;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import me.ixk.framework.ioc.Application;
import org.reflections.Reflections;
import org.reflections.scanners.*;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

public abstract class ReflectionsUtils {

    public static Reflections make() {
        return make(Application.getScanPackage());
    }

    public static Reflections make(Class<?> _class) {
        return make(ClasspathHelper.forClass(_class));
    }

    public static Reflections make(String _package) {
        return make(ClasspathHelper.forPackage(_package));
    }

    public static Reflections make(String prefix, Scanner... scanners) {
        return new Reflections(prefix, scanners);
    }

    public static Reflections make(Collection<URL> urls) {
        return new Reflections(
            new ConfigurationBuilder()
                .setUrls(urls)
                .setScanners(
                    new TypeAnnotationsScanner(),
                    new MethodAnnotationsScanner(),
                    new FieldAnnotationsScanner(),
                    new SubTypesScanner()
                )
        );
    }

    public static Reflections make(String... packages) {
        List<URL> urls = new ArrayList<>(packages.length);
        for (String item : packages) {
            urls.addAll(ClasspathHelper.forPackage(item));
        }
        return make(urls);
    }

    public static Reflections make(URL... urls) {
        return make(Arrays.asList(urls));
    }
}
