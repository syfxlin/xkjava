/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Jwt 工具类
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 5:03
 */
public class Jwt {

    private static final Set<String> SUPPORTED;
    private final byte[] key;
    private final String algo;
    private final Map<String, String> defaultPayload;

    static {
        SUPPORTED = new HashSet<>();
        SUPPORTED.add("SHA256");
        SUPPORTED.add("SHA384");
        SUPPORTED.add("SHA512");
    }

    public Jwt(byte[] key, String algo) {
        this(key, algo, null);
    }

    public Jwt(byte[] key, String algo, Map<String, String> defaultPayload) {
        this.key = key;
        this.algo = algo;
        this.defaultPayload = defaultPayload;
    }

    public String sign(String token) {
        return this.sign(token, false, this.key, this.algo);
    }

    public String sign(String token, boolean raw) {
        return this.sign(token, raw, this.key, this.algo);
    }

    public String sign(String token, boolean raw, byte[] key) {
        return this.sign(token, raw, key, this.algo);
    }

    public String sign(String token, boolean raw, byte[] key, String algo) {
        String sign = Mac.make("Hmac" + algo, token, key, true);
        return raw ? sign : Base64.encode(sign);
    }

    public boolean verify(String token, String sign) {
        return this.verify(token, sign, false, this.key, this.algo);
    }

    public boolean verify(String token, String sign, boolean raw) {
        return this.verify(token, sign, raw, this.key, this.algo);
    }

    public boolean verify(String token, String sign, boolean raw, byte[] key) {
        return this.verify(token, sign, raw, key, this.algo);
    }

    public boolean verify(
        String token,
        String sign,
        boolean raw,
        byte[] key,
        String algo
    ) {
        token =
            raw
                ? token
                : StrUtil.str(Base64.decode(token), StandardCharsets.UTF_8);
        return this.sign(token, raw, key, algo).equals(sign);
    }

    public String encode(Map<String, String> payload) {
        return this.encode(payload, 86400, "Jwt", this.key, this.algo);
    }

    public String encode(Map<String, String> payload, long exp) {
        return this.encode(payload, exp, "Jwt", this.key, this.algo);
    }

    public String encode(Map<String, String> payload, long exp, String type) {
        return this.encode(payload, exp, type, this.key, this.algo);
    }

    public String encode(
        Map<String, String> payload,
        long exp,
        String type,
        byte[] key
    ) {
        return this.encode(payload, exp, type, key, this.algo);
    }

    public String encode(
        Map<String, String> payload,
        long exp,
        String type,
        byte[] key,
        String algo
    ) {
        long timestamp = System.currentTimeMillis() / 1000L;
        Map<String, String> header = new ConcurrentHashMap<>();
        header.put("alg", algo);
        header.put("typ", type);

        Map<String, String> mergePayload = new ConcurrentHashMap<>();
        if (this.defaultPayload != null) {
            mergePayload.putAll(this.defaultPayload);
        }
        mergePayload.put("iat", timestamp + "");
        mergePayload.put("nbf", timestamp + "");
        mergePayload.put("exp", timestamp + exp + "");
        mergePayload.putAll(payload);

        List<String> segments = new ArrayList<>();
        segments.add(
            Base64.encode(Objects.requireNonNull(Json.stringify(header)))
        );
        segments.add(
            Base64.encode(Objects.requireNonNull(Json.stringify(mergePayload)))
        );
        String sign = this.sign(String.join(".", segments), false, key, algo);
        segments.add(sign);
        return String.join(".", segments);
    }

    public Map<String, String> decode(String token) {
        return this.decode(token, this.key, this.algo);
    }

    public Map<String, String> decode(String token, byte[] key) {
        return this.decode(token, key, this.algo);
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> decode(String token, byte[] key, String algo) {
        long timestamp = System.currentTimeMillis() / 1000L;
        String[] deArray = token.split("\\.");
        if (deArray.length < 3) {
            throw new RuntimeException("Error segments count");
        }
        Map<String, String> header = Json.parse(
            StrUtil.str(Base64.decode(deArray[0]), StandardCharsets.UTF_8),
            Map.class
        );
        if (header == null) {
            throw new RuntimeException("Invalid header encoding");
        }
        Map<String, String> payload = Json.parse(
            StrUtil.str(Base64.decode(deArray[1]), StandardCharsets.UTF_8),
            Map.class
        );
        if (payload == null) {
            throw new RuntimeException("Invalid payload encoding");
        }
        String sign = StrUtil.str(
            Base64.decode(deArray[2]),
            StandardCharsets.UTF_8
        );
        if (
            !header.containsKey("alg") || SUPPORTED.contains(header.get("alg"))
        ) {
            if (algo == null) {
                throw new RuntimeException("Algorithm not supported or empty");
            }
        }
        if (algo == null) {
            algo = header.get("alg");
        }
        if (
            !this.verify(deArray[0] + "." + deArray[1], sign, true, key, algo)
        ) {
            throw new RuntimeException("Signature verification failed");
        }
        DateFormat dateFormat = new SimpleDateFormat(
            "EEE, dd MMM yyyy HH:mm:ss Z"
        );
        if (
            payload.containsKey("nbf") &&
            Long.parseLong(payload.get("nbf")) > timestamp
        ) {
            throw new RuntimeException(
                "The token is not yet valid [" +
                dateFormat.format(Long.parseLong(payload.get("nbf"))) +
                "]"
            );
        }
        if (
            payload.containsKey("iat") &&
            Long.parseLong(payload.get("iat")) > timestamp
        ) {
            throw new RuntimeException(
                "The token is not yet valid [" +
                dateFormat.format(Long.parseLong(payload.get("iat"))) +
                "]"
            );
        }
        if (
            payload.containsKey("exp") &&
            Long.parseLong(payload.get("exp")) < timestamp
        ) {
            throw new RuntimeException(
                "The token is not yet valid [" +
                dateFormat.format(Long.parseLong(payload.get("exp"))) +
                "]"
            );
        }
        return payload;
    }
}
