package org.yatech.jedis.utils.lua;

/**
 * Created by Yinon Avraham on 01/09/2015.
 */
abstract class LuaLocal implements LuaValue {

    private final String name;

    LuaLocal(String name) {
        this.name = name;
    }

    String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LuaLocal)) return false;

        LuaLocal luaLocal = (LuaLocal) o;

        if (!name.equals(luaLocal.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
