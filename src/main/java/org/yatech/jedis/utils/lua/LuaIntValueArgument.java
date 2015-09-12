package org.yatech.jedis.utils.lua;

/**
 * Created by Yinon Avraham on 11/09/2015.
 */
public class LuaIntValueArgument extends LuaValueArgument<Integer> {

    LuaIntValueArgument(String name, int defaultValue) {
        super(name, defaultValue);
    }

    LuaIntValueArgument(String name) {
        super(name);
    }

}
