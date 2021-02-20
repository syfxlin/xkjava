/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.ioc.entity;

import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import me.ixk.framework.ioc.binder.DataBinder;
import me.ixk.framework.ioc.entity.AnnotatedEntry.ChangeableEntry;
import me.ixk.framework.util.MergedAnnotation;
import me.ixk.framework.util.ParameterNameDiscoverer;

/**
 * 参数上下文
 *
 * @author Otstar Lin
 * @date 2020/11/9 下午 7:52
 */
public class ParameterContext {

    private final InjectContext context;
    private final AnnotatedEntry<Executable> executableEntry;
    private final ParameterEntry[] parameterEntries;

    public ParameterContext(
        final InjectContext context,
        final Executable executable
    ) {
        this.context = context;
        this.executableEntry = new AnnotatedEntry<>(executable);
        this.parameterEntries =
            new ParameterEntry[executable.getParameterCount()];
        final Parameter[] parameters = executable.getParameters();
        final String[] parameterNames = ParameterNameDiscoverer.getParameterNames(
            executable
        );
        for (int i = 0; i < parameters.length; i++) {
            this.parameterEntries[i] =
                new ParameterEntry(parameters[i], parameterNames[i]);
        }
    }

    public Executable getExecutable() {
        return this.getExecutableEntry().getElement();
    }

    public AnnotatedEntry<Executable> getExecutableEntry() {
        return executableEntry;
    }

    public MergedAnnotation getExecutableAnnotation() {
        return this.getExecutableEntry().getAnnotation();
    }

    public ParameterEntry[] getParameterEntries() {
        return parameterEntries;
    }

    public String[] getParameterNames() {
        return Arrays
            .stream(this.getParameterEntries())
            .map(ParameterEntry::getName)
            .toArray(String[]::new);
    }

    public MergedAnnotation[] getParameterAnnotations() {
        return Arrays
            .stream(this.getParameterEntries())
            .map(ParameterEntry::getAnnotation)
            .toArray(MergedAnnotation[]::new);
    }

    public InjectContext getContext() {
        return context;
    }

    public DataBinder getBinder() {
        return context.getBinder();
    }

    public static class ParameterEntry extends ChangeableEntry<Parameter> {

        private final String name;

        public ParameterEntry(final Parameter element, final String name) {
            super(element);
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
