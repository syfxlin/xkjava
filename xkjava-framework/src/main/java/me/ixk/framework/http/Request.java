/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.http;

import cn.hutool.core.io.IoUtil;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import me.ixk.framework.helpers.Util;
import me.ixk.framework.route.RouteResult;
import me.ixk.framework.utils.JSON;

/**
 * 请求对象
 *
 * @author Otstar Lin
 * @date 2020/10/24 下午 2:06
 */
public class Request extends HttpServletRequestWrapper {
    public static final String REQUEST_BODY = "&body";
    protected volatile String body;
    protected volatile JsonNode parseBody = null;
    protected Map<String, Cookie> cookies;
    protected volatile RouteResult route;

    public Request(final HttpServletRequest request) {
        super(request);
        this.init();
    }

    protected void init() {
        // JSON parse
        if (this.isJson()) {
            try {
                this.body =
                    this.getReader().lines().collect(Collectors.joining());
            } catch (final IOException e) {
                this.body = null;
            }
            this.parseBody = JSON.parse(this.body);
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
            node = JSON.convertToNode(this.getParameterMap());
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
        node = Util.dataGet(node, name);
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

    /* ================ file ============== */

    public Part file(final String name) {
        try {
            return this.getPart(name);
        } catch (final IOException | ServletException e) {
            return null;
        }
    }

    public String fileToString(final String name) {
        return this.fileToString(name, StandardCharsets.UTF_8);
    }

    public String fileToString(final String name, final Charset charset) {
        final Part file = this.file(name);
        if (file == null) {
            return null;
        }
        try {
            return IoUtil
                .getReader(file.getInputStream(), charset)
                .lines()
                .collect(Collectors.joining("\n"));
        } catch (final IOException e) {
            return null;
        }
    }

    public boolean hasFile(final String name) {
        final Part file = this.file(name);
        return file != null && file.getSubmittedFileName() != null;
    }

    public boolean moveFileTo(final String name, final String path) {
        try {
            final Part part = this.file(name);
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
            result = this.file(name);
        }
        if (result == null) {
            result = this.attribute(name);
        }
        if (result == null && REQUEST_BODY.equalsIgnoreCase(name)) {
            result = this.json();
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
        String uri = this.getRequestURI();
        uri = uri.substring(uri.indexOf("://"));
        final int i1 = uri.indexOf("/");
        final int i2 = uri.indexOf("?");
        final int i3 = uri.indexOf("#");
        if (i1 == -1) {
            return "/";
        }
        if (i2 != -1) {
            return uri.substring(i1, i2);
        }
        if (i3 != -1) {
            return uri.substring(i1, i3);
        }
        return uri.substring(i1);
    }

    public String url() {
        final String uri = this.getRequestURI();
        final int i1 = uri.indexOf("?");
        final int i2 = uri.indexOf("#");
        if (i1 != -1) {
            return uri.substring(0, i1);
        }
        if (i2 != -1) {
            return uri.substring(0, i2);
        }
        return uri;
    }

    public String fullUrl() {
        return this.getRequestURI();
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
}
