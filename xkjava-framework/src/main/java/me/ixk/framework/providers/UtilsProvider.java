/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.providers;

import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ConcurrentHashMap;
import javax.crypto.NoSuchPaddingException;
import me.ixk.framework.annotations.Bean;
import me.ixk.framework.annotations.ConditionalOnMissingBean;
import me.ixk.framework.annotations.Provider;
import me.ixk.framework.kernel.Environment;
import me.ixk.framework.utils.Base64;
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
    public Jwt jwt(Environment env) {
        return new Jwt(
            Base64.decode(env.get("app.key", String.class)),
            env.get("app.hash.algo", "HS256"),
            env.get("app.hash.default_payload", new ConcurrentHashMap<>())
        );
    }

    @Bean(name = "crypt")
    @ConditionalOnMissingBean(value = Crypt.class, name = "crypt")
    public Crypt crypt(Environment env)
        throws NoSuchAlgorithmException, NoSuchPaddingException {
        return new Crypt(
            Base64.decode(env.get("app.key", String.class)),
            env.get("app.cipher", "AES/CBC/PKCS5PADDING")
        );
    }

    @Bean(name = "hash")
    @ConditionalOnMissingBean(value = Hash.class, name = "hash")
    public Hash hash() {
        return new Hash();
    }
}
