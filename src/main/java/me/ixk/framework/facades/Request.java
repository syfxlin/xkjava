package me.ixk.framework.facades;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.security.Principal;
import java.util.*;
import java.util.regex.Pattern;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.http.Cookie;
import org.eclipse.jetty.http.*;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.util.Attributes;
import org.eclipse.jetty.util.MultiMap;

public class Request extends AbstractFacade {

    protected static me.ixk.framework.http.Request make() {
        return app.make(me.ixk.framework.http.Request.class);
    }

    public static String getBody() {
        return make().getBody();
    }

    public static me.ixk.framework.http.Request setBody(String body) {
        return make().setBody(body);
    }

    public static JsonNode getParseBody() {
        return make().getParseBody();
    }

    public static me.ixk.framework.http.Request setParseBody(
        JsonNode parseBody
    ) {
        return make().setParseBody(parseBody);
    }

    public static boolean hasHeader(String name) {
        return make().hasHeader(name);
    }

    public static String header(String name) {
        return make().header(name);
    }

    public static Enumeration<String> headers(String name) {
        return make().headers(name);
    }

    public static String header(String name, String _default) {
        return make().header(name, _default);
    }

    public static Enumeration<String> headers(
        String name,
        Enumeration<String> _default
    ) {
        return make().headers(name, _default);
    }

    public static Map<String, Object> all() {
        return make().all();
    }

    public static JsonNode input() {
        return make().input();
    }

    public static JsonNode input(String name) {
        return make().input(name);
    }

    public static JsonNode input(String name, JsonNode _default) {
        return make().input(name, _default);
    }

    public static MultiMap<String> query() {
        return make().query();
    }

    public static String query(String name) {
        return make().query(name);
    }

    public static String query(String name, String _default) {
        return make().query(name, _default);
    }

    public static boolean has(String name) {
        return make().has(name);
    }

    public static Cookie cookie(String name) {
        return make().cookie(name);
    }

    public static Cookie cookie(String name, Cookie _default) {
        return make().cookie(name, _default);
    }

    public static HttpSession session() {
        return make().session();
    }

    public static Object session(String name) {
        return make().session(name);
    }

    public static Object session(String name, Object _default) {
        return make().session(name, _default);
    }

    public static String path() {
        return make().path();
    }

    public static String url() {
        return make().url();
    }

    public static String fullUrl() {
        return make().fullUrl();
    }

    public static String method() {
        return make().method();
    }

    public static boolean isMethod(String method) {
        return make().isMethod(method);
    }

    public static boolean isMethod(HttpMethod method) {
        return make().isMethod(method);
    }

    public static boolean pattern(String regex) {
        return make().pattern(regex);
    }

    public static boolean pattern(Pattern pattern) {
        return make().pattern(pattern);
    }

    public static boolean ajax() {
        return make().ajax();
    }

    public static boolean isJson() {
        return make().isJson();
    }

    public static Part file(String name) throws IOException, ServletException {
        return make().file(name);
    }

    public static boolean hasFile(String name)
        throws IOException, ServletException {
        return make().hasFile(name);
    }

    public static boolean moveFileTo(String name, String path)
        throws IOException, ServletException {
        return make().moveFileTo(name, path);
    }

    public static HttpFields getHttpFields() {
        return make().getHttpFields();
    }

    public static HttpInput getHttpInput() {
        return make().getHttpInput();
    }

    public static me.ixk.framework.http.Request addEventListener(
        EventListener listener
    ) {
        return make().addEventListener(listener);
    }

    public static me.ixk.framework.http.Request extractFormParameters(
        MultiMap<String> params
    ) {
        return make().extractFormParameters(params);
    }

    public static AsyncContext getAsyncContext() {
        return make().getAsyncContext();
    }

    public static HttpChannelState getHttpChannelState() {
        return make().getHttpChannelState();
    }

    public static Object getAttribute(String name) {
        return make().getAttribute(name);
    }

    public static Enumeration<String> getAttributeNames() {
        return make().getAttributeNames();
    }

    public static Attributes getAttributes() {
        return make().getAttributes();
    }

    public static Authentication getAuthentication() {
        return make().getAuthentication();
    }

    public static String getAuthType() {
        return make().getAuthType();
    }

    public static String getCharacterEncoding() {
        return make().getCharacterEncoding();
    }

    public static HttpChannel getHttpChannel() {
        return make().getHttpChannel();
    }

    public static int getContentLength() {
        return make().getContentLength();
    }

    public static long getContentLengthLong() {
        return make().getContentLengthLong();
    }

    public static long getContentRead() {
        return make().getContentRead();
    }

    public static String getContentType() {
        return make().getContentType();
    }

    public static ContextHandler.Context getContext() {
        return make().getContext();
    }

    public static String getContextPath() {
        return make().getContextPath();
    }

    public static Cookie[] getCookies() {
        return make().getCookies();
    }

    public static long getDateHeader(String name) {
        return make().getDateHeader(name);
    }

    public static DispatcherType getDispatcherType() {
        return make().getDispatcherType();
    }

    public static String getHeader(String name) {
        return make().getHeader(name);
    }

    public static Enumeration<String> getHeaderNames() {
        return make().getHeaderNames();
    }

    public static Enumeration<String> getHeaders(String name) {
        return make().getHeaders(name);
    }

    public static int getInputState() {
        return make().getInputState();
    }

    public static ServletInputStream getInputStream() throws IOException {
        return make().getInputStream();
    }

    public static int getIntHeader(String name) {
        return make().getIntHeader(name);
    }

    public static Locale getLocale() {
        return make().getLocale();
    }

    public static Enumeration<Locale> getLocales() {
        return make().getLocales();
    }

    public static String getLocalAddr() {
        return make().getLocalAddr();
    }

    public static String getLocalName() {
        return make().getLocalName();
    }

    public static int getLocalPort() {
        return make().getLocalPort();
    }

    public static String getMethod() {
        return make().getMethod();
    }

    public static String getParameter(String name) {
        return make().getParameter(name);
    }

    public static Map<String, String[]> getParameterMap() {
        return make().getParameterMap();
    }

    public static Enumeration<String> getParameterNames() {
        return make().getParameterNames();
    }

    public static String[] getParameterValues(String name) {
        return make().getParameterValues(name);
    }

    public static MultiMap<String> getQueryParameters() {
        return make().getQueryParameters();
    }

    public static me.ixk.framework.http.Request setQueryParameters(
        MultiMap<String> queryParameters
    ) {
        return make().setQueryParameters(queryParameters);
    }

    public static me.ixk.framework.http.Request setContentParameters(
        MultiMap<String> contentParameters
    ) {
        return make().setContentParameters(contentParameters);
    }

    public static me.ixk.framework.http.Request resetParameters() {
        return make().resetParameters();
    }

    public static String getPathInfo() {
        return make().getPathInfo();
    }

    public static String getPathTranslated() {
        return make().getPathTranslated();
    }

    public static String getProtocol() {
        return make().getProtocol();
    }

    public static HttpVersion getHttpVersion() {
        return make().getHttpVersion();
    }

    public static String getQueryEncoding() {
        return make().getQueryEncoding();
    }

    public static String getQueryString() {
        return make().getQueryString();
    }

    public static BufferedReader getReader() throws IOException {
        return make().getReader();
    }

    public static String getRealPath(String path) {
        return make().getRealPath(path);
    }

    public static InetSocketAddress getRemoteInetSocketAddress() {
        return make().getRemoteInetSocketAddress();
    }

    public static String getRemoteAddr() {
        return make().getRemoteAddr();
    }

    public static String getRemoteHost() {
        return make().getRemoteHost();
    }

    public static int getRemotePort() {
        return make().getRemotePort();
    }

    public static String getRemoteUser() {
        return make().getRemoteUser();
    }

    public static RequestDispatcher getRequestDispatcher(String path) {
        return make().getRequestDispatcher(path);
    }

    public static String getRequestedSessionId() {
        return make().getRequestedSessionId();
    }

    public static String getRequestURI() {
        return make().getRequestURI();
    }

    public static StringBuffer getRequestURL() {
        return make().getRequestURL();
    }

    public static Response getResponse() {
        return make().getResponse();
    }

    public static StringBuilder getRootURL() {
        return make().getRootURL();
    }

    public static String getScheme() {
        return make().getScheme();
    }

    public static String getServerName() {
        return make().getServerName();
    }

    public static int getServerPort() {
        return make().getServerPort();
    }

    public static ServletContext getServletContext() {
        return make().getServletContext();
    }

    public static String getServletName() {
        return make().getServletName();
    }

    public static String getServletPath() {
        return make().getServletPath();
    }

    public static ServletResponse getServletResponse() {
        return make().getServletResponse();
    }

    public static String changeSessionId() {
        return make().changeSessionId();
    }

    public static HttpSession getSession() {
        return make().getSession();
    }

    public static HttpSession getSession(boolean create) {
        return make().getSession(create);
    }

    public static long getTimeStamp() {
        return make().getTimeStamp();
    }

    public static HttpURI getHttpURI() {
        return make().getHttpURI();
    }

    public static UserIdentity getUserIdentity() {
        return make().getUserIdentity();
    }

    public static UserIdentity getResolvedUserIdentity() {
        return make().getResolvedUserIdentity();
    }

    public static UserIdentity.Scope getUserIdentityScope() {
        return make().getUserIdentityScope();
    }

    public static Principal getUserPrincipal() {
        return make().getUserPrincipal();
    }

    public static boolean isHandled() {
        return make().isHandled();
    }

    public static boolean isAsyncStarted() {
        return make().isAsyncStarted();
    }

    public static boolean isAsyncSupported() {
        return make().isAsyncSupported();
    }

    public static boolean isRequestedSessionIdFromCookie() {
        return make().isRequestedSessionIdFromCookie();
    }

    public static boolean isRequestedSessionIdFromUrl() {
        return make().isRequestedSessionIdFromUrl();
    }

    public static boolean isRequestedSessionIdFromURL() {
        return make().isRequestedSessionIdFromURL();
    }

    public static boolean isRequestedSessionIdValid() {
        return make().isRequestedSessionIdValid();
    }

    public static boolean isSecure() {
        return make().isSecure();
    }

    public static me.ixk.framework.http.Request setSecure(boolean secure) {
        return make().setSecure(secure);
    }

    public static boolean isUserInRole(String role) {
        return make().isUserInRole(role);
    }

    public static void removeAttribute(String name) {
        make().removeAttribute(name);
    }

    public static me.ixk.framework.http.Request removeEventListener(
        EventListener listener
    ) {
        return make().removeEventListener(listener);
    }

    public static me.ixk.framework.http.Request setAsyncSupported(
        boolean supported,
        String source
    ) {
        return make().setAsyncSupported(supported, source);
    }

    public static void setAttribute(String name, Object value) {
        make().setAttribute(name, value);
    }

    public static me.ixk.framework.http.Request setAttributes(
        Attributes attributes
    ) {
        return make().setAttributes(attributes);
    }

    public static me.ixk.framework.http.Request setAuthentication(
        Authentication authentication
    ) {
        return make().setAuthentication(authentication);
    }

    public static void setCharacterEncoding(String encoding)
        throws UnsupportedEncodingException {
        make().setCharacterEncoding(encoding);
    }

    public static me.ixk.framework.http.Request setCharacterEncodingUnchecked(
        String encoding
    ) {
        return make().setCharacterEncodingUnchecked(encoding);
    }

    public static me.ixk.framework.http.Request setContentType(
        String contentType
    ) {
        return make().setContentType(contentType);
    }

    public static me.ixk.framework.http.Request setContext(
        ContextHandler.Context context
    ) {
        return make().setContext(context);
    }

    public static boolean takeNewContext() {
        return make().takeNewContext();
    }

    public static me.ixk.framework.http.Request setContextPath(
        String contextPath
    ) {
        return make().setContextPath(contextPath);
    }

    public static me.ixk.framework.http.Request setCookies(Cookie[] cookies) {
        return make().setCookies(cookies);
    }

    public static me.ixk.framework.http.Request setDispatcherType(
        DispatcherType type
    ) {
        return make().setDispatcherType(type);
    }

    public static me.ixk.framework.http.Request setHandled(boolean h) {
        return make().setHandled(h);
    }

    public static me.ixk.framework.http.Request setMethod(String method) {
        return make().setMethod(method);
    }

    public static boolean isHead() {
        return make().isHead();
    }

    public static me.ixk.framework.http.Request setPathInfo(String pathInfo) {
        return make().setPathInfo(pathInfo);
    }

    public static me.ixk.framework.http.Request setHttpVersion(
        HttpVersion version
    ) {
        return make().setHttpVersion(version);
    }

    public static me.ixk.framework.http.Request setQueryEncoding(
        String queryEncoding
    ) {
        return make().setQueryEncoding(queryEncoding);
    }

    public static me.ixk.framework.http.Request setQueryString(
        String queryString
    ) {
        return make().setQueryString(queryString);
    }

    public static me.ixk.framework.http.Request setRemoteAddr(
        InetSocketAddress addr
    ) {
        return make().setRemoteAddr(addr);
    }

    public static me.ixk.framework.http.Request setRequestedSessionId(
        String requestedSessionId
    ) {
        return make().setRequestedSessionId(requestedSessionId);
    }

    public static me.ixk.framework.http.Request setRequestedSessionIdFromCookie(
        boolean requestedSessionIdCookie
    ) {
        return make().setRequestedSessionIdFromCookie(requestedSessionIdCookie);
    }

    public static me.ixk.framework.http.Request setScheme(String scheme) {
        return make().setScheme(scheme);
    }

    public static me.ixk.framework.http.Request setServletPath(
        String servletPath
    ) {
        return make().setServletPath(servletPath);
    }

    public static me.ixk.framework.http.Request setSession(
        HttpSession session
    ) {
        return make().setSession(session);
    }

    public static me.ixk.framework.http.Request setTimeStamp(long ts) {
        return make().setTimeStamp(ts);
    }

    public static me.ixk.framework.http.Request setHttpURI(HttpURI uri) {
        return make().setHttpURI(uri);
    }

    public static me.ixk.framework.http.Request setUserIdentityScope(
        UserIdentity.Scope scope
    ) {
        return make().setUserIdentityScope(scope);
    }

    public static AsyncContext startAsync() throws IllegalStateException {
        return make().startAsync();
    }

    public static AsyncContext startAsync(
        ServletRequest servletRequest,
        ServletResponse servletResponse
    )
        throws IllegalStateException {
        return make().startAsync(servletRequest, servletResponse);
    }

    public static boolean authenticate(HttpServletResponse response)
        throws IOException, ServletException {
        return make().authenticate(response);
    }

    public static Part getPart(String name)
        throws IOException, ServletException {
        return make().getPart(name);
    }

    public static Collection<Part> getParts()
        throws IOException, ServletException {
        return make().getParts();
    }

    public static void login(String username, String password)
        throws ServletException {
        make().login(username, password);
    }

    public static void logout() throws ServletException {
        make().logout();
    }

    public static me.ixk.framework.http.Request mergeQueryParameters(
        String oldQuery,
        String newQuery,
        boolean updateQueryString
    ) {
        return make()
            .mergeQueryParameters(oldQuery, newQuery, updateQueryString);
    }

    public static HttpFields getTrailers() {
        return make().getTrailers();
    }

    public static boolean isPush() {
        return make().isPush();
    }

    public static boolean isPushSupported() {
        return make().isPushSupported();
    }

    public static PushBuilder getPushBuilder() {
        return make().getPushBuilder();
    }

    public static SessionHandler getSessionHandler() {
        return make().getSessionHandler();
    }

    public static String getOriginalURI() {
        return make().getOriginalURI();
    }

    public static me.ixk.framework.http.Request setMetaData(
        MetaData.Request request
    ) {
        return make().setMetaData(request);
    }

    public static MetaData.Request getMetaData() {
        return make().getMetaData();
    }

    public static boolean hasMetaData() {
        return make().hasMetaData();
    }

    public static me.ixk.framework.http.Request setURIPathQuery(
        String requestURI
    ) {
        return make().setURIPathQuery(requestURI);
    }

    public static me.ixk.framework.http.Request setAuthority(
        String host,
        int port
    ) {
        return make().setAuthority(host, port);
    }

    public static me.ixk.framework.http.Request setSessionHandler(
        SessionHandler sessionHandler
    ) {
        return make().setSessionHandler(sessionHandler);
    }

    public static <T extends HttpUpgradeHandler> T upgrade(
        Class<T> handlerClass
    )
        throws IOException, ServletException {
        return make().upgrade(handlerClass);
    }

    public static org.eclipse.jetty.server.Request getOriginRequest() {
        return make().getOriginRequest();
    }

    public static me.ixk.framework.http.Request setOriginRequest(
        org.eclipse.jetty.server.Request request
    ) {
        return make().setOriginRequest(request);
    }

    public static me.ixk.framework.http.Request enterSession(HttpSession s) {
        return make().enterSession(s);
    }

    public static ContextHandler.Context getErrorContext() {
        return make().getErrorContext();
    }

    public static me.ixk.framework.http.Request onCompleted() {
        return make().onCompleted();
    }

    public static me.ixk.framework.http.Request onResponseCommit() {
        return make().onResponseCommit();
    }

    public static HttpSession getSession(SessionHandler sessionHandler) {
        return make().getSession(sessionHandler);
    }

    public static me.ixk.framework.http.Request setAsyncAttributes() {
        return make().setAsyncAttributes();
    }
}
