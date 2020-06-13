package me.ixk.framework.ioc;

public interface RequestAttributes {
    Object getAttribute(String name);
    void setAttribute(String name, Object attribute);
    void removeAttribute(String name);
    String[] getAttributeNames();

    default <T> T getAttribute(String name, Class<T> returnType) {
        return returnType.cast(this.getAttribute(name));
    }

    default <T> T getObject(Class<T> _class) {
        return _class.cast(this.getAttribute(_class.getName()));
    }

    default <T> void setObject(Class<T> _class, T object) {
        this.setAttribute(_class.getName(), object);
    }

    default <T> void removeObject(Class<T> _class) {
        this.removeAttribute(_class.getName());
    }
}
