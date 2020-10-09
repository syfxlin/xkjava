/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.annotations;

public enum JavaVersion {
    EIGHT(8),
    NINE(9),
    TEN(10),
    ELEVEN(11),
    TWELVE(12),
    THIRTEEN(13),
    FOURTEEN(14),
    FIFTEEN(15),;

    private final int version;

    JavaVersion(int version) {
        this.version = version;
    }

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
