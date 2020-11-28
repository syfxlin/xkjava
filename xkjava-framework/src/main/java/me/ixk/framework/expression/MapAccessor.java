/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.expression;

import io.github.imsejin.expression.AccessException;
import io.github.imsejin.expression.EvaluationContext;
import io.github.imsejin.expression.TypedValue;
import io.github.imsejin.expression.asm.MethodVisitor;
import io.github.imsejin.expression.spel.CodeFlow;
import io.github.imsejin.expression.spel.CompilablePropertyAccessor;
import java.util.Map;

/**
 * @author Otstar Lin
 * @date 2020/11/28 下午 10:08
 */
public class MapAccessor implements CompilablePropertyAccessor {

    @Override
    public Class<?>[] getSpecificTargetClasses() {
        return new Class<?>[] { Map.class };
    }

    @Override
    public boolean canRead(
        EvaluationContext context,
        Object target,
        String name
    ) throws AccessException {
        return (
            target instanceof Map && ((Map<?, ?>) target).containsKey(name)
        );
    }

    @Override
    public TypedValue read(
        EvaluationContext context,
        Object target,
        String name
    ) throws AccessException {
        Map<?, ?> map = (Map<?, ?>) target;
        Object value = map.get(name);
        if (value == null && !map.containsKey(name)) {
            throw new MapAccessException(name);
        }
        return new TypedValue(value);
    }

    @Override
    public boolean canWrite(
        EvaluationContext context,
        Object target,
        String name
    ) throws AccessException {
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void write(
        EvaluationContext context,
        Object target,
        String name,
        Object newValue
    ) throws AccessException {
        Map<Object, Object> map = (Map<Object, Object>) target;
        map.put(name, newValue);
    }

    @Override
    public boolean isCompilable() {
        return true;
    }

    @Override
    public Class<?> getPropertyType() {
        return Object.class;
    }

    @Override
    public void generateCode(
        String propertyName,
        MethodVisitor mv,
        CodeFlow cf
    ) {
        String descriptor = cf.lastDescriptor();
        if (descriptor == null || !descriptor.equals("Ljava/util/Map")) {
            if (descriptor == null) {
                cf.loadTarget(mv);
            }
            CodeFlow.insertCheckCast(mv, "Ljava/util/Map");
        }
        mv.visitLdcInsn(propertyName);
        mv.visitMethodInsn(
            INVOKEINTERFACE,
            "java/util/Map",
            "get",
            "(Ljava/lang/Object;)Ljava/lang/Object;",
            true
        );
    }

    @SuppressWarnings("serial")
    private static class MapAccessException extends AccessException {

        private final String key;

        public MapAccessException(String key) {
            super("");
            this.key = key;
        }

        @Override
        public String getMessage() {
            return "Map does not contain a value for key '" + this.key + "'";
        }
    }
}
