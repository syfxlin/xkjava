package me.ixk.framework.facades;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class Crypt extends AbstractFacade {

    protected static me.ixk.framework.utils.Crypt make() {
        return app.make(me.ixk.framework.utils.Crypt.class);
    }

    public static String encrypt(String value) {
        return make().encrypt(value);
    }

    public static String decrypt(String encrypted) {
        return make().decrypt(encrypted);
    }

    public static boolean vaild(ObjectNode payload) {
        return make().vaild(payload);
    }

    public static byte[] generateKey() {
        return make().generateKey();
    }

    public static byte[] generateIv() {
        return make().generateIv();
    }

    public static byte[] generateRandom(int length) {
        return make().generateRandom(length);
    }
}
