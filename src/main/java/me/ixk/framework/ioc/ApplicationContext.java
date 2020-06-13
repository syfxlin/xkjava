package me.ixk.framework.ioc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 存储 Application 内容的 Context，线程非安全，数据是共享的
 */
public class ApplicationContext implements Attributes {
    private static Attributes applicationAttributes;

    private final Map<String, Object> attributes = new ConcurrentHashMap<>();

    public static void resetAttributes() {
        applicationAttributes = null;
    }

    public static void setAttributes(Attributes attributes) {
        applicationAttributes = attributes;
    }

    public static Attributes getAttributes() {
        return applicationAttributes;
    }

    public static <T> T getAttributes(Class<T> _class) {
        return _class.cast(applicationAttributes);
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
