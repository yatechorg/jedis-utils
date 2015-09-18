package org.yatech.jedis.utils.lua;

/**
 * A marker for a local array variable
 *
 * Created by Yinon Avraham on 01/09/2015.
 */
public class LuaLocalArray extends LuaLocal<Void> {

    LuaLocalArray(String name) {
        super(name);
    }

}
