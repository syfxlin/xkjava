/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.task;

import java.time.LocalDateTime;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import me.ixk.framework.annotations.Component;
import me.ixk.framework.annotations.Scheduled;

/**
 * @author Otstar Lin
 * @date 2020/11/26 下午 2:02
 */
@Component
@Slf4j
public class ScheduledTasks {

    public static Random random = new Random();

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
    }
}
