/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.http;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * HttpHeader
 *
 * @author Otstar Lin
 * @date 2020/10/23 上午 9:27
 */
public enum HttpHeader {
    /**
     * General Fields.
     */
    CONNECTION("Connection"),
    CACHE_CONTROL("Cache-Control"),
    DATE("Date"),
    PRAGMA("Pragma"),
    PROXY_CONNECTION("Proxy-Connection"),
    TRAILER("Trailer"),
    TRANSFER_ENCODING("Transfer-Encoding"),
    UPGRADE("Upgrade"),
    VIA("Via"),
    WARNING("Warning"),
    NEGOTIATE("Negotiate"),

    /**
     * Entity Fields.
     */
    ALLOW("Allow"),
    CONTENT_ENCODING("Content-Encoding"),
    CONTENT_LANGUAGE("Content-Language"),
    CONTENT_LENGTH("Content-Length"),
    CONTENT_LOCATION("Content-Location"),
    CONTENT_MD5("Content-MD5"),
    CONTENT_RANGE("Content-Range"),
    CONTENT_TYPE("Content-Type"),
    EXPIRES("Expires"),
    LAST_MODIFIED("Last-Modified"),

    /**
     * Request Fields.
     */
    ACCEPT("Accept"),
    ACCEPT_CHARSET("Accept-Charset"),
    ACCEPT_ENCODING("Accept-Encoding"),
    ACCEPT_LANGUAGE("Accept-Language"),
    AUTHORIZATION("Authorization"),
    EXPECT("Expect"),
    FORWARDED("Forwarded"),
    FROM("From"),
    HOST("Host"),
    IF_MATCH("If-Match"),
    IF_MODIFIED_SINCE("If-Modified-Since"),
    IF_NONE_MATCH("If-None-Match"),
    IF_RANGE("If-Range"),
    IF_UNMODIFIED_SINCE("If-Unmodified-Since"),
    KEEP_ALIVE("Keep-Alive"),
    MAX_FORWARDS("Max-Forwards"),
    PROXY_AUTHORIZATION("Proxy-Authorization"),
    RANGE("Range"),
    REQUEST_RANGE("Request-Range"),
    REFERER("Referer"),
    TE("TE"),
    USER_AGENT("User-Agent"),
    X_FORWARDED_FOR("X-Forwarded-For"),
    X_FORWARDED_PORT("X-Forwarded-Port"),
    X_FORWARDED_PROTO("X-Forwarded-Proto"),
    X_FORWARDED_SERVER("X-Forwarded-Server"),
    X_FORWARDED_HOST("X-Forwarded-Host"),

    /**
     * Response Fields.
     */
    ACCEPT_RANGES("Accept-Ranges"),
    AGE("Age"),
    ETAG("ETag"),
    LOCATION("Location"),
    PROXY_AUTHENTICATE("Proxy-Authenticate"),
    RETRY_AFTER("Retry-After"),
    SERVER("Server"),
    SERVLET_ENGINE("Servlet-Engine"),
    VARY("Vary"),
    WWW_AUTHENTICATE("WWW-Authenticate"),

    /**
     * WebSocket Fields.
     */
    ORIGIN("Origin"),
    SEC_WEBSOCKET_KEY("Sec-WebSocket-Key"),
    SEC_WEBSOCKET_VERSION("Sec-WebSocket-Version"),
    SEC_WEBSOCKET_EXTENSIONS("Sec-WebSocket-Extensions"),
    SEC_WEBSOCKET_SUBPROTOCOL("Sec-WebSocket-Protocol"),
    SEC_WEBSOCKET_ACCEPT("Sec-WebSocket-Accept"),

    /**
     * Other Fields.
     */
    COOKIE("Cookie"),
    SET_COOKIE("Set-Cookie"),
    SET_COOKIE2("Set-Cookie2"),
    MIME_VERSION("MIME-Version"),
    IDENTITY("identity"),

    X_POWERED_BY("X-Powered-By"),
    HTTP2_SETTINGS("HTTP2-Settings"),

    STRICT_TRANSPORT_SECURITY("Strict-Transport-Security"),

    /**
     * HTTP2 Fields.
     */
    C_METHOD(":method"),
    C_SCHEME(":scheme"),
    C_AUTHORITY(":authority"),
    C_PATH(":path"),
    C_STATUS(":status"),
    C_PROTOCOL(":protocol"),

    /**
     * CORS
     */
    ACCESS_CONTROL_ALLOW_ORIGIN("Access-Control-Allow-Origin"),
    ACCESS_CONTROL_ALLOW_CREDENTIALS("Access-Control-Allow-Credentials"),
    ACCESS_CONTROL_ALLOW_METHODS("Access-Control-Allow-Methods"),
    ACCESS_CONTROL_ALLOW_HEADERS("Access-Control-Allow-Headers"),
    ACCESS_CONTROL_REQUEST_METHOD("Access-Control-Request-Method"),

    UNKNOWN("::UNKNOWN::"),;

    public static final Map<String, HttpHeader> CACHE = new HashMap<>(630);

    static {
        for (final HttpHeader header : HttpHeader.values()) {
            if (header != UNKNOWN) {
                if (CACHE.containsKey(header.toString())) {
                    throw new IllegalStateException();
                }
                CACHE.put(header.toString(), header);
            }
        }
    }

    private final String name;
    private final String lowerCase;

    HttpHeader(final String name) {
        this.name = name;
        lowerCase = name.toLowerCase();
    }

    public static HttpHeader from(final String name) {
        HttpHeader header = CACHE.get(name);
        if (header != null) {
            return header;
        }
        try {
            final Constructor<HttpHeader> constructor =
                HttpHeader.class.getDeclaredConstructor(String.class);
            constructor.setAccessible(true);
            header = constructor.newInstance(name);
        } catch (
            InstantiationException
            | IllegalAccessException
            | InvocationTargetException
            | NoSuchMethodException e
        ) {
            header = null;
        }
        if (header != null) {
            CACHE.put(name, header);
        }
        return header;
    }

    public String lowerCaseName() {
        return lowerCase;
    }

    public boolean is(final String s) {
        return name.equalsIgnoreCase(s);
    }

    public String asString() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    public enum Value {
        /**
         * Connection: close
         */
        CLOSE("close"),
        /**
         * Transfer-Encoding: chunked
         */
        CHUNKED("chunked"),
        /**
         * Transfer-Encoding: gzip
         */
        GZIP("gzip"),
        /**
         * Transfer-Encoding: identity
         */
        IDENTITY("identity"),
        /**
         * Connection: keep-alive
         */
        KEEP_ALIVE("keep-alive"),
        /**
         * Expect: 100-continue
         */
        CONTINUE("100-continue"),
        /**
         * 102-processing
         */
        PROCESSING("102-processing"),
        /**
         * TE
         */
        TE("TE"),
        /**
         * bytes
         */
        BYTES("bytes"),
        /**
         * Cache-control: no-cache
         */
        NO_CACHE("no-cache"),
        /**
         * Connection: Upgrade
         */
        UPGRADE("Upgrade"),
        /**
         * UNKNOWN
         */
        UNKNOWN("::UNKNOWN::"),;

        public static final Map<String, Value> CACHE = new HashMap<>();

        static {
            for (Value value : Value.values()) {
                if (value != UNKNOWN) {
                    CACHE.put(value.toString(), value);
                }
            }
        }

        private final String value;

        Value(String value) {
            this.value = value;
        }

        public boolean is(String s) {
            return value.equalsIgnoreCase(s);
        }

        public String asString() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }
    }
}
