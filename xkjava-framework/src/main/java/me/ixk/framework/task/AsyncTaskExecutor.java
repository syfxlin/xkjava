/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.task;

import java.util.concurrent.ExecutorService;

/**
 * 异步任务执行器
 * <p>
 * 原始的 ExecutorService 也可以作为异步执行器，这个接口只为了区分
 *
 * @author Otstar Lin
 * @date 2020/11/26 上午 9:02
 */
public interface AsyncTaskExecutor extends ExecutorService {}
