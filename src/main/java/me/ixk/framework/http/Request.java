/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.http;

import cn.hutool.core.io.IoUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.servlet.*;
import javax.servlet.http.*;
import me.ixk.framework.helpers.Util;
import me.ixk.framework.route.RouteResult;
import me.ixk.framework.utils.JSON;
import org.eclipse.jetty.http.*;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.util.Attributes;
import org.eclipse.jetty.util.MultiMap;

public class Request implements HttpServletRequest {
    public static final String REQUEST_BODY = "&body";

    protected org.eclipse.jetty.server.Request _base;
    protected String _body;
    protected JsonNode _parseBody = null;
    protected Map<String, Cookie> _cookies;
    protected RouteResult _route;

    @Deprecated
    public Request() {
        // only used cglib
    }

    public Request(org.eclipse.jetty.server.Request request) {
        this._base = request;
        this.initRequest();
    }

    protected void initRequest() {
        // 如果是 JSON 就解析 JSON，一旦解析后就无法使用 getOutStream
        String baseType = HttpFields.valueParameters(
            this._base.getContentType(),
            null
        );
        if (
            MimeTypes.Type.APPLICATION_JSON.is(baseType) ||
            MimeTypes.Type.TEXT_JSON.is(baseType)
        ) {
            try {
                this._body =
                    _base.getReader().lines().collect(Collectors.joining());
            } catch (IOException e) {
                this._body = null;
            }
            this.parseBody();
        }
        Cookie[] cookies = _base.getCookies();
        this._cookies = new ConcurrentHashMap<>();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                this._cookies.put(cookie.getName(), cookie);
            }
        }
    }

    public RouteResult getRoute() {
        return _route;
    }

    public Request setRoute(RouteResult route) {
        this._route = route;
        return this;
    }

    protected <T> T getOrDefault(T result, T _default) {
        if (result == null) {
            return _default;
        }
        return result;
    }

    protected void parseBody() {
        if (!this.isJson()) {
            return;
        }
        this._parseBody = JSON.parse(this._body);
    }

    public String getBody() {
        return _body;
    }

    public Request setBody(String body) {
        this._body = body;
        return this;
    }

    public JsonNode getParseBody() {
        return _parseBody;
    }

    public Request setParseBody(JsonNode parseBody) {
        this._parseBody = parseBody;
        return this;
    }

    public boolean hasHeader(String name) {
        return this.getHeader(name) != null;
    }

    public String header(String name) {
        return _base.getHeader(name);
    }

    public Enumeration<String> headers(String name) {
        return _base.getHeaders(name);
    }

    public String header(String name, String _default) {
        return this.getOrDefault(_base.getHeader(name), _default);
    }

    public Enumeration<String> headers(
        String name,
        Enumeration<String> _default
    ) {
        return this.getOrDefault(_base.getHeaders(name), _default);
    }

    public Map<String, Object> all() {
        Map<String, Object> map = new ConcurrentHashMap<>();
        for (Map.Entry<String, String[]> entry : _base
            .getParameterMap()
            .entrySet()) {
            String[] value = entry.getValue();
            map.put(entry.getKey(), value.length == 1 ? value[0] : value);
        }
        map.putAll(this._route.getParams());
        map.putAll(this._cookies);
        try {
            for (Part part : _base.getParts()) {
                map.put(part.getName(), part);
            }
        } catch (IOException | ServletException e) {
            // no code
        }
        if (this._parseBody != null) {
            if (this._parseBody.isObject()) {
                ObjectNode object = (ObjectNode) this._parseBody;
                for (
                    Iterator<Map.Entry<String, JsonNode>> it = object.fields();
                    it.hasNext();
                ) {
                    Map.Entry<String, JsonNode> entry = it.next();
                    map.put(entry.getKey(), entry.getValue());
                }
            } else {
                map.put(REQUEST_BODY, this._parseBody);
            }
        }
        return map;
    }

    public Object all(String name) {
        return this.all(name, null);
    }

    public Object all(String name, Object _default) {
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
        }
        if (object == null) {
            object = _default;
        }
        return object;
    }

    public boolean has(String name) {
        if (_base.getParameterMap().containsKey(name)) {
            return true;
        }
        if (_route.getParams().containsKey(name)) {
            return true;
        }
        if (_cookies.containsKey(name)) {
            return true;
        }
        try {
            if (_base.getPart(name) != null) {
                return true;
            }
        } catch (IOException | ServletException e) {
            // no code
        }
        if (this._parseBody != null) {
            if (this._parseBody.isObject()) {
                ObjectNode object = (ObjectNode) this._parseBody;
                return object.has(name);
            } else {
                return name.equals(REQUEST_BODY);
            }
        }
        return false;
    }

    public JsonNode input() {
        JsonNode node;
        if (this._parseBody != null) {
            node = this._parseBody;
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
        if (this._parseBody == null) {
            node = JSON.convertToNode(_base.getParameter(name));
        } else {
            node =
                Util.dataGet(
                    this._parseBody,
                    name,
                    NullNode.getInstance(),
                    JsonNode.class
                );
        }
        if (node.isNull()) {
            node = null;
        }
        return node;
    }

    public JsonNode input(String name, JsonNode _default) {
        return this.getOrDefault(this.input(name), _default);
    }

    public MultiMap<String> query() {
        return _base.getQueryParameters();
    }

    public String query(String name) {
        return _base.getQueryParameters().getValue(name, 0);
    }

    public String query(String name, String _default) {
        return this.getOrDefault(
                _base.getQueryParameters().getValue(name, 0),
                _default
            );
    }

    public RouteResult route() {
        return this._route;
    }

    public String route(String name) {
        return this._route.getParams().get(name);
    }

    public String route(String name, String _default) {
        return this._route.getParams().getOrDefault(name, _default);
    }

    public Cookie cookie(String name) {
        return this._cookies.get(name);
    }

    public Cookie cookie(String name, Cookie _default) {
        return this._cookies.getOrDefault(name, _default);
    }

    public HttpSession session() {
        return _base.getSession();
    }

    public Object session(String name) {
        return _base.getSession().getAttribute(name);
    }

    public Object session(String name, Object _default) {
        return this.getOrDefault(
                _base.getSession().getAttribute(name),
                _default
            );
    }

    public String path() {
        return _base.getHttpURI().getPath();
    }

    public String url() {
        HttpURI uri = _base.getHttpURI();
        return uri.getScheme() + "://" + uri.getAuthority() + uri.getPath();
    }

    public String fullUrl() {
        return _base.getHttpURI().toString();
    }

    public String method() {
        return _base.getMethod();
    }

    public boolean isMethod(String method) {
        return _base.getMethod().equalsIgnoreCase(method);
    }

    public boolean isMethod(HttpMethod method) {
        return method.is(_base.getMethod());
    }

    public boolean pattern(String regex) {
        return Pattern.matches(regex, _base.getHttpURI().getPath());
    }

    public boolean pattern(Pattern pattern) {
        return pattern.matcher(_base.getHttpURI().getPath()).matches();
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
            _base.getContentType() != null &&
            _base
                .getContentType()
                .startsWith(MimeTypes.Type.APPLICATION_JSON.asString())
        );
    }

    public Part file(String name) {
        try {
            return _base.getPart(name);
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
                _base.getPart(name) != null ||
                _base.getPart(name).getSubmittedFileName() != null
            );
        } catch (IOException | ServletException e) {
            return false;
        }
    }

    public boolean moveFileTo(String name, String path) {
        try {
            Part part = _base.getPart(name);
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
        return _base.getHttpFields();
    }

    public HttpInput getHttpInput() {
        return _base.getHttpInput();
    }

    public Request addEventListener(EventListener listener) {
        _base.addEventListener(listener);
        return this;
    }

    public Request extractFormParameters(MultiMap<String> params) {
        _base.extractFormParameters(params);
        return this;
    }

    @Override
    public AsyncContext getAsyncContext() {
        return _base.getAsyncContext();
    }

    public HttpChannelState getHttpChannelState() {
        return _base.getHttpChannelState();
    }

    @Override
    public Object getAttribute(String name) {
        return _base.getAttribute(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return _base.getAttributeNames();
    }

    public Attributes getAttributes() {
        return _base.getAttributes();
    }

    public Authentication getAuthentication() {
        return _base.getAuthentication();
    }

    @Override
    public String getAuthType() {
        return _base.getAuthType();
    }

    @Override
    public String getCharacterEncoding() {
        return _base.getCharacterEncoding();
    }

    public HttpChannel getHttpChannel() {
        return _base.getHttpChannel();
    }

    @Override
    public int getContentLength() {
        return _base.getContentLength();
    }

    @Override
    public long getContentLengthLong() {
        return _base.getContentLengthLong();
    }

    public long getContentRead() {
        return _base.getContentRead();
    }

    @Override
    public String getContentType() {
        return _base.getContentType();
    }

    public ContextHandler.Context getContext() {
        return _base.getContext();
    }

    @Override
    public String getContextPath() {
        return _base.getContextPath();
    }

    @Override
    public Cookie[] getCookies() {
        Cookie[] cookies = _base.getCookies();
        if (cookies == null) {
            return new Cookie[0];
        }
        return cookies;
    }

    @Override
    public long getDateHeader(String name) {
        return _base.getDateHeader(name);
    }

    @Override
    public DispatcherType getDispatcherType() {
        return _base.getDispatcherType();
    }

    @Override
    public String getHeader(String name) {
        return _base.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return _base.getHeaderNames();
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        return _base.getHeaders(name);
    }

    public int getInputState() {
        return _base.getInputState();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return _base.getInputStream();
    }

    @Override
    public int getIntHeader(String name) {
        return _base.getIntHeader(name);
    }

    @Override
    public Locale getLocale() {
        return _base.getLocale();
    }

    @Override
    public Enumeration<Locale> getLocales() {
        return _base.getLocales();
    }

    @Override
    public String getLocalAddr() {
        return _base.getLocalAddr();
    }

    @Override
    public String getLocalName() {
        return _base.getLocalName();
    }

    @Override
    public int getLocalPort() {
        return _base.getLocalPort();
    }

    @Override
    public String getMethod() {
        return _base.getMethod();
    }

    @Override
    public String getParameter(String name) {
        return _base.getParameter(name);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return _base.getParameterMap();
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return _base.getParameterNames();
    }

    @Override
    public String[] getParameterValues(String name) {
        return _base.getParameterValues(name);
    }

    public MultiMap<String> getQueryParameters() {
        return _base.getQueryParameters();
    }

    public Request setQueryParameters(MultiMap<String> queryParameters) {
        _base.setQueryParameters(queryParameters);
        return this;
    }

    public Request setContentParameters(MultiMap<String> contentParameters) {
        _base.setContentParameters(contentParameters);
        return this;
    }

    public Request resetParameters() {
        _base.resetParameters();
        return this;
    }

    @Override
    public String getPathInfo() {
        return _base.getPathInfo();
    }

    @Override
    public String getPathTranslated() {
        return _base.getPathTranslated();
    }

    @Override
    public String getProtocol() {
        return _base.getProtocol();
    }

    public HttpVersion getHttpVersion() {
        return _base.getHttpVersion();
    }

    public String getQueryEncoding() {
        return _base.getQueryEncoding();
    }

    @Override
    public String getQueryString() {
        return _base.getQueryString();
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return _base.getReader();
    }

    @Override
    public String getRealPath(String path) {
        return _base.getRealPath(path);
    }

    public InetSocketAddress getRemoteInetSocketAddress() {
        return _base.getRemoteInetSocketAddress();
    }

    @Override
    public String getRemoteAddr() {
        return _base.getRemoteAddr();
    }

    @Override
    public String getRemoteHost() {
        return _base.getRemoteHost();
    }

    @Override
    public int getRemotePort() {
        return _base.getRemotePort();
    }

    @Override
    public String getRemoteUser() {
        return _base.getRemoteUser();
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        return _base.getRequestDispatcher(path);
    }

    @Override
    public String getRequestedSessionId() {
        return _base.getRequestedSessionId();
    }

    @Override
    public String getRequestURI() {
        return _base.getRequestURI();
    }

    @Override
    public StringBuffer getRequestURL() {
        return _base.getRequestURL();
    }

    public Response getResponse() {
        return _base.getResponse();
    }

    public StringBuilder getRootURL() {
        return _base.getRootURL();
    }

    @Override
    public String getScheme() {
        return _base.getScheme();
    }

    @Override
    public String getServerName() {
        return _base.getServerName();
    }

    @Override
    public int getServerPort() {
        return _base.getServerPort();
    }

    @Override
    public ServletContext getServletContext() {
        return _base.getServletContext();
    }

    public String getServletName() {
        return _base.getServletName();
    }

    @Override
    public String getServletPath() {
        return _base.getServletPath();
    }

    public ServletResponse getServletResponse() {
        return _base.getServletResponse();
    }

    @Override
    public String changeSessionId() {
        return _base.changeSessionId();
    }

    @Override
    public HttpSession getSession() {
        return _base.getSession();
    }

    @Override
    public HttpSession getSession(boolean create) {
        return _base.getSession(create);
    }

    public long getTimeStamp() {
        return _base.getTimeStamp();
    }

    public HttpURI getHttpURI() {
        return _base.getHttpURI();
    }

    public UserIdentity getUserIdentity() {
        return _base.getUserIdentity();
    }

    public UserIdentity getResolvedUserIdentity() {
        return _base.getResolvedUserIdentity();
    }

    public UserIdentity.Scope getUserIdentityScope() {
        return _base.getUserIdentityScope();
    }

    @Override
    public Principal getUserPrincipal() {
        return _base.getUserPrincipal();
    }

    public boolean isHandled() {
        return _base.isHandled();
    }

    @Override
    public boolean isAsyncStarted() {
        return _base.isAsyncStarted();
    }

    @Override
    public boolean isAsyncSupported() {
        return _base.isAsyncSupported();
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return _base.isRequestedSessionIdFromCookie();
    }

    @Override
    public boolean isRequestedSessionIdFromUrl() {
        return _base.isRequestedSessionIdFromUrl();
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        return _base.isRequestedSessionIdFromURL();
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        return _base.isRequestedSessionIdValid();
    }

    @Override
    public boolean isSecure() {
        return _base.isSecure();
    }

    public Request setSecure(boolean secure) {
        _base.setSecure(secure);
        return this;
    }

    @Override
    public boolean isUserInRole(String role) {
        return _base.isUserInRole(role);
    }

    @Override
    public void removeAttribute(String name) {
        _base.removeAttribute(name);
    }

    public Request removeEventListener(EventListener listener) {
        _base.removeEventListener(listener);
        return this;
    }

    public Request setAsyncSupported(boolean supported, String source) {
        _base.setAsyncSupported(supported, source);
        return this;
    }

    @Override
    public void setAttribute(String name, Object value) {
        _base.setAttribute(name, value);
    }

    public Request setAttributes(Attributes attributes) {
        _base.setAttributes(attributes);
        return this;
    }

    public Request setAuthentication(Authentication authentication) {
        _base.setAuthentication(authentication);
        return this;
    }

    @Override
    public void setCharacterEncoding(String encoding)
        throws UnsupportedEncodingException {
        _base.setCharacterEncoding(encoding);
    }

    public Request setCharacterEncodingUnchecked(String encoding) {
        _base.setCharacterEncodingUnchecked(encoding);
        return this;
    }

    public Request setContentType(String contentType) {
        _base.setContentType(contentType);
        return this;
    }

    public Request setContext(ContextHandler.Context context) {
        _base.setContext(context);
        return this;
    }

    public boolean takeNewContext() {
        return _base.takeNewContext();
    }

    public Request setContextPath(String contextPath) {
        _base.setContextPath(contextPath);
        return this;
    }

    public Request setCookies(Cookie[] cookies) {
        _base.setCookies(cookies);
        return this;
    }

    public Request setDispatcherType(DispatcherType type) {
        _base.setDispatcherType(type);
        return this;
    }

    public Request setHandled(boolean h) {
        _base.setHandled(h);
        return this;
    }

    public Request setMethod(String method) {
        _base.setMethod(method);
        return this;
    }

    public boolean isHead() {
        return _base.isHead();
    }

    public Request setPathInfo(String pathInfo) {
        _base.setPathInfo(pathInfo);
        return this;
    }

    public Request setHttpVersion(HttpVersion version) {
        _base.setHttpVersion(version);
        return this;
    }

    public Request setQueryEncoding(String queryEncoding) {
        _base.setQueryEncoding(queryEncoding);
        return this;
    }

    public Request setQueryString(String queryString) {
        _base.setQueryString(queryString);
        return this;
    }

    public Request setRemoteAddr(InetSocketAddress addr) {
        _base.setRemoteAddr(addr);
        return this;
    }

    public Request setRequestedSessionId(String requestedSessionId) {
        _base.setRequestedSessionId(requestedSessionId);
        return this;
    }

    public Request setRequestedSessionIdFromCookie(
        boolean requestedSessionIdCookie
    ) {
        _base.setRequestedSessionIdFromCookie(requestedSessionIdCookie);
        return this;
    }

    public Request setScheme(String scheme) {
        _base.setScheme(scheme);
        return this;
    }

    public Request setServletPath(String servletPath) {
        _base.setServletPath(servletPath);
        return this;
    }

    public Request setSession(HttpSession session) {
        _base.setSession(session);
        return this;
    }

    public Request setTimeStamp(long ts) {
        _base.setTimeStamp(ts);
        return this;
    }

    public Request setHttpURI(HttpURI uri) {
        _base.setHttpURI(uri);
        return this;
    }

    public Request setUserIdentityScope(UserIdentity.Scope scope) {
        _base.setUserIdentityScope(scope);
        return this;
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        return _base.startAsync();
    }

    @Override
    public AsyncContext startAsync(
        ServletRequest servletRequest,
        ServletResponse servletResponse
    )
        throws IllegalStateException {
        return _base.startAsync(servletRequest, servletResponse);
    }

    @Override
    public String toString() {
        return _base.toString();
    }

    @Override
    public boolean authenticate(HttpServletResponse response)
        throws IOException, ServletException {
        return _base.authenticate(response);
    }

    @Override
    public Part getPart(String name) throws IOException, ServletException {
        return _base.getPart(name);
    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        return _base.getParts();
    }

    @Override
    public void login(String username, String password)
        throws ServletException {
        _base.login(username, password);
    }

    @Override
    public void logout() throws ServletException {
        _base.logout();
    }

    public Request mergeQueryParameters(
        String oldQuery,
        String newQuery,
        boolean updateQueryString
    ) {
        _base.mergeQueryParameters(oldQuery, newQuery, updateQueryString);
        return this;
    }

    public HttpFields getTrailers() {
        return _base.getTrailers();
    }

    public boolean isPush() {
        return _base.isPush();
    }

    public boolean isPushSupported() {
        return _base.isPushSupported();
    }

    public PushBuilder getPushBuilder() {
        return _base.getPushBuilder();
    }

    public SessionHandler getSessionHandler() {
        return _base.getSessionHandler();
    }

    public String getOriginalURI() {
        return _base.getOriginalURI();
    }

    public Request setMetaData(MetaData.Request request) {
        _base.setMetaData(request);
        return this;
    }

    public MetaData.Request getMetaData() {
        return _base.getMetaData();
    }

    public boolean hasMetaData() {
        return _base.hasMetaData();
    }

    public Request setURIPathQuery(String requestURI) {
        _base.setURIPathQuery(requestURI);
        return this;
    }

    public Request setAuthority(String host, int port) {
        _base.setAuthority(host, port);
        return this;
    }

    public Request setSessionHandler(SessionHandler sessionHandler) {
        _base.setSessionHandler(sessionHandler);
        return this;
    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass)
        throws IOException, ServletException {
        return _base.upgrade(handlerClass);
    }

    public org.eclipse.jetty.server.Request getOriginRequest() {
        return this._base;
    }

    public Request setOriginRequest(org.eclipse.jetty.server.Request request) {
        this._base = request;
        return this;
    }

    public Request enterSession(HttpSession s) {
        _base.enterSession(s);
        return this;
    }

    public ContextHandler.Context getErrorContext() {
        return _base.getErrorContext();
    }

    public Request onCompleted() {
        _base.onCompleted();
        return this;
    }

    public Request onResponseCommit() {
        _base.onResponseCommit();
        return this;
    }

    public HttpSession getSession(SessionHandler sessionHandler) {
        return _base.getSession(sessionHandler);
    }

    public Request setAsyncAttributes() {
        _base.setAsyncAttributes();
        return this;
    }
}
