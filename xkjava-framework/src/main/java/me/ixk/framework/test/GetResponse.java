/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.test;

import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.http.HttpRequest;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.nio.charset.StandardCharsets;
import me.ixk.framework.annotations.AliasFor;
import me.ixk.framework.http.HttpMethod;
import me.ixk.framework.test.ClientResponse.RequestProcessor;
import me.ixk.framework.test.GetResponse.ParamProcessor;
import me.ixk.framework.utils.MergedAnnotation;

/**
 * GET 响应
 *
 * @author Otstar Lin
 * @date 2020/11/9 下午 6:47
 */
@Target({ ElementType.TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@ClientResponse(method = HttpMethod.GET, processor = ParamProcessor.class)
public @interface GetResponse {
    @AliasFor(
        value = "url",
        annotation = ClientResponse.class,
        attribute = "value"
    )
    String value() default "";

    @AliasFor(
        value = "value",
        annotation = ClientResponse.class,
        attribute = "url"
    )
    String url() default "";

    String[] param() default {  };

    @AliasFor(annotation = ClientResponse.class, attribute = "cookie")
    String[] cookie() default {  };

    @AliasFor(annotation = ClientResponse.class, attribute = "header")
    String[] header() default {  };

    class ParamProcessor implements RequestProcessor {

        @Override
        public void process(HttpRequest request, MergedAnnotation annotation) {
            final UrlBuilder url = UrlBuilder.of(
                request.getUrl(),
                StandardCharsets.UTF_8
            );
            for (String param : annotation
                .getAnnotation(GetResponse.class)
                .param()) {
                final String[] kv = param.split("=");
                url.addQuery(kv[0].trim(), kv[1].trim());
            }
            request.setUrl(url);
        }
    }
}
