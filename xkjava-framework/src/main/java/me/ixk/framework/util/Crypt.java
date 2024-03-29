/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 加密工具类
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 5:02
 */
public class Crypt {

    private final byte[] key;

    private final Cipher cipher;

    public Crypt(final byte[] key)
        throws NoSuchAlgorithmException, NoSuchPaddingException {
        this(key, "AES/CBC/PKCS5PADDING");
    }

    public Crypt(final byte[] key, final String cipher)
        throws NoSuchPaddingException, NoSuchAlgorithmException {
        this.key = key;
        this.cipher = Cipher.getInstance(cipher);
    }

    public static byte[] generateKey() {
        return generateRandom(256);
    }

    public static byte[] generateIv() {
        return generateRandom(128);
    }

    public static byte[] generateRandom(final int length) {
        final KeyGenerator generator;
        try {
            generator = KeyGenerator.getInstance("AES");
            generator.init(length);
            final SecretKey key = generator.generateKey();
            return key.getEncoded();
        } catch (final NoSuchAlgorithmException e) {
            return null;
        }
    }

    public String encrypt(final String value) {
        try {
            final IvParameterSpec iv = new IvParameterSpec(generateIv());
            final SecretKeySpec aesKeySpec = new SecretKeySpec(key, "AES");

            cipher.init(Cipher.ENCRYPT_MODE, aesKeySpec, iv);

            final String encrypted = new String(
                cipher.doFinal(value.getBytes(StandardCharsets.ISO_8859_1)),
                StandardCharsets.ISO_8859_1
            );
            final String ivEncoded = Base64.encode(iv.getIV());
            final String macEncoded = Mac.make(
                "HmacSHA256",
                ivEncoded + encrypted,
                key
            );
            final ObjectNode json = Json.createObject();
            json.put("iv", ivEncoded);
            json.put("value", encrypted);
            json.put("mac", macEncoded);
            return Base64.encode(json.toString());
        } catch (final Exception e) {
            return null;
        }
    }

    public String decrypt(final String encrypted) {
        try {
            final ObjectNode payload = Json.parseObject(
                StrUtil.str(Base64.decode(encrypted), StandardCharsets.UTF_8)
            );
            if (!this.vaild(Objects.requireNonNull(payload))) {
                return null;
            }
            final IvParameterSpec iv = new IvParameterSpec(
                Base64.decode(payload.get("iv").asText())
            );
            final SecretKeySpec aesKeySpec = new SecretKeySpec(key, "AES");
            cipher.init(Cipher.DECRYPT_MODE, aesKeySpec, iv);
            return new String(
                cipher.doFinal(
                    payload
                        .get("value")
                        .asText()
                        .getBytes(StandardCharsets.ISO_8859_1)
                ),
                StandardCharsets.ISO_8859_1
            );
        } catch (final Exception e) {
            return null;
        }
    }

    public boolean vaild(final ObjectNode payload) {
        if (
            !payload.has("iv") || !payload.has("mac") || !payload.has("value")
        ) {
            return false;
        }
        if (Base64.decode(payload.get("iv").asText()).length != 16) {
            return false;
        }
        return payload
            .get("mac")
            .asText()
            .equals(
                Mac.make(
                    "HmacSHA256",
                    payload.get("iv").asText() + payload.get("value").asText(),
                    key
                )
            );
    }
}
