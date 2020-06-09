package me.ixk.framework.facades;

import java.util.Map;

public class JWT extends AbstractFacade {

    protected static me.ixk.framework.utils.JWT make() {
        return app.make(me.ixk.framework.utils.JWT.class);
    }

    public static String sign(String token) {
        return make().sign(token);
    }

    public static String sign(String token, boolean raw) {
        return make().sign(token, raw);
    }

    public static String sign(String token, boolean raw, String key) {
        return make().sign(token, raw, key);
    }

    public static String sign(
        String token,
        boolean raw,
        String key,
        String algo
    ) {
        return make().sign(token, raw, key, algo);
    }

    public static boolean verify(String token, String sign) {
        return make().verify(token, sign);
    }

    public static boolean verify(String token, String sign, boolean raw) {
        return make().verify(token, sign, raw);
    }

    public static boolean verify(
        String token,
        String sign,
        boolean raw,
        String key
    ) {
        return make().verify(token, sign, raw, key);
    }

    public static boolean verify(
        String token,
        String sign,
        boolean raw,
        String key,
        String algo
    ) {
        return make().verify(token, sign, raw, key, algo);
    }

    public static String encode(Map<String, String> payload) {
        return make().encode(payload);
    }

    public static String encode(Map<String, String> payload, int exp) {
        return make().encode(payload, exp);
    }

    public static String encode(
        Map<String, String> payload,
        int exp,
        String type
    ) {
        return make().encode(payload, exp, type);
    }

    public static String encode(
        Map<String, String> payload,
        int exp,
        String type,
        String key
    ) {
        return make().encode(payload, exp, type, key);
    }

    public static String encode(
        Map<String, String> payload,
        long exp,
        String type,
        String key,
        String algo
    ) {
        return make().encode(payload, exp, type, key, algo);
    }

    public static Map<String, String> decode(String token) {
        return make().decode(token);
    }

    public static Map<String, String> decode(String token, String key) {
        return make().decode(token, key);
    }

    public static Map<String, String> decode(
        String token,
        String key,
        String algo
    ) {
        return make().decode(token, key, algo);
    }
}
