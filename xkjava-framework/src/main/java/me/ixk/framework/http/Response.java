/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.http;

import java.io.IOException;
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
import me.ixk.framework.annotations.ScopeType;
import me.ixk.framework.exceptions.ResponseException;
import me.ixk.framework.utils.Json;

/**
 * @author Otstar Lin
 * @date 2020/10/24 下午 3:45
 */
@Component(name = { "response", "javax.servlet.http.HttpServletResponse" })
@Scope(type = ScopeType.REQUEST)
public class Response extends HttpServletResponseWrapper {

    private static final HttpServletResponse EMPTY = new EmptyResponse();

    /**
     * Only used cglib
     */
    @Deprecated
    public Response() {
        super(EMPTY);
    }

    public Response(HttpServletResponse response) {
        super(response);
    }

    public Response characterEncoding(String charset) {
        this.setCharacterEncoding(charset);
        return this;
    }

    public Response contentLength(long len) {
        this.setContentLengthLong(len);
        return this;
    }

    public Response contentType(String contentType) {
        this.setContentType(contentType);
        return this;
    }

    public Response contentType(MimeType type) {
        return contentType(type.asString());
    }

    public Response content(String content) {
        try {
            this.getWriter().write(content == null ? "" : content);
        } catch (IOException e) {
            throw new ResponseException("Get writer error", e);
        }
        return this;
    }

    public String getContent() {
        try {
            return this.getWriter().toString();
        } catch (IOException e) {
            return null;
        }
    }

    public Response status(int status) {
        this.setStatus(status);
        return this;
    }

    public Response status(HttpStatus status) {
        return this.status(status.getValue(), status.getReasonPhrase());
    }

    public Response status(int status, String reason) {
        this.setStatus(status, reason);
        return this;
    }

    public Response header(String name, String value) {
        this.setHeader(name, value);
        return this;
    }

    public Response header(HttpHeader header, String value) {
        return this.header(header.asString(), value);
    }

    public Response headers(Map<Object, String> headers) {
        return this.setHeaders(headers);
    }

    public Response headers(HttpHeaders headers) {
        return this.setHeaders(headers);
    }

    private Response setHeaders(Map<Object, String> headers) {
        for (Map.Entry<Object, String> header : headers.entrySet()) {
            Object key = header.getKey();
            if (key.getClass().isAssignableFrom(String.class)) {
                this.header((String) key, header.getValue());
            } else if (key.getClass().isAssignableFrom(HttpHeader.class)) {
                this.header((HttpHeader) key, header.getValue());
            }
        }
        return this;
    }

    private Response setHeaders(HttpHeaders headers) {
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            for (String value : entry.getValue()) {
                this.header(entry.getKey(), value);
            }
        }
        return this;
    }

    public Response cookie(Cookie cookie) {
        this.addCookie(cookie);
        return this;
    }

    public Response cookie(SetCookie cookie) {
        return this.cookie((Cookie) cookie);
    }

    public Response cookie(
        String name,
        String value,
        String domain,
        String path,
        int maxAge,
        String comment,
        boolean isHttpOnly,
        boolean isSecure,
        int version
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

    public Response cookies(Collection<SetCookie> cookies) {
        for (SetCookie cookie : cookies) {
            this.cookie(cookie);
        }
        return this;
    }

    public Response cookies(SetCookie[] cookies) {
        for (SetCookie cookie : cookies) {
            this.cookie(cookie);
        }
        return this;
    }

    public Response cookies(Cookie[] cookies) {
        for (Cookie cookie : cookies) {
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

    public Response text(String text) {
        return this.text(text, HttpStatus.OK, new ConcurrentHashMap<>(0));
    }

    public Response text(String text, HttpStatus status) {
        return this.text(text, status, new ConcurrentHashMap<>(0));
    }

    public Response text(String text, int status) {
        return this.text(text, status, new ConcurrentHashMap<>(0));
    }

    public Response text(
        String text,
        HttpStatus status,
        Map<Object, String> headers
    ) {
        this.reset();
        this.content(text);
        this.status(status);
        this.headers(headers);
        this.contentType(MimeType.TEXT_PLAIN.asString());
        return this;
    }

    public Response text(String text, int status, Map<Object, String> headers) {
        return this.text(text, HttpStatus.valueOf(status), headers);
    }

    public Response html(String html) {
        return this.html(html, HttpStatus.OK, new ConcurrentHashMap<>(0));
    }

    public Response html(String html, HttpStatus status) {
        return this.html(html, status, new ConcurrentHashMap<>(0));
    }

    public Response html(String html, int status) {
        return this.html(html, status, new ConcurrentHashMap<>(0));
    }

    public Response html(
        String html,
        HttpStatus status,
        Map<Object, String> headers
    ) {
        this.reset();
        this.content(html);
        this.status(status);
        this.headers(headers);
        this.contentType(MimeType.TEXT_HTML.asString());
        return this;
    }

    public Response html(String html, int status, Map<Object, String> headers) {
        return this.html(html, HttpStatus.valueOf(status), headers);
    }

    public Response json(Object data) {
        return this.json(data, HttpStatus.OK, Collections.emptyMap());
    }

    public Response json(Object data, HttpStatus status) {
        return this.json(data, status, Collections.emptyMap());
    }

    public Response json(Object data, int status) {
        return this.json(data, status, Collections.emptyMap());
    }

    public Response json(
        Object data,
        HttpStatus status,
        Map<Object, String> headers
    ) {
        this.reset();
        this.content(Json.stringify(data));
        this.status(status);
        this.headers(headers);
        this.contentType(MimeType.APPLICATION_JSON.asString());
        return this;
    }

    public Response json(Object data, int status, Map<Object, String> headers) {
        return this.json(data, HttpStatus.valueOf(status), headers);
    }

    public Response redirect(String url) {
        return this.redirect(url, HttpStatus.FOUND);
    }

    public Response redirect(String url, HttpStatus status) {
        return this.redirect(url, status, Collections.emptyMap());
    }

    public Response redirect(String url, int status) {
        return this.redirect(url, status, Collections.emptyMap());
    }

    public Response redirect(
        String url,
        HttpStatus status,
        Map<Object, String> headers
    ) {
        this.reset();
        this.headers(headers);
        this.status(status);
        try {
            this.sendRedirect(url);
        } catch (IOException e) {
            throw new ResponseException(e);
        }
        return this;
    }

    public Response redirect(
        String url,
        int status,
        Map<Object, String> headers
    ) {
        return this.redirect(url, HttpStatus.valueOf(status), headers);
    }

    public void error(String message) {
        this.error(message, HttpStatus.OK);
    }

    public void error(String message, HttpStatus status) {
        this.error(message, status, Collections.emptyMap());
    }

    public void error(String message, int status) {
        this.error(message, status, Collections.emptyMap());
    }

    public void error(
        String message,
        HttpStatus status,
        Map<Object, String> headers
    ) {
        this.reset();
        try {
            this.headers(headers).sendError(status.getValue(), message);
        } catch (IOException e) {
            throw new ResponseException("SendError error", e);
        }
    }

    public void error(String message, int status, Map<Object, String> headers) {
        this.error(message, HttpStatus.valueOf(status), headers);
    }

    private static class EmptyResponse implements HttpServletResponse {

        @Override
        public void addCookie(Cookie cookie) {}

        @Override
        public boolean containsHeader(String name) {
            return false;
        }

        @Override
        public String encodeURL(String url) {
            return null;
        }

        @Override
        public String encodeRedirectURL(String url) {
            return null;
        }

        @Override
        public String encodeUrl(String url) {
            return null;
        }

        @Override
        public String encodeRedirectUrl(String url) {
            return null;
        }

        @Override
        public void sendError(int sc, String msg) throws IOException {}

        @Override
        public void sendError(int sc) throws IOException {}

        @Override
        public void sendRedirect(String location) throws IOException {}

        @Override
        public void setDateHeader(String name, long date) {}

        @Override
        public void addDateHeader(String name, long date) {}

        @Override
        public void setHeader(String name, String value) {}

        @Override
        public void addHeader(String name, String value) {}

        @Override
        public void setIntHeader(String name, int value) {}

        @Override
        public void addIntHeader(String name, int value) {}

        @Override
        public String getCharacterEncoding() {
            return null;
        }

        @Override
        public void setStatus(int sc) {}

        @Override
        public String getContentType() {
            return null;
        }

        @Override
        public void setStatus(int sc, String sm) {}

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
        public String getHeader(String name) {
            return null;
        }

        @Override
        public void setContentType(String type) {}

        @Override
        public Collection<String> getHeaders(String name) {
            return null;
        }

        @Override
        public void setCharacterEncoding(String charset) {}

        @Override
        public Collection<String> getHeaderNames() {
            return null;
        }

        @Override
        public void setContentLength(int len) {}

        @Override
        public void setContentLengthLong(long len) {}

        @Override
        public void setBufferSize(int size) {}

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
        public void setLocale(Locale loc) {}

        @Override
        public Locale getLocale() {
            return null;
        }
    }
}
