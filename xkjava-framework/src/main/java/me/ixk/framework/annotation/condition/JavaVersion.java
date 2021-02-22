/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotation.condition;

/**
 * Java 版本
 *
 * @author Otstar Lin
 * @date 2020/10/13 下午 4:59
 */
public enum JavaVersion {
    /**
     * Java 8
     */
    EIGHT(8),
    /**
     * Java 8
     */
    NINE(9),
    /**
     * Java 10
     */
    TEN(10),
    /**
     * Java 11
     */
    ELEVEN(11),
    /**
     * Java 12
     */
    TWELVE(12),
    /**
     * Java 13
     */
    THIRTEEN(13),
    /**
     * Java 14
     */
    FOURTEEN(14),
    /**
     * Java 15
     */
    FIFTEEN(15);

    private final int version;

    JavaVersion(int version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return this.version + "";
    }

    public int getVersion() {
        return version;
    }

    public boolean isEqualOrNewerThan(JavaVersion version) {
        return version.getVersion() >= this.getVersion();
    }

    public boolean isOlderThan(JavaVersion version) {
        return version.getVersion() < this.getVersion();
    }

    public boolean isEqualOrNewerThan(int version) {
        return version >= this.getVersion();
    }

    public boolean isOlderThan(int version) {
        return version < this.getVersion();
    }

    public static int getCurrentVersion() {
        String version = System.getProperty("java.version");
        if (version.startsWith("1.")) {
            version = version.substring(2, 3);
        } else {
            int dot = version.indexOf(".");
            if (dot != -1) {
                version = version.substring(0, dot);
            }
        }
        return Integer.parseInt(version);
    }
}
