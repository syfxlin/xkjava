package me.ixk.framework.utils;

import com.fasterxml.jackson.databind.node.ObjectNode;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Crypt {
    protected byte[] key;

    protected Cipher cipher;

    public Crypt(String key)
        throws NoSuchAlgorithmException, NoSuchPaddingException {
        this(key, "AES/CBC/PKCS5PADDING");
    }

    public Crypt(String key, String cipher)
        throws NoSuchPaddingException, NoSuchAlgorithmException {
        this.key = key.getBytes(StandardCharsets.ISO_8859_1);
        this.cipher = Cipher.getInstance(cipher);
    }

    public String encrypt(String value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(this.generateIv());
            SecretKeySpec aesKeySpec = new SecretKeySpec(key, "AES");

            cipher.init(Cipher.ENCRYPT_MODE, aesKeySpec, iv);

            String encrypted = new String(
                cipher.doFinal(value.getBytes(StandardCharsets.ISO_8859_1)),
                StandardCharsets.ISO_8859_1
            );
            String ivEncoded = Base64.encode(iv.getIV());
            String macEncoded = Mac.make(
                "HmacSHA256",
                ivEncoded + encrypted,
                key
            );
            ObjectNode json = JSON.createObject();
            json.put("iv", ivEncoded);
            json.put("value", encrypted);
            json.put("mac", macEncoded);
            return Base64.encode(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String decrypt(String encrypted) {
        try {
            ObjectNode payload = JSON.parseObject(Base64.decode(encrypted));
            if (!this.vaild(payload)) {
                return null;
            }
            IvParameterSpec iv = new IvParameterSpec(
                Base64
                    .decode(payload.get("iv").asText())
                    .getBytes(StandardCharsets.ISO_8859_1)
            );
            SecretKeySpec aesKeySpec = new SecretKeySpec(key, "AES");
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
        } catch (Exception e) {
            //            e.printStackTrace();
        }
        return null;
    }

    public boolean vaild(ObjectNode payload) {
        if (
            !payload.has("iv") || !payload.has("mac") || !payload.has("value")
        ) {
            return false;
        }
        if (Base64.decode(payload.get("iv").asText()).length() != 16) {
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

    public byte[] generateKey() {
        return this.generateRandom(256);
    }

    public byte[] generateIv() {
        return this.generateRandom(128);
    }

    public byte[] generateRandom(int length) {
        KeyGenerator generator;
        try {
            generator = KeyGenerator.getInstance("AES");
            generator.init(length);
            SecretKey key = generator.generateKey();
            return key.getEncoded();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
