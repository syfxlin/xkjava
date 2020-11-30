/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc;

import static me.ixk.framework.utils.DataUtils.caseGet;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import me.ixk.framework.annotations.DataBind;
import me.ixk.framework.utils.Convert;
import me.ixk.framework.utils.MergedAnnotation;

/**
 * 对象包装数据绑定器
 *
 * @author Otstar Lin
 * @date 2020/10/14 上午 11:41
 */
public class ObjectWrapperDataBinder implements DataBinder {

    public static final String DEFAULT_VALUE_PREFIX = "&";

    protected final Container container;
    protected final List<Function<String, Object>> getters;
    protected final List<Converter> converters;

    protected volatile String prefix;

    public ObjectWrapperDataBinder(
        final Container container,
        final List<Function<String, Object>> getters
    ) {
        this(container, null, getters, new ArrayList<>());
    }

    public ObjectWrapperDataBinder(
        final Container container,
        final String prefix,
        final List<Function<String, Object>> getters
    ) {
        this(container, prefix, getters, new ArrayList<>());
    }

    public ObjectWrapperDataBinder(
        final Container container,
        final List<Function<String, Object>> getters,
        final List<Converter> converters
    ) {
        this(container, null, getters, converters);
    }

    public ObjectWrapperDataBinder(
        final Container container,
        final String prefix,
        final List<Function<String, Object>> getters,
        final List<Converter> converters
    ) {
        this.container = container;
        this.prefix = prefix;
        this.getters = getters;
        this.converters = converters;
    }

    @Override
    public <T> T getObject(
        String name,
        final Class<T> type,
        final MergedAnnotation annotation
    ) {
        DataBind dataBind = annotation.getAnnotation(DataBind.class);
        if (dataBind != null && dataBind.name().length() != 0) {
            name = dataBind.name();
            this.prefix = "";
        }
        final String currentName = this.currentName(name);
        final String typeName = type.getName();
        Object object = null;
        for (final Function<String, Object> getter : this.getters) {
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
            final String concatName = this.concatName(name);
            final DataBinder binder = this.copy(concatName);
            object = this.container.make(concatName, type, binder);
            if (object == null) {
                object = this.container.make(typeName, type, binder);
            }
        }
        if (
            object == null &&
            dataBind != null &&
            !DataBind.EMPTY.equals(dataBind.defaultValue())
        ) {
            object = dataBind.defaultValue();
        }
        for (Converter converter : this.converters) {
            object = converter.before(object, currentName, type, annotation);
        }
        T result = Convert.convert(type, object);
        for (Converter converter : converters) {
            result = converter.after(result, currentName, type, annotation);
        }
        return result;
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

    protected DataBinder copy(String prefix) {
        return new ObjectWrapperDataBinder(
            this.container,
            prefix,
            this.getters
        );
    }

    public Container getContainer() {
        return container;
    }

    public List<Function<String, Object>> getGetters() {
        return getters;
    }

    public void addGetter(Function<String, Object> getter) {
        this.getters.add(getter);
    }

    public void removeGetter(Function<String, Object> getter) {
        this.getters.remove(getter);
    }

    public void removeGetter(int index) {
        this.getters.remove(index);
    }

    public List<Converter> getConverters() {
        return converters;
    }

    public void addConverter(Converter converter) {
        this.converters.add(converter);
    }

    public void removeConverter(Converter converter) {
        this.converters.remove(converter);
    }

    public void removeConverter(int index) {
        this.converters.remove(index);
    }
}
