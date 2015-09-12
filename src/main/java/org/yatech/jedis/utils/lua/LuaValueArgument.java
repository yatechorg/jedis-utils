package org.yatech.jedis.utils.lua;

/**
 * Created by Yinon Avraham on 11/09/2015.
 */
public abstract class LuaValueArgument<T> extends LuaArgument<T> {

    LuaValueArgument(String name, T defaultValue) {
        super(name, defaultValue);
    }

    LuaValueArgument(String name) {
        super(name);
    }
}
