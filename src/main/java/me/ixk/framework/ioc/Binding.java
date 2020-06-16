package me.ixk.framework.ioc;

import me.ixk.framework.annotations.ScopeType;

public class Binding {
    public ScopeType type;
    public Concrete concrete;

    public Binding(Concrete concrete, ScopeType scopeType) {
        this.type = scopeType;
        this.concrete = concrete;
    }

    public ScopeType getType() {
        return type;
    }

    public void setType(ScopeType type) {
        this.type = type;
    }

    public boolean isShared() {
        return this.type.isShared();
    }

    public Concrete getConcrete() {
        return concrete;
    }

    public void setConcrete(Concrete concrete) {
        this.concrete = concrete;
    }
}
