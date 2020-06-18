package me.ixk.framework.aop;

import java.lang.reflect.Method;
import java.util.List;

public class TargetSource {
    private final Class<?> targetType;
    private final Class<?>[] interfaces;
    private final Object target;

    public TargetSource(
        Object target,
        Class<?> targetType,
        Class<?>[] interfaces
    ) {
        this.targetType = targetType;
        this.interfaces = interfaces;
        this.target = target;
    }

    public Class<?> getTargetType() {
        return targetType;
    }

    public Class<?>[] getInterfaces() {
        return interfaces;
    }

    public Object getTarget() {
        return target;
    }

    public List<Advice> getAdvices(Method method) {
        return AspectManager.getAdvices(method);
    }
}
