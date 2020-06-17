package me.ixk.framework.ioc;

public class RequestContext implements ThreadLocalContext {
    private final ThreadLocal<BindingAndAlias> bindingAndAlias = new InheritableThreadLocal<>();

    @Override
    public BindingAndAlias getContext() {
        BindingAndAlias bindingAndAlias = this.bindingAndAlias.get();
        if (bindingAndAlias == null) {
            throw new NullPointerException("ThreadLocal content is null");
        }
        return bindingAndAlias;
    }

    @Override
    public void setContext(BindingAndAlias bindingAndAlias) {
        this.bindingAndAlias.set(bindingAndAlias);
    }

    @Override
    public void removeContext() {
        this.bindingAndAlias.remove();
    }

    @Override
    public boolean isCreated() {
        return this.bindingAndAlias.get() != null;
    }
}
