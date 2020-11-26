/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.providers;

import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import me.ixk.framework.annotations.Bean;
import me.ixk.framework.annotations.ConditionalOnMissingBean;
import me.ixk.framework.annotations.Provider;
import me.ixk.framework.scheduling.AsyncTaskExecutor;
import me.ixk.framework.scheduling.ScheduledExecutor;
import me.ixk.framework.scheduling.ScheduledExecutor.DaemonThreadFactory;
import me.ixk.framework.scheduling.ScheduledTaskExecutor;

/**
 * 任务提供者
 *
 * @author Otstar Lin
 * @date 2020/11/26 上午 9:11
 */
@Provider
public class TaskProvider {

    @Bean(
        name = "scheduledExecutor",
        type = { AsyncTaskExecutor.class, ScheduledTaskExecutor.class },
        destroyMethod = "shutdown"
    )
    @ConditionalOnMissingBean(
        value = {
            ScheduledExecutor.class,
            AsyncTaskExecutor.class,
            ScheduledTaskExecutor.class,
        },
        name = "scheduledExecutor"
    )
    public ScheduledExecutor scheduledExecutor() {
        return new ScheduledExecutor(
            20,
            new DaemonThreadFactory(),
            new AbortPolicy()
        );
    }
}
