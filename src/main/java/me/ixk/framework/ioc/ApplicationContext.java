package me.ixk.framework.ioc;

import java.util.Map;

public class ApplicationContext implements Context {
    private final BindingAndAlias bindingAndAlias = new BindingAndAlias();

    @Override
    public Map<String, Binding> getBindings() {
        return this.bindingAndAlias.getBindings();
    }

    @Override
    public Map<String, String> getAliases() {
        return this.bindingAndAlias.getAliases();
    }
}
