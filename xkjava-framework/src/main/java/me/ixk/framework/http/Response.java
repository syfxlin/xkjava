/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.http;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import me.ixk.framework.exceptions.ResponseException;
import me.ixk.framework.utils.JSON;
import org.eclipse.jetty.http.HttpContent;
import org.eclipse.jetty.http.HttpCookie;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.MetaData;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.server.HttpChannel;
import org.eclipse.jetty.server.HttpOutput;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.URIUtil;

/**
 * 响应对象
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 10:05
 */
public class Response implements HttpServletResponse {
    protected static final String URL_SPLIT = "/";
    protected final List<SetCookie> cookies = new ArrayList<>();
    protected volatile org.eclipse.jetty.server.Response base;

    @Deprecated
    public Response() {
        // only used cglib
        this.base = null;
    }

    public Response(org.eclipse.jetty.server.Response response) {
        this.base = response;
    }

    /* =============== Quick method =============== */

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
        this.contentType(MimeTypes.Type.TEXT_PLAIN.asString());
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
        this.contentType(MimeTypes.Type.TEXT_HTML.asString());
        return this;
    }

    public Response html(String html, int status, Map<Object, String> headers) {
        return this.html(html, HttpStatus.valueOf(status), headers);
    }

    public Response json(Object data) {
        return this.json(data, HttpStatus.OK, new ConcurrentHashMap<>());
    }

    public Response json(Object data, HttpStatus status) {
        return this.json(data, status, new ConcurrentHashMap<>());
    }

    public Response json(Object data, int status) {
        return this.json(data, status, new ConcurrentHashMap<>());
    }

    public Response json(
        Object data,
        HttpStatus status,
        Map<Object, String> headers
    ) {
        this.reset();
        this.content(JSON.stringify(data));
        this.status(status);
        this.headers(headers);
        this.contentType(MimeTypes.Type.APPLICATION_JSON.asString());
        return this;
    }

    public Response json(Object data, int status, Map<Object, String> headers) {
        return this.json(data, HttpStatus.valueOf(status), headers);
    }

    public Response redirect(String url) {
        return this.redirect(url, HttpStatus.FOUND);
    }

    public Response redirect(String url, HttpStatus status) {
        return this.redirect(url, status, new ConcurrentHashMap<>());
    }

    public Response redirect(String url, int status) {
        return this.redirect(url, status, new ConcurrentHashMap<>());
    }

    public Response redirect(
        String url,
        HttpStatus status,
        Map<Object, String> headers
    ) {
        this.reset();
        this.headers(headers);
        this.setRedirect(status.getValue(), url);
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
        this.error(message, status, new ConcurrentHashMap<>());
    }

    public void error(String message, int status) {
        this.error(message, status, new ConcurrentHashMap<>());
    }

    public void error(
        String message,
        HttpStatus status,
        Map<Object, String> headers
    ) {
        this.reset();
        this.setHeaders(headers).sendError(status.getValue(), message);
    }

    public void error(String message, int status, Map<Object, String> headers) {
        this.error(message, HttpStatus.valueOf(status), headers);
    }

    public void processing() {
        this.processing(new ConcurrentHashMap<>());
    }

    public void processing(Map<Object, String> headers) {
        this.reset();
        this.setHeaders(headers).sendProcessing();
    }

    /* ========= */

    public Response content(String content) {
        this.setContent(content);
        return this;
    }

    public Response status(int sc) {
        base.setStatus(sc);
        return this;
    }

    public Response status(HttpStatus status) {
        return this.setStatusWithReason(
                status.getValue(),
                status.getReasonPhrase()
            );
    }

    public Response status(int sc, String reason) {
        return this.setStatusWithReason(sc, reason);
    }

    public Response header(String name, String value) {
        base.setHeader(name, value);
        return this;
    }

    public Response header(HttpHeader name, String value) {
        base.setHeader(name, value);
        return this;
    }

    public Response headers(Map<Object, String> headers) {
        return this.setHeaders(headers);
    }

    public Response headers(HttpHeaders headers) {
        return this.setHeaders(headers);
    }

    public Response cookie(HttpCookie cookie) {
        return this.addSetCookie(
                cookie.getName(),
                cookie.getValue(),
                cookie.getDomain(),
                cookie.getPath(),
                (int) cookie.getMaxAge(),
                cookie.getComment(),
                cookie.isSecure(),
                cookie.isHttpOnly(),
                cookie.getVersion()
            );
    }

    public Response cookie(Cookie cookie) {
        return this.addSetCookie(
                cookie.getName(),
                cookie.getValue(),
                cookie.getDomain(),
                cookie.getPath(),
                cookie.getMaxAge(),
                cookie.getComment(),
                cookie.getSecure(),
                cookie.isHttpOnly(),
                cookie.getVersion()
            );
    }

    public Response cookie(SetCookie cookie) {
        return this.addCookie(cookie);
    }

    public Response cookie(
        String name,
        String value,
        String domain,
        String path,
        int maxAge,
        String comment,
        boolean isSecure,
        boolean isHttpOnly,
        int version
    ) {
        this.addSetCookie(
                name,
                value,
                domain,
                path,
                maxAge,
                comment,
                isSecure,
                isHttpOnly,
                version
            );
        return this;
    }

    public Response addCookies(Collection<SetCookie> cookies) {
        for (SetCookie cookie : cookies) {
            this.addCookie(cookie);
        }
        return this;
    }

    public Response addCookies(SetCookie[] cookies) {
        for (SetCookie cookie : cookies) {
            this.addCookie(cookie);
        }
        return this;
    }

    public List<SetCookie> getCookies() {
        return cookies;
    }

    public Response pushCookieToHeader() {
        for (SetCookie cookie : this.cookies) {
            base.addCookie(cookie);
        }
        this.cookies.clear();
        return this;
    }

    public Response setHeaders(Map<Object, String> headers) {
        for (Map.Entry<Object, String> header : headers.entrySet()) {
            Object key = header.getKey();
            if (key.getClass().isAssignableFrom(String.class)) {
                base.setHeader((String) key, header.getValue());
            } else if (key.getClass().isAssignableFrom(HttpHeader.class)) {
                base.setHeader((HttpHeader) key, header.getValue());
            }
        }
        return this;
    }

    public Response setHeaders(HttpHeaders headers) {
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            for (String value : entry.getValue()) {
                base.setHeader(entry.getKey(), value);
            }
        }
        return this;
    }

    public String getContent() {
        return this.getWriter().toString();
    }

    public Response setContent(String content) {
        this.getWriter().write(content);
        return this;
    }

    public Response setRedirect(int code, String location) {
        if (
            (code < HttpServletResponse.SC_MULTIPLE_CHOICES) ||
            (code >= HttpServletResponse.SC_BAD_REQUEST)
        ) {
            throw new IllegalArgumentException("Not a 3xx redirect code");
        }

        if (location == null) {
            throw new IllegalArgumentException();
        }

        HttpChannel channel = this.getHttpChannel();

        if (!URIUtil.hasScheme(location)) {
            StringBuilder buf = channel.getRequest().getRootURL();
            if (location.startsWith(URL_SPLIT)) {
                // absolute in context
                location = URIUtil.canonicalEncodedPath(location);
            } else {
                // relative to request
                String path = channel.getRequest().getRequestURI();
                String parent = (path.endsWith(URL_SPLIT))
                    ? path
                    : URIUtil.parentPath(path);
                location =
                    URIUtil.canonicalEncodedPath(
                        URIUtil.addEncodedPaths(parent, location)
                    );
                if (location != null && !location.startsWith(URL_SPLIT)) {
                    buf.append('/');
                }
            }

            if (location == null) {
                throw new IllegalStateException("path cannot be above root");
            }
            buf.append(location);

            location = buf.toString();
        }

        resetBuffer();
        setHeader(HttpHeader.LOCATION, location);
        setStatus(code);
        // closeOutput();
        return this;
    }

    /* ============================== */

    public HttpOutput getHttpOutput() {
        return base.getHttpOutput();
    }

    public boolean isIncluding() {
        return base.isIncluding();
    }

    public Response include() {
        base.include();
        return this;
    }

    public Response included() {
        base.included();
        return this;
    }

    public Response addCookie(SetCookie cookie) {
        this.cookies.add(cookie);
        return this;
    }

    public Response addSetCookie(
        String name,
        String value,
        String domain,
        String path,
        int maxAge,
        String comment,
        boolean isSecure,
        boolean isHttpOnly,
        int version
    ) {
        this.addCookie(
                new SetCookie(
                    name,
                    value,
                    domain,
                    path,
                    maxAge,
                    isHttpOnly,
                    isSecure,
                    comment,
                    version
                )
            );
        return this;
    }

    @Override
    public boolean containsHeader(String name) {
        return base.containsHeader(name);
    }

    @Override
    public String encodeURL(String url) {
        return base.encodeURL(url);
    }

    @Override
    public String encodeRedirectURL(String url) {
        return base.encodeRedirectURL(url);
    }

    @SuppressWarnings("deprecation")
    @Override
    public String encodeUrl(String url) {
        return base.encodeUrl(url);
    }

    @SuppressWarnings("deprecation")
    @Override
    public String encodeRedirectUrl(String url) {
        return base.encodeRedirectUrl(url);
    }

    @Override
    public void sendError(int code, String message) {
        try {
            base.sendError(code, message);
        } catch (IOException e) {
            throw new ResponseException("Send error is error", e);
        }
    }

    @Override
    public void sendError(int sc) {
        try {
            base.sendError(sc);
        } catch (IOException e) {
            throw new ResponseException("Send error is error", e);
        }
    }

    @Override
    public void sendRedirect(String location) {
        try {
            base.sendRedirect(location);
        } catch (IOException e) {
            throw new ResponseException("Send redirect error", e);
        }
    }

    @Override
    public void setDateHeader(String name, long date) {
        base.setDateHeader(name, date);
    }

    @Override
    public void addDateHeader(String name, long date) {
        base.addDateHeader(name, date);
    }

    @Override
    public void setHeader(String name, String value) {
        base.setHeader(name, value);
    }

    @Override
    public void addHeader(String name, String value) {
        base.addHeader(name, value);
    }

    @Override
    public void setIntHeader(String name, int value) {
        base.setIntHeader(name, value);
    }

    @Override
    public void addIntHeader(String name, int value) {
        base.addIntHeader(name, value);
    }

    public void sendProcessing() {
        try {
            base.sendProcessing();
        } catch (IOException e) {
            throw new ResponseException("Send processing error", e);
        }
    }

    public void sendRedirect(int code, String location) {
        try {
            base.sendRedirect(code, location);
        } catch (IOException e) {
            throw new ResponseException("Send redirect error", e);
        }
    }

    public Response dateHeader(String name, long date) {
        base.setDateHeader(name, date);
        return this;
    }

    @Override
    public Collection<String> getHeaderNames() {
        return base.getHeaderNames();
    }

    public Response aDateHeader(String name, long date) {
        base.addDateHeader(name, date);
        return this;
    }

    @Override
    public String getHeader(String name) {
        return base.getHeader(name);
    }

    public Response setHeader(HttpHeader name, String value) {
        base.setHeader(name, value);
        return this;
    }

    @Override
    public Collection<String> getHeaders(String name) {
        return base.getHeaders(name);
    }

    public Response aHeader(String name, String value) {
        base.addHeader(name, value);
        return this;
    }

    public Response intHeader(String name, int value) {
        base.setIntHeader(name, value);
        return this;
    }

    public Response aIntHeader(String name, int value) {
        base.addIntHeader(name, value);
        return this;
    }

    public Response setStatusWithReason(int sc, String sm) {
        base.setStatusWithReason(sc, sm);
        return this;
    }

    @Override
    public String getCharacterEncoding() {
        return base.getCharacterEncoding();
    }

    @Override
    public String getContentType() {
        return base.getContentType();
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return base.getOutputStream();
    }

    @Override
    public void setStatus(int sc) {
        base.setStatus(sc);
    }

    @Override
    public PrintWriter getWriter() {
        try {
            return base.getWriter();
        } catch (IOException e) {
            throw new ResponseException("Get writer error", e);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setStatus(int sc, String sm) {
        base.setStatus(sc, sm);
    }

    @Override
    public void setContentType(String contentType) {
        base.setContentType(contentType);
    }

    @Override
    public void setCharacterEncoding(String encoding) {
        base.setCharacterEncoding(encoding);
    }

    public boolean isWriting() {
        return base.isWriting();
    }

    public Response contentLength(int len) {
        base.setContentLength(len);
        return this;
    }

    public long getContentLength() {
        return base.getContentLength();
    }

    @Override
    public void setContentLength(int len) {
        base.setContentLength(len);
    }

    @Override
    public void setContentLengthLong(long length) {
        base.setContentLengthLong(length);
    }

    public boolean isAllContentWritten(long written) {
        return base.isAllContentWritten(written);
    }

    public Response closeOutput() throws IOException {
        base.closeOutput();
        return this;
    }

    public long getLongContentLength() {
        return base.getLongContentLength();
    }

    public Response setLongContentLength(long len) {
        base.setLongContentLength(len);
        return this;
    }

    public Response contentLengthLong(long length) {
        base.setContentLengthLong(length);
        return this;
    }

    public Response characterEncoding(String encoding) {
        base.setCharacterEncoding(encoding);
        return this;
    }

    public Response contentType(String contentType) {
        base.setContentType(contentType);
        return this;
    }

    public Response bufferSize(int size) {
        base.setBufferSize(size);
        return this;
    }

    public Response resetForForward() {
        base.resetForForward();
        return this;
    }

    public Response locale(Locale locale) {
        base.setLocale(locale);
        return this;
    }

    public String getReason() {
        return base.getReason();
    }

    public HttpFields getHttpFields() {
        return base.getHttpFields();
    }

    public long getContentCount() {
        return base.getContentCount();
    }

    @Override
    public void setBufferSize(int size) {
        base.setBufferSize(size);
    }

    @Override
    public String toString() {
        return base.toString();
    }

    public org.eclipse.jetty.server.Response getOriginResponse() {
        return this.base;
    }

    @Override
    public int getBufferSize() {
        return base.getBufferSize();
    }

    public Response setOriginResponse(
        org.eclipse.jetty.server.Response response
    ) {
        this.base = response;
        return this;
    }

    @Override
    public void flushBuffer() throws IOException {
        base.flushBuffer();
    }

    public HttpChannel getHttpChannel() {
        return base.getHttpChannel();
    }

    @Override
    public void reset() {
        base.reset();
    }

    public void addCookie(HttpCookie cookie) {
        base.addCookie(cookie);
    }

    public void replaceCookie(HttpCookie cookie) {
        base.replaceCookie(cookie);
    }

    @Override
    public void resetBuffer() {
        base.resetBuffer();
    }

    public boolean isContentComplete(long written) {
        return base.isContentComplete(written);
    }

    @Override
    public boolean isCommitted() {
        return base.isCommitted();
    }

    public Supplier<HttpFields> getTrailers() {
        return base.getTrailers();
    }

    @Override
    public void setLocale(Locale locale) {
        base.setLocale(locale);
    }

    public Response setTrailers(Supplier<HttpFields> trailers) {
        base.setTrailers(trailers);
        return this;
    }

    public MetaData.Response getCommittedMetaData() {
        return base.getCommittedMetaData();
    }

    @Override
    public Locale getLocale() {
        return base.getLocale();
    }

    public Response putHeaders(
        HttpContent content,
        long contentLength,
        boolean etag
    ) {
        base.putHeaders(content, contentLength, etag);
        return this;
    }

    @Override
    public int getStatus() {
        return base.getStatus();
    }

    public Response reopen() {
        base.reopen();
        return this;
    }

    public Response errorClose() {
        base.errorClose();
        return this;
    }

    public boolean isStreaming() {
        return base.isStreaming();
    }

    public boolean isWritingOrStreaming() {
        return base.isWritingOrStreaming();
    }

    public Response completeOutput(Callback callback) {
        base.completeOutput(callback);
        return this;
    }

    public Response resetContent() {
        base.resetContent();
        return this;
    }

    @Override
    public void addCookie(Cookie cookie) {
        this.addSetCookie(
                cookie.getName(),
                cookie.getValue(),
                cookie.getDomain(),
                cookie.getPath(),
                cookie.getMaxAge(),
                cookie.getComment(),
                cookie.getSecure(),
                cookie.isHttpOnly(),
                cookie.getVersion()
            );
    }
}
