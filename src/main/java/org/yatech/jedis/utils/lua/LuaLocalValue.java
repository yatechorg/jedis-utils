package org.yatech.jedis.utils.lua;

/**
 * A marker for a local value variable
 *
 * Created by Yinon Avraham on 01/09/2015.
 */
public class LuaLocalValue extends LuaLocal {

    LuaLocalValue(String name) {
        super(name);
    }

}
