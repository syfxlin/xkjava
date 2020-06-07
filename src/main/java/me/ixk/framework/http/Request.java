package me.ixk.framework.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.servlet.*;
import javax.servlet.http.*;
import me.ixk.framework.utils.Helper;
import me.ixk.framework.utils.JSON;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.SessionManager;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.util.Attributes;
import org.eclipse.jetty.util.MultiMap;

public class Request {
    protected org.eclipse.jetty.server.Request _base;
    protected String _body;
    protected JsonNode _parseBody = null;
    protected Map<String, Cookie> _cookies;

    public Request(org.eclipse.jetty.server.Request request) {
        this._base = request;
        this.initRequest();
    }

    protected void initRequest() {
        try {
            this._body =
                _base.getReader().lines().collect(Collectors.joining());
        } catch (IOException e) {
            this._body = null;
        }
        Cookie[] cookies = _base.getCookies();
        this._cookies = new ConcurrentHashMap<>();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                this._cookies.put(cookie.getName(), cookie);
            }
        }
        this.parseBody();
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
        map.putAll(_base.getParameterMap());
        map.putAll(this._cookies);
        try {
            for (Part part : _base.getParts()) {
                map.put(part.getName(), part);
            }
        } catch (IOException | ServletException e) {
            // no code
        }
        if (this._parseBody != null && this._parseBody.isObject()) {
            ObjectNode object = (ObjectNode) this._parseBody;
            for (
                Iterator<Map.Entry<String, JsonNode>> it = object.fields();
                it.hasNext();
            ) {
                Map.Entry<String, JsonNode> entry = it.next();
                map.put(entry.getKey(), entry.getValue());
            }
        } else {
            map.put("_body", this._parseBody);
        }
        return map;
    }

    public JsonNode input() {
        if (this._parseBody != null) {
            return this._parseBody;
        } else {
            return JSON.convertToNode(this.getParameterMap());
        }
    }

    public JsonNode input(String name) {
        if (this._parseBody == null) {
            return JSON.convertToNode(_base.getParameter(name));
        }
        return Helper.dataGet(this._parseBody, name, null);
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

    public boolean has(String name) {
        // TODO: unset
        return false;
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
        return _base.getUri().getPath();
    }

    public String url() {
        HttpURI uri = _base.getUri();
        return uri.getScheme() + "://" + uri.getAuthority() + uri.getPath();
    }

    public String fullUrl() {
        return _base.getUri().toString();
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
        return Pattern.matches(regex, _base.getUri().getPath());
    }

    public boolean pattern(Pattern pattern) {
        return pattern.matcher(_base.getUri().getPath()).matches();
    }

    public boolean ajax() {
        return this.hasHeader("X-Requested-With");
    }

    public boolean isJson() {
        return (
            _base.getContentType() != null &&
            _base.getContentType().startsWith("application/json")
        );
    }

    public Part file(String name) throws IOException, ServletException {
        return _base.getPart(name);
    }

    public boolean hasFile(String name) throws IOException, ServletException {
        return _base.getPart(name) != null;
    }

    public boolean moveFileTo(String name, String path)
        throws IOException, ServletException {
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
    }

    /* ================== */

    public HttpFields getHttpFields() {
        return _base.getHttpFields();
    }

    public HttpInput<?> getHttpInput() {
        return _base.getHttpInput();
    }

    public Request addEventListener(EventListener listener) {
        _base.addEventListener(listener);
        return this;
    }

    public Request extractParameters() {
        _base.extractParameters();
        return this;
    }

    public Request extractFormParameters(MultiMap<String> params) {
        _base.extractFormParameters(params);
        return this;
    }

    public AsyncContext getAsyncContext() {
        return _base.getAsyncContext();
    }

    public HttpChannelState getHttpChannelState() {
        return _base.getHttpChannelState();
    }

    public Object getAttribute(String name) {
        return _base.getAttribute(name);
    }

    public Enumeration<String> getAttributeNames() {
        return _base.getAttributeNames();
    }

    public Attributes getAttributes() {
        return _base.getAttributes();
    }

    public Authentication getAuthentication() {
        return _base.getAuthentication();
    }

    public String getAuthType() {
        return _base.getAuthType();
    }

    public String getCharacterEncoding() {
        return _base.getCharacterEncoding();
    }

    public HttpChannel<?> getHttpChannel() {
        return _base.getHttpChannel();
    }

    public int getContentLength() {
        return _base.getContentLength();
    }

    public long getContentLengthLong() {
        return _base.getContentLengthLong();
    }

    public long getContentRead() {
        return _base.getContentRead();
    }

    public String getContentType() {
        return _base.getContentType();
    }

    public ContextHandler.Context getContext() {
        return _base.getContext();
    }

    public String getContextPath() {
        return _base.getContextPath();
    }

    public Cookie[] getCookies() {
        return _base.getCookies();
    }

    public long getDateHeader(String name) {
        return _base.getDateHeader(name);
    }

    public DispatcherType getDispatcherType() {
        return _base.getDispatcherType();
    }

    public String getHeader(String name) {
        return _base.getHeader(name);
    }

    public Enumeration<String> getHeaderNames() {
        return _base.getHeaderNames();
    }

    public Enumeration<String> getHeaders(String name) {
        return _base.getHeaders(name);
    }

    public int getInputState() {
        return _base.getInputState();
    }

    public ServletInputStream getInputStream() throws IOException {
        return _base.getInputStream();
    }

    public int getIntHeader(String name) {
        return _base.getIntHeader(name);
    }

    public Locale getLocale() {
        return _base.getLocale();
    }

    public Enumeration<Locale> getLocales() {
        return _base.getLocales();
    }

    public String getLocalAddr() {
        return _base.getLocalAddr();
    }

    public String getLocalName() {
        return _base.getLocalName();
    }

    public int getLocalPort() {
        return _base.getLocalPort();
    }

    public String getMethod() {
        return _base.getMethod();
    }

    public String getParameter(String name) {
        return _base.getParameter(name);
    }

    public Map<String, String[]> getParameterMap() {
        return _base.getParameterMap();
    }

    public Enumeration<String> getParameterNames() {
        return _base.getParameterNames();
    }

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

    public String getPathInfo() {
        return _base.getPathInfo();
    }

    public String getPathTranslated() {
        return _base.getPathTranslated();
    }

    public String getProtocol() {
        return _base.getProtocol();
    }

    public HttpVersion getHttpVersion() {
        return _base.getHttpVersion();
    }

    public String getQueryEncoding() {
        return _base.getQueryEncoding();
    }

    public String getQueryString() {
        return _base.getQueryString();
    }

    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new StringReader(this._body));
    }

    public String getRealPath(String path) {
        return _base.getRealPath(path);
    }

    public InetSocketAddress getRemoteInetSocketAddress() {
        return _base.getRemoteInetSocketAddress();
    }

    public String getRemoteAddr() {
        return _base.getRemoteAddr();
    }

    public String getRemoteHost() {
        return _base.getRemoteHost();
    }

    public int getRemotePort() {
        return _base.getRemotePort();
    }

    public String getRemoteUser() {
        return _base.getRemoteUser();
    }

    public RequestDispatcher getRequestDispatcher(String path) {
        return _base.getRequestDispatcher(path);
    }

    public String getRequestedSessionId() {
        return _base.getRequestedSessionId();
    }

    public String getRequestURI() {
        return _base.getRequestURI();
    }

    public StringBuffer getRequestURL() {
        return _base.getRequestURL();
    }

    public Response getResponse() {
        return _base.getResponse();
    }

    public StringBuilder getRootURL() {
        return _base.getRootURL();
    }

    public String getScheme() {
        return _base.getScheme();
    }

    public String getServerName() {
        return _base.getServerName();
    }

    public int getServerPort() {
        return _base.getServerPort();
    }

    public ServletContext getServletContext() {
        return _base.getServletContext();
    }

    public String getServletName() {
        return _base.getServletName();
    }

    public String getServletPath() {
        return _base.getServletPath();
    }

    public ServletResponse getServletResponse() {
        return _base.getServletResponse();
    }

    public String changeSessionId() {
        return _base.changeSessionId();
    }

    public HttpSession getSession() {
        return _base.getSession();
    }

    public HttpSession getSession(boolean create) {
        return _base.getSession(create);
    }

    public org.eclipse.jetty.server.SessionManager getSessionManager() {
        return _base.getSessionManager();
    }

    public long getTimeStamp() {
        return _base.getTimeStamp();
    }

    public HttpURI getUri() {
        return _base.getUri();
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

    public Principal getUserPrincipal() {
        return _base.getUserPrincipal();
    }

    public boolean isHandled() {
        return _base.isHandled();
    }

    public boolean isAsyncStarted() {
        return _base.isAsyncStarted();
    }

    public boolean isAsyncSupported() {
        return _base.isAsyncSupported();
    }

    public boolean isRequestedSessionIdFromCookie() {
        return _base.isRequestedSessionIdFromCookie();
    }

    public boolean isRequestedSessionIdFromUrl() {
        return _base.isRequestedSessionIdFromUrl();
    }

    public boolean isRequestedSessionIdFromURL() {
        return _base.isRequestedSessionIdFromURL();
    }

    public boolean isRequestedSessionIdValid() {
        return _base.isRequestedSessionIdValid();
    }

    public boolean isSecure() {
        return _base.isSecure();
    }

    public Request setSecure(boolean secure) {
        _base.setSecure(secure);
        return this;
    }

    public boolean isUserInRole(String role) {
        return _base.isUserInRole(role);
    }

    public HttpSession recoverNewSession(Object key) {
        return _base.recoverNewSession(key);
    }

    public Request removeAttribute(String name) {
        _base.removeAttribute(name);
        return this;
    }

    public Request removeEventListener(EventListener listener) {
        _base.removeEventListener(listener);
        return this;
    }

    public Request saveNewSession(Object key, HttpSession session) {
        _base.saveNewSession(key, session);
        return this;
    }

    public Request setAsyncSupported(boolean supported) {
        _base.setAsyncSupported(supported);
        return this;
    }

    public Request setAttribute(String name, Object value) {
        _base.setAttribute(name, value);
        return this;
    }

    public Request setAttributes(Attributes attributes) {
        _base.setAttributes(attributes);
        return this;
    }

    public Request setAuthentication(Authentication authentication) {
        _base.setAuthentication(authentication);
        return this;
    }

    public Request setCharacterEncoding(String encoding)
        throws UnsupportedEncodingException {
        _base.setCharacterEncoding(encoding);
        return this;
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

    public Request setMethod(HttpMethod httpMethod, String method) {
        _base.setMethod(httpMethod, method);
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

    public Request setRequestURI(String requestURI) {
        _base.setRequestURI(requestURI);
        return this;
    }

    public Request setScheme(String scheme) {
        _base.setScheme(scheme);
        return this;
    }

    public Request setServerName(String host) {
        _base.setServerName(host);
        return this;
    }

    public Request setServerPort(int port) {
        _base.setServerPort(port);
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

    public Request setSessionManager(SessionManager sessionManager) {
        _base.setSessionManager(sessionManager);
        return this;
    }

    public Request setTimeStamp(long ts) {
        _base.setTimeStamp(ts);
        return this;
    }

    public Request setUri(HttpURI uri) {
        _base.setUri(uri);
        return this;
    }

    public Request setUserIdentityScope(UserIdentity.Scope scope) {
        _base.setUserIdentityScope(scope);
        return this;
    }

    public AsyncContext startAsync() throws IllegalStateException {
        return _base.startAsync();
    }

    public AsyncContext startAsync(
        ServletRequest servletRequest,
        ServletResponse servletResponse
    )
        throws IllegalStateException {
        return _base.startAsync(servletRequest, servletResponse);
    }

    public String toString() {
        return _base.toString();
    }

    public boolean authenticate(HttpServletResponse response)
        throws IOException, ServletException {
        return _base.authenticate(response);
    }

    public Part getPart(String name) throws IOException, ServletException {
        return _base.getPart(name);
    }

    public Collection<Part> getParts() throws IOException, ServletException {
        return _base.getParts();
    }

    public Request login(String username, String password)
        throws ServletException {
        _base.login(username, password);
        return this;
    }

    public Request logout() throws ServletException {
        _base.logout();
        return this;
    }

    public Request mergeQueryParameters(
        String newQuery,
        boolean updateQueryString
    ) {
        _base.mergeQueryParameters(newQuery, updateQueryString);
        return this;
    }

    public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass)
        throws IOException, ServletException {
        return _base.upgrade(handlerClass);
    }

    public org.eclipse.jetty.server.Request getOriginRequest() {
        return this._base;
    }
}
