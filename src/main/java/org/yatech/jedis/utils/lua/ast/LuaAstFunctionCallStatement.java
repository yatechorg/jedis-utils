package org.yatech.jedis.utils.lua.ast;

/**
 * Created by Yinon Avraham on 11/09/2015.
 */
public class LuaAstFunctionCallStatement extends LuaAstStatement {

    private final LuaAstFunction function;

    public LuaAstFunctionCallStatement(LuaAstFunction function) {
        this.function = function;
    }

    public LuaAstFunction getFunction() {
        return function;
    }

    @Override
    public void accept(LuaAstVisitor visitor) {
        visitor.visit(this);
    }
}
