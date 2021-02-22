/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.task;

import com.alibaba.ttl.TransmittableThreadLocal;
import java.util.Random;
import java.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import me.ixk.framework.annotation.core.Component;
import me.ixk.framework.annotation.task.Async;

/**
 * @author Otstar Lin
 * @date 2020/11/26 上午 11:06
 */
@Component
@Slf4j
public class AsyncTask {

    public static final Random random = new Random();
    public static final TransmittableThreadLocal<String> TTL = new TransmittableThreadLocal<>();

    @Async
    public void doTaskOne() throws Exception {
        log.info("开始做任务一, Thread: {}", Thread.currentThread().getName());
        final long start = System.currentTimeMillis();
        Thread.sleep(random.nextInt(1000));
        final long end = System.currentTimeMillis();
        log.info("完成任务一，耗时：" + (end - start) + "毫秒");
    }

    @Async("test")
    public void doTaskTwo() throws Exception {
        log.info("开始做任务二, Thread: {}", Thread.currentThread().getName());
        final long start = System.currentTimeMillis();
        Thread.sleep(random.nextInt(1000));
        final long end = System.currentTimeMillis();
        log.info("完成任务二，耗时：" + (end - start) + "毫秒");
    }

    @Async("poolExecutor")
    public Future<String> doTaskThree() throws Exception {
        log.info("开始做任务三, Thread: {}", Thread.currentThread().getName());
        log.info("任务三获取 ThreadLocal 内容：{}", TTL.get());
        final long start = System.currentTimeMillis();
        Thread.sleep(random.nextInt(1000));
        final long end = System.currentTimeMillis();
        log.info("完成任务三，耗时：" + (end - start) + "毫秒");
        return AsyncResult.of("任务三完成");
    }
}
