/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.scheduling;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import me.ixk.framework.annotations.Scheduled;
import me.ixk.framework.annotations.ScopeType;
import me.ixk.framework.ioc.Binding;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.registry.BeanBindRegistry;
import me.ixk.framework.utils.MergedAnnotation;

/**
 * @author Otstar Lin
 * @date 2020/11/29 下午 9:59
 */
public class ScheduledRegistry implements BeanBindRegistry {

    private static final String EXECUTED = "executed";

    @Override
    public Binding register(
        final XkJava app,
        final AnnotatedElement element,
        final ScopeType scopeType,
        final MergedAnnotation annotation
    ) {
        return app.bind(
            ((Method) element).getName(),
            (container, dataBinder) -> {
                final ScheduledTaskExecutor executor = app.make(
                    ScheduledTaskExecutor.class
                );
                if (executor == null) {
                    throw new NullPointerException(
                        "No executor specified and no default executor set on scheduled task"
                    );
                }
                for (final Scheduled scheduled : annotation.getAnnotations(
                    Scheduled.class
                )) {
                    this.processScheduled(
                            scheduled,
                            executor,
                            app,
                            (Method) element
                        );
                }
                return EXECUTED;
            },
            null,
            scopeType
        );
    }

    private void processScheduled(
        final Scheduled scheduled,
        final ScheduledTaskExecutor executor,
        final XkJava app,
        final Method method
    ) {
        final Runnable runnable = () -> app.call(method);

        // Cron 定时任务
        final String cron = scheduled.cron();
        if (!cron.isEmpty()) {
            final String zone = scheduled.zone();
            executor.scheduleCron(runnable, cron, zone);
            return;
        }

        // 初始延迟
        long initialDelay = scheduled.initialDelay();
        final String initialDelayString = scheduled.initialDelayString();
        if (!initialDelayString.isEmpty()) {
            if (initialDelay >= 0) {
                throw new IllegalArgumentException(
                    "Specify 'initialDelay' or 'initialDelayString', not both"
                );
            }
            initialDelay = Duration.parse(initialDelayString).toMillis();
        }
        if (initialDelay < 0) {
            initialDelay = 0;
        }

        // 固定延迟
        long fixedDelay = scheduled.fixedDelay();
        final String fixedDelayString = scheduled.fixedDelayString();
        if (!fixedDelayString.isEmpty()) {
            if (fixedDelay >= 0) {
                throw new IllegalArgumentException(
                    "Specify 'fixedDelay' or 'fixedDelayString', not both"
                );
            }
            fixedDelay = Duration.parse(fixedDelayString).toMillis();
        }
        if (fixedDelay < 0) {
            fixedDelay = 0;
        }

        // 固定频率
        long fixedRate = scheduled.fixedRate();
        final String fixedRateString = scheduled.fixedRateString();
        if (!fixedRateString.isEmpty()) {
            if (fixedRate >= 0) {
                throw new IllegalArgumentException(
                    "Specify 'fixedRate' or 'fixedRateString', not both"
                );
            }
            fixedRate = Duration.parse(fixedRateString).toMillis();
        }
        if (fixedRate < 0) {
            fixedRate = 0;
        }

        if (fixedDelay != 0 && fixedRate != 0) {
            throw new IllegalArgumentException(
                "Specify 'fixedDelay' or 'fixedRate', not both"
            );
        }

        if (fixedDelay == 0 && fixedRate == 0) {
            executor.schedule(runnable, initialDelay, TimeUnit.MILLISECONDS);
        } else if (fixedDelay > 0) {
            executor.scheduleWithFixedDelay(
                runnable,
                initialDelay,
                fixedDelay,
                TimeUnit.MILLISECONDS
            );
        } else {
            executor.scheduleAtFixedRate(
                runnable,
                initialDelay,
                fixedRate,
                TimeUnit.MILLISECONDS
            );
        }
    }
}
