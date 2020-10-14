/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.utils;

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.hibernate.validator.HibernateValidator;

/**
 * 验证工具类
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 5:14
 */
public class Validation {
    protected static final Validator VALIDATOR = javax
        .validation.Validation.byProvider(HibernateValidator.class)
        .configure()
        .failFast(false)
        .buildValidatorFactory()
        .getValidator();

    @SuppressWarnings("unchecked")
    public static <T> ValidResult<T> validate(T obj, Class<?>... groups) {
        obj = (T) ReflectUtils.getProxyTarget(obj);
        Set<ConstraintViolation<T>> violationSet = VALIDATOR.validate(
            obj,
            groups
        );
        return new ValidResult<>(obj, violationSet);
    }

    @SuppressWarnings("unchecked")
    public static <T> ValidResult<T> validate(T obj, String propertyName) {
        obj = (T) ReflectUtils.getProxyTarget(obj);
        Set<ConstraintViolation<T>> violationSet = VALIDATOR.validateProperty(
            obj,
            propertyName
        );
        return new ValidResult<>(obj, violationSet);
    }
}
