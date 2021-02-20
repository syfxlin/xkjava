package me.ixk.framework.event;

import cn.hutool.core.util.ReflectUtil;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.ixk.framework.annotation.EventListener;
import me.ixk.framework.ioc.XkJava;
import me.ixk.framework.processor.AbstractAnnotationProcessor;
import me.ixk.framework.util.ClassUtils;
import me.ixk.framework.util.MergedAnnotation;

/**
 * 发布者
 *
 * @author Otstar Lin
 * @date 2021/2/20 下午 8:43
 */
public class EventPublisher extends AbstractAnnotationProcessor {

    private final Map<Class<? extends ApplicationEvent>, List<ApplicationListener>> listeners = new ConcurrentHashMap<>();

    public EventPublisher(final XkJava app) {
        super(app);
    }

    @Override
    public void process() {
        this.processAnnotation(
                EventListener.class,
                this::processAnnotation,
                this::processAnnotation
            );
    }

    @SuppressWarnings("unchecked")
    private void processAnnotation(final AnnotatedElement element) {
        final MergedAnnotation annotation = MergedAnnotation.from(element);
        final ApplicationListener onEvent;
        if (
            element instanceof Class &&
            ApplicationListener.class.isAssignableFrom((Class<?>) element)
        ) {
            final Class<? extends ApplicationListener> listenerType = (Class<? extends ApplicationListener>) element;
            onEvent = event -> app.make(listenerType).onEvent(event);
        } else if (element instanceof Method) {
            final Method method = (Method) element;
            onEvent =
                event -> {
                    final Object listener = app.make(
                        method.getDeclaringClass()
                    );
                    ReflectUtil.invoke(listener, method, event);
                };
        } else {
            throw new IllegalArgumentException(
                "@EventListener not annotated [" + element + "]"
            );
        }
        final EventListener eventListener = annotation.getAnnotation(
            EventListener.class
        );
        for (final Class<? extends ApplicationEvent> eventType : eventListener.events()) {
            this.addListener(eventType, onEvent);
        }
    }

    public void publishEvent(final ApplicationEvent event) {
        final Class<?> eventType = ClassUtils.getUserClass(event);
        final List<ApplicationListener> listeners =
            this.listeners.get(eventType);
        if (listeners != null) {
            for (final ApplicationListener listener : listeners) {
                listener.onEvent(event);
            }
        }
    }

    public void addListener(
        final Class<? extends ApplicationEvent> eventType,
        ApplicationListener listener
    ) {
        listeners.compute(
            eventType,
            (type, list) -> {
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.add(listener);
                return list;
            }
        );
    }

    public void removeListener(
        final Class<? extends ApplicationEvent> eventType,
        ApplicationListener listener
    ) {
        listeners.compute(
            eventType,
            (type, list) -> {
                if (list != null) {
                    list.remove(listener);
                }
                return list;
            }
        );
    }
}
