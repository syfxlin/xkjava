/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.http;

import cn.hutool.core.io.IoUtil;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
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
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;
import me.ixk.framework.annotations.Component;
import me.ixk.framework.annotations.Scope;
import me.ixk.framework.annotations.ScopeType;
import me.ixk.framework.route.RouteResult;
import me.ixk.framework.utils.DataUtils;
import me.ixk.framework.utils.Json;

/**
 * 请求对象
 *
 * @author Otstar Lin
 * @date 2020/10/24 下午 2:06
 */
@Component(name = { "request", "javax.servlet.http.HttpServletRequest" })
@Scope(type = ScopeType.REQUEST)
public class Request extends HttpServletRequestWrapper {

    private static final HttpServletRequest EMPTY = new EmptyRequest();
    public static final String REQUEST_BODY = "&BODY";
    protected volatile String body;
    protected volatile JsonNode parseBody = null;
    protected Map<String, Cookie> cookies;
    protected volatile RouteResult route;

    /**
     * Only used cglib
     */
    @Deprecated
    public Request() {
        super(EMPTY);
    }

    public Request(final HttpServletRequest request) {
        super(request);
        this.init();
    }

    protected void init() {
        // JSON parse
        if (this.isJson()) {
            try {
                this.body =
                    IoUtil.read(
                        this.getInputStream(),
                        this.getCharacterEncoding()
                    );
            } catch (final IOException e) {
                this.body = null;
            }
            this.parseBody = Json.parse(this.body);
        }
        // Cookies
        final Cookie[] cookies = this.getCookies();
        this.cookies = new ConcurrentHashMap<>(cookies.length);
        for (final Cookie cookie : cookies) {
            this.cookies.put(cookie.getName(), cookie);
        }
    }

    private boolean isJson() {
        final String contentType = this.getContentType();
        if (contentType == null) {
            return false;
        }
        final int splitIndex = contentType.indexOf(";");
        final String baseType =
            (
                splitIndex < 0
                    ? contentType
                    : contentType.substring(0, splitIndex)
            ).trim();
        return (
            MimeType.APPLICATION_JSON.is(baseType) ||
            MimeType.TEXT_JSON.is(baseType)
        );
    }

    public RouteResult getRoute() {
        return route;
    }

    public Request setRoute(final RouteResult route) {
        this.route = route;
        return this;
    }

    public String getBody() {
        return body;
    }

    public JsonNode getParseBody() {
        return parseBody;
    }

    protected <T> T getOrDefault(final T result, final T defaultValue) {
        if (result == null) {
            return defaultValue;
        }
        return result;
    }

    /* ================ header ============== */

    public boolean hasHeader(final String name) {
        return this.header(name) != null;
    }

    public String header(final String name) {
        return this.header(name, null);
    }

    public Enumeration<String> headers(final String name) {
        return this.headers(name, null);
    }

    public String header(final String name, final String defaultValue) {
        return this.getOrDefault(this.getHeader(name), defaultValue);
    }

    public Enumeration<String> headers(
        final String name,
        final Enumeration<String> defaultValue
    ) {
        return this.getOrDefault(this.getHeaders(name), defaultValue);
    }

    public String header(final HttpHeader header) {
        return this.header(header.asString());
    }

    public Enumeration<String> headers(final HttpHeader header) {
        return this.headers(header.asString());
    }

    public String header(final HttpHeader header, final String defaultValue) {
        return this.header(header.asString(), defaultValue);
    }

    public Enumeration<String> headers(
        final HttpHeader header,
        final Enumeration<String> defaultValue
    ) {
        return this.headers(header.asString(), defaultValue);
    }

    /* ================ query ============== */

    public boolean hasQuery(final String name) {
        return this.query(name) != null;
    }

    public String query(final String name) {
        return this.query(name, null);
    }

    public String query(final String name, final String defaultValue) {
        return this.getOrDefault(this.getParameter(name), defaultValue);
    }

    public String[] queries(final String name) {
        return this.queries(name, null);
    }

    public String[] queries(final String name, final String[] defaultValue) {
        return this.getOrDefault(this.getParameterValues(name), defaultValue);
    }

    /* ================ input ============== */

    public boolean hasInput(final String name) {
        return this.input(name) != null;
    }

    public JsonNode json() {
        final JsonNode node;
        if (this.parseBody == null) {
            node =
                Json.convertToNode(
                    this.getParameterMap()
                        .entrySet()
                        .stream()
                        .collect(
                            Collectors.toMap(
                                Entry::getKey,
                                e ->
                                    e.getValue().length == 1
                                        ? e.getValue()[0]
                                        : e.getValue()
                            )
                        )
                );
        } else {
            node = this.parseBody;
        }
        return node == null || node.isNull() ? null : node;
    }

    public JsonNode input() {
        return this.json();
    }

    public JsonNode input(final String name) {
        return this.input(name, null);
    }

    public JsonNode input(final String name, final JsonNode defaultValue) {
        JsonNode node = this.json();
        if (REQUEST_BODY.equals(name)) {
            return node;
        }
        node = DataUtils.dataGet(node, name);
        return node == null || node.isNull() ? defaultValue : node;
    }

    /* ================ route ============== */

    public boolean hasRoute(final String name) {
        return this.route(name) != null;
    }

    public String route(final String name) {
        return this.route(name, null);
    }

    public String route(final String name, final String defaultValue) {
        return this.route.getParams().getOrDefault(name, defaultValue);
    }

    /* ================ cookie ============== */

    public boolean hasCookie(final String name) {
        return this.cookie(name) != null;
    }

    public Cookie cookie(final String name) {
        return this.cookies.get(name);
    }

    public Cookie cookie(final String name, final Cookie defaultValue) {
        return this.cookies.getOrDefault(name, defaultValue);
    }

    /* ================ session ============== */

    public boolean hasSession(final String name) {
        return this.session(name) != null;
    }

    public HttpSession session() {
        return this.getSession();
    }

    public Object session(final String name) {
        return this.session(name, null);
    }

    public Object session(final String name, final Object defaultValue) {
        final HttpSession session = this.session();
        if (session == null) {
            return null;
        }
        return this.getOrDefault(session.getAttribute(name), defaultValue);
    }

    /* ================ part ============== */

    public Part part(final String name) {
        try {
            return this.getPart(name);
        } catch (final IOException | ServletException e) {
            return null;
        }
    }

    public String partToString(final String name) {
        return this.partToString(name, StandardCharsets.UTF_8);
    }

    public String partToString(final String name, final Charset charset) {
        final Part file = this.part(name);
        if (file == null) {
            return null;
        }
        try {
            return IoUtil.read(file.getInputStream(), charset);
        } catch (final IOException e) {
            return null;
        }
    }

    public boolean hasPart(final String name) {
        final Part file = this.part(name);
        return file != null && file.getSubmittedFileName() != null;
    }

    public boolean movePartTo(final String name, final String path) {
        try {
            final Part part = this.part(name);
            if (part == null) {
                return false;
            }
            final File file = new File(path);
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
        } catch (final IOException e) {
            return false;
        }
    }

    /* ================ attribute ============== */

    public Object attribute(String name) {
        return this.attribute(name, null);
    }

    public Object attribute(String name, Object defaultValue) {
        return this.getOrDefault(this.getAttribute(name), defaultValue);
    }

    public boolean hasAttribute(String name) {
        return this.attribute(name) != null;
    }

    /* ================ all ============== */

    public Object all(final String name) {
        return this.all(name, null);
    }

    public Object all(final String name, final Object defaultValue) {
        Object result = this.queries(name);
        if (result != null && ((String[]) result).length == 1) {
            result = ((String[]) result)[0];
        }
        if (result == null) {
            result = this.input(name);
        }
        if (result == null) {
            result = this.route(name);
        }
        if (result == null) {
            result = this.cookie(name);
        }
        if (result == null) {
            result = this.session(name);
        }
        if (result == null) {
            result = this.part(name);
        }
        if (result == null) {
            result = this.attribute(name);
        }
        if (result == null) {
            result = defaultValue;
        }
        return result;
    }

    public boolean has(final String name) {
        return this.all(name) != null;
    }

    /* ================ other ============== */

    public String path() {
        return this.getRequestURI();
    }

    public String url() {
        return this.getRequestURL().toString();
    }

    public String method() {
        return this.getMethod();
    }

    public boolean isMethod(final String method) {
        return this.method().equalsIgnoreCase(method);
    }

    public boolean isMethod(final HttpMethod method) {
        if (method == null) {
            return false;
        }
        return method.is(this.method());
    }

    public boolean pattern(final String regex) {
        return Pattern.matches(regex, this.path());
    }

    public boolean pattern(final Pattern pattern) {
        if (pattern == null) {
            return false;
        }
        return pattern.matcher(this.path()).matches();
    }

    public boolean ajax() {
        final String xrw = this.header("X-Requested-With");
        final String acc = this.header("Accept");
        return (
            "XMLHttpRequest".equals(xrw) ||
            (
                acc != null &&
                acc.startsWith(MimeType.APPLICATION_JSON.asString())
            )
        );
    }

    @Override
    public Cookie[] getCookies() {
        final Cookie[] cookies = super.getCookies();
        return cookies == null ? new Cookie[0] : cookies;
    }

    private static class EmptyRequest implements HttpServletRequest {

        @Override
        public String getAuthType() {
            return null;
        }

        @Override
        public Cookie[] getCookies() {
            return new Cookie[0];
        }

        @Override
        public long getDateHeader(String name) {
            return 0;
        }

        @Override
        public String getHeader(String name) {
            return null;
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            return null;
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            return null;
        }

        @Override
        public int getIntHeader(String name) {
            return 0;
        }

        @Override
        public String getMethod() {
            return null;
        }

        @Override
        public String getPathInfo() {
            return null;
        }

        @Override
        public String getPathTranslated() {
            return null;
        }

        @Override
        public String getContextPath() {
            return null;
        }

        @Override
        public String getQueryString() {
            return null;
        }

        @Override
        public String getRemoteUser() {
            return null;
        }

        @Override
        public boolean isUserInRole(String role) {
            return false;
        }

        @Override
        public Principal getUserPrincipal() {
            return null;
        }

        @Override
        public String getRequestedSessionId() {
            return null;
        }

        @Override
        public String getRequestURI() {
            return null;
        }

        @Override
        public StringBuffer getRequestURL() {
            return null;
        }

        @Override
        public String getServletPath() {
            return null;
        }

        @Override
        public HttpSession getSession(boolean create) {
            return null;
        }

        @Override
        public HttpSession getSession() {
            return null;
        }

        @Override
        public String changeSessionId() {
            return null;
        }

        @Override
        public boolean isRequestedSessionIdValid() {
            return false;
        }

        @Override
        public boolean isRequestedSessionIdFromCookie() {
            return false;
        }

        @Override
        public boolean isRequestedSessionIdFromURL() {
            return false;
        }

        @Override
        public boolean isRequestedSessionIdFromUrl() {
            return false;
        }

        @Override
        public boolean authenticate(HttpServletResponse response)
            throws IOException, ServletException {
            return false;
        }

        @Override
        public void login(String username, String password)
            throws ServletException {}

        @Override
        public void logout() throws ServletException {}

        @Override
        public Collection<Part> getParts()
            throws IOException, ServletException {
            return null;
        }

        @Override
        public Part getPart(String name) throws IOException, ServletException {
            return null;
        }

        @Override
        public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass)
            throws IOException, ServletException {
            return null;
        }

        @Override
        public Object getAttribute(String name) {
            return null;
        }

        @Override
        public Enumeration<String> getAttributeNames() {
            return null;
        }

        @Override
        public String getCharacterEncoding() {
            return null;
        }

        @Override
        public void setCharacterEncoding(String env)
            throws UnsupportedEncodingException {}

        @Override
        public int getContentLength() {
            return 0;
        }

        @Override
        public long getContentLengthLong() {
            return 0;
        }

        @Override
        public String getContentType() {
            return null;
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            return null;
        }

        @Override
        public String getParameter(String name) {
            return null;
        }

        @Override
        public Enumeration<String> getParameterNames() {
            return null;
        }

        @Override
        public String[] getParameterValues(String name) {
            return new String[0];
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            return null;
        }

        @Override
        public String getProtocol() {
            return null;
        }

        @Override
        public String getScheme() {
            return null;
        }

        @Override
        public String getServerName() {
            return null;
        }

        @Override
        public int getServerPort() {
            return 0;
        }

        @Override
        public BufferedReader getReader() throws IOException {
            return null;
        }

        @Override
        public String getRemoteAddr() {
            return null;
        }

        @Override
        public String getRemoteHost() {
            return null;
        }

        @Override
        public void setAttribute(String name, Object o) {}

        @Override
        public void removeAttribute(String name) {}

        @Override
        public Locale getLocale() {
            return null;
        }

        @Override
        public Enumeration<Locale> getLocales() {
            return null;
        }

        @Override
        public boolean isSecure() {
            return false;
        }

        @Override
        public RequestDispatcher getRequestDispatcher(String path) {
            return null;
        }

        @Override
        public String getRealPath(String path) {
            return null;
        }

        @Override
        public int getRemotePort() {
            return 0;
        }

        @Override
        public String getLocalName() {
            return null;
        }

        @Override
        public String getLocalAddr() {
            return null;
        }

        @Override
        public int getLocalPort() {
            return 0;
        }

        @Override
        public ServletContext getServletContext() {
            return null;
        }

        @Override
        public AsyncContext startAsync() throws IllegalStateException {
            return null;
        }

        @Override
        public AsyncContext startAsync(
            ServletRequest servletRequest,
            ServletResponse servletResponse
        ) throws IllegalStateException {
            return null;
        }

        @Override
        public boolean isAsyncStarted() {
            return false;
        }

        @Override
        public boolean isAsyncSupported() {
            return false;
        }

        @Override
        public AsyncContext getAsyncContext() {
            return null;
        }

        @Override
        public DispatcherType getDispatcherType() {
            return null;
        }
    }
}
