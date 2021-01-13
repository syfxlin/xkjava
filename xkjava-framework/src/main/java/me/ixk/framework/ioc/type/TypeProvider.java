package me.ixk.framework.ioc.type;

import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

/**
 * Type 提供者
 *
 * @author Otstar Lin
 * @date 2020/12/29 下午 12:25
 */
public interface TypeProvider {
    /**
     * 获取 Type
     *
     * @return Type
     */
    Type getType();

    /**
     * 获取源对象
     *
     * @return 源对象
     */
    Object getSource();

    /**
     * 是否需要代理
     *
     * @return 是否需要
     */
    default boolean useProxy() {
        return false;
    }

    class FieldTypeProvider implements TypeProvider {

        private final Field field;

        public FieldTypeProvider(Field field) {
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

        @Override
        public boolean useProxy() {
            return true;
        }
    }

    class ParameterTypeProvider implements TypeProvider {

        private final Parameter parameter;

        public ParameterTypeProvider(Parameter parameter) {
            this.parameter = parameter;
        }

        public ParameterTypeProvider(
            Executable executable,
            int parameterIndex
        ) {
            this.parameter = executable.getParameters()[parameterIndex];
        }

        @Override
        public Type getType() {
            return this.parameter.getParameterizedType();
        }

        @Override
        public Object getSource() {
            return this.parameter;
        }
    }

    class ReturnValueTypeProvider implements TypeProvider {

        private final Method method;

        public ReturnValueTypeProvider(Method method) {
            this.method = method;
        }

        @Override
        public Type getType() {
            return this.method.getGenericReturnType();
        }

        @Override
        public Object getSource() {
            return this.method;
        }
    }

    class ArrayTypeProvider implements TypeProvider {

        private final Class<?> clazz;

        public ArrayTypeProvider(Class<?> clazz) {
            if (!clazz.isArray()) {
                throw new IllegalArgumentException(
                    "Target [" + clazz.getName() + "] is not array"
                );
            }
            this.clazz = clazz;
        }

        @Override
        public Type getType() {
            return this.clazz.getComponentType();
        }

        @Override
        public Object getSource() {
            return this.clazz;
        }
    }
}
