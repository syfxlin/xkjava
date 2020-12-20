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
    APPLICATION_JSON_UTF_8("application/json;charset=utf-8", APPLICATION_JSON),
    AUDIO_AAC("audio/aac"),
    APPLICATION_X_ABIWORD("application/x-abiword"),
    APPLICATION_X_FREEARC("application/x-freearc"),
    VIDEO_X_MSVIDEO("video/x-msvideo"),
    APPLICATION_VND_AMAZON_EBOOK("application/vnd.amazon.ebook"),
    APPLICATION_OCTET_STREAM("application/octet-stream"),
    IMAGE_BMP("image/bmp"),
    APPLICATION_X_BZIP("application/x-bzip"),
    APPLICATION_X_BZIP_2("application/x-bzip2"),
    APPLICATION_X_CSH("application/x-csh"),
    TEXT_CSS("text/css"),
    TEXT_CSV("text/csv"),
    APPLICATION_MSWORD("application/msword"),
    APPLICATION_VND_OPENXMLFORMATS_OFFICEDOCUMENT_WORDPROCESSINGML_DOCUMENT(
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    ),
    APPLICATION_VND_MS_FONTOBJECT("application/vnd.ms-fontobject"),
    APPLICATION_EPUB_ZIP("application/epub+zip"),
    IMAGE_GIF("image/gif"),
    IMAGE_VND_MICROSOFT_ICON("image/vnd.microsoft.icon"),
    TEXT_CALENDAR("text/calendar"),
    APPLICATION_JAVA_ARCHIVE("application/java-archive"),
    IMAGE_JPEG("image/jpeg"),
    TEXT_JAVASCRIPT("text/javascript"),
    APPLICATION_LD_JSON("application/ld+json"),
    AUDIO_MIDI("audio/midi"),
    AUDIO_X_MIDI("audio/x-midi"),
    AUDIO_MPEG("audio/mpeg"),
    VIDEO_MPEG("video/mpeg"),
    APPLICATION_VND_APPLE_INSTALLER_XML("application/vnd.apple.installer+xml"),
    APPLICATION_VND_OASIS_OPENDOCUMENT_PRESENTATION(
        "application/vnd.oasis.opendocument.presentation"
    ),
    APPLICATION_VND_OASIS_OPENDOCUMENT_SPREADSHEET(
        "application/vnd.oasis.opendocument.spreadsheet"
    ),
    APPLICATION_VND_OASIS_OPENDOCUMENT_TEXT(
        "application/vnd.oasis.opendocument.text"
    ),
    AUDIO_OGG("audio/ogg"),
    VIDEO_OGG("video/ogg"),
    APPLICATION_OGG("application/ogg"),
    FONT_OTF("font/otf"),
    IMAGE_PNG("image/png"),
    APPLICATION_PDF("application/pdf"),
    APPLICATION_VND_MS_POWERPOINT("application/vnd.ms-powerpoint"),
    APPLICATION_VND_OPENXMLFORMATS_OFFICEDOCUMENT_PRESENTATIONML_PRESENTATION(
        "application/vnd.openxmlformats-officedocument.presentationml.presentation"
    ),
    APPLICATION_X_RAR_COMPRESSED("application/x-rar-compressed"),
    APPLICATION_RTF("application/rtf"),
    APPLICATION_X_SH("application/x-sh"),
    IMAGE_SVG_XML("image/svg+xml"),
    APPLICATION_X_SHOCKWAVE_FLASH("application/x-shockwave-flash"),
    APPLICATION_X_TAR("application/x-tar"),
    IMAGE_TIFF("image/tiff"),
    FONT_TTF("font/ttf"),
    APPLICATION_VND_VISIO("application/vnd.visio"),
    AUDIO_WAV("audio/wav"),
    AUDIO_WEBM("audio/webm"),
    VIDEO_WEBM("video/webm"),
    IMAGE_WEBP("image/webp"),
    FONT_WOFF("font/woff"),
    FONT_WOFF_2("font/woff2"),
    APPLICATION_XHTML_XML("application/xhtml+xml"),
    APPLICATION_VND_MS_EXCEL("application/vnd.ms-excel"),
    APPLICATION_VND_OPENXMLFORMATS_OFFICEDOCUMENT_SPREADSHEETML_SHEET(
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    ),
    APPLICATION_VND_MOZILLA_XUL_XML("application/vnd.mozilla.xul+xml"),
    APPLICATION_ZIP("application/zip"),
    VIDEO_3_GPP("video/3gpp"),
    AUDIO_3_GPP("audio/3gpp"),
    VIDEO_3_GPP_2("video/3gpp2"),
    AUDIO_3_GPP_2("audio/3gpp2"),
    APPLICATION_X_7_Z_COMPRESSED("application/x-7z-compressed");

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
