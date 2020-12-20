/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.http.result;

import cn.hutool.core.util.ReflectUtil;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.ixk.framework.exceptions.HttpException;
import me.ixk.framework.http.HttpStatus;
import me.ixk.framework.http.MimeType;
import me.ixk.framework.view.FilterCallback;

/**
 * 响应工具类
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 9:11
 */
public class Result {

    public static final String EMPTY_RETURN = "empty:";
    public static final String HTML_RETURN = "html:";
    public static final String JSON_RETURN = "json:";
    public static final String REDIRECT_RETURN = "redirect:";
    public static final String TEXT_RETURN = "text:";
    public static final String VIEW_RETURN = "view:";

    public static <T extends AbstractHttpResult> T custom(
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

    public static StringJsonResult stringJson(String json) {
        return new StringJsonResult(json);
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

    public static StreamResult stream(String contentType, InputStream stream) {
        return new StreamResult(contentType, stream);
    }

    public static StreamResult stream(
        MimeType contentType,
        InputStream stream
    ) {
        return new StreamResult(contentType, stream);
    }

    public static FileResult file(String contentType, String file) {
        return new FileResult(file).contentType(contentType);
    }

    public static FileResult file(MimeType contentType, String file) {
        return new FileResult(file).contentType(contentType);
    }

    public static FileResult file(String contentType, File file) {
        return new FileResult(file).contentType(contentType);
    }

    public static FileResult file(MimeType contentType, File file) {
        return new FileResult(file).contentType(contentType);
    }

    public static FileResult file(String file) {
        return new FileResult(file);
    }

    public static FileResult file(File file) {
        return new FileResult(file);
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
