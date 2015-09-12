package org.yatech.jedis.utils.lua.ast;

/**
 * Created by Yinon Avraham on 11/09/2015.
 */
public class LuaAstLocalDeclaration extends LuaAstExpression {

    private final String name;

    public LuaAstLocalDeclaration(String name) {
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
