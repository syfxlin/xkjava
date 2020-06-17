package me.ixk.framework.ioc;

import java.util.List;
import java.util.Map;
import me.ixk.framework.annotations.ScopeType;
import me.ixk.framework.aop.Advice;

public class Binding {
    private ScopeType type;
    private Concrete concrete;
    private Map<String, List<Advice>> adviceMap;

    public Binding(
        Concrete concrete,
        ScopeType scopeType
        // Map<String, List<Advice>> adviceMap
    ) {
        this.type = scopeType;
        this.concrete = concrete;
        // this.adviceMap = adviceMap;
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

    public Map<String, List<Advice>> getAdviceMap() {
        return adviceMap;
    }

    public void setAdviceMap(Map<String, List<Advice>> adviceMap) {
        this.adviceMap = adviceMap;
    }

    public boolean hasAdvice() {
        return this.adviceMap != null && !this.adviceMap.isEmpty();
    }
}
