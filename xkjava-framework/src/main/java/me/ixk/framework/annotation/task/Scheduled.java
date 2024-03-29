/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotation.task;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.annotation.condition.ConditionalOnBean;
import me.ixk.framework.annotation.core.Bean;
import me.ixk.framework.annotation.core.BindRegistry;
import me.ixk.framework.task.ScheduledExecutor;
import me.ixk.framework.task.ScheduledRegistry;

/**
 * 定时任务
 *
 * @author Otstar Lin
 * @date 2020/11/25 下午 11:24
 */
@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Schedules.class)
@Bean
@BindRegistry(ScheduledRegistry.class)
@ConditionalOnBean(value = ScheduledExecutor.class)
public @interface Scheduled {
    String CRON_DISABLED = "-";

    String cron() default "";

    String zone() default "";

    long fixedDelay() default -1;

    String fixedDelayString() default "";

    long fixedRate() default -1;

    String fixedRateString() default "";

    long initialDelay() default -1;

    String initialDelayString() default "";
}
