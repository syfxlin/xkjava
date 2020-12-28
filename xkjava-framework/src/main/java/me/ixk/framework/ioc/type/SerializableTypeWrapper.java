/*
 * Copyright 2002-2019 the original author or authors.
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

import cn.hutool.core.util.ReflectUtil;
import io.github.imsejin.expression.util.ConcurrentReferenceHashMap;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Objects;

/**
 * SerializableTypeWrapper
 *
 * @author Phillip Webb
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @date 2020/12/29 上午 12:45
 */
final class SerializableTypeWrapper {

    static final ConcurrentReferenceHashMap<Type, Type> CACHE = new ConcurrentReferenceHashMap<>(
        256
    );
    private static final Class<?>[] SUPPORTED_SERIALIZABLE_TYPES = {
        GenericArrayType.class,
        ParameterizedType.class,
        TypeVariable.class,
        WildcardType.class,
    };

    private SerializableTypeWrapper() {}

    public static Type forField(Field field) {
        return forTypeProvider(new FieldTypeProvider(field));
    }

    public static Type forMethodParameter(MethodParameter methodParameter) {
        return forTypeProvider(
            new MethodParameterTypeProvider(methodParameter)
        );
    }

    @SuppressWarnings("unchecked")
    public static <T extends Type> T unwrap(T type) {
        Type unwrapped = null;
        if (type instanceof SerializableTypeProxy) {
            unwrapped =
                ((SerializableTypeProxy) type).getTypeProvider().getType();
        }
        return (unwrapped != null ? (T) unwrapped : type);
    }

    static Type forTypeProvider(TypeProvider provider) {
        Type providedType = provider.getType();
        if (providedType == null || providedType instanceof Serializable) {
            // No serializable type wrapping necessary (e.g. for java.lang.Class)
            return providedType;
        }
        if (
            System.getProperty("org.graalvm.nativeimage.imagecode") != null ||
            !Serializable.class.isAssignableFrom(Class.class)
        ) {
            // Let's skip any wrapping attempts if types are generally not serializable in
            // the current runtime environment (even java.lang.Class itself, e.g. on Graal)
            return providedType;
        }

        // Obtain a serializable type proxy for the given provider...
        Type cached = CACHE.get(providedType);
        if (cached != null) {
            return cached;
        }
        for (Class<?> type : SUPPORTED_SERIALIZABLE_TYPES) {
            if (type.isInstance(providedType)) {
                ClassLoader classLoader = provider.getClass().getClassLoader();
                Class<?>[] interfaces = new Class<?>[] {
                    type,
                    SerializableTypeProxy.class,
                    Serializable.class,
                };
                InvocationHandler handler = new TypeProxyInvocationHandler(
                    provider
                );
                cached =
                    (Type) Proxy.newProxyInstance(
                        classLoader,
                        interfaces,
                        handler
                    );
                CACHE.put(providedType, cached);
                return cached;
            }
        }
        throw new IllegalArgumentException(
            "Unsupported Type class: " + providedType.getClass().getName()
        );
    }

    interface SerializableTypeProxy {
        /**
         * Return the underlying type provider.
         */
        TypeProvider getTypeProvider();
    }

    interface TypeProvider extends Serializable {
        /**
         * Return the (possibly non {@link Serializable}) {@link Type}.
         */
        Type getType();

        /**
         * Return the source of the type, or {@code null} if not known.
         * <p>The default implementations returns {@code null}.
         */
        default Object getSource() {
            return null;
        }
    }

    private static class TypeProxyInvocationHandler
        implements InvocationHandler, Serializable {

        private final TypeProvider provider;

        public TypeProxyInvocationHandler(TypeProvider provider) {
            this.provider = provider;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
            if (method.getName().equals("equals") && args != null) {
                Object other = args[0];
                // Unwrap proxies for speed
                if (other instanceof Type) {
                    other = unwrap((Type) other);
                }
                return Objects.equals(this.provider.getType(), other);
            } else if (method.getName().equals("hashCode")) {
                return Objects.hashCode(this.provider.getType());
            } else if (method.getName().equals("getTypeProvider")) {
                return this.provider;
            }

            if (Type.class == method.getReturnType() && args == null) {
                return forTypeProvider(
                    new MethodInvokeTypeProvider(this.provider, method, -1)
                );
            } else if (Type[].class == method.getReturnType() && args == null) {
                Type[] result = new Type[(
                    (Type[]) method.invoke(this.provider.getType())
                ).length];
                for (int i = 0; i < result.length; i++) {
                    result[i] =
                        forTypeProvider(
                            new MethodInvokeTypeProvider(
                                this.provider,
                                method,
                                i
                            )
                        );
                }
                return result;
            }

            try {
                return method.invoke(this.provider.getType(), args);
            } catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            }
        }
    }

    static class FieldTypeProvider implements TypeProvider {

        private final String fieldName;

        private final Class<?> declaringClass;

        private transient Field field;

        public FieldTypeProvider(Field field) {
            this.fieldName = field.getName();
            this.declaringClass = field.getDeclaringClass();
            this.field = field;
        }

        @Override
        public Type getType() {
            return this.field.getGenericType();
        }

        @Override
        public Object getSource() {
            return this.field;
        }

        private void readObject(ObjectInputStream inputStream)
            throws IOException, ClassNotFoundException {
            inputStream.defaultReadObject();
            try {
                this.field =
                    this.declaringClass.getDeclaredField(this.fieldName);
            } catch (Throwable ex) {
                throw new IllegalStateException(
                    "Could not find original class structure",
                    ex
                );
            }
        }
    }

    static class MethodParameterTypeProvider implements TypeProvider {

        private final String methodName;

        private final Class<?>[] parameterTypes;

        private final Class<?> declaringClass;

        private final int parameterIndex;

        private transient MethodParameter methodParameter;

        public MethodParameterTypeProvider(MethodParameter methodParameter) {
            this.methodName =
                (
                    methodParameter.getMethod() != null
                        ? methodParameter.getMethod().getName()
                        : null
                );
            this.parameterTypes =
                methodParameter.getExecutable().getParameterTypes();
            this.declaringClass = methodParameter.getDeclaringClass();
            this.parameterIndex = methodParameter.getParameterIndex();
            this.methodParameter = methodParameter;
        }

        @Override
        public Type getType() {
            return this.methodParameter.getGenericParameterType();
        }

        @Override
        public Object getSource() {
            return this.methodParameter;
        }

        private void readObject(ObjectInputStream inputStream)
            throws IOException, ClassNotFoundException {
            inputStream.defaultReadObject();
            try {
                if (this.methodName != null) {
                    this.methodParameter =
                        new MethodParameter(
                            this.declaringClass.getDeclaredMethod(
                                    this.methodName,
                                    this.parameterTypes
                                ),
                            this.parameterIndex
                        );
                } else {
                    this.methodParameter =
                        new MethodParameter(
                            this.declaringClass.getDeclaredConstructor(
                                    this.parameterTypes
                                ),
                            this.parameterIndex
                        );
                }
            } catch (Throwable ex) {
                throw new IllegalStateException(
                    "Could not find original class structure",
                    ex
                );
            }
        }
    }

    static class MethodInvokeTypeProvider implements TypeProvider {

        private final TypeProvider provider;

        private final String methodName;

        private final Class<?> declaringClass;

        private final int index;

        private transient Method method;

        private transient volatile Object result;

        public MethodInvokeTypeProvider(
            TypeProvider provider,
            Method method,
            int index
        ) {
            this.provider = provider;
            this.methodName = method.getName();
            this.declaringClass = method.getDeclaringClass();
            this.index = index;
            this.method = method;
        }

        @Override
        public Type getType() {
            Object result = this.result;
            if (result == null) {
                // Lazy invocation of the target method on the provided type
                result =
                    ReflectUtil.invoke(this.provider.getType(), this.method);
                // Cache the result for further calls to getType()
                this.result = result;
            }
            return (
                result instanceof Type[]
                    ? ((Type[]) result)[this.index]
                    : (Type) result
            );
        }

        @Override
        public Object getSource() {
            return null;
        }

        private void readObject(ObjectInputStream inputStream)
            throws IOException, ClassNotFoundException {
            inputStream.defaultReadObject();
            Method method = ReflectUtil.getMethod(
                this.declaringClass,
                this.methodName
            );
            if (method == null) {
                throw new IllegalStateException(
                    "Cannot find method on deserialization: " + this.methodName
                );
            }
            if (
                method.getReturnType() != Type.class &&
                method.getReturnType() != Type[].class
            ) {
                throw new IllegalStateException(
                    "Invalid return type on deserialized method - needs to be Type or Type[]: " +
                    method
                );
            }
            this.method = method;
        }
    }
}
