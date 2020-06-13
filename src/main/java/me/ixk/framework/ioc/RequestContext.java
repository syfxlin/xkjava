package me.ixk.framework.ioc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import me.ixk.framework.http.CookieManager;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;
import me.ixk.framework.http.SessionManager;
import me.ixk.framework.kernel.Auth;
import me.ixk.framework.servlet.DispatcherServlet;

/**
 * 存储 Request 请求内容的 Context，线程安全
 */
public class RequestContext implements Attributes {
    private static final ThreadLocal<RequestContext> requestAttributes = new ThreadLocal<>();

    private static final ThreadLocal<RequestContext> inheritableRequestAttributes = new InheritableThreadLocal<>();

    private final Map<String, Object> attributes = new ConcurrentHashMap<>();

    public static RequestContext create() {
        RequestContext requestContext = new RequestContext();
        setAttributes(requestContext);
        return requestContext;
    }

    public static void resetAttributes() {
        requestAttributes.remove();
    }

    public static void setAttributes(RequestContext attributes) {
        setAttributes(attributes, false);
    }

    public static void setAttributes(
        RequestContext attributes,
        boolean inheritable
    ) {
        if (attributes == null) {
            resetAttributes();
        } else {
            if (inheritable) {
                inheritableRequestAttributes.set(attributes);
                requestAttributes.remove();
            } else {
                requestAttributes.set(attributes);
                inheritableRequestAttributes.remove();
            }
        }
    }

    public static RequestContext getAttributes() {
        RequestContext attributes = requestAttributes.get();
        if (attributes == null) {
            attributes = inheritableRequestAttributes.get();
        }
        return attributes;
    }

    public static RequestContext currentAttributes()
        throws IllegalStateException {
        RequestContext attributes = getAttributes();
        if (attributes == null) {
            throw new NullPointerException(
                "Current request attributes is null"
            );
        }
        return attributes;
    }

    public DispatcherServlet getDispatcherServlet() {
        return this.getObject(DispatcherServlet.class);
    }

    public HttpServlet getHttpServlet() {
        return this.getObject(HttpServlet.class);
    }

    public Request getRequest() {
        return this.getObject(Request.class);
    }

    public HttpServletRequest getHttpServletRequest() {
        return this.getObject(HttpServletRequest.class);
    }

    public Response getResponse() {
        return this.getObject(Response.class);
    }

    public HttpServletResponse getHttpServletResponse() {
        return this.getObject(HttpServletResponse.class);
    }

    public CookieManager getCookieManager() {
        return this.getObject(CookieManager.class);
    }

    public SessionManager getSessionManager() {
        return this.getObject(SessionManager.class);
    }

    public Auth getAuth() {
        return this.getObject(Auth.class);
    }

    public void setDispatcherServlet(DispatcherServlet dispatcherServlet) {
        this.setObject(DispatcherServlet.class, dispatcherServlet);
    }

    public void setHttpServlet(HttpServlet httpServlet) {
        this.setObject(HttpServlet.class, httpServlet);
    }

    public void setRequest(Request request) {
        this.setObject(Request.class, request);
    }

    public void setHttpServletRequest(HttpServletRequest request) {
        this.setObject(HttpServletRequest.class, request);
    }

    public void setResponse(Response response) {
        this.setObject(Response.class, response);
    }

    public void setHttpServletResponse(HttpServletResponse response) {
        this.setObject(HttpServletResponse.class, response);
    }

    public void setCookieManager(CookieManager cookieManager) {
        this.setObject(CookieManager.class, cookieManager);
    }

    public void setSessionManager(SessionManager sessionManager) {
        this.setObject(SessionManager.class, sessionManager);
    }

    public void setAuth(Auth auth) {
        this.setObject(Auth.class, auth);
    }

    public void setHandler(String handler) {
        this.setAttribute("handler", handler);
    }

    public String getHandler() {
        return this.getAttribute("handler", String.class);
    }

    public Class<?> getController() {
        try {
            return Class.forName(this.getHandler().split("@")[0]);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public String getControllerMethod() {
        return this.getHandler().split("@")[1];
    }

    @Override
    public Object getAttribute(String name) {
        return this.attributes.get(name);
    }

    @Override
    public void setAttribute(String name, Object attribute) {
        this.attributes.put(name, attribute);
    }

    @Override
    public void removeAttribute(String name) {
        this.attributes.remove(name);
    }

    @Override
    public String[] getAttributeNames() {
        return this.attributes.keySet().toArray(new String[0]);
    }
}
