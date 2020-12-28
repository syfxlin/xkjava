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

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import io.github.imsejin.expression.util.ConcurrentReferenceHashMap;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * ResolvableType
 *
 * @author Phillip Webb
 * @author Juergen Hoeller
 * @author Stephane Nicoll
 * @date 2020/12/29 上午 12:44
 */
public class ResolvableType implements Serializable {

    public static final ResolvableType NONE = new ResolvableType(
        EmptyType.INSTANCE,
        null,
        null,
        0
    );

    private static final ResolvableType[] EMPTY_TYPES_ARRAY = new ResolvableType[0];

    private static final ConcurrentReferenceHashMap<ResolvableType, ResolvableType> cache = new ConcurrentReferenceHashMap<>(
        256
    );

    private final Type type;

    private final SerializableTypeWrapper.TypeProvider typeProvider;

    private final VariableResolver variableResolver;

    private final ResolvableType componentType;

    private final Integer hash;

    private Class<?> resolved;

    private volatile ResolvableType superType;

    private volatile ResolvableType[] interfaces;

    private volatile ResolvableType[] generics;

    private ResolvableType(
        Type type,
        SerializableTypeWrapper.TypeProvider typeProvider,
        VariableResolver variableResolver
    ) {
        this.type = type;
        this.typeProvider = typeProvider;
        this.variableResolver = variableResolver;
        this.componentType = null;
        this.hash = calculateHashCode();
        this.resolved = null;
    }

    private ResolvableType(
        Type type,
        SerializableTypeWrapper.TypeProvider typeProvider,
        VariableResolver variableResolver,
        Integer hash
    ) {
        this.type = type;
        this.typeProvider = typeProvider;
        this.variableResolver = variableResolver;
        this.componentType = null;
        this.hash = hash;
        this.resolved = resolveClass();
    }

    private ResolvableType(
        Type type,
        SerializableTypeWrapper.TypeProvider typeProvider,
        VariableResolver variableResolver,
        ResolvableType componentType
    ) {
        this.type = type;
        this.typeProvider = typeProvider;
        this.variableResolver = variableResolver;
        this.componentType = componentType;
        this.hash = null;
        this.resolved = resolveClass();
    }

    private ResolvableType(Class<?> clazz) {
        this.resolved = (clazz != null ? clazz : Object.class);
        this.type = this.resolved;
        this.typeProvider = null;
        this.variableResolver = null;
        this.componentType = null;
        this.hash = null;
    }

    public static ResolvableType forClass(Class<?> clazz) {
        return new ResolvableType(clazz);
    }

    public static ResolvableType forRawClass(Class<?> clazz) {
        return new ResolvableType(clazz) {
            @Override
            public ResolvableType[] getGenerics() {
                return EMPTY_TYPES_ARRAY;
            }

            @Override
            public boolean isAssignableFrom(Class<?> other) {
                return (clazz == null || ClassUtil.isAssignable(clazz, other));
            }

            @Override
            public boolean isAssignableFrom(ResolvableType other) {
                Class<?> otherClass = other.resolve();
                return (
                    otherClass != null &&
                    (clazz == null || ClassUtil.isAssignable(clazz, otherClass))
                );
            }
        };
    }

    public static ResolvableType forClass(
        Class<?> baseType,
        Class<?> implementationClass
    ) {
        ResolvableType asType = forType(implementationClass).as(baseType);
        return (asType == NONE ? forType(baseType) : asType);
    }

    public static ResolvableType forClassWithGenerics(
        Class<?> clazz,
        Class<?>... generics
    ) {
        ResolvableType[] resolvableGenerics = new ResolvableType[generics.length];
        for (int i = 0; i < generics.length; i++) {
            resolvableGenerics[i] = forClass(generics[i]);
        }
        return forClassWithGenerics(clazz, resolvableGenerics);
    }

    public static ResolvableType forClassWithGenerics(
        Class<?> clazz,
        ResolvableType... generics
    ) {
        TypeVariable<?>[] variables = clazz.getTypeParameters();

        Type[] arguments = new Type[generics.length];
        for (int i = 0; i < generics.length; i++) {
            ResolvableType generic = generics[i];
            Type argument = (generic != null ? generic.getType() : null);
            arguments[i] =
                (
                    argument != null && !(argument instanceof TypeVariable)
                        ? argument
                        : variables[i]
                );
        }

        ParameterizedType syntheticType = new SyntheticParameterizedType(
            clazz,
            arguments
        );
        return forType(
            syntheticType,
            new TypeVariablesVariableResolver(variables, generics)
        );
    }

    public static ResolvableType forInstance(Object instance) {
        if (instance instanceof ResolvableTypeProvider) {
            ResolvableType type =
                ((ResolvableTypeProvider) instance).getResolvableType();
            if (type != null) {
                return type;
            }
        }
        return ResolvableType.forClass(instance.getClass());
    }

    public static ResolvableType forField(Field field) {
        return forType(
            null,
            new SerializableTypeWrapper.FieldTypeProvider(field),
            null
        );
    }

    public static ResolvableType forField(
        Field field,
        Class<?> implementationClass
    ) {
        ResolvableType owner = forType(implementationClass)
            .as(field.getDeclaringClass());
        return forType(
            null,
            new SerializableTypeWrapper.FieldTypeProvider(field),
            owner.asVariableResolver()
        );
    }

    public static ResolvableType forField(
        Field field,
        ResolvableType implementationType
    ) {
        ResolvableType owner =
            (implementationType != null ? implementationType : NONE);
        owner = owner.as(field.getDeclaringClass());
        return forType(
            null,
            new SerializableTypeWrapper.FieldTypeProvider(field),
            owner.asVariableResolver()
        );
    }

    public static ResolvableType forField(Field field, int nestingLevel) {
        return forType(
            null,
            new SerializableTypeWrapper.FieldTypeProvider(field),
            null
        )
            .getNested(nestingLevel);
    }

    public static ResolvableType forField(
        Field field,
        int nestingLevel,
        Class<?> implementationClass
    ) {
        ResolvableType owner = forType(implementationClass)
            .as(field.getDeclaringClass());
        return forType(
            null,
            new SerializableTypeWrapper.FieldTypeProvider(field),
            owner.asVariableResolver()
        )
            .getNested(nestingLevel);
    }

    public static ResolvableType forConstructorParameter(
        Constructor<?> constructor,
        int parameterIndex
    ) {
        return forMethodParameter(
            new MethodParameter(constructor, parameterIndex)
        );
    }

    public static ResolvableType forConstructorParameter(
        Constructor<?> constructor,
        int parameterIndex,
        Class<?> implementationClass
    ) {
        MethodParameter methodParameter = new MethodParameter(
            constructor,
            parameterIndex,
            implementationClass
        );
        return forMethodParameter(methodParameter);
    }

    public static ResolvableType forMethodReturnType(Method method) {
        return forMethodParameter(new MethodParameter(method, -1));
    }

    public static ResolvableType forMethodReturnType(
        Method method,
        Class<?> implementationClass
    ) {
        MethodParameter methodParameter = new MethodParameter(
            method,
            -1,
            implementationClass
        );
        return forMethodParameter(methodParameter);
    }

    public static ResolvableType forMethodParameter(
        Method method,
        int parameterIndex
    ) {
        return forMethodParameter(new MethodParameter(method, parameterIndex));
    }

    public static ResolvableType forMethodParameter(
        Method method,
        int parameterIndex,
        Class<?> implementationClass
    ) {
        MethodParameter methodParameter = new MethodParameter(
            method,
            parameterIndex,
            implementationClass
        );
        return forMethodParameter(methodParameter);
    }

    public static ResolvableType forMethodParameter(
        MethodParameter methodParameter
    ) {
        return forMethodParameter(methodParameter, (Type) null);
    }

    public static ResolvableType forMethodParameter(
        MethodParameter methodParameter,
        ResolvableType implementationType
    ) {
        implementationType =
            (
                implementationType != null
                    ? implementationType
                    : forType(methodParameter.getContainingClass())
            );
        ResolvableType owner = implementationType.as(
            methodParameter.getDeclaringClass()
        );
        return forType(
            null,
            new SerializableTypeWrapper.MethodParameterTypeProvider(
                methodParameter
            ),
            owner.asVariableResolver()
        )
            .getNested(
                methodParameter.getNestingLevel(),
                methodParameter.typeIndexesPerLevel
            );
    }

    public static ResolvableType forMethodParameter(
        MethodParameter methodParameter,
        Type targetType
    ) {
        return forMethodParameter(
            methodParameter,
            targetType,
            methodParameter.getNestingLevel()
        );
    }

    static ResolvableType forMethodParameter(
        MethodParameter methodParameter,
        Type targetType,
        int nestingLevel
    ) {
        ResolvableType owner = forType(methodParameter.getContainingClass())
            .as(methodParameter.getDeclaringClass());
        return forType(
            targetType,
            new SerializableTypeWrapper.MethodParameterTypeProvider(
                methodParameter
            ),
            owner.asVariableResolver()
        )
            .getNested(nestingLevel, methodParameter.typeIndexesPerLevel);
    }

    public static ResolvableType forArrayComponent(
        ResolvableType componentType
    ) {
        Class<?> arrayClass = Array
            .newInstance(componentType.resolve(), 0)
            .getClass();
        return new ResolvableType(arrayClass, null, null, componentType);
    }

    public static ResolvableType forType(Type type) {
        return forType(type, null, null);
    }

    public static ResolvableType forType(Type type, ResolvableType owner) {
        VariableResolver variableResolver = null;
        if (owner != null) {
            variableResolver = owner.asVariableResolver();
        }
        return forType(type, variableResolver);
    }

    static ResolvableType forType(
        Type type,
        VariableResolver variableResolver
    ) {
        return forType(type, null, variableResolver);
    }

    static ResolvableType forType(
        Type type,
        SerializableTypeWrapper.TypeProvider typeProvider,
        VariableResolver variableResolver
    ) {
        if (type == null && typeProvider != null) {
            type = SerializableTypeWrapper.forTypeProvider(typeProvider);
        }
        if (type == null) {
            return NONE;
        }

        // For simple Class references, build the wrapper right away -
        // no expensive resolution necessary, so not worth caching...
        if (type instanceof Class) {
            return new ResolvableType(
                type,
                typeProvider,
                variableResolver,
                (ResolvableType) null
            );
        }

        // Purge empty entries on access since we don't have a clean-up thread or the like.
        cache.purgeUnreferencedEntries();

        // Check the cache - we may have a ResolvableType which has been resolved before...
        ResolvableType resultType = new ResolvableType(
            type,
            typeProvider,
            variableResolver
        );
        ResolvableType cachedType = cache.get(resultType);
        if (cachedType == null) {
            cachedType =
                new ResolvableType(
                    type,
                    typeProvider,
                    variableResolver,
                    resultType.hash
                );
            cache.put(cachedType, cachedType);
        }
        resultType.resolved = cachedType.resolved;
        return resultType;
    }

    public static void clearCache() {
        cache.clear();
        SerializableTypeWrapper.CACHE.clear();
    }

    public Type getType() {
        return SerializableTypeWrapper.unwrap(this.type);
    }

    public Class<?> getRawClass() {
        if (this.type == this.resolved) {
            return this.resolved;
        }
        Type rawType = this.type;
        if (rawType instanceof ParameterizedType) {
            rawType = ((ParameterizedType) rawType).getRawType();
        }
        return (rawType instanceof Class ? (Class<?>) rawType : null);
    }

    public Object getSource() {
        Object source =
            (this.typeProvider != null ? this.typeProvider.getSource() : null);
        return (source != null ? source : this.type);
    }

    public Class<?> toClass() {
        return resolve(Object.class);
    }

    public boolean isInstance(Object obj) {
        return (obj != null && isAssignableFrom(obj.getClass()));
    }

    public boolean isAssignableFrom(Class<?> other) {
        return isAssignableFrom(forClass(other), null);
    }

    public boolean isAssignableFrom(ResolvableType other) {
        return isAssignableFrom(other, null);
    }

    private boolean isAssignableFrom(
        ResolvableType other,
        Map<Type, Type> matchedBefore
    ) {
        // If we cannot resolve types, we are not assignable
        if (this == NONE || other == NONE) {
            return false;
        }

        // Deal with array by delegating to the component type
        if (isArray()) {
            return (
                other.isArray() &&
                getComponentType().isAssignableFrom(other.getComponentType())
            );
        }

        if (
            matchedBefore != null && matchedBefore.get(this.type) == other.type
        ) {
            return true;
        }

        // Deal with wildcard bounds
        WildcardBounds ourBounds = WildcardBounds.get(this);
        WildcardBounds typeBounds = WildcardBounds.get(other);

        // In the form X is assignable to <? extends Number>
        if (typeBounds != null) {
            return (
                ourBounds != null &&
                ourBounds.isSameKind(typeBounds) &&
                ourBounds.isAssignableFrom(typeBounds.getBounds())
            );
        }

        // In the form <? extends Number> is assignable to X...
        if (ourBounds != null) {
            return ourBounds.isAssignableFrom(other);
        }

        // Main assignability check about to follow
        boolean exactMatch = (matchedBefore != null); // We're checking nested generic variables now...
        boolean checkGenerics = true;
        Class<?> ourResolved = null;
        if (this.type instanceof TypeVariable) {
            TypeVariable<?> variable = (TypeVariable<?>) this.type;
            // Try default variable resolution
            if (this.variableResolver != null) {
                ResolvableType resolved =
                    this.variableResolver.resolveVariable(variable);
                if (resolved != null) {
                    ourResolved = resolved.resolve();
                }
            }
            if (ourResolved == null) {
                // Try variable resolution against target type
                if (other.variableResolver != null) {
                    ResolvableType resolved = other.variableResolver.resolveVariable(
                        variable
                    );
                    if (resolved != null) {
                        ourResolved = resolved.resolve();
                        checkGenerics = false;
                    }
                }
            }
            if (ourResolved == null) {
                // Unresolved type variable, potentially nested -> never insist on exact match
                exactMatch = false;
            }
        }
        if (ourResolved == null) {
            ourResolved = resolve(Object.class);
        }
        Class<?> otherResolved = other.toClass();

        // We need an exact type match for generics
        // List<CharSequence> is not assignable from List<String>
        if (
            exactMatch
                ? !ourResolved.equals(otherResolved)
                : !ClassUtil.isAssignable(ourResolved, otherResolved)
        ) {
            return false;
        }

        if (checkGenerics) {
            // Recursively check each generic
            ResolvableType[] ourGenerics = getGenerics();
            ResolvableType[] typeGenerics = other.as(ourResolved).getGenerics();
            if (ourGenerics.length != typeGenerics.length) {
                return false;
            }
            if (matchedBefore == null) {
                matchedBefore = new IdentityHashMap<>(1);
            }
            matchedBefore.put(this.type, other.type);
            for (int i = 0; i < ourGenerics.length; i++) {
                if (
                    !ourGenerics[i].isAssignableFrom(
                            typeGenerics[i],
                            matchedBefore
                        )
                ) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean isArray() {
        if (this == NONE) {
            return false;
        }
        return (
            (this.type instanceof Class && ((Class<?>) this.type).isArray()) ||
            this.type instanceof GenericArrayType ||
            resolveType().isArray()
        );
    }

    public ResolvableType getComponentType() {
        if (this == NONE) {
            return NONE;
        }
        if (this.componentType != null) {
            return this.componentType;
        }
        if (this.type instanceof Class) {
            Class<?> componentType = ((Class<?>) this.type).getComponentType();
            return forType(componentType, this.variableResolver);
        }
        if (this.type instanceof GenericArrayType) {
            return forType(
                ((GenericArrayType) this.type).getGenericComponentType(),
                this.variableResolver
            );
        }
        return resolveType().getComponentType();
    }

    public ResolvableType asCollection() {
        return as(Collection.class);
    }

    public ResolvableType asMap() {
        return as(Map.class);
    }

    public ResolvableType as(Class<?> type) {
        if (this == NONE) {
            return NONE;
        }
        Class<?> resolved = resolve();
        if (resolved == null || resolved == type) {
            return this;
        }
        for (ResolvableType interfaceType : getInterfaces()) {
            ResolvableType interfaceAsType = interfaceType.as(type);
            if (interfaceAsType != NONE) {
                return interfaceAsType;
            }
        }
        return getSuperType().as(type);
    }

    public ResolvableType getSuperType() {
        Class<?> resolved = resolve();
        if (resolved == null) {
            return NONE;
        }
        try {
            Type superclass = resolved.getGenericSuperclass();
            if (superclass == null) {
                return NONE;
            }
            ResolvableType superType = this.superType;
            if (superType == null) {
                superType = forType(superclass, this);
                this.superType = superType;
            }
            return superType;
        } catch (TypeNotPresentException ex) {
            // Ignore non-present types in generic signature
            return NONE;
        }
    }

    public ResolvableType[] getInterfaces() {
        Class<?> resolved = resolve();
        if (resolved == null) {
            return EMPTY_TYPES_ARRAY;
        }
        ResolvableType[] interfaces = this.interfaces;
        if (interfaces == null) {
            Type[] genericIfcs = resolved.getGenericInterfaces();
            interfaces = new ResolvableType[genericIfcs.length];
            for (int i = 0; i < genericIfcs.length; i++) {
                interfaces[i] = forType(genericIfcs[i], this);
            }
            this.interfaces = interfaces;
        }
        return interfaces;
    }

    public boolean hasGenerics() {
        return (getGenerics().length > 0);
    }

    boolean isEntirelyUnresolvable() {
        if (this == NONE) {
            return false;
        }
        ResolvableType[] generics = getGenerics();
        for (ResolvableType generic : generics) {
            if (
                !generic.isUnresolvableTypeVariable() &&
                !generic.isWildcardWithoutBounds()
            ) {
                return false;
            }
        }
        return true;
    }

    public boolean hasUnresolvableGenerics() {
        if (this == NONE) {
            return false;
        }
        ResolvableType[] generics = getGenerics();
        for (ResolvableType generic : generics) {
            if (
                generic.isUnresolvableTypeVariable() ||
                generic.isWildcardWithoutBounds()
            ) {
                return true;
            }
        }
        Class<?> resolved = resolve();
        if (resolved != null) {
            try {
                for (Type genericInterface : resolved.getGenericInterfaces()) {
                    if (genericInterface instanceof Class) {
                        if (
                            forClass((Class<?>) genericInterface).hasGenerics()
                        ) {
                            return true;
                        }
                    }
                }
            } catch (TypeNotPresentException ex) {
                // Ignore non-present types in generic signature
            }
            return getSuperType().hasUnresolvableGenerics();
        }
        return false;
    }

    private boolean isUnresolvableTypeVariable() {
        if (this.type instanceof TypeVariable) {
            if (this.variableResolver == null) {
                return true;
            }
            TypeVariable<?> variable = (TypeVariable<?>) this.type;
            ResolvableType resolved =
                this.variableResolver.resolveVariable(variable);
            return resolved == null || resolved.isUnresolvableTypeVariable();
        }
        return false;
    }

    private boolean isWildcardWithoutBounds() {
        if (this.type instanceof WildcardType) {
            WildcardType wt = (WildcardType) this.type;
            if (wt.getLowerBounds().length == 0) {
                Type[] upperBounds = wt.getUpperBounds();
                return (
                    upperBounds.length == 0 ||
                    (upperBounds.length == 1 && Object.class == upperBounds[0])
                );
            }
        }
        return false;
    }

    public ResolvableType getNested(int nestingLevel) {
        return getNested(nestingLevel, null);
    }

    public ResolvableType getNested(
        int nestingLevel,
        Map<Integer, Integer> typeIndexesPerLevel
    ) {
        ResolvableType result = this;
        for (int i = 2; i <= nestingLevel; i++) {
            if (result.isArray()) {
                result = result.getComponentType();
            } else {
                // Handle derived types
                while (result != ResolvableType.NONE && !result.hasGenerics()) {
                    result = result.getSuperType();
                }
                Integer index =
                    (
                        typeIndexesPerLevel != null
                            ? typeIndexesPerLevel.get(i)
                            : null
                    );
                index =
                    (index == null ? result.getGenerics().length - 1 : index);
                result = result.getGeneric(index);
            }
        }
        return result;
    }

    public ResolvableType getGeneric(int... indexes) {
        ResolvableType[] generics = getGenerics();
        if (indexes == null || indexes.length == 0) {
            return (generics.length == 0 ? NONE : generics[0]);
        }
        ResolvableType generic = this;
        for (int index : indexes) {
            generics = generic.getGenerics();
            if (index < 0 || index >= generics.length) {
                return NONE;
            }
            generic = generics[index];
        }
        return generic;
    }

    public ResolvableType[] getGenerics() {
        if (this == NONE) {
            return EMPTY_TYPES_ARRAY;
        }
        ResolvableType[] generics = this.generics;
        if (generics == null) {
            if (this.type instanceof Class) {
                Type[] typeParams = ((Class<?>) this.type).getTypeParameters();
                generics = new ResolvableType[typeParams.length];
                for (int i = 0; i < generics.length; i++) {
                    generics[i] = ResolvableType.forType(typeParams[i], this);
                }
            } else if (this.type instanceof ParameterizedType) {
                Type[] actualTypeArguments =
                    ((ParameterizedType) this.type).getActualTypeArguments();
                generics = new ResolvableType[actualTypeArguments.length];
                for (int i = 0; i < actualTypeArguments.length; i++) {
                    generics[i] =
                        forType(actualTypeArguments[i], this.variableResolver);
                }
            } else {
                generics = resolveType().getGenerics();
            }
            this.generics = generics;
        }
        return generics;
    }

    public Class<?>[] resolveGenerics() {
        ResolvableType[] generics = getGenerics();
        Class<?>[] resolvedGenerics = new Class<?>[generics.length];
        for (int i = 0; i < generics.length; i++) {
            resolvedGenerics[i] = generics[i].resolve();
        }
        return resolvedGenerics;
    }

    public Class<?>[] resolveGenerics(Class<?> fallback) {
        ResolvableType[] generics = getGenerics();
        Class<?>[] resolvedGenerics = new Class<?>[generics.length];
        for (int i = 0; i < generics.length; i++) {
            resolvedGenerics[i] = generics[i].resolve(fallback);
        }
        return resolvedGenerics;
    }

    public Class<?> resolveGeneric(int... indexes) {
        return getGeneric(indexes).resolve();
    }

    public Class<?> resolve() {
        return this.resolved;
    }

    public Class<?> resolve(Class<?> fallback) {
        return (this.resolved != null ? this.resolved : fallback);
    }

    private Class<?> resolveClass() {
        if (this.type == EmptyType.INSTANCE) {
            return null;
        }
        if (this.type instanceof Class) {
            return (Class<?>) this.type;
        }
        if (this.type instanceof GenericArrayType) {
            Class<?> resolvedComponent = getComponentType().resolve();
            return (
                resolvedComponent != null
                    ? Array.newInstance(resolvedComponent, 0).getClass()
                    : null
            );
        }
        return resolveType().resolve();
    }

    ResolvableType resolveType() {
        if (this.type instanceof ParameterizedType) {
            return forType(
                ((ParameterizedType) this.type).getRawType(),
                this.variableResolver
            );
        }
        if (this.type instanceof WildcardType) {
            Type resolved = resolveBounds(
                ((WildcardType) this.type).getUpperBounds()
            );
            if (resolved == null) {
                resolved =
                    resolveBounds(((WildcardType) this.type).getLowerBounds());
            }
            return forType(resolved, this.variableResolver);
        }
        if (this.type instanceof TypeVariable) {
            TypeVariable<?> variable = (TypeVariable<?>) this.type;
            // Try default variable resolution
            if (this.variableResolver != null) {
                ResolvableType resolved =
                    this.variableResolver.resolveVariable(variable);
                if (resolved != null) {
                    return resolved;
                }
            }
            // Fallback to bounds
            return forType(
                resolveBounds(variable.getBounds()),
                this.variableResolver
            );
        }
        return NONE;
    }

    private Type resolveBounds(Type[] bounds) {
        if (bounds.length == 0 || bounds[0] == Object.class) {
            return null;
        }
        return bounds[0];
    }

    private ResolvableType resolveVariable(TypeVariable<?> variable) {
        if (this.type instanceof TypeVariable) {
            return resolveType().resolveVariable(variable);
        }
        if (this.type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) this.type;
            Class<?> resolved = resolve();
            if (resolved == null) {
                return null;
            }
            TypeVariable<?>[] variables = resolved.getTypeParameters();
            for (int i = 0; i < variables.length; i++) {
                if (
                    Objects.equals(variables[i].getName(), variable.getName())
                ) {
                    Type actualType = parameterizedType.getActualTypeArguments()[i];
                    return forType(actualType, this.variableResolver);
                }
            }
            Type ownerType = parameterizedType.getOwnerType();
            if (ownerType != null) {
                return forType(ownerType, this.variableResolver)
                    .resolveVariable(variable);
            }
        }
        if (this.type instanceof WildcardType) {
            ResolvableType resolved = resolveType().resolveVariable(variable);
            if (resolved != null) {
                return resolved;
            }
        }
        if (this.variableResolver != null) {
            return this.variableResolver.resolveVariable(variable);
        }
        return null;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ResolvableType)) {
            return false;
        }

        ResolvableType otherType = (ResolvableType) other;
        if (!Objects.equals(this.type, otherType.type)) {
            return false;
        }
        if (
            this.typeProvider != otherType.typeProvider &&
            (
                this.typeProvider == null ||
                otherType.typeProvider == null ||
                !Objects.equals(
                    this.typeProvider.getType(),
                    otherType.typeProvider.getType()
                )
            )
        ) {
            return false;
        }
        if (
            this.variableResolver != otherType.variableResolver &&
            (
                this.variableResolver == null ||
                otherType.variableResolver == null ||
                !Objects.equals(
                    this.variableResolver.getSource(),
                    otherType.variableResolver.getSource()
                )
            )
        ) {
            return false;
        }
        return Objects.equals(this.componentType, otherType.componentType);
    }

    @Override
    public int hashCode() {
        return (this.hash != null ? this.hash : calculateHashCode());
    }

    private int calculateHashCode() {
        int hashCode = Objects.hashCode(this.type);
        if (this.typeProvider != null) {
            hashCode =
                31 * hashCode + Objects.hashCode(this.typeProvider.getType());
        }
        if (this.variableResolver != null) {
            hashCode =
                31 *
                hashCode +
                Objects.hashCode(this.variableResolver.getSource());
        }
        if (this.componentType != null) {
            hashCode = 31 * hashCode + Objects.hashCode(this.componentType);
        }
        return hashCode;
    }

    VariableResolver asVariableResolver() {
        if (this == NONE) {
            return null;
        }
        return new DefaultVariableResolver(this);
    }

    private Object readResolve() {
        return (this.type == EmptyType.INSTANCE ? NONE : this);
    }

    @Override
    public String toString() {
        if (isArray()) {
            return getComponentType() + "[]";
        }
        if (this.resolved == null) {
            return "?";
        }
        if (this.type instanceof TypeVariable) {
            TypeVariable<?> variable = (TypeVariable<?>) this.type;
            if (
                this.variableResolver == null ||
                this.variableResolver.resolveVariable(variable) == null
            ) {
                // Don't bother with variable boundaries for toString()...
                // Can cause infinite recursions in case of self-references
                return "?";
            }
        }
        if (hasGenerics()) {
            return (
                this.resolved.getName() +
                '<' +
                StrUtil.join(", ", (Object[]) getGenerics()) +
                '>'
            );
        }
        return this.resolved.getName();
    }

    interface VariableResolver extends Serializable {
        /**
         * Return the source of the resolver (used for hashCode and equals).
         */
        Object getSource();

        /**
         * Resolve the specified variable.
         *
         * @param variable the variable to resolve
         *
         * @return the resolved variable, or {@code null} if not found
         */
        ResolvableType resolveVariable(TypeVariable<?> variable);
    }

    @SuppressWarnings("serial")
    private static class DefaultVariableResolver implements VariableResolver {

        private final ResolvableType source;

        DefaultVariableResolver(ResolvableType resolvableType) {
            this.source = resolvableType;
        }

        @Override
        public ResolvableType resolveVariable(TypeVariable<?> variable) {
            return this.source.resolveVariable(variable);
        }

        @Override
        public Object getSource() {
            return this.source;
        }
    }

    @SuppressWarnings("serial")
    private static class TypeVariablesVariableResolver
        implements VariableResolver {

        private final TypeVariable<?>[] variables;

        private final ResolvableType[] generics;

        public TypeVariablesVariableResolver(
            TypeVariable<?>[] variables,
            ResolvableType[] generics
        ) {
            this.variables = variables;
            this.generics = generics;
        }

        @Override
        public ResolvableType resolveVariable(TypeVariable<?> variable) {
            TypeVariable<?> variableToCompare = SerializableTypeWrapper.unwrap(
                variable
            );
            for (int i = 0; i < this.variables.length; i++) {
                TypeVariable<?> resolvedVariable = SerializableTypeWrapper.unwrap(
                    this.variables[i]
                );
                if (Objects.equals(resolvedVariable, variableToCompare)) {
                    return this.generics[i];
                }
            }
            return null;
        }

        @Override
        public Object getSource() {
            return this.generics;
        }
    }

    private static final class SyntheticParameterizedType
        implements ParameterizedType, Serializable {

        private final Type rawType;

        private final Type[] typeArguments;

        public SyntheticParameterizedType(Type rawType, Type[] typeArguments) {
            this.rawType = rawType;
            this.typeArguments = typeArguments;
        }

        @Override
        public String getTypeName() {
            String typeName = this.rawType.getTypeName();
            if (this.typeArguments.length > 0) {
                StringJoiner stringJoiner = new StringJoiner(", ", "<", ">");
                for (Type argument : this.typeArguments) {
                    stringJoiner.add(argument.getTypeName());
                }
                return typeName + stringJoiner;
            }
            return typeName;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }

        @Override
        public Type getRawType() {
            return this.rawType;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return this.typeArguments;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof ParameterizedType)) {
                return false;
            }
            ParameterizedType otherType = (ParameterizedType) other;
            return (
                otherType.getOwnerType() == null &&
                this.rawType.equals(otherType.getRawType()) &&
                Arrays.equals(
                    this.typeArguments,
                    otherType.getActualTypeArguments()
                )
            );
        }

        @Override
        public int hashCode() {
            return (
                this.rawType.hashCode() *
                31 +
                Arrays.hashCode(this.typeArguments)
            );
        }

        @Override
        public String toString() {
            return getTypeName();
        }
    }

    private static class WildcardBounds {

        private final Kind kind;

        private final ResolvableType[] bounds;

        /**
         * Internal constructor to create a new {@link WildcardBounds}
         * instance.
         *
         * @param kind   the kind of bounds
         * @param bounds the bounds
         *
         * @see #get(ResolvableType)
         */
        public WildcardBounds(Kind kind, ResolvableType[] bounds) {
            this.kind = kind;
            this.bounds = bounds;
        }

        /**
         * Get a {@link WildcardBounds} instance for the specified type,
         * returning {@code null} if the specified type cannot be resolved to a
         * {@link WildcardType}.
         *
         * @param type the source type
         *
         * @return a {@link WildcardBounds} instance or {@code null}
         */
        public static WildcardBounds get(ResolvableType type) {
            ResolvableType resolveToWildcard = type;
            while (!(resolveToWildcard.getType() instanceof WildcardType)) {
                if (resolveToWildcard == NONE) {
                    return null;
                }
                resolveToWildcard = resolveToWildcard.resolveType();
            }
            WildcardType wildcardType = (WildcardType) resolveToWildcard.type;
            Kind boundsType =
                (
                    wildcardType.getLowerBounds().length > 0
                        ? Kind.LOWER
                        : Kind.UPPER
                );
            Type[] bounds =
                (
                    boundsType == Kind.UPPER
                        ? wildcardType.getUpperBounds()
                        : wildcardType.getLowerBounds()
                );
            ResolvableType[] resolvableBounds = new ResolvableType[bounds.length];
            for (int i = 0; i < bounds.length; i++) {
                resolvableBounds[i] =
                    ResolvableType.forType(bounds[i], type.variableResolver);
            }
            return new WildcardBounds(boundsType, resolvableBounds);
        }

        /**
         * Return {@code true} if this bounds is the same kind as the specified
         * bounds.
         */
        public boolean isSameKind(WildcardBounds bounds) {
            return this.kind == bounds.kind;
        }

        /**
         * Return {@code true} if this bounds is assignable to all the specified
         * types.
         *
         * @param types the types to test against
         *
         * @return {@code true} if this bounds is assignable to all types
         */
        public boolean isAssignableFrom(ResolvableType... types) {
            for (ResolvableType bound : this.bounds) {
                for (ResolvableType type : types) {
                    if (!isAssignable(bound, type)) {
                        return false;
                    }
                }
            }
            return true;
        }

        private boolean isAssignable(
            ResolvableType source,
            ResolvableType from
        ) {
            return (
                this.kind == Kind.UPPER
                    ? source.isAssignableFrom(from)
                    : from.isAssignableFrom(source)
            );
        }

        /**
         * Return the underlying bounds.
         */
        public ResolvableType[] getBounds() {
            return this.bounds;
        }

        /**
         * The various kinds of bounds.
         */
        enum Kind {
            UPPER,
            LOWER,
        }
    }

    @SuppressWarnings("serial")
    static class EmptyType implements Type, Serializable {

        static final Type INSTANCE = new EmptyType();

        Object readResolve() {
            return INSTANCE;
        }
    }
}
