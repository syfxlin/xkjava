package me.ixk.framework.facades;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import me.ixk.framework.http.SetCookie;
import org.eclipse.jetty.http.HttpContent;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.server.HttpOutput;

public class Response extends AbstractFacade {

    protected static me.ixk.framework.http.Response make() {
        return app.make(me.ixk.framework.http.Response.class);
    }

    public static me.ixk.framework.http.Response text(String text)
        throws IOException {
        return make().text(text);
    }

    public static me.ixk.framework.http.Response text(String text, int status)
        throws IOException {
        return make().text(text, status);
    }

    public static me.ixk.framework.http.Response text(
        String text,
        int status,
        Map<Object, String> headers
    )
        throws IOException {
        return make().text(text, status, headers);
    }

    public static me.ixk.framework.http.Response html(String html)
        throws IOException {
        return make().html(html);
    }

    public static me.ixk.framework.http.Response html(String html, int status)
        throws IOException {
        return make().html(html, status);
    }

    public static me.ixk.framework.http.Response html(
        String html,
        int status,
        Map<Object, String> headers
    )
        throws IOException {
        return make().html(html, status, headers);
    }

    public static me.ixk.framework.http.Response json(Object data)
        throws IOException {
        return make().json(data);
    }

    public static me.ixk.framework.http.Response json(Object data, int status)
        throws IOException {
        return make().json(data, status);
    }

    public static me.ixk.framework.http.Response json(
        Object data,
        int status,
        Map<Object, String> headers
    )
        throws IOException {
        return make().json(data, status, headers);
    }

    public static me.ixk.framework.http.Response redirect(String url)
        throws IOException {
        return make().redirect(url);
    }

    public static me.ixk.framework.http.Response redirect(
        String url,
        int status
    )
        throws IOException {
        return make().redirect(url, status);
    }

    public static me.ixk.framework.http.Response redirect(
        String url,
        int status,
        Map<Object, String> headers
    )
        throws IOException {
        return make().redirect(url, status, headers);
    }

    public static void error(String message) throws IOException {
        make().error(message);
    }

    public static void error(String message, int status) throws IOException {
        make().error(message, status);
    }

    public static void error(
        String message,
        int status,
        Map<Object, String> headers
    )
        throws IOException {
        make().error(message, status, headers);
    }

    public static void processing() throws IOException {
        make().processing();
    }

    public static void processing(Map<Object, String> headers)
        throws IOException {
        make().processing(headers);
    }

    public static String getContent() throws IOException {
        return make().getContent();
    }

    public static me.ixk.framework.http.Response setContent(String content)
        throws IOException {
        return make().setContent(content);
    }

    public static me.ixk.framework.http.Response content(String content)
        throws IOException {
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

    public static me.ixk.framework.http.Response setHeaders(
        HttpContent httpContent
    ) {
        return make().setHeaders(httpContent);
    }

    public static HttpOutput getHttpOutput() {
        return make().getHttpOutput();
    }

    public static me.ixk.framework.http.Response setHttpOutput(HttpOutput out) {
        return make().setHttpOutput(out);
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

    public static void sendError(int sc) throws IOException {
        make().sendError(sc);
    }

    public static void sendError(int code, String message) throws IOException {
        make().sendError(code, message);
    }

    public static void sendProcessing() throws IOException {
        make().sendProcessing();
    }

    public static void sendRedirect(int code, String location)
        throws IOException {
        make().sendRedirect(code, location);
    }

    public static void sendRedirect(String location) throws IOException {
        make().sendRedirect(location);
    }

    public static me.ixk.framework.http.Response setDateHeader(
        String name,
        long date
    ) {
        return make().setDateHeader(name, date);
    }

    public static me.ixk.framework.http.Response addDateHeader(
        String name,
        long date
    ) {
        return make().addDateHeader(name, date);
    }

    public static me.ixk.framework.http.Response setHeader(
        HttpHeader name,
        String value
    ) {
        return make().setHeader(name, value);
    }

    public static me.ixk.framework.http.Response setHeader(
        String name,
        String value
    ) {
        return make().setHeader(name, value);
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

    public static me.ixk.framework.http.Response addHeader(
        String name,
        String value
    ) {
        return make().addHeader(name, value);
    }

    public static me.ixk.framework.http.Response setIntHeader(
        String name,
        int value
    ) {
        return make().setIntHeader(name, value);
    }

    public static me.ixk.framework.http.Response addIntHeader(
        String name,
        int value
    ) {
        return make().addIntHeader(name, value);
    }

    public static me.ixk.framework.http.Response setStatus(int sc) {
        return make().setStatus(sc);
    }

    public static me.ixk.framework.http.Response setStatus(int sc, String sm) {
        return make().setStatus(sc, sm);
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

    public static PrintWriter getWriter() throws IOException {
        return make().getWriter();
    }

    public static me.ixk.framework.http.Response setContentLength(int len) {
        return make().setContentLength(len);
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

    public static me.ixk.framework.http.Response setContentLengthLong(
        long length
    ) {
        return make().setContentLengthLong(length);
    }

    public static me.ixk.framework.http.Response setCharacterEncoding(
        String encoding
    ) {
        return make().setCharacterEncoding(encoding);
    }

    public static me.ixk.framework.http.Response setContentType(
        String contentType
    ) {
        return make().setContentType(contentType);
    }

    public static me.ixk.framework.http.Response setBufferSize(int size) {
        return make().setBufferSize(size);
    }

    public static int getBufferSize() {
        return make().getBufferSize();
    }

    public static me.ixk.framework.http.Response flushBuffer()
        throws IOException {
        return make().flushBuffer();
    }

    public static me.ixk.framework.http.Response reset() {
        return make().reset();
    }

    public static me.ixk.framework.http.Response reset(
        boolean preserveCookies
    ) {
        return make().reset(preserveCookies);
    }

    public static me.ixk.framework.http.Response resetForForward() {
        return make().resetForForward();
    }

    public static me.ixk.framework.http.Response resetBuffer() {
        return make().resetBuffer();
    }

    public static boolean isCommitted() {
        return make().isCommitted();
    }

    public static me.ixk.framework.http.Response setLocale(Locale locale) {
        return make().setLocale(locale);
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

    public List<SetCookie> getCookies() {
        return make().getCookies();
    }

    public me.ixk.framework.http.Response pushCookieToHeader() {
        return make().pushCookieToHeader();
    }
}
