package me.ixk.route;

import java.util.Map;
import java.util.Set;
import me.ixk.middleware.HandlerInterface;

public class DispatcherResult {
    protected DispatcherStatus status = DispatcherStatus.NOT_FOUND;

    protected HandlerInterface handler = null;

    protected Map<String, String> params = null;

    protected Set<String> allowedMethods = null;

    public DispatcherResult() {}

    public DispatcherResult(DispatcherStatus status) {
        this.status = status;
    }

    public DispatcherResult(DispatcherStatus status, HandlerInterface handler) {
        this.status = status;
        this.handler = handler;
    }

    public DispatcherResult(
        DispatcherStatus status,
        HandlerInterface handler,
        Map<String, String> params
    ) {
        this.status = status;
        this.handler = handler;
        this.params = params;
    }

    public DispatcherStatus getStatus() {
        return status;
    }

    public void setStatus(DispatcherStatus status) {
        this.status = status;
    }

    public HandlerInterface getHandler() {
        return handler;
    }

    public void setHandler(HandlerInterface handler) {
        this.handler = handler;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public Set<String> getAllowedMethods() {
        return allowedMethods;
    }

    public void setAllowedMethods(Set<String> allowedMethods) {
        this.allowedMethods = allowedMethods;
    }
}
