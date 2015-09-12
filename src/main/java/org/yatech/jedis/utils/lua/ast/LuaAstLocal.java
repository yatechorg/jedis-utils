package org.yatech.jedis.utils.lua.ast;

/**
 * Created by Yinon Avraham on 11/09/2015.
 */
public class LuaAstLocal extends LuaAstExpression {

    private final String name;

    public LuaAstLocal(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public void accept(LuaAstVisitor visitor) {
        visitor.visit(this);
    }
}
