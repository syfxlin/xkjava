/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.http;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.io.IoUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import me.ixk.framework.annotations.Component;
import me.ixk.framework.annotations.Scope;
import me.ixk.framework.exceptions.ResponseException;
import me.ixk.framework.utils.Json;

/**
 * @author Otstar Lin
 * @date 2020/10/24 下午 3:45
 */
@Component(name = { "response" })
@Scope(type = "request")
public class Response extends HttpServletResponseWrapper {

    private static final HttpServletResponse EMPTY = new EmptyResponse();

    /**
     * Only used cglib
     */
    @Deprecated
    public Response() {
        super(EMPTY);
    }

    public Response(final HttpServletResponse response) {
        super(response);
    }

    public Response characterEncoding(final String charset) {
        this.setCharacterEncoding(charset);
        return this;
    }

    public Response contentLength(final long len) {
        this.setContentLengthLong(len);
        return this;
    }

    public Response contentType(final String contentType) {
        this.setContentType(contentType);
        return this;
    }

    public Response contentType(final MimeType type) {
        return contentType(type.asString());
    }

    public Response content(final String content) {
        try {
            this.getWriter().write(content == null ? "" : content);
        } catch (final IOException e) {
            throw new ResponseException("Get writer error", e);
        }
        return this;
    }

    public Response content(final InputStream in) {
        try {
            IoUtil.copy(in, this.getOutputStream());
        } catch (final UtilException | IOException e) {
            throw new ResponseException("Get writer error", e);
        }
        return this;
    }

    public String getContent() {
        try {
            return this.getWriter().toString();
        } catch (final IOException e) {
            return null;
        }
    }

    public Response status(final int status) {
        this.setStatus(status);
        return this;
    }

    public Response status(final HttpStatus status) {
        return this.status(status.getValue(), status.getReasonPhrase());
    }

    public Response status(final int status, final String reason) {
        this.setStatus(status, reason);
        return this;
    }

    public Response header(final String name, final String value) {
        this.setHeader(name, value);
        return this;
    }

    public Response header(final HttpHeader header, final String value) {
        return this.header(header.asString(), value);
    }

    public Response headers(final Map<Object, String> headers) {
        return this.setHeaders(headers);
    }

    public Response headers(final HttpHeaders headers) {
        return this.setHeaders(headers);
    }

    private Response setHeaders(final Map<Object, String> headers) {
        for (final Map.Entry<Object, String> header : headers.entrySet()) {
            final Object key = header.getKey();
            if (key.getClass().isAssignableFrom(String.class)) {
                this.header((String) key, header.getValue());
            } else if (key.getClass().isAssignableFrom(HttpHeader.class)) {
                this.header((HttpHeader) key, header.getValue());
            }
        }
        return this;
    }

    private Response setHeaders(final HttpHeaders headers) {
        for (final Map.Entry<String, List<String>> entry : headers.entrySet()) {
            for (final String value : entry.getValue()) {
                this.header(entry.getKey(), value);
            }
        }
        return this;
    }

    public Response cookie(final Cookie cookie) {
        this.addCookie(cookie);
        return this;
    }

    public Response cookie(final SetCookie cookie) {
        return this.cookie((Cookie) cookie);
    }

    public Response cookie(
        final String name,
        final String value,
        final String domain,
        final String path,
        final int maxAge,
        final String comment,
        final boolean isHttpOnly,
        final boolean isSecure,
        final int version
    ) {
        return this.cookie(
                new SetCookie(name, value)
                    .domain(domain)
                    .path(path)
                    .maxAge(maxAge)
                    .comment(comment)
                    .httpOnly(isHttpOnly)
                    .secure(isSecure)
                    .version(version)
            );
    }

    public Response cookies(final Collection<SetCookie> cookies) {
        for (final SetCookie cookie : cookies) {
            this.cookie(cookie);
        }
        return this;
    }

    public Response cookies(final SetCookie[] cookies) {
        for (final SetCookie cookie : cookies) {
            this.cookie(cookie);
        }
        return this;
    }

    public Response cookies(final Cookie[] cookies) {
        for (final Cookie cookie : cookies) {
            this.cookie(cookie);
        }
        return this;
    }

    /* ================ quick ============== */

    public Response make() {
        return this;
    }

    public Response ok() {
        return this.status(HttpStatus.OK);
    }

    public Response text(final String text) {
        return this.text(text, HttpStatus.OK, new ConcurrentHashMap<>(0));
    }

    public Response text(final String text, final HttpStatus status) {
        return this.text(text, status, new ConcurrentHashMap<>(0));
    }

    public Response text(final String text, final int status) {
        return this.text(text, status, new ConcurrentHashMap<>(0));
    }

    public Response text(
        final String text,
        final HttpStatus status,
        final Map<Object, String> headers
    ) {
        this.reset();
        this.content(text);
        this.status(status);
        this.headers(headers);
        this.contentType(MimeType.TEXT_PLAIN.asString());
        return this;
    }

    public Response text(
        final String text,
        final int status,
        final Map<Object, String> headers
    ) {
        return this.text(text, HttpStatus.valueOf(status), headers);
    }

    public Response html(final String html) {
        return this.html(html, HttpStatus.OK, new ConcurrentHashMap<>(0));
    }

    public Response html(final String html, final HttpStatus status) {
        return this.html(html, status, new ConcurrentHashMap<>(0));
    }

    public Response html(final String html, final int status) {
        return this.html(html, status, new ConcurrentHashMap<>(0));
    }

    public Response html(
        final String html,
        final HttpStatus status,
        final Map<Object, String> headers
    ) {
        this.reset();
        this.content(html);
        this.status(status);
        this.headers(headers);
        this.contentType(MimeType.TEXT_HTML.asString());
        return this;
    }

    public Response html(
        final String html,
        final int status,
        final Map<Object, String> headers
    ) {
        return this.html(html, HttpStatus.valueOf(status), headers);
    }

    public Response json(final Object data) {
        return this.json(data, HttpStatus.OK, Collections.emptyMap());
    }

    public Response json(final Object data, final HttpStatus status) {
        return this.json(data, status, Collections.emptyMap());
    }

    public Response json(final Object data, final int status) {
        return this.json(data, status, Collections.emptyMap());
    }

    public Response json(
        final Object data,
        final HttpStatus status,
        final Map<Object, String> headers
    ) {
        this.reset();
        this.content(Json.stringify(data));
        this.status(status);
        this.headers(headers);
        this.contentType(MimeType.APPLICATION_JSON.asString());
        return this;
    }

    public Response json(
        final Object data,
        final int status,
        final Map<Object, String> headers
    ) {
        return this.json(data, HttpStatus.valueOf(status), headers);
    }

    public Response redirect(final String url) {
        return this.redirect(url, HttpStatus.FOUND);
    }

    public Response redirect(final String url, final HttpStatus status) {
        return this.redirect(url, status, Collections.emptyMap());
    }

    public Response redirect(final String url, final int status) {
        return this.redirect(url, status, Collections.emptyMap());
    }

    public Response redirect(
        final String url,
        final HttpStatus status,
        final Map<Object, String> headers
    ) {
        this.reset();
        this.headers(headers);
        this.status(status);
        try {
            this.sendRedirect(url);
        } catch (final IOException e) {
            throw new ResponseException(e);
        }
        return this;
    }

    public Response redirect(
        final String url,
        final int status,
        final Map<Object, String> headers
    ) {
        return this.redirect(url, HttpStatus.valueOf(status), headers);
    }

    public void error(final String message) {
        this.error(message, HttpStatus.OK);
    }

    public void error(final String message, final HttpStatus status) {
        this.error(message, status, Collections.emptyMap());
    }

    public void error(final String message, final int status) {
        this.error(message, status, Collections.emptyMap());
    }

    public void error(
        final String message,
        final HttpStatus status,
        final Map<Object, String> headers
    ) {
        this.reset();
        try {
            this.headers(headers).sendError(status.getValue(), message);
        } catch (final IOException e) {
            throw new ResponseException("SendError error", e);
        }
    }

    public void error(
        final String message,
        final int status,
        final Map<Object, String> headers
    ) {
        this.error(message, HttpStatus.valueOf(status), headers);
    }

    private static class EmptyResponse implements HttpServletResponse {

        @Override
        public void addCookie(final Cookie cookie) {}

        @Override
        public boolean containsHeader(final String name) {
            return false;
        }

        @Override
        public String encodeURL(final String url) {
            return null;
        }

        @Override
        public String encodeRedirectURL(final String url) {
            return null;
        }

        @Override
        public String encodeUrl(final String url) {
            return null;
        }

        @Override
        public String encodeRedirectUrl(final String url) {
            return null;
        }

        @Override
        public void sendError(final int sc, final String msg)
            throws IOException {}

        @Override
        public void sendError(final int sc) throws IOException {}

        @Override
        public void sendRedirect(final String location) throws IOException {}

        @Override
        public void setDateHeader(final String name, final long date) {}

        @Override
        public void addDateHeader(final String name, final long date) {}

        @Override
        public void setHeader(final String name, final String value) {}

        @Override
        public void addHeader(final String name, final String value) {}

        @Override
        public void setIntHeader(final String name, final int value) {}

        @Override
        public void addIntHeader(final String name, final int value) {}

        @Override
        public String getCharacterEncoding() {
            return null;
        }

        @Override
        public void setStatus(final int sc) {}

        @Override
        public String getContentType() {
            return null;
        }

        @Override
        public void setStatus(final int sc, final String sm) {}

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            return null;
        }

        @Override
        public int getStatus() {
            return 0;
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            return null;
        }

        @Override
        public String getHeader(final String name) {
            return null;
        }

        @Override
        public Collection<String> getHeaders(final String name) {
            return null;
        }

        @Override
        public void setCharacterEncoding(final String charset) {}

        @Override
        public void setContentLength(final int len) {}

        @Override
        public Collection<String> getHeaderNames() {
            return null;
        }

        @Override
        public void setContentLengthLong(final long len) {}

        @Override
        public void setContentType(final String type) {}

        @Override
        public void setBufferSize(final int size) {}

        @Override
        public int getBufferSize() {
            return 0;
        }

        @Override
        public void flushBuffer() throws IOException {}

        @Override
        public void resetBuffer() {}

        @Override
        public boolean isCommitted() {
            return false;
        }

        @Override
        public void reset() {}

        @Override
        public void setLocale(final Locale loc) {}

        @Override
        public Locale getLocale() {
            return null;
        }
    }
}
