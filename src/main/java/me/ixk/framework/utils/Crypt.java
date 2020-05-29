package me.ixk.framework.utils;

import com.google.gson.JsonObject;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

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
            JsonObject json = new JsonObject();
            json.addProperty("iv", ivEncoded);
            json.addProperty("value", encrypted);
            json.addProperty("mac", macEncoded);
            return Base64.encode(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String decrypt(String encrypted) {
        try {
            JsonObject payload = JSON.parseObject(Base64.decode(encrypted));
            if (!this.vaild(payload)) {
                return null;
            }
            IvParameterSpec iv = new IvParameterSpec(
                Base64
                    .decode(payload.get("iv").getAsString())
                    .getBytes(StandardCharsets.ISO_8859_1)
            );
            SecretKeySpec aesKeySpec = new SecretKeySpec(key, "AES");
            cipher.init(Cipher.DECRYPT_MODE, aesKeySpec, iv);
            return new String(
                cipher.doFinal(
                    payload
                        .get("value")
                        .getAsString()
                        .getBytes(StandardCharsets.ISO_8859_1)
                ),
                StandardCharsets.ISO_8859_1
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean vaild(JsonObject payload) {
        if (
            !payload.has("iv") || !payload.has("mac") || !payload.has("value")
        ) {
            return false;
        }
        if (Base64.decode(payload.get("iv").getAsString()).length() != 16) {
            return false;
        }
        return payload
            .get("mac")
            .getAsString()
            .equals(
                Mac.make(
                    "HmacSHA256",
                    payload.get("iv").getAsString() +
                    payload.get("value").getAsString(),
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
