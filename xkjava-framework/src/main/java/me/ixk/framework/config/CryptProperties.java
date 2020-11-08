/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.config;

import java.util.Map;
import me.ixk.framework.annotations.ConfigurationProperties;
import me.ixk.framework.annotations.Property;
import me.ixk.framework.config.PropertyResolver.StringMapResolver;
import me.ixk.framework.utils.Base64;

/**
 * 加密配置文件
 *
 * @author Otstar Lin
 * @date 2020/11/8 下午 7:20
 */
@ConfigurationProperties(prefix = "xkjava.crypt", ignoreUnknownFields = false)
public class CryptProperties {
    private String key;

    @Property(skip = true)
    private String decodeKey;

    @Property(name = "crypt.algo", defaultValue = "AES/CBC/PKCS5PADDING")
    private String cryptAlgo;

    @Property(name = "hash.algo", defaultValue = "SHA256")
    private String hashAlgo;

    @Property(
        name = "hash.defaultPayload",
        resolver = StringMapResolver.class,
        defaultValue = ""
    )
    private Map<String, String> hashDefaultPayload;

    public String getKey() {
        return key;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    public String getCryptAlgo() {
        return cryptAlgo;
    }

    public void setCryptAlgo(final String cryptAlgo) {
        this.cryptAlgo = cryptAlgo;
    }

    public String getHashAlgo() {
        return hashAlgo;
    }

    public void setHashAlgo(final String hashAlgo) {
        this.hashAlgo = hashAlgo;
    }

    public Map<String, String> getHashDefaultPayload() {
        return hashDefaultPayload;
    }

    public void setHashDefaultPayload(
        final Map<String, String> hashDefaultPayload
    ) {
        this.hashDefaultPayload = hashDefaultPayload;
    }

    public String getDecodeKey() {
        if (decodeKey == null) {
            decodeKey = Base64.decode(key);
        }
        return decodeKey;
    }
}
