package me.ixk.framework.http.result;

import me.ixk.framework.web.WebContext;

/**
 * @author Otstar Lin
 * @date 2021/1/11 下午 9:57
 */
@FunctionalInterface
public interface AsyncResult<V> {
    /**
     * 执行
     *
     * @param context Web 上下文
     * @return 返回值
     * @throws Exception 异常
     */
    V handle(WebContext context) throws Exception;
}
