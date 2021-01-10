/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.context;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.servlet.http.HttpServletRequest;

/**
 * RequestAttributeContext
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 12:39
 */
public interface RequestAttributeContext extends Context {
    String INSTANCE_ATTRIBUTE_NAME =
        RequestAttributeContext.class.getName() + ".INSTANCE_ATTRIBUTE_NAME";

    /**
     * 删除 Context
     */
    void removeContext();

    /**
     * 获取 Context
     *
     * @return Request 对象
     */
    HttpServletRequest getContext();

    /**
     * 设置 Context
     *
     * @param request Request 对象
     */
    void setContext(HttpServletRequest request);

    /**
     * 获取所有实例
     *
     * @return 所有实例
     */
    @Override
    @SuppressWarnings("unchecked")
    default ConcurrentMap<String, Object> getInstances() {
        ConcurrentMap<String, Object> instances = (ConcurrentMap<String, Object>) this.getContext()
            .getAttribute(INSTANCE_ATTRIBUTE_NAME);
        if (instances == null) {
            instances = new ConcurrentHashMap<>(50);
            this.getContext().setAttribute(INSTANCE_ATTRIBUTE_NAME, instances);
        }
        return instances;
    }

    /**
     * 是否启动
     *
     * @return 是否启动
     */
    @Override
    boolean isCreated();

    @Override
    default boolean useProxy() {
        return true;
    }
}
