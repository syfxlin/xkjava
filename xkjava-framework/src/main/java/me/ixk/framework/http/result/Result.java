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
        final Class<T> resultHandler,
        final Object... parameters
    ) {
        return ReflectUtil.newInstance(resultHandler, parameters);
    }

    public static EmptyResult empty() {
        return new EmptyResult();
    }

    public static HtmlResult html(final String html) {
        return new HtmlResult(html);
    }

    public static JsonResult json() {
        return new JsonResult();
    }

    public static JsonResult json(final Object object) {
        return new JsonResult(object);
    }

    public static JsonResult json(final JsonNode jsonNode) {
        return new JsonResult(jsonNode);
    }

    public static StringJsonResult stringJson(final String json) {
        return new StringJsonResult(json);
    }

    public static TextResult text(final String text) {
        return new TextResult(text);
    }

    public static RedirectResult redirect(final String url) {
        return new RedirectResult(url);
    }

    public static RedirectResult redirect(final String url, final int status) {
        return new RedirectResult(url, status);
    }

    public static RedirectResult redirect(
        final String url,
        final HttpStatus status
    ) {
        return new RedirectResult(url, status);
    }

    public static ViewResult view(final String view) {
        return new ViewResult(view);
    }

    public static ViewResult view(
        final String view,
        final Map<String, Object> data
    ) {
        return new ViewResult(view, data);
    }

    public static ViewResult view(
        final String view,
        final Map<String, Object> data,
        final FilterCallback callback
    ) {
        return new ViewResult(view, data, callback);
    }

    public static StreamResult stream(
        final String contentType,
        final InputStream stream
    ) {
        return new StreamResult(stream, contentType);
    }

    public static StreamResult stream(
        final MimeType contentType,
        final InputStream stream
    ) {
        return new StreamResult(stream, contentType);
    }

    public static FileResult file(final String contentType, final String file) {
        return new FileResult(file).contentType(contentType);
    }

    public static FileResult file(
        final MimeType contentType,
        final String file
    ) {
        return new FileResult(file).contentType(contentType);
    }

    public static FileResult file(final String contentType, final File file) {
        return new FileResult(file).contentType(contentType);
    }

    public static FileResult file(final MimeType contentType, final File file) {
        return new FileResult(file).contentType(contentType);
    }

    public static FileResult file(final String file) {
        return new FileResult(file);
    }

    public static FileResult file(final File file) {
        return new FileResult(file);
    }

    public static void abort(final HttpStatus status) {
        abort(status, "");
    }

    public static void abort(final HttpStatus status, final String message) {
        abort(status, message, new ConcurrentHashMap<>());
    }

    public static void abort(
        final HttpStatus status,
        final String message,
        final Map<String, String> headers
    ) {
        abort(status, message, headers, null);
    }

    public static void abort(
        final HttpStatus status,
        final String message,
        final Map<String, String> headers,
        final Throwable e
    ) {
        throw new HttpException(status, message, headers, e);
    }
}
