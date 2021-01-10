/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import me.ixk.framework.annotations.Autowired;
import me.ixk.framework.annotations.Bean;
import me.ixk.framework.annotations.Enable;
import me.ixk.framework.test.XkJavaTest;
import org.junit.jupiter.api.Test;

/**
 * @author Otstar Lin
 * @date 2020/11/26 上午 11:01
 */
@XkJavaTest
@Slf4j
@Enable(name = "async")
class AsyncTest {

    @Autowired
    AsyncTask asyncTask;

    @Bean
    public ExecutorService poolExecutor() {
        return Executors.newCachedThreadPool();
    }

    @Test
    void async() throws Exception {
        log.info("Thread: {}", Thread.currentThread().getName());
        asyncTask.doTaskOne();
        asyncTask.doTaskTwo();
        final Future<String> three = asyncTask.doTaskThree();
        if (three.isDone()) {
            log.info("Three return value: {}", three.get());
        }
        Thread.sleep(10000);
    }
}
