/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc;

import java.util.Map;

public class With {
    private final String withPrefix;

    private final Map<String, Object> withMap;

    public With(String withPrefix, Map<String, Object> withMap) {
        this.withPrefix = withPrefix;
        this.withMap = withMap;
    }

    public String getPrefix() {
        return withPrefix;
    }

    public Map<String, Object> getMap() {
        return withMap;
    }

    public String concat(String name) {
        return withPrefix == null || withPrefix.length() == 0
            ? name
            : this.withPrefix + "." + name;
    }

    public String concatPrefix(String name) {
        return withPrefix == null
            ? ""
            : (
                this.withPrefix.length() == 0
                    ? name
                    : this.withPrefix + "." + name
            );
    }
}
