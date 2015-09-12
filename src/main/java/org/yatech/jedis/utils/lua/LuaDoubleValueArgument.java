package org.yatech.jedis.utils.lua;

/**
 * Created by Yinon Avraham on 11/09/2015.
 */
public class LuaDoubleValueArgument extends LuaValueArgument<Double> {

    LuaDoubleValueArgument(String name, double defaultValue) {
        super(name, defaultValue);
    }

    LuaDoubleValueArgument(String name) {
        super(name);
    }

}
