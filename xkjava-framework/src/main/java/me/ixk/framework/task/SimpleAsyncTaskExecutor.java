package me.ixk.framework.task;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.jetbrains.annotations.NotNull;

/**
 * 简单异步任务执行器
 * <p>
 * 注意：本执行器并不是线程池
 *
 * @author Otstar Lin
 * @date 2021/1/10 下午 3:58
 */
public class SimpleAsyncTaskExecutor
    extends AbstractExecutorService
    implements ExecutorService {

    private final ThreadFactory threadFactory;
    private final AtomicInteger threadCount = new AtomicInteger(0);

    public SimpleAsyncTaskExecutor() {
        this.threadFactory =
            r -> {
                final Thread thread = new Thread(r);
                thread.setName(
                    this.getClass().getSimpleName() +
                    "-" +
                    threadCount.incrementAndGet()
                );
                return thread;
            };
    }

    public SimpleAsyncTaskExecutor(String threadNamePrefix) {
        this.threadFactory =
            r -> {
                final Thread thread = new Thread(r);
                thread.setName(
                    threadNamePrefix + "-" + threadCount.incrementAndGet()
                );
                return thread;
            };
    }

    public SimpleAsyncTaskExecutor(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
    }

    @Override
    public void shutdown() {}

    @NotNull
    @Override
    public List<Runnable> shutdownNow() {
        return Collections.emptyList();
    }

    @Override
    public boolean isShutdown() {
        return false;
    }

    @Override
    public boolean isTerminated() {
        return false;
    }

    @Override
    public boolean awaitTermination(long timeout, @NotNull TimeUnit unit) {
        return false;
    }

    @Override
    public void execute(@NotNull Runnable command) {
        final Thread thread = this.threadFactory.newThread(command);
        thread.start();
    }
}
