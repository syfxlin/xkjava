package me.ixk.framework.task;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;

/**
 * @author Otstar Lin
 * @date 2021/1/10 下午 4:26
 */
public class SyncTaskExecutor
    extends AbstractExecutorService
    implements AsyncTaskExecutor {

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
        command.run();
    }
}
