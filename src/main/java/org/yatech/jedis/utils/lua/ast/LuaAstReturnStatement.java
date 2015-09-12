package org.yatech.jedis.utils.lua.ast;

/**
 * Created by Yinon Avraham on 11/09/2015.
 */
public class LuaAstReturnStatement extends LuaAstStatement {

    private final LuaAstExpression expression;

    public LuaAstReturnStatement(LuaAstExpression expression) {
        this.expression = expression;
    }

    public LuaAstReturnStatement() {
        this.expression = null;
    }

    public LuaAstExpression getExpression() {
        return expression;
    }

    @Override
    public void accept(LuaAstVisitor visitor) {
        visitor.visit(this);
    }
}
