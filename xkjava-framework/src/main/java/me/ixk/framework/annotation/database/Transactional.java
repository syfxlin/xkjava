/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotation.database;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.apache.ibatis.session.TransactionIsolationLevel;

/**
 * 声明式事务
 *
 * @author Otstar Lin
 * @date 2020/10/13 下午 5:52
 */
@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Transactional {
    TransactionIsolationLevel isolation() default TransactionIsolationLevel.READ_COMMITTED;

    Class<? extends Exception>[] rollbackFor() default {};
}
