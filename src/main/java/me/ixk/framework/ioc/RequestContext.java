package me.ixk.framework.ioc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    public static void removeAttributes() {
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
            removeAttributes();
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

    public void setHandler(Class<?> controllerType, String methodName) {
        this.setAttribute("controllerType", controllerType);
        this.setAttribute("controllerMethod", methodName);
    }

    public Class<?> getControllerType() {
        return this.getAttribute("controllerType", Class.class);
    }

    public String getControllerMethod() {
        return this.getAttribute("controllerMethod", String.class);
    }

    @Override
    public boolean hasAttribute(String name) {
        return this.attributes.containsKey(name);
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
