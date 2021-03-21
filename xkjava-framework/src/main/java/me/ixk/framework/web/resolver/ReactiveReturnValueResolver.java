package me.ixk.framework.web.resolver;

import me.ixk.framework.annotation.core.Order;
import me.ixk.framework.annotation.web.WebResolver;
import me.ixk.framework.web.MethodReturnValue;
import me.ixk.framework.web.WebContext;
import me.ixk.framework.web.async.WebAsyncManager;
import me.ixk.framework.web.async.WebDeferredTask;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Otstar Lin
 * @date 2021/3/21 下午 3:02
 */
@WebResolver
@Order(Order.HIGHEST_PRECEDENCE)
public class ReactiveReturnValueResolver
    implements ResponseReturnValueResolver {

    @Override
    public boolean supportsReturnType(
        final Object value,
        final MethodReturnValue returnValue,
        final WebContext context
    ) {
        return value instanceof Mono || value instanceof Flux;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object resolveReturnValue(
        final Object value,
        final MethodReturnValue returnValue,
        final WebContext context
    ) {
        final WebAsyncManager asyncManager = context.async();
        final WebDeferredTask<Object> task = new WebDeferredTask<>();
        asyncManager.startDeferred(task);
        final Flux<Object> flux = value instanceof Flux
            ? (Flux<Object>) value
            : ((Mono<Object>) value).flux();
        flux.subscribe(
            result -> asyncManager.pushConcurrentResult(result, context),
            task::result,
            task::complete
        );
        return null;
    }
}
