package me.ixk.framework.ioc;

import java.util.Map;

public class With {
    private String withPrefix;

    private Map<String, Object> withMap;

    public With(String withPrefix, Map<String, Object> withMap) {
        this.withPrefix = withPrefix;
        this.withMap = withMap;
    }

    public void setPrefix(String withPrefix) {
        this.withPrefix = withPrefix;
    }

    public void setMap(Map<String, Object> withMap) {
        this.withMap = withMap;
    }

    public String getPrefix() {
        return withPrefix;
    }

    public Map<String, Object> getMap() {
        return withMap;
    }
}
