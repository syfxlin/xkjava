package me.ixk.framework.ioc;

public class RequestContext {
    private static final ThreadLocal<RequestAttributes> requestAttributes = new ThreadLocal<>();

    private static final ThreadLocal<RequestAttributes> inheritableRequestAttributes = new InheritableThreadLocal<>();

    public static void resetRequestAttributes() {
        requestAttributes.remove();
    }

    public static void setRequestAttributes(RequestAttributes attributes) {
        setRequestAttributes(attributes, false);
    }

    public static void setRequestAttributes(
        RequestAttributes attributes,
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

    public static RequestAttributes getRequestAttributes() {
        RequestAttributes attributes = requestAttributes.get();
        if (attributes == null) {
            attributes = inheritableRequestAttributes.get();
        }
        return attributes;
    }

    public static RequestAttributes currentRequestAttributes()
        throws IllegalStateException {
        RequestAttributes attributes = getRequestAttributes();
        if (attributes == null) {
            throw new NullPointerException(
                "Current request attributes is null"
            );
        }
        return attributes;
    }

    public static <T extends RequestAttributes> T currentRequestAttributes(
        Class<T> _class
    ) {
        return _class.cast(currentRequestAttributes());
    }
}
