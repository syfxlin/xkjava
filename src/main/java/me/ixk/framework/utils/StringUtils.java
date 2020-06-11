package me.ixk.framework.utils;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.*;

public abstract class StringUtils {
    public static final String[] EMPTY_STRING_ARRAY = new String[0];

    public static boolean isEmpty(Object str) {
        return (str == null || "".equals(str));
    }

    public static boolean hasLength(String str) {
        return (str != null && !str.isEmpty());
    }

    public static boolean containsText(String str) {
        if (!hasLength(str)) {
            return false;
        }

        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsWhitespace(String str) {
        if (!hasLength(str)) {
            return false;
        }

        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static String trim(String str) {
        if (!hasLength(str)) {
            return str;
        }

        return str.trim();
    }

    public static String trimAll(String str) {
        if (!hasLength(str)) {
            return str;
        }

        int len = str.length();
        StringBuilder sb = new StringBuilder(str.length());
        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            if (!Character.isWhitespace(c)) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String ltrim(String str) {
        if (!hasLength(str)) {
            return str;
        }

        StringBuilder sb = new StringBuilder(str);
        while (sb.length() > 0 && Character.isWhitespace(sb.charAt(0))) {
            sb.deleteCharAt(0);
        }
        return sb.toString();
    }

    public static String rtrim(String str) {
        if (!hasLength(str)) {
            return str;
        }

        StringBuilder sb = new StringBuilder(str);
        while (
            sb.length() > 0 &&
            Character.isWhitespace(sb.charAt(sb.length() - 1))
        ) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public static boolean startsWithIgnoreCase(String str, String prefix) {
        return (
            str != null &&
            prefix != null &&
            str.length() >= prefix.length() &&
            str.regionMatches(true, 0, prefix, 0, prefix.length())
        );
    }

    public static boolean endsWithIgnoreCase(String str, String suffix) {
        return (
            str != null &&
            suffix != null &&
            str.length() >= suffix.length() &&
            str.regionMatches(
                true,
                str.length() - suffix.length(),
                suffix,
                0,
                suffix.length()
            )
        );
    }

    public static String urldecode(String source, Charset charset) {
        int length = source.length();
        if (length == 0) {
            return source;
        }
        Assert.notNull(charset, "Charset must not be null");

        ByteArrayOutputStream bos = new ByteArrayOutputStream(length);
        boolean changed = false;
        for (int i = 0; i < length; i++) {
            int ch = source.charAt(i);
            if (ch == '%') {
                if (i + 2 < length) {
                    char hex1 = source.charAt(i + 1);
                    char hex2 = source.charAt(i + 2);
                    int u = Character.digit(hex1, 16);
                    int l = Character.digit(hex2, 16);
                    if (u == -1 || l == -1) {
                        throw new IllegalArgumentException(
                            "Invalid encoded sequence \"" +
                            source.substring(i) +
                            "\""
                        );
                    }
                    bos.write((char) ((u << 4) + l));
                    i += 2;
                    changed = true;
                } else {
                    throw new IllegalArgumentException(
                        "Invalid encoded sequence \"" +
                        source.substring(i) +
                        "\""
                    );
                }
            } else {
                bos.write(ch);
            }
        }
        return (changed ? new String(bos.toByteArray(), charset) : source);
    }

    public static String[] toArray(Collection<String> collection) {
        if (collection == null || collection.isEmpty()) {
            return EMPTY_STRING_ARRAY;
        }
        return collection.toArray(EMPTY_STRING_ARRAY);
    }

    public static String[] toArray(Enumeration<String> enumeration) {
        if (enumeration == null) {
            return EMPTY_STRING_ARRAY;
        }
        return toArray(Collections.list(enumeration));
    }

    public static String[] addToArray(String[] array, String str) {
        if (array == null || array.length == 0) {
            return new String[] { str };
        }
        String[] newArr = new String[array.length + 1];
        System.arraycopy(array, 0, newArr, 0, array.length);
        newArr[array.length] = str;
        return newArr;
    }

    public static String[] concatArray(String[] array1, String[] array2) {
        if (array1 == null || array1.length == 0) {
            return array2;
        }
        if (array2 == null || array2.length == 0) {
            return array1;
        }
        String[] newArr = new String[array1.length + array2.length];
        System.arraycopy(array1, 0, newArr, 0, array1.length);
        System.arraycopy(array2, 0, newArr, array1.length, array2.length);
        return newArr;
    }

    public static String[] mergeArray(String[] array1, String[] array2) {
        if (array1 == null || array1.length == 0) {
            return array2;
        }
        if (array2 == null || array2.length == 0) {
            return array1;
        }

        List<String> result = new ArrayList<>(Arrays.asList(array1));
        for (String str : array2) {
            if (!result.contains(str)) {
                result.add(str);
            }
        }
        return toArray(result);
    }

    public static String[] sortArray(String[] array) {
        if (array == null || array.length == 0) {
            return array;
        }
        Arrays.sort(array);
        return array;
    }

    public static String[] removeDuplicate(String[] array) {
        if (array == null || array.length == 0) {
            return array;
        }
        Set<String> set = new LinkedHashSet<>(Arrays.asList(array));
        return toArray(set);
    }

    public static String[] removeEmpty(String[] array) {
        List<String> list = new ArrayList<>();
        for (String str : array) {
            if (str != null && str.length() > 0) {
                list.add(str);
            }
        }
        return toArray(list);
    }
}
