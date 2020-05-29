package me.ixk.utils;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class Hash {

    public String make(String value) {
        return BCrypt.withDefaults().hashToString(10, value.toCharArray());
    }

    public boolean check(String value, String hashedValue) {
        return BCrypt
            .verifyer()
            .verify(value.toCharArray(), hashedValue.toCharArray())
            .verified;
    }
}
