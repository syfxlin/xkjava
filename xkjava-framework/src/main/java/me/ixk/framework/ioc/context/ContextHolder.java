package me.ixk.framework.ioc.context;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import me.ixk.framework.ioc.Container;

/**
 * @author Otstar Lin
 * @date 2021/1/13 下午 4:22
 */
public class ContextHolder {

    private final Map<Class<? extends Context>, Object> contextHolderMap = new ConcurrentHashMap<>();
    private final AtomicBoolean isSnapshot = new AtomicBoolean(false);
    private final Container container;

    public ContextHolder(final Container container) {
        this.container = container;
    }

    public void snapshot() {
        synchronized (container.getContexts()) {
            final Map<Class<? extends Context>, Context> contexts = container.getContexts();
            for (final Entry<Class<? extends Context>, Context> entry : contexts.entrySet()) {
                final Class<? extends Context> contextType = entry.getKey();
                if (ThreadLocalContext.class.isAssignableFrom(contextType)) {
                    final ThreadLocalContext context = (ThreadLocalContext) entry.getValue();
                    contextHolderMap.put(contextType, context.getContext());
                }
            }
            this.isSnapshot.set(true);
        }
    }

    public void restore() {
        if (!this.isSnapshot.get()) {
            throw new IllegalStateException(
                "ContextHolder snapshot is required to restore"
            );
        }
        synchronized (container.getContexts()) {
            final Map<Class<? extends Context>, Context> contexts = container.getContexts();
            for (final Entry<Class<? extends Context>, Context> entry : contexts.entrySet()) {
                final Class<? extends Context> contextType = entry.getKey();
                if (contextHolderMap.containsKey(contextType)) {
                    final ThreadLocalContext context = (ThreadLocalContext) contexts.get(
                        contextType
                    );
                    context.setContext(contextHolderMap.get(contextType));
                }
            }
        }
        this.clear();
    }

    public void clear() {
        this.contextHolderMap.clear();
        this.isSnapshot.set(false);
    }
}
