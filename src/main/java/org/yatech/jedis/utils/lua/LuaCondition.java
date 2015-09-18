package org.yatech.jedis.utils.lua;

import org.yatech.jedis.utils.lua.ast.LuaAstBooleanExpression;

/**
 * Created by Yinon Avraham on 11/09/2015.
 */
public class LuaCondition implements LuaValue<Void> {

    private final LuaAstBooleanExpression expression;

    LuaCondition(LuaAstBooleanExpression expression) {
        this.expression = expression;
    }

    LuaAstBooleanExpression getExpression() {
        return expression;
    }
}
