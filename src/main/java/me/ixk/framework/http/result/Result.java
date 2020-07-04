/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.http.result;

import cn.hutool.core.util.ReflectUtil;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;
import me.ixk.framework.http.HttpStatus;
import me.ixk.framework.view.FilterCallback;

public class Result {

    public static <T extends HttpResult> T custom(
        Class<T> resultHandler,
        Object... parameters
    ) {
        return ReflectUtil.newInstance(resultHandler, parameters);
    }

    public static EmptyResult empty() {
        return new EmptyResult();
    }

    public static HtmlResult html(String html) {
        return new HtmlResult(html);
    }

    public static JsonResult json() {
        return new JsonResult();
    }

    public static JsonResult json(Object object) {
        return new JsonResult(object);
    }

    public static JsonResult json(JsonNode jsonNode) {
        return new JsonResult(jsonNode);
    }

    public static TextResult text(String text) {
        return new TextResult(text);
    }

    public static RedirectResult redirect(String url) {
        return new RedirectResult(url);
    }

    public static RedirectResult redirect(String url, int status) {
        return new RedirectResult(url, status);
    }

    public static RedirectResult redirect(String url, HttpStatus status) {
        return new RedirectResult(url, status);
    }

    public static ViewResult view(String view) {
        return new ViewResult(view);
    }

    public static ViewResult view(String view, Map<String, Object> data) {
        return new ViewResult(view, data);
    }

    public static ViewResult view(
        String view,
        Map<String, Object> data,
        FilterCallback callback
    ) {
        return new ViewResult(view, data, callback);
    }
}
