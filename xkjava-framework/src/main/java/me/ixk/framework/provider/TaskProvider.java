/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.provider;

import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import me.ixk.framework.annotation.condition.ConditionalOnEnable;
import me.ixk.framework.annotation.condition.ConditionalOnMissingBean;
import me.ixk.framework.annotation.core.Bean;
import me.ixk.framework.annotation.core.Provider;
import me.ixk.framework.task.ScheduledExecutor;
import me.ixk.framework.task.ScheduledExecutor.DaemonThreadFactory;

/**
 * 任务提供者
 *
 * @author Otstar Lin
 * @date 2020/11/26 上午 9:11
 */
@Provider
public class TaskProvider {

    @Bean(name = "scheduledExecutor", destroyMethod = "shutdown")
    @ConditionalOnMissingBean(
        value = { ScheduledExecutor.class },
        name = "scheduledExecutor"
    )
    @ConditionalOnEnable(name = "scheduled")
    public ScheduledExecutor scheduledExecutor() {
        return new ScheduledExecutor(
            4,
            new DaemonThreadFactory(),
            new AbortPolicy()
        );
    }
}
