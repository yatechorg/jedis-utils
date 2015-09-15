package org.yatech.jedis.utils.lua;

/**
 * An argument placeholder for a string typed value
 *
 * Created by Yinon Avraham on 11/09/2015.
 */
public class LuaStringValueArgument extends LuaValueArgument<String> {

    LuaStringValueArgument(String name, String defaultValue) {
        super(name, defaultValue);
    }

    LuaStringValueArgument(String name) {
        super(name);
    }

}
