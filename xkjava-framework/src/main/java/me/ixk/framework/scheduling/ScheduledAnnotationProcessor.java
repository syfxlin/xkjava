/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.scheduling;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import me.ixk.framework.annotations.AnnotationProcessor;
import me.ixk.framework.annotations.Order;
import me.ixk.framework.annotations.Scheduled;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.processor.AbstractAnnotationProcessor;
import me.ixk.framework.utils.AnnotationUtils;
import me.ixk.framework.utils.MergedAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Otstar Lin
 * @date 2020/11/26 下午 1:51
 */
@AnnotationProcessor
@Order(Order.MEDIUM_PRECEDENCE + 3)
public class ScheduledAnnotationProcessor extends AbstractAnnotationProcessor {
    private static final Logger log = LoggerFactory.getLogger(
        ScheduledAnnotationProcessor.class
    );

    private ScheduledTaskExecutor executor;

    public ScheduledAnnotationProcessor(XkJava app) {
        super(app);
    }

    @Override
    public void process() {
        this.executor = this.app.make(ScheduledTaskExecutor.class);
        if (executor == null) {
            throw new NullPointerException(
                "No executor specified and no default executor set on scheduled task"
            );
        }
        this.processAnnotation(
                Scheduled.class,
                clazz -> {},
                method -> {
                    final MergedAnnotation annotation = AnnotationUtils.getAnnotation(
                        method
                    );
                    final List<Scheduled> schedules = annotation.getAnnotations(
                        Scheduled.class
                    );
                    if (!schedules.isEmpty()) {
                        for (final Scheduled schedule : schedules) {
                            this.processScheduled(schedule, method);
                        }
                    }
                }
            );
    }

    private void processScheduled(
        final Scheduled scheduled,
        final Method method
    ) {
        Runnable runnable = () -> this.app.call(method);

        // Cron 定时任务
        String cron = scheduled.cron();
        if (!cron.isEmpty()) {
            final String zone = scheduled.zone();
            this.executor.scheduleCron(runnable, cron, zone);
            return;
        }

        // 初始延迟
        long initialDelay = scheduled.initialDelay();
        String initialDelayString = scheduled.initialDelayString();
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
