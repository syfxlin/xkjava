/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import me.ixk.framework.annotations.Autowired;
import me.ixk.framework.test.XkJavaTest;
import org.junit.jupiter.api.Test;

/**
 * @author Otstar Lin
 * @date 2020/11/26 上午 8:38
 */
@XkJavaTest
@Slf4j
class ScheduledExecutorTest {

    @Autowired
    ScheduledExecutor executor;

    @Test
    void schedule() throws InterruptedException {
        final CountDownLatch count = new CountDownLatch(10);
        executor.scheduleAtFixedRate(
            () -> {
                log.info("Task1: " + Thread.currentThread().getName());
                count.countDown();
            },
            0,
            1,
            TimeUnit.SECONDS
        );

        executor.scheduleAtFixedRate(
            () -> {
                log.info("Task2: " + Thread.currentThread().getName());
                count.countDown();
            },
            0,
            1,
            TimeUnit.SECONDS
        );

        count.await();
    }

    @Test
    void async() throws ExecutionException, InterruptedException {
        final Future<String> task1 = executor.submit(
            () -> {
                log.info("Task1");
                return "Task1";
            }
        );
        final Future<String> task2 = executor.submit(
            () -> {
                log.info("Task2");
                return "Task2";
            }
        );
        final String r1 = task1.get();
        final String r2 = task2.get();
        assertEquals("Task1", r1);
        assertEquals("Task2", r2);
    }
}
