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
    private static final ThreadLocal<Attributes> requestAttributes = new ThreadLocal<>();

    private static final ThreadLocal<Attributes> inheritableRequestAttributes = new InheritableThreadLocal<>();

    private final Map<String, Object> attributes = new ConcurrentHashMap<>();

    public static void resetRequestAttributes() {
        requestAttributes.remove();
    }

    public static void setRequestAttributes(Attributes attributes) {
        setRequestAttributes(attributes, false);
    }

    public static void setRequestAttributes(
        Attributes attributes,
        boolean inheritable
    ) {
        if (attributes == null) {
            resetRequestAttributes();
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

    public static Attributes getRequestAttributes() {
        Attributes attributes = requestAttributes.get();
        if (attributes == null) {
            attributes = inheritableRequestAttributes.get();
        }
        return attributes;
    }

    public static Attributes currentRequestAttributes()
        throws IllegalStateException {
        Attributes attributes = getRequestAttributes();
        if (attributes == null) {
            throw new NullPointerException(
                "Current request attributes is null"
            );
        }
        return attributes;
    }

    public static <T extends Attributes> T currentRequestAttributes(
        Class<T> _class
    ) {
        return _class.cast(currentRequestAttributes());
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
