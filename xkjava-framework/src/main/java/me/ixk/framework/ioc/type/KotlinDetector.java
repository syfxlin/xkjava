/*
 * Copyright 2002-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.ixk.framework.ioc.type;

import java.lang.annotation.Annotation;

/**
 * @author Juergen Hoeller
 * @author Sebastien Deleuze
 */
@SuppressWarnings("unchecked")
public abstract class KotlinDetector {

    private static final Class<? extends Annotation> KOTLIN_METADATA;

    private static final boolean KOTLIN_REFLECT_PRESENT;

    static {
        Class<?> metadata;
        ClassLoader classLoader = KotlinDetector.class.getClassLoader();
        try {
            metadata = classLoader.loadClass("kotlin.Metadata");
        } catch (ClassNotFoundException ex) {
            // Kotlin API not available - no Kotlin support
            metadata = null;
        }
        KOTLIN_METADATA = (Class<? extends Annotation>) metadata;
        KOTLIN_REFLECT_PRESENT =
            isPresent("kotlin.reflect.full.KClasses", classLoader);
    }

    public static boolean isPresent(String className, ClassLoader classLoader) {
        try {
            classLoader.loadClass(className);
            return true;
        } catch (IllegalAccessError err) {
            throw new IllegalStateException(
                "Readability mismatch in inheritance hierarchy of class [" +
                className +
                "]: " +
                err.getMessage(),
                err
            );
        } catch (Throwable ex) {
            // Typically ClassNotFoundException or NoClassDefFoundError...
            return false;
        }
    }

    /**
     * Determine whether Kotlin is present in general.
     */
    public static boolean isKotlinPresent() {
        return (KOTLIN_METADATA != null);
    }

    /**
     * Determine whether Kotlin reflection is present.
     *
     * @since 5.1
     */
    public static boolean isKotlinReflectPresent() {
        return KOTLIN_REFLECT_PRESENT;
    }

    /**
     * Determine whether the given {@code Class} is a Kotlin type (with Kotlin
     * metadata present on it).
     */
    public static boolean isKotlinType(Class<?> clazz) {
        return (
            KOTLIN_METADATA != null &&
            clazz.getDeclaredAnnotation(KOTLIN_METADATA) != null
        );
    }
}
