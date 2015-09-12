package org.yatech.jedis.utils.lua.ast;

/**
 * Created by Yinon Avraham on 11/09/2015.
 */
public abstract class LuaAstValue<T> extends LuaAstExpression {

    private final T value;

    public LuaAstValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }
}
