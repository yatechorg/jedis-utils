package org.yatech.jedis.utils.lua.ast;

/**
 * Created by Yinon Avraham on 11/09/2015.
 */
public class LuaAstIfStatement extends LuaAstStatement {

    private final LuaAstBooleanExpression condition;
    private final LuaAstScript scriptBlock;

    public LuaAstIfStatement(LuaAstBooleanExpression condition, LuaAstScript scriptBlock) {
        this.condition = condition;
        this.scriptBlock = scriptBlock;
    }

    public LuaAstBooleanExpression getCondition() {
        return condition;
    }

    public LuaAstScript getScriptBlock() {
        return scriptBlock;
    }

    @Override
    public void accept(LuaAstVisitor visitor) {
        visitor.visit(this);
    }
}
