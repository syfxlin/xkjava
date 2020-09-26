/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.http.result;

import cn.hutool.core.util.ReflectUtil;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.ixk.framework.exceptions.HttpException;
import me.ixk.framework.http.HttpStatus;
import me.ixk.framework.view.FilterCallback;

public abstract class Result {

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

    public static void abort(HttpStatus status) {
        abort(status, "");
    }

    public static void abort(HttpStatus status, String message) {
        abort(status, message, new ConcurrentHashMap<>());
    }

    public static void abort(
        HttpStatus status,
        String message,
        Map<String, String> headers
    ) {
        abort(status, message, headers, null);
    }

    public static void abort(
        HttpStatus status,
        String message,
        Map<String, String> headers,
        Throwable e
    ) {
        throw new HttpException(status, message, headers, e);
    }
}
