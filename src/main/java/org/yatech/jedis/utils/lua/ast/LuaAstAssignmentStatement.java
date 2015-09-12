package org.yatech.jedis.utils.lua.ast;

/**
 * Created by Yinon Avraham on 11/09/2015.
 */
public class LuaAstAssignmentStatement extends LuaAstStatement {

    private final LuaAstExpression local;
    private final LuaAstExpression expression;

    public LuaAstAssignmentStatement(LuaAstLocal local, LuaAstExpression expression) {
        this.local = local;
        this.expression = expression;
    }

    public LuaAstAssignmentStatement(LuaAstLocalDeclaration local, LuaAstExpression expression) {
        this.local = local;
        this.expression = expression;
    }

    public LuaAstExpression getLocal() {
        return local;
    }

    public LuaAstExpression getExpression() {
        return expression;
    }

    @Override
    public void accept(LuaAstVisitor visitor) {
        visitor.visit(this);
    }
}
