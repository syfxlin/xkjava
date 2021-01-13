package me.ixk.framework.ioc.type;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Type 包装
 *
 * @author Otstar Lin
 * @date 2020/12/29 下午 12:38
 */
public class TypeWrapper<T> implements TypeProvider {

    private final Class<T> clazz;
    private final TypeProvider provider;

    private TypeWrapper(final Class<T> clazz) {
        this(clazz, null);
    }

    private TypeWrapper(final Class<T> clazz, final TypeProvider provider) {
        this.clazz = clazz;
        this.provider = provider;
    }

    @Override
    public Type getType() {
        return provider == null ? null : provider.getType();
    }

    @Override
    public Object getSource() {
        return provider == null ? null : provider.getSource();
    }

    public Class<T> getClazz() {
        return this.clazz;
    }

    public TypeProvider getProvider() {
        return provider;
    }

    @Override
    public boolean useProxy() {
        if (provider == null) {
            return false;
        }
        return provider.useProxy();
    }

    public boolean canGetGeneric() {
        return provider != null;
    }

    public Class<?> getGeneric(final int index) {
        final Type type = this.getType();
        if (type instanceof Class) {
            return (Class<?>) type;
        }
        if (type instanceof ParameterizedType) {
            final Type[] actualTypeArguments =
                ((ParameterizedType) type).getActualTypeArguments();
            final Type typeArgument = actualTypeArguments[index];
            if (typeArgument instanceof Class) {
                return (Class<?>) typeArgument;
            }
        }
        return null;
    }

    public static TypeWrapper<?> forParameter(final Parameter parameter) {
        return new TypeWrapper<>(
            parameter.getType(),
            new ParameterTypeProvider(parameter)
        );
    }

    public static TypeWrapper<?> forField(final Field field) {
        return new TypeWrapper<>(field.getType(), new FieldTypeProvider(field));
    }

    public static TypeWrapper<?> forReturnValue(final Method method) {
        return new TypeWrapper<>(
            method.getReturnType(),
            new ReturnValueTypeProvider(method)
        );
    }

    public static <E> TypeWrapper<E> forClass(final Class<E> clazz) {
        if (clazz.isArray()) {
            return new TypeWrapper<>(clazz, new ArrayTypeProvider(clazz));
        }
        return new TypeWrapper<>(clazz);
    }
}
