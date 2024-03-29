/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.task;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;

/**
 * 异步调用返回值
 *
 * @author Otstar Lin
 * @date 2020/11/26 上午 11:21
 */
public class AsyncResult<V> implements Future<V> {

    private final V value;
    private final Throwable exception;

    public AsyncResult(final V value) {
        this(value, null);
    }

    public AsyncResult(final V value, final Throwable exception) {
        this.value = value;
        this.exception = exception;
    }

    public static <V> AsyncResult<V> of(final V value) {
        return new AsyncResult<>(value);
    }

    public static <V> AsyncResult<V> of(final Throwable exception) {
        return new AsyncResult<>(null, exception);
    }

    @Override
    public boolean cancel(final boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return true;
    }

    @Override
    public V get() throws ExecutionException {
        if (this.exception != null) {
            throw (
                this.exception instanceof ExecutionException
                    ? (ExecutionException) this.exception
                    : new ExecutionException(this.exception)
            );
        }
        return this.value;
    }

    @Override
    public V get(final long timeout, final @NotNull TimeUnit unit)
        throws InterruptedException, ExecutionException {
        return get();
    }
}
