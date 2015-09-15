package org.yatech.jedis.utils.lua;

/**
 * An argument placeholder for an int typed value
 *
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
