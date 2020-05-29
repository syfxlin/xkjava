package me.ixk.framework.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JWT {
    protected String key;

    protected String algo;

    protected Map<String, String> defaultPayload;

    protected static Map<String, String> supported;

    static {
        supported = new ConcurrentHashMap<>();
        supported.put("HS256", "SHA256");
        supported.put("HS384", "SHA384");
        supported.put("HS512", "SHA512");
    }

    public JWT(String key, String algo) {
        this(key, algo, null);
    }

    public JWT(String key, String algo, Map<String, String> defaultPayload) {
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

    public String sign(String token, boolean raw, String key) {
        return this.sign(token, raw, key, this.algo);
    }

    public String sign(String token, boolean raw, String key, String algo) {
        String sign = Mac.make("Hmac" + supported.get(algo), token, key, true);
        return raw ? sign : Base64.encode(sign);
    }

    public boolean verify(String token, String sign) {
        return this.verify(token, sign, false, this.key, this.algo);
    }

    public boolean verify(String token, String sign, boolean raw) {
        return this.verify(token, sign, raw, this.key, this.algo);
    }

    public boolean verify(String token, String sign, boolean raw, String key) {
        return this.verify(token, sign, raw, key, this.algo);
    }

    public boolean verify(
        String token,
        String sign,
        boolean raw,
        String key,
        String algo
    ) {
        token = raw ? token : Base64.decode(token);
        return this.sign(token, raw, key, algo).equals(sign);
    }

    public String encode(Map<String, String> payload) {
        return this.encode(payload, 86400, "JWT", this.key, this.algo);
    }

    public String encode(Map<String, String> payload, int exp) {
        return this.encode(payload, exp, "JWT", this.key, this.algo);
    }

    public String encode(Map<String, String> payload, int exp, String type) {
        return this.encode(payload, exp, type, this.key, this.algo);
    }

    public String encode(
        Map<String, String> payload,
        int exp,
        String type,
        String key
    ) {
        return this.encode(payload, exp, type, key, this.algo);
    }

    public String encode(
        Map<String, String> payload,
        long exp,
        String type,
        String key,
        String algo
    ) {
        long timestamp = new Date().getTime();
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
        segments.add(Base64.encode(JSON.stringify(header)));
        segments.add(Base64.encode(JSON.stringify(mergePayload)));
        String sign = this.sign(String.join(".", segments), false, key, algo);
        segments.add(sign);
        return String.join(".", segments);
    }

    public Map<String, String> decode(String token) {
        return this.decode(token, this.key, this.algo);
    }

    public Map<String, String> decode(String token, String key) {
        return this.decode(token, key, this.algo);
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> decode(String token, String key, String algo) {
        long timestamp = new Date().getTime();
        String[] deArray = token.split("\\.");
        if (deArray.length < 3) {
            throw new RuntimeException("Error segments count");
        }
        Map<String, String> header = JSON.parse(
            Base64.decode(deArray[0]),
            Map.class
        );
        if (header == null) {
            throw new RuntimeException("Invalid header encoding");
        }
        Map<String, String> payload = JSON.parse(
            Base64.decode(deArray[1]),
            Map.class
        );
        if (payload == null) {
            throw new RuntimeException("Invalid payload encoding");
        }
        String sign = Base64.decode(deArray[2]);
        if (
            !header.containsKey("alg") ||
            supported.containsKey(header.get("alg"))
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
