/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.util;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Ansi
 *
 * @author Otstar Lin
 * @date 2020/10/14 下午 4:57
 */
public class Ansi {

    public static final String SANE = "\u001B[0m";

    public static final String HIGH_INTENSITY = "\u001B[1m";
    public static final String LOW_INTENSITY = "\u001B[2m";

    public enum Color {
        /**
         * 黑色
         */
        BLACK("\u001B[30m"),
        /**
         * 红色
         */
        RED("\u001B[31m"),
        /**
         * 绿色
         */
        GREEN("\u001B[32m"),
        /**
         * 黄色
         */
        YELLOW("\u001B[33m"),
        /**
         * 蓝色
         */
        BLUE("\u001B[34m"),
        /**
         * 品红色
         */
        MAGENTA("\u001B[35m"),
        /**
         * 青色
         */
        CYAN("\u001B[36m"),
        /**
         * 白色
         */
        WHITE("\u001B[37m"),

        /**
         * 背景黑色
         */
        BACKGROUND_BLACK("\u001B[40m"),
        /**
         * 背景红色
         */
        BACKGROUND_RED("\u001B[41m"),
        /**
         * 背景绿色
         */
        BACKGROUND_GREEN("\u001B[42m"),
        /**
         * 背景黄色
         */
        BACKGROUND_YELLOW("\u001B[43m"),
        /**
         * 背景蓝色
         */
        BACKGROUND_BLUE("\u001B[44m"),
        /**
         * 背景品红色
         */
        BACKGROUND_MAGENTA("\u001B[45m"),
        /**
         * 背景青色
         */
        BACKGROUND_CYAN("\u001B[46m"),
        /**
         * 背景白色
         */
        BACKGROUND_WHITE("\u001B[47m");

        private final String ansi;

        Color(final String ansi) {
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
        /**
         * 斜体
         */
        ITALIC("\u001B[3m"),
        /**
         * 下划线
         */
        UNDERLINE("\u001B[4m"),
        /**
         * 闪烁
         */
        BLINK("\u001B[5m"),
        /**
         * 快速闪烁
         */
        RAPID_BLINK("\u001B[6m"),
        /**
         * 反转
         */
        REVERSE_VIDEO("\u001B[7m"),
        /**
         * 隐藏
         */
        INVISIBLE_TEXT("\u001B[8m");

        private final String ansi;

        Style(final String ansi) {
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

    public Ansi(final String... ansi) {
        this.ansi = new LinkedList<>(Arrays.asList(ansi));
    }

    public Ansi and(final Color color) {
        this.ansi.add(color.getAnsi());
        return this;
    }

    public Ansi and(final Style style) {
        this.ansi.add(style.getAnsi());
        return this;
    }

    public String format(final String text, final Object... args) {
        final StringBuilder builder = new StringBuilder();
        for (final String s : ansi) {
            builder.append(s);
        }
        builder.append(text);
        builder.append(SANE);
        return String.format(builder.toString(), args);
    }

    public static Ansi make(final Color color) {
        return new Ansi(color.getAnsi());
    }

    public static Ansi make(final Style style) {
        return new Ansi(style.getAnsi());
    }

    public static String split() {
        return new Ansi(Color.GREEN.getAnsi()).format("   |   ");
    }
}
