package me.ixk.utils;

import java.nio.charset.StandardCharsets;
import javax.crypto.spec.SecretKeySpec;

public class Mac {

    protected static String byteArrayToHexString(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (int n = 0; b != null && n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1) hs.append('0');
            hs.append(stmp);
        }
        return hs.toString().toLowerCase();
    }

    public static String make(String algo, String data, String key) {
        return make(
            algo,
            data,
            key.getBytes(StandardCharsets.ISO_8859_1),
            false
        );
    }

    public static String make(String algo, String data, byte[] key) {
        return make(algo, data, key, false);
    }

    public static String make(
        String algo,
        String data,
        String key,
        boolean raw
    ) {
        return make(algo, data, key.getBytes(StandardCharsets.ISO_8859_1), raw);
    }

    public static String make(
        String algo,
        String data,
        byte[] key,
        boolean raw
    ) {
        try {
            SecretKeySpec hmacKeySpec = new SecretKeySpec(key, algo);
            javax.crypto.Mac hmac = javax.crypto.Mac.getInstance(algo);
            hmac.init(hmacKeySpec);
            byte[] rawHmac = hmac.doFinal(
                data.getBytes(StandardCharsets.ISO_8859_1)
            );
            return raw
                ? new String(rawHmac, StandardCharsets.ISO_8859_1)
                : byteArrayToHexString(rawHmac);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}