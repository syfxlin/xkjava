/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.http;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Mime 类型
 *
 * @author Otstar Lin
 * @date 2020/10/24 下午 2:25
 */
public enum MimeType {
    /**
     * 默认类型
     */
    NONE("none"),
    /**
     * application/x-www-form-urlencoded
     */
    FORM_ENCODED("application/x-www-form-urlencoded"),
    /**
     * message/http
     */
    MESSAGE_HTTP("message/http"),
    /**
     * multipart/byteranges
     */
    MULTIPART_BYTERANGES("multipart/byteranges"),
    /**
     * multipart/form-data
     */
    MULTIPART_FORM_DATA("multipart/form-data"),
    /**
     * text/html
     */
    TEXT_HTML("text/html"),
    /**
     * text/plain
     */
    TEXT_PLAIN("text/plain"),
    /**
     * text/xml
     */
    TEXT_XML("text/xml"),
    /**
     * text/json
     */
    TEXT_JSON("text/json", StandardCharsets.UTF_8),
    /**
     * application/json
     */
    APPLICATION_JSON("application/json", StandardCharsets.UTF_8),
    /**
     * text/html;charset=iso-8859-1
     */
    TEXT_HTML_8859_1("text/html;charset=iso-8859-1", TEXT_HTML),
    /**
     * text/html;charset=utf-8
     */
    TEXT_HTML_UTF_8("text/html;charset=utf-8", TEXT_HTML),
    /**
     * text/plain;charset=iso-8859-1
     */
    TEXT_PLAIN_8859_1("text/plain;charset=iso-8859-1", TEXT_PLAIN),
    /**
     * text/plain;charset=utf-8
     */
    TEXT_PLAIN_UTF_8("text/plain;charset=utf-8", TEXT_PLAIN),
    /**
     * text/xml;charset=iso-8859-1
     */
    TEXT_XML_8859_1("text/xml;charset=iso-8859-1", TEXT_XML),
    /**
     * text/xml;charset=utf-8
     */
    TEXT_XML_UTF_8("text/xml;charset=utf-8", TEXT_XML),
    /**
     * text/json;charset=iso-8859-1
     */
    TEXT_JSON_8859_1("text/json;charset=iso-8859-1", TEXT_JSON),
    /**
     * text/json;charset=utf-8
     */
    TEXT_JSON_UTF_8("text/json;charset=utf-8", TEXT_JSON),
    /**
     * application/json;charset=iso-8859-1
     */
    APPLICATION_JSON_8859_1(
        "application/json;charset=iso-8859-1",
        APPLICATION_JSON
    ),
    /**
     * application/json;charset=utf-8
     */
    APPLICATION_JSON_UTF_8("application/json;charset=utf-8", APPLICATION_JSON),;

    public static final Map<String, MimeType> CACHE = new HashMap<>(512);

    static {
        for (final MimeType type : MimeType.values()) {
            CACHE.put(type.toString(), type);

            final int charset = type.toString().indexOf(";charset=");
            if (charset > 0) {
                final String alt = type
                    .toString()
                    .replace(";charset=", "; charset=");
                CACHE.put(alt, type);
            }
        }
    }

    private final String string;
    private final MimeType base;
    private final Charset charset;

    MimeType(final String s) {
        string = s;
        base = this;
        charset = null;
    }

    MimeType(final String s, final MimeType base) {
        string = s;
        this.base = base;
        final int i = s.indexOf(";charset=");
        charset = Charset.forName(s.substring(i + 9));
    }

    MimeType(final String s, final Charset cs) {
        string = s;
        base = this;
        charset = cs;
    }

    public MimeType getBase() {
        return base;
    }

    public Charset getCharset() {
        return charset;
    }

    public boolean is(final String s) {
        return string.equalsIgnoreCase(s);
    }

    public String asString() {
        return string;
    }

    @Override
    public String toString() {
        return string;
    }
}
