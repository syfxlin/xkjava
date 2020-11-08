/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.providers;

import java.security.NoSuchAlgorithmException;
import javax.crypto.NoSuchPaddingException;
import me.ixk.framework.annotations.Bean;
import me.ixk.framework.annotations.ConditionalOnMissingBean;
import me.ixk.framework.annotations.Provider;
import me.ixk.framework.config.CryptProperties;
import me.ixk.framework.utils.Crypt;
import me.ixk.framework.utils.Hash;
import me.ixk.framework.utils.Jwt;

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
    public Jwt jwt(CryptProperties properties) {
        return new Jwt(
            properties.getDecodeKey(),
            properties.getHashAlgo(),
            properties.getHashDefaultPayload()
        );
    }

    @Bean(name = "crypt")
    @ConditionalOnMissingBean(value = Crypt.class, name = "crypt")
    public Crypt crypt(CryptProperties properties)
        throws NoSuchAlgorithmException, NoSuchPaddingException {
        return new Crypt(properties.getDecodeKey(), properties.getCryptAlgo());
    }

    @Bean(name = "hash")
    @ConditionalOnMissingBean(value = Hash.class, name = "hash")
    public Hash hash() {
        return new Hash();
    }
}
