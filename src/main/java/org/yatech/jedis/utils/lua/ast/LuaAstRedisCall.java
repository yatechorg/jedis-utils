package org.yatech.jedis.utils.lua.ast;

import java.util.List;

/**
 * Created by Yinon Avraham on 11/09/2015.
 */
public class LuaAstRedisCall extends LuaAstFunction {

    private final String methodName;
    private final List<LuaAstExpression> arguments;

    public LuaAstRedisCall(String methodName, List<LuaAstExpression> arguments) {
        this.methodName = methodName;
        this.arguments = arguments;
    }

    public String getMethodName() {
        return methodName;
    }

    public List<LuaAstExpression> getArguments() {
        return arguments;
    }

    @Override
    public void accept(LuaAstVisitor visitor) {
        visitor.visit(this);
    }
}
