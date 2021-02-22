package me.ixk.framework.websocket;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Otstar Lin
 * @date 2021/2/22 下午 8:33
 */
public class WebSocketExtension {

    private final String name;

    private final Map<String, String> parameters;

    public WebSocketExtension(final String name) {
        this(name, null);
    }

    public WebSocketExtension(
        final String name,
        final Map<String, String> parameters
    ) {
        this.name = name;
        if (parameters != null && !parameters.isEmpty()) {
            final Map<String, String> map = new LinkedHashMap<>(
                parameters.size()
            );
            map.putAll(parameters);
            this.parameters = Collections.unmodifiableMap(map);
        } else {
            this.parameters = Collections.emptyMap();
        }
    }

    public String getName() {
        return this.name;
    }

    public Map<String, String> getParameters() {
        return this.parameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WebSocketExtension that = (WebSocketExtension) o;
        return (
            Objects.equals(name, that.name) &&
            Objects.equals(parameters, that.parameters)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, parameters);
    }

    @Override
    public String toString() {
        return (
            "WebSocketExtension{" +
            "name='" +
            name +
            '\'' +
            ", parameters=" +
            parameters +
            '}'
        );
    }
}
