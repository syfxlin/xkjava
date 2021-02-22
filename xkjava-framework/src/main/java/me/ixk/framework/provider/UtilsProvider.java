/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.provider;

import java.security.NoSuchAlgorithmException;
import javax.crypto.NoSuchPaddingException;
import me.ixk.framework.annotation.condition.ConditionalOnMissingBean;
import me.ixk.framework.annotation.core.Bean;
import me.ixk.framework.annotation.core.Provider;
import me.ixk.framework.config.CryptProperties;
import me.ixk.framework.util.Crypt;
import me.ixk.framework.util.Hash;
import me.ixk.framework.util.Jwt;

/**
 * 工具类提供者
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 1:52
 */
@Provider
public class UtilsProvider {

    @Bean(name = "jwt")
    @ConditionalOnMissingBean(value = Jwt.class, name = "jwt")
    public Jwt jwt(final CryptProperties properties) {
        return new Jwt(
            properties.getDecodeKey(),
            properties.getHashAlgo(),
            properties.getHashDefaultPayload()
        );
    }

    @Bean(name = "crypt")
    @ConditionalOnMissingBean(value = Crypt.class, name = "crypt")
    public Crypt crypt(final CryptProperties properties)
        throws NoSuchAlgorithmException, NoSuchPaddingException {
        return new Crypt(properties.getDecodeKey(), properties.getCryptAlgo());
    }

    @Bean(name = "hash")
    @ConditionalOnMissingBean(value = Hash.class, name = "hash")
    public Hash hash() {
        return new Hash();
    }
}
