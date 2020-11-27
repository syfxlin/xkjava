/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.cache;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.codec.RedisCodec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import me.ixk.framework.utils.Convert;
import org.jetbrains.annotations.Nullable;

/**
 * Redis 缓存
 *
 * @author Otstar Lin
 * @date 2020/11/27 下午 6:39
 */
public class RedisCache implements Cache {
    private final String name;
    private final RedisClient client;

    public RedisCache(final String name, final RedisClient client) {
        this.name = name;
        this.client = client;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object getNativeCache() {
        return this.client;
    }

    @Override
    public <T> T get(final Object key, final Class<T> returnType) {
        return Convert.convert(returnType, this.getCommands().get(key));
    }

    @Override
    public void put(final Object key, final Object value) {
        this.getCommands().set(key, value);
    }

    @Override
    public void evict(final Object key) {
        this.getCommands().del(key);
    }

    @Override
    public void clear() {
        this.getCommands().flushall();
    }

    private RedisCommands<Object, Object> getCommands() {
        final StatefulRedisConnection<Object, Object> connect =
            this.client.connect(new SerializedObjectCodec());
        return connect.sync();
    }

    public static class SerializedObjectCodec
        implements RedisCodec<Object, Object> {

        @Override
        public Object decodeKey(final ByteBuffer bytes) {
            return getObject(bytes);
        }

        @Override
        public Object decodeValue(final ByteBuffer bytes) {
            return getObject(bytes);
        }

        @Override
        public ByteBuffer encodeKey(final Object key) {
            return getBuffer(key);
        }

        @Override
        public ByteBuffer encodeValue(final Object value) {
            return getBuffer(value);
        }

        @Nullable
        private Object getObject(final ByteBuffer bytes) {
            try {
                final byte[] array = new byte[bytes.remaining()];
                bytes.get(array);
                final ObjectInputStream is = new ObjectInputStream(
                    new ByteArrayInputStream(array)
                );
                return is.readObject();
            } catch (final Exception e) {
                return null;
            }
        }

        @Nullable
        private ByteBuffer getBuffer(final Object key) {
            try {
                final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                final ObjectOutputStream os = new ObjectOutputStream(bytes);
                os.writeObject(key);
                return ByteBuffer.wrap(bytes.toByteArray());
            } catch (final IOException e) {
                return null;
            }
        }
    }
}
