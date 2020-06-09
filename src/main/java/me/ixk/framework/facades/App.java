package me.ixk.framework.facades;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.ioc.BootCallback;
import me.ixk.framework.ioc.Concrete;
import me.ixk.framework.ioc.Container;
import me.ixk.framework.kernel.AnnotationProcessorManager;
import me.ixk.framework.kernel.ProviderManager;

public class App extends AbstractFacade {

    public static Application boot() {
        return app.boot();
    }

    public static ProviderManager getProviderManager() {
        return app.getProviderManager();
    }

    public static void setProviderManager(ProviderManager providerManager) {
        app.setProviderManager(providerManager);
    }

    public static AnnotationProcessorManager getAnnotationProcessorManager() {
        return app.getAnnotationProcessorManager();
    }

    public static void setAnnotationProcessorManager(
        AnnotationProcessorManager annotationProcessorManager
    ) {
        app.setAnnotationProcessorManager(annotationProcessorManager);
    }

    public static boolean isBooted() {
        return app.isBooted();
    }

    public static void booting(BootCallback callback) {
        app.booting(callback);
    }

    public static void booted(BootCallback callback) {
        app.booted(callback);
    }

    public static Container bind(String _abstract) {
        return app.bind(_abstract);
    }

    public static Container bind(String _abstract, String concrete) {
        return app.bind(_abstract, concrete);
    }

    public static Container bind(
        String _abstract,
        String concrete,
        boolean shared
    ) {
        return app.bind(_abstract, concrete, shared);
    }

    public static Container bind(
        String _abstract,
        String concrete,
        String alias
    ) {
        return app.bind(_abstract, concrete, alias);
    }

    public static Container bind(
        String _abstract,
        String concrete,
        boolean shared,
        String alias
    ) {
        return app.bind(_abstract, concrete, shared, alias);
    }

    public static Container bind(
        String _abstract,
        String concrete,
        boolean shared,
        String alias,
        boolean overwrite
    ) {
        return app.bind(_abstract, concrete, shared, alias, overwrite);
    }

    public static Container bind(String _abstract, Concrete concrete) {
        return app.bind(_abstract, concrete);
    }

    public static Container bind(
        String _abstract,
        Concrete concrete,
        boolean shared
    ) {
        return app.bind(_abstract, concrete, shared);
    }

    public static Container bind(
        String _abstract,
        Concrete concrete,
        String alias
    ) {
        return app.bind(_abstract, concrete, alias);
    }

    public static Container bind(
        String _abstract,
        Concrete concrete,
        boolean shared,
        String alias
    ) {
        return app.bind(_abstract, concrete, shared, alias);
    }

    public static Container bind(
        String _abstract,
        Concrete concrete,
        boolean shared,
        String alias,
        boolean overwrite
    ) {
        return app.bind(_abstract, concrete, shared, alias, overwrite);
    }

    public static Container bind(Class<?> _abstract) {
        return app.bind(_abstract);
    }

    public static Container bind(Class<?> _abstract, Class<?> concrete) {
        return app.bind(_abstract, concrete);
    }

    public static Container bind(
        Class<?> _abstract,
        Class<?> concrete,
        boolean shared
    ) {
        return app.bind(_abstract, concrete, shared);
    }

    public static Container bind(
        Class<?> _abstract,
        Class<?> concrete,
        boolean shared,
        String alias
    ) {
        return app.bind(_abstract, concrete, shared, alias);
    }

    public static Container bind(
        Class<?> _abstract,
        Class<?> concrete,
        boolean shared,
        String alias,
        boolean overwrite
    ) {
        return app.bind(_abstract, concrete, shared, alias, overwrite);
    }

    public static Container bind(Class<?> _abstract, Concrete concrete) {
        return app.bind(_abstract, concrete);
    }

    public static Container bind(
        Class<?> _abstract,
        Concrete concrete,
        boolean shared
    ) {
        return app.bind(_abstract, concrete, shared);
    }

    public static Container bind(
        Class<?> _abstract,
        Concrete concrete,
        String alias
    ) {
        return app.bind(_abstract, concrete, alias);
    }

    public static Container bind(
        Class<?> _abstract,
        Concrete concrete,
        boolean shared,
        String alias
    ) {
        return app.bind(_abstract, concrete, shared, alias);
    }

    public static Container bind(
        Class<?> _abstract,
        Concrete concrete,
        boolean shared,
        String alias,
        boolean overwrite
    ) {
        return app.bind(_abstract, concrete, shared, alias, overwrite);
    }

    public static Container singleton(String _abstract) {
        return app.singleton(_abstract);
    }

    public static Container singleton(String _abstract, String concrete) {
        return app.singleton(_abstract, concrete);
    }

    public static Container singleton(
        String _abstract,
        String concrete,
        String alias
    ) {
        return app.singleton(_abstract, concrete, alias);
    }

    public static Container singleton(
        String _abstract,
        String concrete,
        String alias,
        boolean overwrite
    ) {
        return app.singleton(_abstract, concrete, alias, overwrite);
    }

    public static Container singleton(String _abstract, Concrete concrete) {
        return app.singleton(_abstract, concrete);
    }

    public static Container singleton(
        String _abstract,
        Concrete concrete,
        String alias
    ) {
        return app.singleton(_abstract, concrete, alias);
    }

    public static Container singleton(
        String _abstract,
        Concrete concrete,
        String alias,
        boolean overwrite
    ) {
        return app.singleton(_abstract, concrete, alias, overwrite);
    }

    public static Container singleton(Class<?> _abstract) {
        return app.singleton(_abstract);
    }

    public static Container singleton(Class<?> _abstract, Class<?> concrete) {
        return app.singleton(_abstract, concrete);
    }

    public static Container singleton(
        Class<?> _abstract,
        Class<?> concrete,
        String alias
    ) {
        return app.singleton(_abstract, concrete, alias);
    }

    public static Container singleton(
        Class<?> _abstract,
        Class<?> concrete,
        String alias,
        boolean overwrite
    ) {
        return app.singleton(_abstract, concrete, alias, overwrite);
    }

    public static Container singleton(Class<?> _abstract, Concrete concrete) {
        return app.singleton(_abstract, concrete);
    }

    public static Container singleton(
        Class<?> _abstract,
        Concrete concrete,
        String alias
    ) {
        return app.singleton(_abstract, concrete, alias);
    }

    public static Container singleton(
        Class<?> _abstract,
        Concrete concrete,
        String alias,
        boolean overwrite
    ) {
        return app.singleton(_abstract, concrete, alias, overwrite);
    }

    public static Container instance(String _abstract, Object instance) {
        return app.instance(_abstract, instance);
    }

    public static Container instance(
        String _abstract,
        Object instance,
        String alias
    ) {
        return app.instance(_abstract, instance, alias);
    }

    public static Container instance(Class<?> _abstract, Object instance) {
        return app.instance(_abstract, instance);
    }

    public static Container instance(
        Class<?> _abstract,
        Object instance,
        String alias
    ) {
        return app.instance(_abstract, instance, alias);
    }

    public static Object build(Concrete concrete, Map<String, Object> args) {
        return app.build(concrete, args);
    }

    public static Object build(String _class, Map<String, Object> args) {
        return app.build(_class, args);
    }

    public static Object build(Class<?> _class, Map<String, Object> args) {
        return app.build(_class, args);
    }

    public static Object make(String _abstract) {
        return app.make(_abstract);
    }

    public static <T> T make(String _abstract, Class<T> returnType) {
        return app.make(_abstract, returnType);
    }

    public static <T> T make(
        String _abstract,
        Class<T> returnType,
        Map<String, Object> args
    ) {
        return app.make(_abstract, returnType, args);
    }

    public static <T> T make(Class<T> _abstract) {
        return app.make(_abstract);
    }

    public static <T> T make(Class<T> _abstract, Map<String, Object> args) {
        return app.make(_abstract, args);
    }

    public static Container alias(String alias, String _abstract) {
        return app.alias(alias, _abstract);
    }

    public static boolean has(String _abstract) {
        return app.has(_abstract);
    }

    public static boolean has(Class<?> _abstract) {
        return app.has(_abstract);
    }

    public static <T> T call(
        String[] target,
        Class<T> returnType,
        Map<String, Object> args
    ) {
        return app.call(target, returnType, args);
    }

    public static <T> T call(String[] target, Class<T> returnType) {
        return app.call(target, returnType);
    }

    public static <T> T call(
        String[] target,
        Class<?>[] paramTypes,
        Class<T> returnType,
        Map<String, Object> args
    ) {
        return app.call(target, paramTypes, returnType, args);
    }

    public static <T> T call(
        String[] target,
        Class<?>[] paramTypes,
        Class<T> returnType
    ) {
        return app.call(target, paramTypes, returnType);
    }

    public static <T> T call(
        String target,
        Class<T> returnType,
        Map<String, Object> args
    ) {
        return app.call(target, returnType, args);
    }

    public static <T> T call(String target, Class<T> returnType) {
        return app.call(target, returnType);
    }

    public static <T> T call(
        String target,
        Class<?>[] paramTypes,
        Class<T> returnType,
        Map<String, Object> args
    ) {
        return app.call(target, paramTypes, returnType, args);
    }

    public static <T> T call(
        String target,
        Class<?>[] paramTypes,
        Class<T> returnType
    ) {
        return app.call(target, paramTypes, returnType);
    }

    public static <T> T call(
        Class<?> _class,
        Method method,
        Class<T> returnType
    ) {
        return app.call(_class, method, returnType);
    }

    public static <T> T call(
        Class<?> _class,
        Method method,
        Class<T> returnType,
        Map<String, Object> args
    ) {
        return app.call(_class, method, returnType, args);
    }

    public static <T> T call(Method method, Class<T> returnType) {
        return app.call(method, returnType);
    }

    public static <T> T call(
        Method method,
        Class<T> returnType,
        Map<String, Object> args
    ) {
        return app.call(method, returnType, args);
    }

    public static Set<Class<?>> getClassesByAnnotation(
        Class<? extends Annotation> annotation
    ) {
        return app.getClassesByAnnotation(annotation);
    }

    public static Set<Class<?>> getClassesBySuper(Class<?> appClass) {
        return app.getClassesBySuper(appClass);
    }

    public static void remove(String _abstract) {
        app.remove(_abstract);
    }

    public static void remove(Class<?> _abstract) {
        app.remove(_abstract);
    }
}
