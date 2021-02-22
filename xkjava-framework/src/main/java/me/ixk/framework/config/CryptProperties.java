/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.config;

import cn.hutool.core.codec.Base64;
import java.util.Map;
import me.ixk.framework.annotation.core.ConfigurationProperties;
import me.ixk.framework.annotation.core.PropertyValue;
import me.ixk.framework.ioc.PropertyResolver.StringMapResolver;

/**
 * 加密配置文件
 *
 * @author Otstar Lin
 * @date 2020/11/8 下午 7:20
 */
@ConfigurationProperties(prefix = "xkjava.crypt", ignoreUnknownFields = false)
public class CryptProperties {

    private String key;

    @PropertyValue(skip = true)
    private byte[] decodeKey;

    @PropertyValue(name = "crypt.algo", defaultValue = "AES/CBC/PKCS5PADDING")
    private String cryptAlgo;

    @PropertyValue(name = "hash.algo", defaultValue = "SHA256")
    private String hashAlgo;

    @PropertyValue(
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

    public byte[] getDecodeKey() {
        if (decodeKey == null) {
            decodeKey = Base64.decode(key);
        }
        return decodeKey;
    }
}
