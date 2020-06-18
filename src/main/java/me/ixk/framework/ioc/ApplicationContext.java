package me.ixk.framework.ioc;

import java.util.Map;
import me.ixk.framework.annotations.ScopeType;

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

    @Override
    public boolean matchesScope(ScopeType scopeType) {
        switch (scopeType) {
            case SINGLETON:
            case PROTOTYPE:
                return true;
            default:
                return false;
        }
    }
}
