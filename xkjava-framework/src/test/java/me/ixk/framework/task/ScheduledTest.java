/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.task;

import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import me.ixk.framework.annotations.Autowired;
import me.ixk.framework.annotations.Enable;
import me.ixk.framework.test.XkJavaTest;
import org.junit.jupiter.api.Test;

/**
 * @author Otstar Lin
 * @date 2020/11/26 下午 2:05
 */
@XkJavaTest
@Slf4j
@Enable(name = "scheduled")
class ScheduledTest {

    @Autowired
    ScheduledTasks scheduledTask;

    @Test
    void schedule() throws InterruptedException {
        log.info(
            "Thread: {}, Time: {}",
            Thread.currentThread().getName(),
            LocalDateTime.now()
        );
        Thread.sleep(20000);
    }
}
