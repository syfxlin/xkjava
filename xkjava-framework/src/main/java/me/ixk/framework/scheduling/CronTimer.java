/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.scheduling;

import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Otstar Lin
 * @date 2020/11/26 下午 2:58
 */
public class CronTimer implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(CronTimer.class);
    private final ScheduledExecutor executor;
    private boolean isStop = false;

    public CronTimer(ScheduledExecutor executor) {
        this.executor = executor;
    }

    @Override
    public void run() {
        long thisTime = System.currentTimeMillis();
        long nextTime;
        long sleep;
        while (!isStop) {
            // 推送到调度线程池判断，防止影响 CronTimer
            this.executor.execute(
                    () -> {
                        for (CronTask task : this.executor.getCronTasks()) {
                            if (task.isMatchNow()) {
                                this.executor.execute(task.getTask());
                            }
                        }
                    }
                );
            nextTime = ((thisTime / 1000) + 1) * 1000;
            sleep = nextTime - System.currentTimeMillis();
            try {
                TimeUnit.MILLISECONDS.sleep(sleep);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            thisTime = System.currentTimeMillis();
        }
    }

    public void stop() {
        isStop = false;
        if (log.isDebugEnabled()) {
            log.debug("CronTimer stop");
        }
    }
}
