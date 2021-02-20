/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.util;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 验证结果（组）
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 5:14
 */
public class ValidGroup {
    private final Map<String, ValidResult<Object>> validResultMap = new ConcurrentHashMap<>();

    public Map<String, ValidResult<Object>> getErrors() {
        return validResultMap;
    }

    public void addValidResult(String name, ValidResult<Object> validResult) {
        this.validResultMap.put(name, validResult);
    }

    public boolean isOk() {
        return this.validResultMap.isEmpty();
    }

    public boolean isFail() {
        return !this.isOk();
    }

    public ValidResult<Object> getError(String name) {
        return this.validResultMap.get(name);
    }

    public Collection<ValidResult<Object>> getErrorResults() {
        return this.validResultMap.values();
    }
}
