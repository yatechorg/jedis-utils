package org.yatech.jedis.utils.lua;

import java.util.UUID;

/**
 * Created by Yinon Avraham on 11/09/2015.
 */
public class LuaKeyArgument extends LuaArgument<String> {

    LuaKeyArgument(String name, String defaultKey) {
        super(name, defaultKey);
    }

    LuaKeyArgument(String name) {
        super(name);
    }

}
