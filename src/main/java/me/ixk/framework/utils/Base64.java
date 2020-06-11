package me.ixk.framework.utils;

import java.nio.charset.StandardCharsets;

public abstract class Base64 {

    public static String encode(String data) {
        return encode(data.getBytes(StandardCharsets.ISO_8859_1));
    }

    public static String encode(byte[] data) {
        return java.util.Base64.getEncoder().encodeToString(data);
    }

    public static String decode(String data) {
        return decode(data.getBytes(StandardCharsets.ISO_8859_1));
    }

    public static String decode(byte[] data) {
        return new String(
            java.util.Base64.getDecoder().decode(data),
            StandardCharsets.ISO_8859_1
        );
    }

    public static String encodeUrlSafe(String data) {
        return encodeUrlSafe(data.getBytes(StandardCharsets.ISO_8859_1));
    }

    public static String encodeUrlSafe(byte[] data) {
        return new String(java.util.Base64.getUrlEncoder().encode(data));
    }

    public static String decodeUrlSafe(String data) {
        return decodeUrlSafe(data.getBytes(StandardCharsets.ISO_8859_1));
    }

    public static String decodeUrlSafe(byte[] data) {
        return new String(java.util.Base64.getUrlDecoder().decode(data));
    }

    public static byte[] toByte(String data) {
        return data.getBytes(StandardCharsets.ISO_8859_1);
    }
}
