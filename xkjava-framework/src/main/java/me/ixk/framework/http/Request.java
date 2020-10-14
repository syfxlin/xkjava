/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.http;

import cn.hutool.core.io.IoUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;
import me.ixk.framework.helpers.Util;
import me.ixk.framework.route.RouteResult;
import me.ixk.framework.utils.JSON;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.http.MetaData;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.server.HttpChannel;
import org.eclipse.jetty.server.HttpChannelState;
import org.eclipse.jetty.server.HttpInput;
import org.eclipse.jetty.server.PushBuilder;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.UserIdentity;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.util.Attributes;
import org.eclipse.jetty.util.MultiMap;

/**
 * 请求对象
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 9:43
 */
public class Request implements HttpServletRequest {
    public static final String REQUEST_BODY = "&body";

    protected final org.eclipse.jetty.server.Request base;
    protected volatile String body;
    protected volatile JsonNode parseBody = null;
    protected Map<String, Cookie> cookies;
    protected volatile RouteResult route;

    @Deprecated
    public Request() {
        // only used cglib
        this.base = null;
    }

    public Request(org.eclipse.jetty.server.Request request) {
        this.base = request;
        this.initRequest();
    }

    protected void initRequest() {
        // 如果是 JSON 就解析 JSON，一旦解析后就无法使用 getOutStream
        String baseType = HttpFields.valueParameters(
            this.base.getContentType(),
            null
        );
        if (
            MimeTypes.Type.APPLICATION_JSON.is(baseType) ||
            MimeTypes.Type.TEXT_JSON.is(baseType)
        ) {
            try {
                this.body =
                    base.getReader().lines().collect(Collectors.joining());
            } catch (IOException e) {
                this.body = null;
            }
            this.parseBody();
        }
        Cookie[] cookies = base.getCookies();
        this.cookies = new ConcurrentHashMap<>();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                this.cookies.put(cookie.getName(), cookie);
            }
        }
    }

    public RouteResult getRoute() {
        return route;
    }

    public Request setRoute(RouteResult route) {
        this.route = route;
        return this;
    }

    protected <T> T getOrDefault(T result, T defaultValue) {
        if (result == null) {
            return defaultValue;
        }
        return result;
    }

    protected void parseBody() {
        if (!this.isJson()) {
            return;
        }
        this.parseBody = JSON.parse(this.body);
    }

    public String getBody() {
        return body;
    }

    public Request setBody(String body) {
        this.body = body;
        return this;
    }

    public JsonNode getParseBody() {
        return parseBody;
    }

    public Request setParseBody(JsonNode parseBody) {
        this.parseBody = parseBody;
        return this;
    }

    public boolean hasHeader(String name) {
        return this.getHeader(name) != null;
    }

    public String header(String name) {
        return base.getHeader(name);
    }

    public Enumeration<String> headers(String name) {
        return base.getHeaders(name);
    }

    public String header(String name, String defaultValue) {
        return this.getOrDefault(base.getHeader(name), defaultValue);
    }

    public Enumeration<String> headers(
        String name,
        Enumeration<String> defaultValue
    ) {
        return this.getOrDefault(base.getHeaders(name), defaultValue);
    }

    public Map<String, Object> all() {
        Map<String, Object> map = new ConcurrentHashMap<>();
        for (Map.Entry<String, String[]> entry : base
            .getParameterMap()
            .entrySet()) {
            String[] value = entry.getValue();
            map.put(entry.getKey(), value.length == 1 ? value[0] : value);
        }
        map.putAll(this.route.getParams());
        map.putAll(this.cookies);
        try {
            for (Part part : base.getParts()) {
                map.put(part.getName(), part);
            }
        } catch (IOException | ServletException e) {
            // no code
        }
        if (this.parseBody != null) {
            if (this.parseBody.isObject()) {
                ObjectNode object = (ObjectNode) this.parseBody;
                for (
                    Iterator<Map.Entry<String, JsonNode>> it = object.fields();
                    it.hasNext();
                ) {
                    Map.Entry<String, JsonNode> entry = it.next();
                    map.put(entry.getKey(), entry.getValue());
                }
            } else {
                map.put(REQUEST_BODY, this.parseBody);
            }
        }
        return map;
    }

    public Object all(String name) {
        return this.all(name, null);
    }

    public Object all(String name, Object defaultValue) {
        Object object = this.getParameterValues(name);
        if (object != null) {
            String[] arr = (String[]) object;
            if (arr.length == 1) {
                object = arr[0];
            }
        }
        if (object == null) {
            object = this.input(name);
        }
        if (object == null) {
            object = this.route(name);
        }
        if (object == null) {
            object = this.cookie(name);
        }
        if (object == null) {
            object = this.file(name);
        }
        if (object == null && REQUEST_BODY.equals(name)) {
            object = this.getParseBody();
            if (object != null && ((JsonNode) object).isNull()) {
                object = null;
            }
        }
        if (object == null) {
            object = defaultValue;
        }
        return object;
    }

    public boolean has(String name) {
        if (base.getParameterMap().containsKey(name)) {
            return true;
        }
        if (route.getParams().containsKey(name)) {
            return true;
        }
        if (cookies.containsKey(name)) {
            return true;
        }
        try {
            if (base.getPart(name) != null) {
                return true;
            }
        } catch (IOException | ServletException e) {
            // no code
        }
        if (this.parseBody != null) {
            if (this.parseBody.isObject()) {
                ObjectNode object = (ObjectNode) this.parseBody;
                return object.has(name);
            } else {
                return name.equals(REQUEST_BODY);
            }
        }
        return false;
    }

    public JsonNode input() {
        JsonNode node;
        if (this.parseBody != null) {
            node = this.parseBody;
        } else {
            node = JSON.convertToNode(this.getParameterMap());
        }
        if (node.isNull()) {
            node = null;
        }
        return node;
    }

    public JsonNode input(String name) {
        JsonNode node;
        if (this.parseBody == null) {
            node = JSON.convertToNode(base.getParameter(name));
        } else {
            node = Util.dataGet(this.parseBody, name);
        }
        if (node != null && node.isNull()) {
            node = null;
        }
        return node;
    }

    public JsonNode input(String name, JsonNode defaultValue) {
        return this.getOrDefault(this.input(name), defaultValue);
    }

    public Map<String, String[]> query() {
        return base.getParameterMap();
    }

    public String query(String name) {
        return base.getParameter(name);
    }

    public String query(String name, String defaultValue) {
        return this.getOrDefault(base.getParameter(name), defaultValue);
    }

    public RouteResult route() {
        return this.route;
    }

    public String route(String name) {
        return this.route.getParams().get(name);
    }

    public String route(String name, String defaultValue) {
        return this.route.getParams().getOrDefault(name, defaultValue);
    }

    public Cookie cookie(String name) {
        return this.cookies.get(name);
    }

    public Cookie cookie(String name, Cookie defaultValue) {
        return this.cookies.getOrDefault(name, defaultValue);
    }

    public HttpSession session() {
        return base.getSession();
    }

    public Object session(String name) {
        return base.getSession().getAttribute(name);
    }

    public Object session(String name, Object defaultValue) {
        return this.getOrDefault(
                base.getSession().getAttribute(name),
                defaultValue
            );
    }

    public String path() {
        return base.getHttpURI().getPath();
    }

    public String url() {
        HttpURI uri = base.getHttpURI();
        return uri.getScheme() + "://" + uri.getAuthority() + uri.getPath();
    }

    public String fullUrl() {
        return base.getHttpURI().toString();
    }

    public String method() {
        return base.getMethod();
    }

    public boolean isMethod(String method) {
        return base.getMethod().equalsIgnoreCase(method);
    }

    public boolean isMethod(HttpMethod method) {
        return method.is(base.getMethod());
    }

    public boolean pattern(String regex) {
        return Pattern.matches(regex, base.getHttpURI().getPath());
    }

    public boolean pattern(Pattern pattern) {
        return pattern.matcher(base.getHttpURI().getPath()).matches();
    }

    public boolean ajax() {
        String xrw = this.getHeader("X-Requested-With");
        String acc = this.getHeader("Accept");
        return (
            "XMLHttpRequest".equals(xrw) ||
            (
                acc != null &&
                acc.startsWith(MimeTypes.Type.APPLICATION_JSON.asString())
            )
        );
    }

    public boolean isJson() {
        return (
            base.getContentType() != null &&
            base
                .getContentType()
                .startsWith(MimeTypes.Type.APPLICATION_JSON.asString())
        );
    }

    public Part file(String name) {
        try {
            return base.getPart(name);
        } catch (IOException | ServletException e) {
            return null;
        }
    }

    public String fileToString(String name) {
        return this.fileToString(name, StandardCharsets.UTF_8);
    }

    public String fileToString(String name, Charset charset) {
        try {
            return IoUtil
                .getReader(this.file(name).getInputStream(), charset)
                .lines()
                .collect(Collectors.joining("\n"));
        } catch (IOException e) {
            return null;
        }
    }

    public boolean hasFile(String name) {
        try {
            return (
                base.getPart(name) != null ||
                base.getPart(name).getSubmittedFileName() != null
            );
        } catch (IOException | ServletException e) {
            return false;
        }
    }

    public boolean moveFileTo(String name, String path) {
        try {
            Part part = base.getPart(name);
            if (part == null) {
                return false;
            }
            File file = new File(path);
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    return false;
                }
            }
            return (
                Files.copy(
                    part.getInputStream(),
                    file.toPath(),
                    StandardCopyOption.REPLACE_EXISTING
                ) !=
                0
            );
        } catch (IOException | ServletException e) {
            return false;
        }
    }

    /* ================== */

    public HttpFields getHttpFields() {
        return base.getHttpFields();
    }

    public HttpInput getHttpInput() {
        return base.getHttpInput();
    }

    public Request addEventListener(EventListener listener) {
        base.addEventListener(listener);
        return this;
    }

    public Request extractFormParameters(MultiMap<String> params) {
        base.extractFormParameters(params);
        return this;
    }

    public HttpChannelState getHttpChannelState() {
        return base.getHttpChannelState();
    }

    @Override
    public Object getAttribute(String name) {
        return base.getAttribute(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return base.getAttributeNames();
    }

    @Override
    public String getCharacterEncoding() {
        return base.getCharacterEncoding();
    }

    @Override
    public void setCharacterEncoding(String encoding)
        throws UnsupportedEncodingException {
        base.setCharacterEncoding(encoding);
    }

    @Override
    public int getContentLength() {
        return base.getContentLength();
    }

    @Override
    public long getContentLengthLong() {
        return base.getContentLengthLong();
    }

    @Override
    public String getContentType() {
        return base.getContentType();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return base.getInputStream();
    }

    @Override
    public String getParameter(String name) {
        return base.getParameter(name);
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return base.getParameterNames();
    }

    @Override
    public String[] getParameterValues(String name) {
        return base.getParameterValues(name);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return base.getParameterMap();
    }

    @Override
    public String getProtocol() {
        return base.getProtocol();
    }

    @Override
    public String getScheme() {
        return base.getScheme();
    }

    @Override
    public String getServerName() {
        return base.getServerName();
    }

    @Override
    public int getServerPort() {
        return base.getServerPort();
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return base.getReader();
    }

    @Override
    public String getRemoteAddr() {
        return base.getRemoteAddr();
    }

    @Override
    public String getRemoteHost() {
        return base.getRemoteHost();
    }

    @Override
    public void setAttribute(String name, Object value) {
        base.setAttribute(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        base.removeAttribute(name);
    }

    @Override
    public Locale getLocale() {
        return base.getLocale();
    }

    @Override
    public Enumeration<Locale> getLocales() {
        return base.getLocales();
    }

    @Override
    public boolean isSecure() {
        return base.isSecure();
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        return base.getRequestDispatcher(path);
    }

    @Override
    public String getRealPath(String path) {
        return base.getRealPath(path);
    }

    @Override
    public int getRemotePort() {
        return base.getRemotePort();
    }

    @Override
    public String getLocalName() {
        return base.getLocalName();
    }

    @Override
    public String getLocalAddr() {
        return base.getLocalAddr();
    }

    @Override
    public int getLocalPort() {
        return base.getLocalPort();
    }

    @Override
    public ServletContext getServletContext() {
        return base.getServletContext();
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        return base.startAsync();
    }

    @Override
    public AsyncContext startAsync(
        ServletRequest servletRequest,
        ServletResponse servletResponse
    )
        throws IllegalStateException {
        return base.startAsync(servletRequest, servletResponse);
    }

    @Override
    public boolean isAsyncStarted() {
        return base.isAsyncStarted();
    }

    @Override
    public boolean isAsyncSupported() {
        return base.isAsyncSupported();
    }

    @Override
    public AsyncContext getAsyncContext() {
        return base.getAsyncContext();
    }

    @Override
    public DispatcherType getDispatcherType() {
        return base.getDispatcherType();
    }

    public Request setDispatcherType(DispatcherType type) {
        base.setDispatcherType(type);
        return this;
    }

    public Request setSecure(boolean secure) {
        base.setSecure(secure);
        return this;
    }

    public Request setRemoteAddr(InetSocketAddress addr) {
        base.setRemoteAddr(addr);
        return this;
    }

    public Request setScheme(String scheme) {
        base.setScheme(scheme);
        return this;
    }

    public Request setContentType(String contentType) {
        base.setContentType(contentType);
        return this;
    }

    public Attributes getAttributes() {
        return base.getAttributes();
    }

    public Request setAttributes(Attributes attributes) {
        base.setAttributes(attributes);
        return this;
    }

    public Authentication getAuthentication() {
        return base.getAuthentication();
    }

    public Request setAuthentication(Authentication authentication) {
        base.setAuthentication(authentication);
        return this;
    }

    @Override
    public String getAuthType() {
        return base.getAuthType();
    }

    @Override
    public Cookie[] getCookies() {
        Cookie[] cookies = base.getCookies();
        if (cookies == null) {
            return new Cookie[0];
        }
        return cookies;
    }

    @Override
    public long getDateHeader(String name) {
        return base.getDateHeader(name);
    }

    @Override
    public String getHeader(String name) {
        return base.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        return base.getHeaders(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return base.getHeaderNames();
    }

    @Override
    public int getIntHeader(String name) {
        return base.getIntHeader(name);
    }

    @Override
    public String getMethod() {
        return base.getMethod();
    }

    @Override
    public String getPathInfo() {
        return base.getPathInfo();
    }

    @Override
    public String getPathTranslated() {
        return base.getPathTranslated();
    }

    @Override
    public String getContextPath() {
        return base.getContextPath();
    }

    @Override
    public String getQueryString() {
        return base.getQueryString();
    }

    @Override
    public String getRemoteUser() {
        return base.getRemoteUser();
    }

    @Override
    public boolean isUserInRole(String role) {
        return base.isUserInRole(role);
    }

    @Override
    public Principal getUserPrincipal() {
        return base.getUserPrincipal();
    }

    @Override
    public String getRequestedSessionId() {
        return base.getRequestedSessionId();
    }

    @Override
    public String getRequestURI() {
        return base.getRequestURI();
    }

    @Override
    public StringBuffer getRequestURL() {
        return base.getRequestURL();
    }

    @Override
    public String getServletPath() {
        return base.getServletPath();
    }

    @Override
    public HttpSession getSession(boolean create) {
        return base.getSession(create);
    }

    @Override
    public HttpSession getSession() {
        return base.getSession();
    }

    @Override
    public String changeSessionId() {
        return base.changeSessionId();
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        return base.isRequestedSessionIdValid();
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return base.isRequestedSessionIdFromCookie();
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        return base.isRequestedSessionIdFromURL();
    }

    @Override
    public boolean isRequestedSessionIdFromUrl() {
        return base.isRequestedSessionIdFromUrl();
    }

    @Override
    public boolean authenticate(HttpServletResponse response)
        throws IOException, ServletException {
        return base.authenticate(response);
    }

    @Override
    public void login(String username, String password)
        throws ServletException {
        base.login(username, password);
    }

    @Override
    public void logout() throws ServletException {
        base.logout();
    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        return base.getParts();
    }

    @Override
    public Part getPart(String name) throws IOException, ServletException {
        return base.getPart(name);
    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass)
        throws IOException, ServletException {
        return base.upgrade(handlerClass);
    }

    public Request setRequestedSessionIdFromCookie(
        boolean requestedSessionIdCookie
    ) {
        base.setRequestedSessionIdFromCookie(requestedSessionIdCookie);
        return this;
    }

    public Request setSession(HttpSession session) {
        base.setSession(session);
        return this;
    }

    public Request setServletPath(String servletPath) {
        base.setServletPath(servletPath);
        return this;
    }

    public Request setRequestedSessionId(String requestedSessionId) {
        base.setRequestedSessionId(requestedSessionId);
        return this;
    }

    public Request setQueryString(String queryString) {
        base.setQueryString(queryString);
        return this;
    }

    public Request setContextPath(String contextPath) {
        base.setContextPath(contextPath);
        return this;
    }

    public Request setPathInfo(String pathInfo) {
        base.setPathInfo(pathInfo);
        return this;
    }

    public Request setMethod(String method) {
        base.setMethod(method);
        return this;
    }

    public Request setCookies(Cookie[] cookies) {
        base.setCookies(cookies);
        return this;
    }

    public HttpChannel getHttpChannel() {
        return base.getHttpChannel();
    }

    public long getContentRead() {
        return base.getContentRead();
    }

    public ContextHandler.Context getContext() {
        return base.getContext();
    }

    public Request setContext(ContextHandler.Context context) {
        base.setContext(context);
        return this;
    }

    public int getInputState() {
        return base.getInputState();
    }

    public MultiMap<String> getQueryParameters() {
        return base.getQueryParameters();
    }

    public Request setQueryParameters(MultiMap<String> queryParameters) {
        base.setQueryParameters(queryParameters);
        return this;
    }

    public Request setContentParameters(MultiMap<String> contentParameters) {
        base.setContentParameters(contentParameters);
        return this;
    }

    public Request resetParameters() {
        base.resetParameters();
        return this;
    }

    public HttpVersion getHttpVersion() {
        return base.getHttpVersion();
    }

    public Request setHttpVersion(HttpVersion version) {
        base.setHttpVersion(version);
        return this;
    }

    public String getQueryEncoding() {
        return base.getQueryEncoding();
    }

    public Request setQueryEncoding(String queryEncoding) {
        base.setQueryEncoding(queryEncoding);
        return this;
    }

    public InetSocketAddress getRemoteInetSocketAddress() {
        return base.getRemoteInetSocketAddress();
    }

    public Response getResponse() {
        return base.getResponse();
    }

    public StringBuilder getRootUri() {
        return base.getRootURL();
    }

    public String getServletName() {
        return base.getServletName();
    }

    public ServletResponse getServletResponse() {
        return base.getServletResponse();
    }

    public long getTimeStamp() {
        return base.getTimeStamp();
    }

    public Request setTimeStamp(long ts) {
        base.setTimeStamp(ts);
        return this;
    }

    public HttpURI getHttpUri() {
        return base.getHttpURI();
    }

    public Request setHttpUri(HttpURI uri) {
        base.setHttpURI(uri);
        return this;
    }

    public UserIdentity getUserIdentity() {
        return base.getUserIdentity();
    }

    public UserIdentity getResolvedUserIdentity() {
        return base.getResolvedUserIdentity();
    }

    public UserIdentity.Scope getUserIdentityScope() {
        return base.getUserIdentityScope();
    }

    public Request setUserIdentityScope(UserIdentity.Scope scope) {
        base.setUserIdentityScope(scope);
        return this;
    }

    public boolean isHandled() {
        return base.isHandled();
    }

    public Request setHandled(boolean h) {
        base.setHandled(h);
        return this;
    }

    public Request removeEventListener(EventListener listener) {
        base.removeEventListener(listener);
        return this;
    }

    public Request setAsyncSupported(boolean supported, String source) {
        base.setAsyncSupported(supported, source);
        return this;
    }

    public Request setCharacterEncodingUnchecked(String encoding) {
        base.setCharacterEncodingUnchecked(encoding);
        return this;
    }

    public boolean takeNewContext() {
        return base.takeNewContext();
    }

    public boolean isHead() {
        return base.isHead();
    }

    @Override
    public String toString() {
        return base.toString();
    }

    public Request mergeQueryParameters(
        String oldQuery,
        String newQuery,
        boolean updateQueryString
    ) {
        base.mergeQueryParameters(oldQuery, newQuery, updateQueryString);
        return this;
    }

    public HttpFields getTrailers() {
        return base.getTrailers();
    }

    public boolean isPush() {
        return base.isPush();
    }

    public boolean isPushSupported() {
        return base.isPushSupported();
    }

    public PushBuilder getPushBuilder() {
        return base.getPushBuilder();
    }

    public SessionHandler getSessionHandler() {
        return base.getSessionHandler();
    }

    public Request setSessionHandler(SessionHandler sessionHandler) {
        base.setSessionHandler(sessionHandler);
        return this;
    }

    public String getOriginalUri() {
        return base.getOriginalURI();
    }

    public MetaData.Request getMetaData() {
        return base.getMetaData();
    }

    public Request setMetaData(MetaData.Request request) {
        base.setMetaData(request);
        return this;
    }

    public boolean hasMetaData() {
        return base.hasMetaData();
    }

    public Request setUriPathQuery(String requestUri) {
        base.setURIPathQuery(requestUri);
        return this;
    }

    public Request setAuthority(String host, int port) {
        base.setAuthority(host, port);
        return this;
    }

    public org.eclipse.jetty.server.Request getOriginRequest() {
        return this.base;
    }

    public Request enterSession(HttpSession s) {
        base.enterSession(s);
        return this;
    }

    public ContextHandler.Context getErrorContext() {
        return base.getErrorContext();
    }

    public Request onCompleted() {
        base.onCompleted();
        return this;
    }

    public Request onResponseCommit() {
        base.onResponseCommit();
        return this;
    }

    public HttpSession getSession(SessionHandler sessionHandler) {
        return base.getSession(sessionHandler);
    }

    public Request setAsyncAttributes() {
        base.setAsyncAttributes();
        return this;
    }
}
