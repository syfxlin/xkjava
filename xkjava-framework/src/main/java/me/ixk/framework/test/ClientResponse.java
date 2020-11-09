/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.test;

import cn.hutool.http.HttpRequest;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.ixk.framework.annotations.AliasFor;
import me.ixk.framework.http.HttpMethod;
import me.ixk.framework.http.MimeType;
import me.ixk.framework.utils.MergedAnnotation;

/**
 * HTTP 响应
 *
 * @author Otstar Lin
 * @date 2020/11/9 下午 2:19
 */
@Target({ ElementType.TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ClientResponse {
    @AliasFor("url")
    String value() default "";

    @AliasFor("value")
    String url() default "";

    boolean async() default false;

    HttpMethod method() default HttpMethod.GET;

    /**
     * Cookie
     * <p>
     * 样例："key=value"
     *
     * @return Cookie
     */
    String[] cookie() default {  };

    /**
     * 头字段
     * <p>
     * 样例："Host: localhost"
     *
     * @return 头字段
     */
    String[] header() default {  };

    String body() default "";

    /**
     * Form 字段
     * <p>
     * 样例："key=value" "name=:classpath:/test.file"
     *
     * @return Form 字段
     */
    String[] form() default {  };

    MimeType contentType() default MimeType.NONE;

    Class<? extends RequestProcessor> processor() default RequestProcessor.class;

    interface RequestProcessor {
        /**
         * 处理
         *
         * @param request    请求对象
         * @param annotation 注解
         */
        void process(HttpRequest request, MergedAnnotation annotation);
    }
}
