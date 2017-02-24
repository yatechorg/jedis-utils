package org.yatech.jedis.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * <p>Created on 14/05/16
 *
 * @author Yinon Avraham
 */
final class Utils {

    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    private Utils() {}

    static <T> T checkNotNull(T argValue, String argName) {
        checkTrue(argValue != null, argName, "must not be null");
        return argValue;
    }

    static int checkNotNegative(int argValue, String argName) {
        checkTrue(argValue >= 0, argName, "must be non-negative");
        return argValue;
    }

    static void checkTrue(boolean condition, String argName, String message) {
        if (!condition) {
            throw new IllegalArgumentException(argName + ": " + message);
        }
    }
    static String[] toStringArray(Collection<?> collection) {
        if (collection == null) {
            return null;
        }
        if (collection.isEmpty()) {
            return EMPTY_STRING_ARRAY;
        }
        List<String> list = new ArrayList<>(collection.size());
        for (Object o : collection) {
            list.add(o == null ? null : o.toString());
        }
        return list.toArray(new String[list.size()]);
    }
}
