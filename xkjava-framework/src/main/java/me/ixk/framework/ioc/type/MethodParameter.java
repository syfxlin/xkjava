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

import io.github.imsejin.expression.core.KotlinDetector;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import kotlin.Unit;
import kotlin.reflect.KFunction;
import kotlin.reflect.KParameter;
import kotlin.reflect.jvm.ReflectJvmMapping;

/**
 * MethodParameter
 *
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Andy Clement
 * @author Sam Brannen
 * @author Sebastien Deleuze
 * @author Phillip Webb
 * @date 2020/12/29 上午 12:44
 */
public class MethodParameter {

    private final Executable executable;

    private final int parameterIndex;
    Map<Integer, Integer> typeIndexesPerLevel;
    private volatile Parameter parameter;
    private int nestingLevel;
    private volatile Class<?> containingClass;

    private volatile Class<?> parameterType;

    private volatile Type genericParameterType;

    public MethodParameter(Method method, int parameterIndex) {
        this(method, parameterIndex, 1);
    }

    public MethodParameter(
        Method method,
        int parameterIndex,
        int nestingLevel
    ) {
        this.executable = method;
        this.parameterIndex = validateIndex(method, parameterIndex);
        this.nestingLevel = nestingLevel;
    }

    public MethodParameter(Constructor<?> constructor, int parameterIndex) {
        this(constructor, parameterIndex, 1);
    }

    public MethodParameter(
        Constructor<?> constructor,
        int parameterIndex,
        int nestingLevel
    ) {
        this.executable = constructor;
        this.parameterIndex = validateIndex(constructor, parameterIndex);
        this.nestingLevel = nestingLevel;
    }

    MethodParameter(
        Executable executable,
        int parameterIndex,
        Class<?> containingClass
    ) {
        this.executable = executable;
        this.parameterIndex = validateIndex(executable, parameterIndex);
        this.nestingLevel = 1;
        this.containingClass = containingClass;
    }

    @Deprecated
    public static MethodParameter forMethodOrConstructor(
        Object methodOrConstructor,
        int parameterIndex
    ) {
        if (!(methodOrConstructor instanceof Executable)) {
            throw new IllegalArgumentException(
                "Given object [" +
                methodOrConstructor +
                "] is neither a Method nor a Constructor"
            );
        }
        return forExecutable((Executable) methodOrConstructor, parameterIndex);
    }

    public static MethodParameter forExecutable(
        Executable executable,
        int parameterIndex
    ) {
        if (executable instanceof Method) {
            return new MethodParameter((Method) executable, parameterIndex);
        } else if (executable instanceof Constructor) {
            return new MethodParameter(
                (Constructor<?>) executable,
                parameterIndex
            );
        } else {
            throw new IllegalArgumentException(
                "Not a Method/Constructor: " + executable
            );
        }
    }

    public static MethodParameter forParameter(Parameter parameter) {
        return forExecutable(
            parameter.getDeclaringExecutable(),
            findParameterIndex(parameter)
        );
    }

    protected static int findParameterIndex(Parameter parameter) {
        Executable executable = parameter.getDeclaringExecutable();
        Parameter[] allParams = executable.getParameters();
        // Try first with identity checks for greater performance.
        for (int i = 0; i < allParams.length; i++) {
            if (parameter == allParams[i]) {
                return i;
            }
        }
        // Potentially try again with object equality checks in order to avoid race
        // conditions while invoking java.lang.reflect.Executable.getParameters().
        for (int i = 0; i < allParams.length; i++) {
            if (parameter.equals(allParams[i])) {
                return i;
            }
        }
        throw new IllegalArgumentException(
            "Given parameter [" +
            parameter +
            "] does not match any parameter in the declaring executable"
        );
    }

    private static int validateIndex(
        Executable executable,
        int parameterIndex
    ) {
        int count = executable.getParameterCount();
        return parameterIndex;
    }

    public Method getMethod() {
        return (
            this.executable instanceof Method ? (Method) this.executable : null
        );
    }

    public Constructor<?> getConstructor() {
        return (
            this.executable instanceof Constructor
                ? (Constructor<?>) this.executable
                : null
        );
    }

    public Class<?> getDeclaringClass() {
        return this.executable.getDeclaringClass();
    }

    public Executable getExecutable() {
        return this.executable;
    }

    public Parameter getParameter() {
        if (this.parameterIndex < 0) {
            throw new IllegalStateException(
                "Cannot retrieve Parameter descriptor for method return type"
            );
        }
        Parameter parameter = this.parameter;
        if (parameter == null) {
            parameter = getExecutable().getParameters()[this.parameterIndex];
            this.parameter = parameter;
        }
        return parameter;
    }

    public int getParameterIndex() {
        return this.parameterIndex;
    }

    @Deprecated
    public void increaseNestingLevel() {
        this.nestingLevel++;
    }

    @Deprecated
    public void decreaseNestingLevel() {
        getTypeIndexesPerLevel().remove(this.nestingLevel);
        this.nestingLevel--;
    }

    public int getNestingLevel() {
        return this.nestingLevel;
    }

    @Deprecated
    public void setTypeIndexForCurrentLevel(int typeIndex) {
        getTypeIndexesPerLevel().put(this.nestingLevel, typeIndex);
    }

    private Map<Integer, Integer> getTypeIndexesPerLevel() {
        if (this.typeIndexesPerLevel == null) {
            this.typeIndexesPerLevel = new HashMap<>(4);
        }
        return this.typeIndexesPerLevel;
    }

    public Class<?> getContainingClass() {
        Class<?> containingClass = this.containingClass;
        return (
            containingClass != null ? containingClass : getDeclaringClass()
        );
    }

    @Deprecated
    void setContainingClass(Class<?> containingClass) {
        this.containingClass = containingClass;
        this.parameterType = null;
    }

    public Class<?> getParameterType() {
        Class<?> paramType = this.parameterType;
        if (paramType != null) {
            return paramType;
        }
        if (getContainingClass() != getDeclaringClass()) {
            paramType =
                ResolvableType.forMethodParameter(this, null, 1).resolve();
        }
        if (paramType == null) {
            paramType = computeParameterType();
        }
        this.parameterType = paramType;
        return paramType;
    }

    @Deprecated
    void setParameterType(Class<?> parameterType) {
        this.parameterType = parameterType;
    }

    public Type getGenericParameterType() {
        Type paramType = this.genericParameterType;
        if (paramType == null) {
            if (this.parameterIndex < 0) {
                Method method = getMethod();
                paramType =
                    (
                        method != null
                            ? (
                                KotlinDetector.isKotlinReflectPresent() &&
                                    KotlinDetector.isKotlinType(
                                        getContainingClass()
                                    )
                                    ? KotlinDelegate.getGenericReturnType(
                                        method
                                    )
                                    : method.getGenericReturnType()
                            )
                            : void.class
                    );
            } else {
                Type[] genericParameterTypes =
                    this.executable.getGenericParameterTypes();
                int index = this.parameterIndex;
                if (
                    this.executable instanceof Constructor &&
                    isInnerClass(this.executable.getDeclaringClass()) &&
                    genericParameterTypes.length ==
                    this.executable.getParameterCount() -
                    1
                ) {
                    // Bug in javac: type array excludes enclosing instance parameter
                    // for inner classes with at least one generic constructor parameter,
                    // so access it with the actual parameter index lowered by 1
                    index = this.parameterIndex - 1;
                }
                paramType =
                    (
                        index >= 0 && index < genericParameterTypes.length
                            ? genericParameterTypes[index]
                            : computeParameterType()
                    );
            }
            this.genericParameterType = paramType;
        }
        return paramType;
    }

    private Class<?> computeParameterType() {
        if (this.parameterIndex < 0) {
            Method method = getMethod();
            if (method == null) {
                return void.class;
            }
            if (
                KotlinDetector.isKotlinReflectPresent() &&
                KotlinDetector.isKotlinType(getContainingClass())
            ) {
                return KotlinDelegate.getReturnType(method);
            }
            return method.getReturnType();
        }
        return this.executable.getParameterTypes()[this.parameterIndex];
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MethodParameter)) {
            return false;
        }
        MethodParameter otherParam = (MethodParameter) other;
        return (
            getContainingClass() == otherParam.getContainingClass() &&
            Objects.equals(
                this.typeIndexesPerLevel,
                otherParam.typeIndexesPerLevel
            ) &&
            this.nestingLevel == otherParam.nestingLevel &&
            this.parameterIndex == otherParam.parameterIndex &&
            this.executable.equals(otherParam.executable)
        );
    }

    @Override
    public int hashCode() {
        return (31 * this.executable.hashCode() + this.parameterIndex);
    }

    @Override
    public String toString() {
        Method method = getMethod();
        return (
            (
                method != null
                    ? "method '" + method.getName() + "'"
                    : "constructor"
            ) +
            " parameter " +
            this.parameterIndex
        );
    }

    private static boolean isInnerClass(Class<?> clazz) {
        return (
            clazz.isMemberClass() && !Modifier.isStatic(clazz.getModifiers())
        );
    }

    private static class KotlinDelegate {

        /**
         * Check whether the specified {@link MethodParameter} represents a
         * nullable Kotlin type, an optional parameter (with a default value in
         * the Kotlin declaration) or a {@code Continuation} parameter used in
         * suspending functions.
         */
        public static boolean isOptional(MethodParameter param) {
            Method method = param.getMethod();
            int index = param.getParameterIndex();
            if (method != null && index == -1) {
                KFunction<?> function = ReflectJvmMapping.getKotlinFunction(
                    method
                );
                return (
                    function != null &&
                    function.getReturnType().isMarkedNullable()
                );
            }
            KFunction<?> function;
            Predicate<KParameter> predicate;
            if (method != null) {
                if (
                    param
                        .getParameterType()
                        .getName()
                        .equals("kotlin.coroutines.Continuation")
                ) {
                    return true;
                }
                function = ReflectJvmMapping.getKotlinFunction(method);
                predicate = p -> KParameter.Kind.VALUE.equals(p.getKind());
            } else {
                Constructor<?> ctor = param.getConstructor();
                function = ReflectJvmMapping.getKotlinFunction(ctor);
                predicate =
                    p ->
                        (
                            KParameter.Kind.VALUE.equals(p.getKind()) ||
                            KParameter.Kind.INSTANCE.equals(p.getKind())
                        );
            }
            if (function != null) {
                int i = 0;
                for (KParameter kParameter : function.getParameters()) {
                    if (predicate.test(kParameter)) {
                        if (index == i++) {
                            return (
                                kParameter.getType().isMarkedNullable() ||
                                kParameter.isOptional()
                            );
                        }
                    }
                }
            }
            return false;
        }

        /**
         * Return the generic return type of the method, with support of
         * suspending functions via Kotlin reflection.
         */
        private static Type getGenericReturnType(Method method) {
            try {
                KFunction<?> function = ReflectJvmMapping.getKotlinFunction(
                    method
                );
                if (function != null && function.isSuspend()) {
                    return ReflectJvmMapping.getJavaType(
                        function.getReturnType()
                    );
                }
            } catch (UnsupportedOperationException ex) {
                // probably a synthetic class - let's use java reflection instead
            }
            return method.getGenericReturnType();
        }

        /**
         * Return the return type of the method, with support of suspending
         * functions via Kotlin reflection.
         */
        private static Class<?> getReturnType(Method method) {
            try {
                KFunction<?> function = ReflectJvmMapping.getKotlinFunction(
                    method
                );
                if (function != null && function.isSuspend()) {
                    Type paramType = ReflectJvmMapping.getJavaType(
                        function.getReturnType()
                    );
                    if (paramType == Unit.class) {
                        paramType = void.class;
                    }
                    return ResolvableType
                        .forType(paramType)
                        .resolve(method.getReturnType());
                }
            } catch (UnsupportedOperationException ex) {
                // probably a synthetic class - let's use java reflection instead
            }
            return method.getReturnType();
        }
    }
}
