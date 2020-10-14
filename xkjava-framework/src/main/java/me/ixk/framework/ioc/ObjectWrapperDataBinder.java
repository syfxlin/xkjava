/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc;

import static me.ixk.framework.helpers.Util.caseGet;

import cn.hutool.core.util.ReflectUtil;
import java.util.List;
import java.util.function.Function;
import me.ixk.framework.annotations.DataBind;
import me.ixk.framework.utils.Convert;

/**
 * 对象包装数据绑定器
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 11:41
 */
public class ObjectWrapperDataBinder implements DataBinder {
    public static final String DEFAULT_VALUE_PREFIX = "&";
    public static final String DEFAULT_DATABIND_NAME = "default";

    protected final Container container;

    protected final List<Function<String, Object>> getters;

    protected volatile String prefix = null;

    public ObjectWrapperDataBinder(
        Container container,
        List<Function<String, Object>> getters
    ) {
        this.container = container;
        this.getters = getters;
    }

    public ObjectWrapperDataBinder(
        final Container container,
        final String prefix,
        final List<Function<String, Object>> getters
    ) {
        this.container = container;
        this.prefix = prefix;
        this.getters = getters;
    }

    @Override
    public <T> T getObject(final String name, final Class<T> type) {
        return this.getObject(name, type, null);
    }

    @Override
    public <T> T getObject(
        String name,
        final Class<T> type,
        final DataBind dataBind
    ) {
        if (dataBind != null) {
            name =
                dataBind.name().length() == 0
                    ? this.getDefaultDataBindName()
                    : dataBind.name();
            this.prefix = "";
        }
        String currentName = this.currentName(name);
        String typeName = type.getName();
        Object object = null;
        for (Function<String, Object> getter : this.getters) {
            object =
                caseGet(
                    currentName,
                    caseName -> {
                        final Object target = getter.apply(caseName);
                        if (target == null) {
                            return getter.apply(
                                DEFAULT_VALUE_PREFIX + caseName
                            );
                        }
                        return target;
                    }
                );
            if (object == null) {
                object = getter.apply(typeName);
            }
            if (object == null) {
                object = getter.apply(DEFAULT_VALUE_PREFIX + typeName);
            }
            if (object != null) {
                break;
            }
        }
        if (object == null) {
            String concatName = this.concatName(name);
            DataBinder binder = ReflectUtil.newInstance(
                this.getClass(),
                this.container,
                concatName,
                this.getters
            );
            object = this.container.make(concatName, type, binder);
            if (object == null) {
                object = this.container.make(typeName, type, binder);
            }
        }
        return Convert.convert(type, object);
    }

    private String currentName(final String name) {
        if (this.prefix == null || this.prefix.isEmpty()) {
            return name;
        }
        return this.prefix + "." + name;
    }

    private String concatName(final String name) {
        if (this.prefix == null) {
            return "";
        }
        if (this.prefix.isEmpty()) {
            return name;
        }
        return this.prefix + "." + name;
    }

    protected String getDefaultDataBindName() {
        return DEFAULT_DATABIND_NAME;
    }

    public Container getContainer() {
        return container;
    }

    public List<Function<String, Object>> getGetters() {
        return getters;
    }
}
