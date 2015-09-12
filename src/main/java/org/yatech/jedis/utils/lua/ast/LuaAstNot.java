package org.yatech.jedis.utils.lua.ast;

/**
 * Created by Yinon Avraham on 11/09/2015.
 */
public class LuaAstNot extends LuaAstBooleanExpression {

    private final LuaAstExpression expression;

    public LuaAstNot(LuaAstExpression expression) {
        this.expression = expression;
    }

    public LuaAstExpression getExpression() {
        return expression;
    }

    @Override
    public void accept(LuaAstVisitor visitor) {
        visitor.visit(this);
    }
}
