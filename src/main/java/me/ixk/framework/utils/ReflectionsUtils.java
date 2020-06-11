package me.ixk.framework.utils;

import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import org.reflections.scanners.*;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

public abstract class ReflectionsUtils {

    public static org.reflections.Reflections make(String _package) {
        return make(ClasspathHelper.forPackage(_package));
    }

    public static org.reflections.Reflections make(
        String _prefix,
        Scanner... scanners
    ) {
        return new org.reflections.Reflections(_prefix, scanners);
    }

    public static org.reflections.Reflections make(Collection<URL> urls) {
        return new org.reflections.Reflections(
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

    public static org.reflections.Reflections make(URL... urls) {
        return make(Arrays.asList(urls));
    }
}
