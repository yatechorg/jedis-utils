package org.yatech.jedis.collections;

/**
 * <p>Created on 14/05/16
 *
 * @author Yinon Avraham
 */
final class Utils {

    private Utils() {}

    static void assertNotNull(Object argValue, String argName) {
        if (argValue == null) {
            throw new IllegalArgumentException(argName + ": must not be null");
        }
    }

    static void assertTrue(boolean condition, String argName, String message) {
        if (!condition) {
            throw new IllegalArgumentException(argName + ": " + message);
        }
    }
}
