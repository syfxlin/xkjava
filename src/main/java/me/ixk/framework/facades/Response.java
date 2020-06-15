package me.ixk.framework.facades;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import me.ixk.framework.http.SetCookie;
import org.eclipse.jetty.http.*;
import org.eclipse.jetty.server.HttpChannel;
import org.eclipse.jetty.server.HttpOutput;
import org.eclipse.jetty.util.Callback;

public class Response extends AbstractFacade {

    protected static me.ixk.framework.http.Response make() {
        return app.make(me.ixk.framework.http.Response.class);
    }

    public static me.ixk.framework.http.Response text(String text) {
        return make().text(text);
    }

    public static me.ixk.framework.http.Response text(String text, int status) {
        return make().text(text, status);
    }

    public static me.ixk.framework.http.Response text(
        String text,
        int status,
        Map<Object, String> headers
    ) {
        return make().text(text, status, headers);
    }

    public static me.ixk.framework.http.Response html(String html) {
        return make().html(html);
    }

    public static me.ixk.framework.http.Response html(String html, int status) {
        return make().html(html, status);
    }

    public static me.ixk.framework.http.Response html(
        String html,
        int status,
        Map<Object, String> headers
    ) {
        return make().html(html, status, headers);
    }

    public static me.ixk.framework.http.Response json(Object data) {
        return make().json(data);
    }

    public static me.ixk.framework.http.Response json(Object data, int status) {
        return make().json(data, status);
    }

    public static me.ixk.framework.http.Response json(
        Object data,
        int status,
        Map<Object, String> headers
    ) {
        return make().json(data, status, headers);
    }

    public static me.ixk.framework.http.Response redirect(String url) {
        return make().redirect(url);
    }

    public static me.ixk.framework.http.Response redirect(
        String url,
        int status
    ) {
        return make().redirect(url, status);
    }

    public static me.ixk.framework.http.Response redirect(
        String url,
        int status,
        Map<Object, String> headers
    ) {
        return make().redirect(url, status, headers);
    }

    public static void error(String message) {
        make().error(message);
    }

    public static void error(String message, int status) {
        make().error(message, status);
    }

    public static void error(
        String message,
        int status,
        Map<Object, String> headers
    ) {
        make().error(message, status, headers);
    }

    public static void processing() {
        make().processing();
    }

    public static void processing(Map<Object, String> headers) {
        make().processing(headers);
    }

    public static String getContent() {
        return make().getContent();
    }

    public static me.ixk.framework.http.Response setContent(String content) {
        return make().setContent(content);
    }

    public static me.ixk.framework.http.Response content(String content) {
        return make().content(content);
    }

    public static me.ixk.framework.http.Response status(int sc) {
        return make().status(sc);
    }

    public static me.ixk.framework.http.Response header(
        String name,
        String value
    ) {
        return make().header(name, value);
    }

    public static me.ixk.framework.http.Response header(
        HttpHeader name,
        String value
    ) {
        return make().header(name, value);
    }

    public static me.ixk.framework.http.Response setHeaders(
        Map<Object, String> headers
    ) {
        return make().setHeaders(headers);
    }

    public static me.ixk.framework.http.Response cookie(
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
        return make()
            .cookie(
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
    }

    public static me.ixk.framework.http.Response addCookies(
        Collection<SetCookie> cookies
    ) {
        return make().addCookies(cookies);
    }

    public static me.ixk.framework.http.Response addCookies(
        SetCookie[] cookies
    ) {
        return make().addCookies(cookies);
    }

    public static List<SetCookie> getCookies() {
        return make().getCookies();
    }

    public static me.ixk.framework.http.Response pushCookieToHeader() {
        return make().pushCookieToHeader();
    }

    public static HttpOutput getHttpOutput() {
        return make().getHttpOutput();
    }

    public static boolean isIncluding() {
        return make().isIncluding();
    }

    public static me.ixk.framework.http.Response include() {
        return make().include();
    }

    public static me.ixk.framework.http.Response included() {
        return make().included();
    }

    public static me.ixk.framework.http.Response addCookie(SetCookie cookie) {
        return make().addCookie(cookie);
    }

    public static me.ixk.framework.http.Response addSetCookie(
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
        return make()
            .addSetCookie(
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
    }

    public static boolean containsHeader(String name) {
        return make().containsHeader(name);
    }

    public static String encodeURL(String url) {
        return make().encodeURL(url);
    }

    public static String encodeRedirectURL(String url) {
        return make().encodeRedirectURL(url);
    }

    public static String encodeUrl(String url) {
        return make().encodeUrl(url);
    }

    public static String encodeRedirectUrl(String url) {
        return make().encodeRedirectUrl(url);
    }

    public static void sendError(int sc) {
        make().sendError(sc);
    }

    public static void sendError(int code, String message) {
        make().sendError(code, message);
    }

    public static void sendProcessing() {
        make().sendProcessing();
    }

    public static void sendRedirect(int code, String location) {
        make().sendRedirect(code, location);
    }

    public static void sendRedirect(String location) {
        make().sendRedirect(location);
    }

    public static void setDateHeader(String name, long date) {
        make().setDateHeader(name, date);
    }

    public static me.ixk.framework.http.Response dateHeader(
        String name,
        long date
    ) {
        return make().dateHeader(name, date);
    }

    public static void addDateHeader(String name, long date) {
        make().addDateHeader(name, date);
    }

    public static me.ixk.framework.http.Response aDateHeader(
        String name,
        long date
    ) {
        return make().aDateHeader(name, date);
    }

    public static me.ixk.framework.http.Response setHeader(
        HttpHeader name,
        String value
    ) {
        return make().setHeader(name, value);
    }

    public static void setHeader(String name, String value) {
        make().setHeader(name, value);
    }

    public static Collection<String> getHeaderNames() {
        return make().getHeaderNames();
    }

    public static String getHeader(String name) {
        return make().getHeader(name);
    }

    public static Collection<String> getHeaders(String name) {
        return make().getHeaders(name);
    }

    public static void addHeader(String name, String value) {
        make().addHeader(name, value);
    }

    public static me.ixk.framework.http.Response aHeader(
        String name,
        String value
    ) {
        return make().aHeader(name, value);
    }

    public static void setIntHeader(String name, int value) {
        make().setIntHeader(name, value);
    }

    public static me.ixk.framework.http.Response intHeader(
        String name,
        int value
    ) {
        return make().intHeader(name, value);
    }

    public static void addIntHeader(String name, int value) {
        make().addIntHeader(name, value);
    }

    public static me.ixk.framework.http.Response aIntHeader(
        String name,
        int value
    ) {
        return make().aIntHeader(name, value);
    }

    public static void setStatus(int sc) {
        make().setStatus(sc);
    }

    public static void setStatus(int sc, String sm) {
        make().setStatus(sc, sm);
    }

    public static me.ixk.framework.http.Response setStatusWithReason(
        int sc,
        String sm
    ) {
        return make().setStatusWithReason(sc, sm);
    }

    public static String getCharacterEncoding() {
        return make().getCharacterEncoding();
    }

    public static String getContentType() {
        return make().getContentType();
    }

    public static ServletOutputStream getOutputStream() throws IOException {
        return make().getOutputStream();
    }

    public static boolean isWriting() {
        return make().isWriting();
    }

    public static PrintWriter getWriter() {
        return make().getWriter();
    }

    public static void setContentLength(int len) {
        make().setContentLength(len);
    }

    public static me.ixk.framework.http.Response contentLength(int len) {
        return make().contentLength(len);
    }

    public static long getContentLength() {
        return make().getContentLength();
    }

    public static boolean isAllContentWritten(long written) {
        return make().isAllContentWritten(written);
    }

    public static me.ixk.framework.http.Response closeOutput()
        throws IOException {
        return make().closeOutput();
    }

    public static long getLongContentLength() {
        return make().getLongContentLength();
    }

    public static me.ixk.framework.http.Response setLongContentLength(
        long len
    ) {
        return make().setLongContentLength(len);
    }

    public static void setContentLengthLong(long length) {
        make().setContentLengthLong(length);
    }

    public static me.ixk.framework.http.Response contentLengthLong(
        long length
    ) {
        return make().contentLengthLong(length);
    }

    public static void setCharacterEncoding(String encoding) {
        make().setCharacterEncoding(encoding);
    }

    public static me.ixk.framework.http.Response characterEncoding(
        String encoding
    ) {
        return make().characterEncoding(encoding);
    }

    public static void setContentType(String contentType) {
        make().setContentType(contentType);
    }

    public static me.ixk.framework.http.Response contentType(
        String contentType
    ) {
        return make().contentType(contentType);
    }

    public static void setBufferSize(int size) {
        make().setBufferSize(size);
    }

    public static me.ixk.framework.http.Response bufferSize(int size) {
        return make().bufferSize(size);
    }

    public static int getBufferSize() {
        return make().getBufferSize();
    }

    public static void flushBuffer() throws IOException {
        make().flushBuffer();
    }

    public static void reset() {
        make().reset();
    }

    public static me.ixk.framework.http.Response resetForForward() {
        return make().resetForForward();
    }

    public static void resetBuffer() {
        make().resetBuffer();
    }

    public static boolean isCommitted() {
        return make().isCommitted();
    }

    public static void setLocale(Locale locale) {
        make().setLocale(locale);
    }

    public static me.ixk.framework.http.Response locale(Locale locale) {
        return make().locale(locale);
    }

    public static Locale getLocale() {
        return make().getLocale();
    }

    public static int getStatus() {
        return make().getStatus();
    }

    public static String getReason() {
        return make().getReason();
    }

    public static HttpFields getHttpFields() {
        return make().getHttpFields();
    }

    public static long getContentCount() {
        return make().getContentCount();
    }

    public static org.eclipse.jetty.server.Response getOriginResponse() {
        return make().getOriginResponse();
    }

    public static me.ixk.framework.http.Response setOriginResponse(
        org.eclipse.jetty.server.Response response
    ) {
        return make().setOriginResponse(response);
    }

    public static void addCookie(Cookie cookie) {
        make().addCookie(cookie);
    }

    public static HttpChannel getHttpChannel() {
        return make().getHttpChannel();
    }

    public static void addCookie(HttpCookie cookie) {
        make().addCookie(cookie);
    }

    public static void replaceCookie(HttpCookie cookie) {
        make().replaceCookie(cookie);
    }

    public static boolean isContentComplete(long written) {
        return make().isContentComplete(written);
    }

    public static me.ixk.framework.http.Response setTrailers(
        Supplier<HttpFields> trailers
    ) {
        return make().setTrailers(trailers);
    }

    public static Supplier<HttpFields> getTrailers() {
        return make().getTrailers();
    }

    public static MetaData.Response getCommittedMetaData() {
        return make().getCommittedMetaData();
    }

    public static me.ixk.framework.http.Response putHeaders(
        HttpContent content,
        long contentLength,
        boolean etag
    ) {
        return make().putHeaders(content, contentLength, etag);
    }

    public static me.ixk.framework.http.Response reopen() {
        return make().reopen();
    }

    public static me.ixk.framework.http.Response errorClose() {
        return make().errorClose();
    }

    public static boolean isStreaming() {
        return make().isStreaming();
    }

    public static boolean isWritingOrStreaming() {
        return make().isWritingOrStreaming();
    }

    public static me.ixk.framework.http.Response completeOutput(
        Callback callback
    ) {
        return make().completeOutput(callback);
    }

    public static me.ixk.framework.http.Response resetContent() {
        return make().resetContent();
    }
}
