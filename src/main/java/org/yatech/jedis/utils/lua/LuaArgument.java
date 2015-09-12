package org.yatech.jedis.utils.lua;

/**
 * Created by Yinon Avraham on 11/09/2015.
 */
abstract class LuaArgument<T> {

    private final String name;
    private T value;

    LuaArgument(String name, T defaultValue) {
        this.name = name;
        this.value = defaultValue;
    }

    LuaArgument(String name) {
        this(name, null);
    }

    String getName() {
        return name;
    }

    T getValue() {
        return value;
    }

    void setValue(T value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LuaArgument)) return false;

        LuaArgument that = (LuaArgument) o;

        if (!name.equals(that.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name + "=" + value;
    }
}
