package me.ixk.framework.http;

import com.google.gson.Gson;
import org.eclipse.jetty.http.HttpContent;
import org.eclipse.jetty.http.HttpCookie;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.server.HttpOutput;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Response {
    protected org.eclipse.jetty.server.Response _base;

    public Response(org.eclipse.jetty.server.Response response) {
        this._base = response;
    }

    public Response text(String text) throws IOException {
        return this.text(text, 200, new ConcurrentHashMap<>());
    }

    public Response text(String text, int status) throws IOException {
        return this.text(text, status, new ConcurrentHashMap<>());
    }

    public Response text(String text, int status, Map<Object, String> headers)
        throws IOException {
        return this.reset()
            .setContent(text)
            .setStatus(status)
            .setHeaders(headers)
            .setContentType("text/plain");
    }

    public Response html(String html) throws IOException {
        return this.html(html, 200, new ConcurrentHashMap<>());
    }

    public Response html(String html, int status) throws IOException {
        return this.html(html, status, new ConcurrentHashMap<>());
    }

    public Response html(String html, int status, Map<Object, String> headers)
        throws IOException {
        return this.reset()
            .setContent(html)
            .setStatus(status)
            .setHeaders(headers)
            .setContentType("text/html");
    }

    public Response json(Object data) throws IOException {
        return this.json(data, 200, new ConcurrentHashMap<>());
    }

    public Response json(Object data, int status) throws IOException {
        return this.json(data, status, new ConcurrentHashMap<>());
    }

    public Response json(Object data, int status, Map<Object, String> headers)
        throws IOException {
        return this.reset()
            .setContent(new Gson().toJson(data))
            .setStatus(status)
            .setHeaders(headers)
            .setContentType("application/json");
    }

    public void redirect(String url) throws IOException {
        this.redirect(url, 200, new ConcurrentHashMap<>());
    }

    public void redirect(String url, int status) throws IOException {
        this.redirect(url, status, new ConcurrentHashMap<>());
    }

    public void redirect(String url, int status, Map<Object, String> headers)
        throws IOException {
        this.reset().setHeaders(headers).sendRedirect(status, url);
    }

    public void error(String message) throws IOException {
        this.error(message, 200, new ConcurrentHashMap<>());
    }

    public void error(String message, int status) throws IOException {
        this.error(message, status, new ConcurrentHashMap<>());
    }

    public void error(String message, int status, Map<Object, String> headers)
        throws IOException {
        this.reset().setHeaders(headers).sendError(status, message);
    }

    public void processing() throws IOException {
        this.processing(new ConcurrentHashMap<>());
    }

    public void processing(Map<Object, String> headers) throws IOException {
        this.reset().setHeaders(headers).sendProcessing();
    }

    public String getContent() throws IOException {
        return _base.getWriter().toString();
    }

    public Response setContent(String content) throws IOException {
        _base.getWriter().flush();
        _base.getWriter().write(content);
        return this;
    }

    public Response content(String content) throws IOException {
        this.setContent(content);
        return this;
    }

    public Response status(int sc) {
        _base.setStatus(sc);
        return this;
    }

    public Response header(String name, String value) {
        _base.setHeader(name, value);
        return this;
    }

    public Response header(HttpHeader name, String value) {
        _base.setHeader(name, value);
        return this;
    }

    public Response setHeaders(Map<Object, String> headers) {
        for (Map.Entry<Object, String> header : headers.entrySet()) {
            Object key = header.getKey();
            if (key.getClass().isAssignableFrom(String.class)) {
                _base.setHeader((String) key, header.getValue());
            } else if (key.getClass().isAssignableFrom(HttpHeader.class)) {
                _base.setHeader((HttpHeader) key, header.getValue());
            }
        }
        return this;
    }

    public Response cookie(
        String name,
        String value,
        String domain,
        String path,
        long maxAge,
        String comment,
        boolean isSecure,
        boolean isHttpOnly,
        int version
    ) {
        _base.addSetCookie(
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

    public Response addCookies(List<Object> cookies) {
        for (Object cookie : cookies) {
            if (cookie.getClass().isAssignableFrom(Cookie.class)) {
                _base.addCookie((Cookie) cookie);
            } else if (cookie.getClass().isAssignableFrom(HttpCookie.class)) {
                _base.addCookie((HttpCookie) cookie);
            }
        }
        return this;
    }

    /* =========================== */

    public Response setHeaders(HttpContent httpContent) {
        _base.setHeaders(httpContent);
        return this;
    }

    public HttpOutput getHttpOutput() {
        return _base.getHttpOutput();
    }

    public Response setHttpOutput(HttpOutput out) {
        _base.setHttpOutput(out);
        return this;
    }

    public boolean isIncluding() {
        return _base.isIncluding();
    }

    public Response include() {
        _base.include();
        return this;
    }

    public Response included() {
        _base.included();
        return this;
    }

    public Response addCookie(HttpCookie cookie) {
        _base.addCookie(cookie);
        return this;
    }

    public Response addCookie(Cookie cookie) {
        _base.addCookie(cookie);
        return this;
    }

    public Response addSetCookie(
        String name,
        String value,
        String domain,
        String path,
        long maxAge,
        String comment,
        boolean isSecure,
        boolean isHttpOnly,
        int version
    ) {
        _base.addSetCookie(
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

    public boolean containsHeader(String name) {
        return _base.containsHeader(name);
    }

    public String encodeURL(String url) {
        return _base.encodeURL(url);
    }

    public String encodeRedirectURL(String url) {
        return _base.encodeRedirectURL(url);
    }

    public String encodeUrl(String url) {
        return _base.encodeUrl(url);
    }

    public String encodeRedirectUrl(String url) {
        return _base.encodeRedirectUrl(url);
    }

    public void sendError(int sc) throws IOException {
        _base.sendError(sc);
    }

    public void sendError(int code, String message) throws IOException {
        _base.sendError(code, message);
    }

    public void sendProcessing() throws IOException {
        _base.sendProcessing();
    }

    public void sendRedirect(int code, String location) throws IOException {
        _base.sendRedirect(code, location);
    }

    public void sendRedirect(String location) throws IOException {
        _base.sendRedirect(location);
    }

    public Response setDateHeader(String name, long date) {
        _base.setDateHeader(name, date);
        return this;
    }

    public Response addDateHeader(String name, long date) {
        _base.addDateHeader(name, date);
        return this;
    }

    public Response setHeader(HttpHeader name, String value) {
        _base.setHeader(name, value);
        return this;
    }

    public Response setHeader(String name, String value) {
        _base.setHeader(name, value);
        return this;
    }

    public Collection<String> getHeaderNames() {
        return _base.getHeaderNames();
    }

    public String getHeader(String name) {
        return _base.getHeader(name);
    }

    public Collection<String> getHeaders(String name) {
        return _base.getHeaders(name);
    }

    public Response addHeader(String name, String value) {
        _base.addHeader(name, value);
        return this;
    }

    public Response setIntHeader(String name, int value) {
        _base.setIntHeader(name, value);
        return this;
    }

    public Response addIntHeader(String name, int value) {
        _base.addIntHeader(name, value);
        return this;
    }

    public Response setStatus(int sc) {
        _base.setStatus(sc);
        return this;
    }

    public Response setStatus(int sc, String sm) {
        _base.setStatus(sc, sm);
        return this;
    }

    public Response setStatusWithReason(int sc, String sm) {
        _base.setStatusWithReason(sc, sm);
        return this;
    }

    public String getCharacterEncoding() {
        return _base.getCharacterEncoding();
    }

    public String getContentType() {
        return _base.getContentType();
    }

    public ServletOutputStream getOutputStream() throws IOException {
        return _base.getOutputStream();
    }

    public boolean isWriting() {
        return _base.isWriting();
    }

    public PrintWriter getWriter() throws IOException {
        return _base.getWriter();
    }

    public Response setContentLength(int len) {
        _base.setContentLength(len);
        return this;
    }

    public long getContentLength() {
        return _base.getContentLength();
    }

    public boolean isAllContentWritten(long written) {
        return _base.isAllContentWritten(written);
    }

    public Response closeOutput() throws IOException {
        _base.closeOutput();
        return this;
    }

    public long getLongContentLength() {
        return _base.getLongContentLength();
    }

    public Response setLongContentLength(long len) {
        _base.setLongContentLength(len);
        return this;
    }

    public Response setContentLengthLong(long length) {
        _base.setContentLengthLong(length);
        return this;
    }

    public Response setCharacterEncoding(String encoding) {
        _base.setCharacterEncoding(encoding);
        return this;
    }

    public Response setContentType(String contentType) {
        _base.setContentType(contentType);
        return this;
    }

    public Response setBufferSize(int size) {
        _base.setBufferSize(size);
        return this;
    }

    public int getBufferSize() {
        return _base.getBufferSize();
    }

    public Response flushBuffer() throws IOException {
        _base.flushBuffer();
        return this;
    }

    public Response reset() {
        _base.reset();
        return this;
    }

    public Response reset(boolean preserveCookies) {
        _base.reset(preserveCookies);
        return this;
    }

    public Response resetForForward() {
        _base.resetForForward();
        return this;
    }

    public Response resetBuffer() {
        _base.resetBuffer();
        return this;
    }

    public boolean isCommitted() {
        return _base.isCommitted();
    }

    public Response setLocale(Locale locale) {
        _base.setLocale(locale);
        return this;
    }

    public Locale getLocale() {
        return _base.getLocale();
    }

    public int getStatus() {
        return _base.getStatus();
    }

    public String getReason() {
        return _base.getReason();
    }

    public HttpFields getHttpFields() {
        return _base.getHttpFields();
    }

    public long getContentCount() {
        return _base.getContentCount();
    }

    public String toString() {
        return _base.toString();
    }
}