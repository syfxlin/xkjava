/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.utils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Ansi {
    public static final String SANE = "\u001B[0m";

    public static final String HIGH_INTENSITY = "\u001B[1m";
    public static final String LOW_INTENSITY = "\u001B[2m";

    public enum Color {
        BLACK("\u001B[30m"),
        RED("\u001B[31m"),
        GREEN("\u001B[32m"),
        YELLOW("\u001B[33m"),
        BLUE("\u001B[34m"),
        MAGENTA("\u001B[35m"),
        CYAN("\u001B[36m"),
        WHITE("\u001B[37m"),

        BACKGROUND_BLACK("\u001B[40m"),
        BACKGROUND_RED("\u001B[41m"),
        BACKGROUND_GREEN("\u001B[42m"),
        BACKGROUND_YELLOW("\u001B[43m"),
        BACKGROUND_BLUE("\u001B[44m"),
        BACKGROUND_MAGENTA("\u001B[45m"),
        BACKGROUND_CYAN("\u001B[46m"),
        BACKGROUND_WHITE("\u001B[47m"),;

        private final String ansi;

        Color(String ansi) {
            this.ansi = ansi;
        }

        public String getAnsi() {
            return this.ansi;
        }

        @Override
        public String toString() {
            return this.ansi;
        }
    }

    public enum Style {
        ITALIC("\u001B[3m"),
        UNDERLINE("\u001B[4m"),
        BLINK("\u001B[5m"),
        RAPID_BLINK("\u001B[6m"),
        REVERSE_VIDEO("\u001B[7m"),
        INVISIBLE_TEXT("\u001B[8m"),;

        private final String ansi;

        Style(String ansi) {
            this.ansi = ansi;
        }

        public String getAnsi() {
            return this.ansi;
        }

        @Override
        public String toString() {
            return this.ansi;
        }
    }

    private final List<String> ansi;

    public Ansi(String... ansi) {
        this.ansi = new LinkedList<>(Arrays.asList(ansi));
    }

    public Ansi and(Color color) {
        this.ansi.add(color.getAnsi());
        return this;
    }

    public Ansi and(Style style) {
        this.ansi.add(style.getAnsi());
        return this;
    }

    public String format(String text, Object... args) {
        StringBuilder builder = new StringBuilder();
        for (String s : ansi) {
            builder.append(s);
        }
        builder.append(text);
        builder.append(SANE);
        return String.format(builder.toString(), args);
    }

    public static Ansi make(Color color) {
        return new Ansi(color.getAnsi());
    }

    public static Ansi make(Style style) {
        return new Ansi(style.getAnsi());
    }
}
