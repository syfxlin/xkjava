/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.framework.expression;

import io.github.imsejin.expression.util.StringUtils;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author Otstar Lin
 * @date 2020/11/28 下午 11:12
 */
public class PropertyPlaceholderHelper {

    private static final Map<String, String> WELL_KNOWN_SIMPLE_PREFIXES = new HashMap<>(
        4
    );

    static {
        WELL_KNOWN_SIMPLE_PREFIXES.put("}", "{");
        WELL_KNOWN_SIMPLE_PREFIXES.put("]", "[");
        WELL_KNOWN_SIMPLE_PREFIXES.put(")", "(");
    }

    private final String placeholderPrefix;

    private final String placeholderSuffix;

    private final String simplePrefix;

    private final String valueSeparator;

    private final boolean ignoreUnresolvablePlaceholders;

    public PropertyPlaceholderHelper(
        final String placeholderPrefix,
        final String placeholderSuffix
    ) {
        this(placeholderPrefix, placeholderSuffix, null, true);
    }

    public PropertyPlaceholderHelper(
        final String placeholderPrefix,
        final String placeholderSuffix,
        final String valueSeparator,
        final boolean ignoreUnresolvablePlaceholders
    ) {
        this.placeholderPrefix = placeholderPrefix;
        this.placeholderSuffix = placeholderSuffix;
        final String simplePrefixForSuffix = WELL_KNOWN_SIMPLE_PREFIXES.get(
            this.placeholderSuffix
        );
        if (
            simplePrefixForSuffix != null &&
            this.placeholderPrefix.endsWith(simplePrefixForSuffix)
        ) {
            this.simplePrefix = simplePrefixForSuffix;
        } else {
            this.simplePrefix = this.placeholderPrefix;
        }
        this.valueSeparator = valueSeparator;
        this.ignoreUnresolvablePlaceholders = ignoreUnresolvablePlaceholders;
    }

    public String replacePlaceholders(
        final String value,
        final Properties properties
    ) {
        return replacePlaceholders(value, properties::getProperty);
    }

    public String replacePlaceholders(
        final String value,
        final PlaceholderResolver placeholderResolver
    ) {
        return parseStringValue(value, placeholderResolver, null);
    }

    protected String parseStringValue(
        final String value,
        final PlaceholderResolver placeholderResolver,
        Set<String> visitedPlaceholders
    ) {
        int startIndex = value.indexOf(this.placeholderPrefix);
        if (startIndex == -1) {
            return value;
        }

        final StringBuilder result = new StringBuilder(value);
        while (startIndex != -1) {
            final int endIndex = findPlaceholderEndIndex(result, startIndex);
            if (endIndex != -1) {
                String placeholder = result.substring(
                    startIndex + this.placeholderPrefix.length(),
                    endIndex
                );
                final String originalPlaceholder = placeholder;
                if (visitedPlaceholders == null) {
                    visitedPlaceholders = new HashSet<>(4);
                }
                if (!visitedPlaceholders.add(originalPlaceholder)) {
                    throw new IllegalArgumentException(
                        "Circular placeholder reference '" +
                        originalPlaceholder +
                        "' in property definitions"
                    );
                }
                // Recursive invocation, parsing placeholders contained in the placeholder key.
                placeholder =
                    parseStringValue(
                        placeholder,
                        placeholderResolver,
                        visitedPlaceholders
                    );
                // Now obtain the value for the fully resolved key...
                String propVal = placeholderResolver.resolvePlaceholder(
                    placeholder
                );
                if (propVal == null && this.valueSeparator != null) {
                    final int separatorIndex = placeholder.indexOf(
                        this.valueSeparator
                    );
                    if (separatorIndex != -1) {
                        final String actualPlaceholder = placeholder.substring(
                            0,
                            separatorIndex
                        );
                        final String defaultValue = placeholder.substring(
                            separatorIndex + this.valueSeparator.length()
                        );
                        propVal =
                            placeholderResolver.resolvePlaceholder(
                                actualPlaceholder
                            );
                        if (propVal == null) {
                            propVal = defaultValue;
                        }
                    }
                }
                if (propVal != null) {
                    // Recursive invocation, parsing placeholders contained in the
                    // previously resolved placeholder value.
                    propVal =
                        parseStringValue(
                            propVal,
                            placeholderResolver,
                            visitedPlaceholders
                        );
                    result.replace(
                        startIndex,
                        endIndex + this.placeholderSuffix.length(),
                        propVal
                    );
                    startIndex =
                        result.indexOf(
                            this.placeholderPrefix,
                            startIndex + propVal.length()
                        );
                } else if (this.ignoreUnresolvablePlaceholders) {
                    // Proceed with unprocessed value.
                    startIndex =
                        result.indexOf(
                            this.placeholderPrefix,
                            endIndex + this.placeholderSuffix.length()
                        );
                } else {
                    throw new IllegalArgumentException(
                        "Could not resolve placeholder '" +
                        placeholder +
                        "'" +
                        " in value \"" +
                        value +
                        "\""
                    );
                }
                visitedPlaceholders.remove(originalPlaceholder);
            } else {
                startIndex = -1;
            }
        }
        return result.toString();
    }

    private int findPlaceholderEndIndex(
        final CharSequence buf,
        final int startIndex
    ) {
        int index = startIndex + this.placeholderPrefix.length();
        int withinNestedPlaceholder = 0;
        while (index < buf.length()) {
            if (
                StringUtils.substringMatch(buf, index, this.placeholderSuffix)
            ) {
                if (withinNestedPlaceholder > 0) {
                    withinNestedPlaceholder--;
                    index = index + this.placeholderSuffix.length();
                } else {
                    return index;
                }
            } else if (
                StringUtils.substringMatch(buf, index, this.simplePrefix)
            ) {
                withinNestedPlaceholder++;
                index = index + this.simplePrefix.length();
            } else {
                index++;
            }
        }
        return -1;
    }

    @FunctionalInterface
    public interface PlaceholderResolver {
        /**
         * 处理占位符
         *
         * @param placeholderName 占位符
         *
         * @return 结果
         */
        String resolvePlaceholder(String placeholderName);
    }
}
