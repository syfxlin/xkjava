/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.web.resolver;

import me.ixk.framework.web.ExceptionInfo;
import me.ixk.framework.web.WebContext;
import me.ixk.framework.web.WebDataBinder;

/**
 * 异常解决器
 *
 * @author Otstar Lin
 * @date 2020/11/24 上午 8:53
 */
public interface HandlerExceptionResolver {
    String NO_RESOLVER = "NO_RESOLVER";

    /**
     * 解决异常
     *
     * @param e       异常
     * @param info    异常信息
     * @param context 上下文信息
     * @param binder  数据绑定器
     * @return 返回
     */
    Object resolveException(
        Throwable e,
        ExceptionInfo info,
        WebContext context,
        WebDataBinder binder
    );
}
