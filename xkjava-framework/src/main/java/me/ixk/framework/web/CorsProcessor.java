/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import me.ixk.framework.annotations.Component;
import me.ixk.framework.annotations.CrossOrigin;
import me.ixk.framework.http.HttpHeader;
import me.ixk.framework.http.HttpMethod;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;

/**
 * CORS 处理器
 *
 * @author Otstar Lin
 * @date 2020/10/31 下午 7:38
 */
@Component(name = "corsProcessor")
public class CorsProcessor {
    public static final String DYNAMIC_ORIGIN = "DYNAMIC";
    public static final String ALL = "*";
    public static final String TRUE = "true";

    public void processRequest(
        CrossOrigin crossOrigin,
        Request request,
        Response response
    ) {
        this.processRequest(new Configuration(crossOrigin), request, response);
    }

    public void processRequest(
        Configuration config,
        final Request request,
        final Response response
    ) {
        response.header(
            HttpHeader.ACCESS_CONTROL_ALLOW_ORIGIN,
            config.getAllowedOrigin(request)
        );
        response.header(
            HttpHeader.ACCESS_CONTROL_ALLOW_METHODS,
            config.getAllowedMethods()
        );
        response.header(
            HttpHeader.ACCESS_CONTROL_ALLOW_HEADERS,
            config.getAllowedHeaders()
        );
        final String allowCredentials = config.getAllowCredentials();
        if (allowCredentials != null) {
            response.header(
                HttpHeader.ACCESS_CONTROL_ALLOW_CREDENTIALS,
                allowCredentials
            );
        }
    }

    public boolean isPreFlightRequest(Request request) {
        return (
            HttpMethod.OPTIONS.is(request.getMethod()) &&
            request.header(HttpHeader.ORIGIN) != null &&
            request.header(HttpHeader.ACCESS_CONTROL_REQUEST_METHOD) != null
        );
    }

    public static class Configuration {
        private final String allowedOrigin;
        private final List<String> allowedMethods;
        private final List<String> allowedHeaders;
        private final boolean allowCredentials;
        private final boolean allowedAllMethod;
        private final boolean allowedAllHeaders;

        public Configuration(
            final String allowedOrigin,
            final List<String> allowedMethods,
            final List<String> allowedHeaders,
            final boolean allowCredentials
        ) {
            this.allowedOrigin = allowedOrigin;
            this.allowedMethods = allowedMethods;
            this.allowedHeaders = allowedHeaders;
            this.allowCredentials = allowCredentials;
            this.allowedAllMethod = this.allowedMethods.contains(ALL);
            this.allowedAllHeaders = this.allowedHeaders.contains(ALL);
        }

        public Configuration(final CrossOrigin crossOrigin) {
            this.allowedOrigin = crossOrigin.origin();
            this.allowedMethods = new ArrayList<>();
            this.allowedMethods.addAll(
                    Arrays
                        .stream(crossOrigin.methods())
                        .map(HttpMethod::asString)
                        .collect(Collectors.toList())
                );
            this.allowedHeaders = new ArrayList<>();
            this.allowedHeaders.addAll(
                    Arrays.asList(crossOrigin.allowedHeaders())
                );
            this.allowCredentials = crossOrigin.allowCredentials();
            this.allowedAllMethod = this.allowedMethods.contains(ALL);
            this.allowedAllHeaders = this.allowedHeaders.contains(ALL);
        }

        public String getAllowedOrigin(Request request) {
            return DYNAMIC_ORIGIN.equalsIgnoreCase(this.allowedOrigin)
                ? request.header(HttpHeader.ORIGIN)
                : this.allowedOrigin;
        }

        public String getAllowedMethods() {
            return String.join(", ", this.allowedMethods);
        }

        public String getAllowedHeaders() {
            return String.join(", ", this.allowedHeaders);
        }

        public String getAllowCredentials() {
            return allowCredentials ? TRUE : null;
        }
    }
}
