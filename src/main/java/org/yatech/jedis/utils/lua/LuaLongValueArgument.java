package org.yatech.jedis.utils.lua;

/**
 * An argument placeholder for a long typed value
 *
 * Created by Yinon Avraham on 11/09/2015.
 */
public class LuaLongValueArgument extends LuaValueArgument<Long> {

    LuaLongValueArgument(String name, long defaultValue) {
        super(name, defaultValue);
    }

    LuaLongValueArgument(String name) {
        super(name);
    }

}
