/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.property;

import static me.ixk.framework.utils.DataUtils.caseGet;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Map 配置数据源
 *
 * @author Otstar Lin
 * @date 2020/12/21 下午 8:53
 */
public class MapPropertySource<T> extends PropertySource<Map<String, T>> {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public MapPropertySource(final String name, final Properties properties) {
        super(name, (Map) properties);
    }

    public MapPropertySource(final String name, final Map<String, T> source) {
        super(name, source);
    }

    @Override
    public Object getProperty(final String name) {
        return caseGet(name, this.source::get);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setProperty(final String name, final Object value) {
        this.source.put(name, (T) value);
    }

    @Override
    public Set<String> getPropertyNames() {
        return this.source.keySet();
    }

    @Override
    public void removeProperty(final String name) {
        this.source.remove(name);
    }
}
