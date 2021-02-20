/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import me.ixk.framework.annotation.Component;
import me.ixk.framework.annotation.CrossOrigin;
import me.ixk.framework.http.HttpHeader;
import me.ixk.framework.http.HttpMethod;

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
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        this.processRequest(new Configuration(crossOrigin), request, response);
    }

    public void processRequest(
        Configuration config,
        final HttpServletRequest request,
        final HttpServletResponse response
    ) {
        response.addHeader(
            HttpHeader.ACCESS_CONTROL_ALLOW_ORIGIN.asString(),
            config.getAllowedOrigin(request)
        );
        response.addHeader(
            HttpHeader.ACCESS_CONTROL_ALLOW_METHODS.asString(),
            config.getAllowedMethods()
        );
        response.addHeader(
            HttpHeader.ACCESS_CONTROL_ALLOW_HEADERS.asString(),
            config.getAllowedHeaders()
        );
        final String allowCredentials = config.getAllowCredentials();
        if (allowCredentials != null) {
            response.addHeader(
                HttpHeader.ACCESS_CONTROL_ALLOW_CREDENTIALS.asString(),
                allowCredentials
            );
        }
    }

    public boolean isPreFlightRequest(HttpServletRequest request) {
        return (
            HttpMethod.OPTIONS.is(request.getMethod()) &&
            request.getHeader(HttpHeader.ORIGIN.asString()) != null &&
            request.getHeader(
                HttpHeader.ACCESS_CONTROL_REQUEST_METHOD.asString()
            ) !=
            null
        );
    }

    public static class Configuration {

        private final String allowedOrigin;
        private final List<String> allowedMethods;
        private final List<String> allowedHeaders;
        private final boolean allowCredentials;

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
        }

        public String getAllowedOrigin(HttpServletRequest request) {
            return DYNAMIC_ORIGIN.equalsIgnoreCase(this.allowedOrigin)
                ? request.getHeader(HttpHeader.ORIGIN.asString())
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
