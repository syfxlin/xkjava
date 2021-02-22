package me.ixk.framework.server;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;

/**
 * 过滤器描述
 *
 * @author Otstar Lin
 * @date 2021/2/22 下午 2:57
 */
public class FilterSpec {

    private final String name;
    private final String[] url;
    private final Filter filter;
    private final DispatcherType[] dispatcherTypes;
    private final Map<String, String> initParams;
    private final boolean asyncSupported;

    public FilterSpec(
        final String name,
        final String[] url,
        final Filter filter,
        final DispatcherType[] dispatcherTypes,
        final Map<String, String> initParams,
        final boolean asyncSupported
    ) {
        this.name = name;
        this.url = url;
        this.filter = filter;
        this.dispatcherTypes = dispatcherTypes;
        this.initParams = initParams;
        this.asyncSupported = asyncSupported;
    }

    public String getName() {
        return name;
    }

    public String[] getUrl() {
        return url;
    }

    public Filter getFilter() {
        return filter;
    }

    public EnumSet<DispatcherType> getDispatcherTypes() {
        final EnumSet<DispatcherType> set = EnumSet.noneOf(
            DispatcherType.class
        );
        set.addAll(Arrays.asList(dispatcherTypes));
        return set;
    }

    public Map<String, String> getInitParams() {
        return initParams;
    }

    public boolean isAsyncSupported() {
        return asyncSupported;
    }
}
