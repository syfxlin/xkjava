/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.validation.ConstraintViolation;

/**
 * 验证结果（单个）
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 5:15
 */
public class ValidResult<T> {

    private final T target;
    private final Map<String, ConstraintViolation<T>> errors = new ConcurrentHashMap<>();

    public ValidResult(
        final T target,
        final Set<ConstraintViolation<T>> violationSet
    ) {
        this.target = target;
        for (final ConstraintViolation<T> violation : violationSet) {
            this.errors.put(violation.getPropertyPath().toString(), violation);
        }
    }

    public boolean isOk() {
        return this.errors.isEmpty();
    }

    public boolean isFail() {
        return !this.isOk();
    }

    public T getTarget() {
        return target;
    }

    @SuppressWarnings("unchecked")
    public Class<T> getTargetClass() {
        return (Class<T>) target.getClass();
    }

    public Collection<ConstraintViolation<T>> getViolationSet() {
        return this.errors.values();
    }

    public int getErrorCount() {
        return this.errors.size();
    }

    public Map<String, String> getErrorMessages() {
        final Map<String, String> map = new ConcurrentHashMap<>();
        for (final Map.Entry<String, ConstraintViolation<T>> entry : this.errors.entrySet()) {
            map.put(entry.getKey(), entry.getValue().getMessage());
        }
        return map;
    }

    public Map<String, ConstraintViolation<T>> getErrors() {
        return errors;
    }

    public ConstraintViolation<T> getViolation(final String name) {
        return this.errors.get(name);
    }

    public String getMessage(final String name) {
        final ConstraintViolation<T> violation = this.getViolation(name);
        if (violation == null) {
            return null;
        }
        return violation.getMessage();
    }

    public Object getInvalidValue(final String name) {
        final ConstraintViolation<T> violation = this.getViolation(name);
        if (violation == null) {
            return null;
        }
        return violation.getInvalidValue();
    }
}
