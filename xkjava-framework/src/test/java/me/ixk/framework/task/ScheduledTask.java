/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.task;

import com.alibaba.ttl.TransmittableThreadLocal;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import me.ixk.framework.annotation.Component;
import me.ixk.framework.annotation.Scheduled;

/**
 * @author Otstar Lin
 * @date 2020/11/26 下午 2:02
 */
@Component
@Slf4j
public class ScheduledTask {

    public static final TransmittableThreadLocal<String> TTL = new TransmittableThreadLocal<>();

    @Scheduled(initialDelay = 2000)
    public void doTaskOne() {
        log.info(
            "开始做任务一, Thread: {}, Time: {}",
            Thread.currentThread().getName(),
            LocalDateTime.now()
        );
    }

    @Scheduled(fixedDelay = 2000)
    public void doTaskTwo() {
        log.info(
            "开始做任务二, Thread: {}, Time: {}",
            Thread.currentThread().getName(),
            LocalDateTime.now()
        );
    }

    @Scheduled(fixedRate = 2000)
    public void doTaskThree() {
        log.info(
            "开始做任务三, Thread: {}, Time: {}",
            Thread.currentThread().getName(),
            LocalDateTime.now()
        );
    }

    @Scheduled(cron = "0/2 * * * * *")
    public void doTaskFour() {
        log.info(
            "开始做任务四, Thread: {}, Time: {}",
            Thread.currentThread().getName(),
            LocalDateTime.now()
        );
        log.info("任务四 TTL 值：{}", TTL.get());
    }
}
