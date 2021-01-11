/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.web.resolver;

import me.ixk.framework.web.WebContext;

/**
 * 后置异常处理器
 *
 * @author Otstar Lin
 * @date 2020/11/24 上午 11:32
 */
public interface AfterHandlerExceptionResolver {
    /**
     * 解决异常
     *
     * @param e       异常
     * @param context 上下文信息
     * @return 返回
     */
    boolean resolveException(Throwable e, WebContext context);
}
