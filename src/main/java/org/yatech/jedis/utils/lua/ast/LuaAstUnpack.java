package org.yatech.jedis.utils.lua.ast;

/**
 * Created by Yinon Avraham on 11/09/2015.
 */
public class LuaAstUnpack extends LuaAstFunction {

    private final LuaAstLocal local;

    public LuaAstLocal getLocal() {
        return local;
    }

    public LuaAstUnpack(LuaAstLocal local) {

        this.local = local;
    }

    @Override
    public void accept(LuaAstVisitor visitor) {
        visitor.visit(this);
    }
}
