/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotation.task;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.annotation.core.RepeatItem;

/**
 * 定时任务（多）
 *
 * @author Otstar Lin
 * @date 2020/11/25 下午 11:25
 */
@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@RepeatItem(Scheduled.class)
public @interface Schedules {
    Scheduled[] value();
}
