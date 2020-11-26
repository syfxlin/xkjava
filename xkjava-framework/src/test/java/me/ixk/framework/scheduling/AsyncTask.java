/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.scheduling;

import java.util.Random;
import java.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import me.ixk.framework.annotations.Async;
import me.ixk.framework.annotations.Component;

/**
 * @author Otstar Lin
 * @date 2020/11/26 上午 11:06
 */
@Component
@Slf4j
public class AsyncTask {
    public static Random random = new Random();

    @Async
    public void doTaskOne() throws Exception {
        log.info("开始做任务一, Thread: {}", Thread.currentThread().getName());
        long start = System.currentTimeMillis();
        Thread.sleep(random.nextInt(1000));
        long end = System.currentTimeMillis();
        log.info("完成任务一，耗时：" + (end - start) + "毫秒");
    }

    @Async
    public void doTaskTwo() throws Exception {
        log.info("开始做任务二, Thread: {}", Thread.currentThread().getName());
        long start = System.currentTimeMillis();
        Thread.sleep(random.nextInt(1000));
        long end = System.currentTimeMillis();
        log.info("完成任务二，耗时：" + (end - start) + "毫秒");
    }

    @Async
    public Future<String> doTaskThree() throws Exception {
        log.info("开始做任务三, Thread: {}", Thread.currentThread().getName());
        long start = System.currentTimeMillis();
        Thread.sleep(random.nextInt(1000));
        long end = System.currentTimeMillis();
        log.info("完成任务三，耗时：" + (end - start) + "毫秒");
        return AsyncResult.of("任务三完成");
    }
}
