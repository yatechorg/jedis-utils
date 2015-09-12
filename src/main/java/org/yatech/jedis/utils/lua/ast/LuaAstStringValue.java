package org.yatech.jedis.utils.lua.ast;

/**
 * Created by Yinon Avraham on 11/09/2015.
 */
public class LuaAstStringValue extends LuaAstValue<String> {

    public LuaAstStringValue(String value) {
        super(value);
    }

    @Override
    public void accept(LuaAstVisitor visitor) {
        visitor.visit(this);
    }

}
