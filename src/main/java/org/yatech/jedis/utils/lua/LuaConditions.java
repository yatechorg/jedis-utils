package org.yatech.jedis.utils.lua;

import org.yatech.jedis.utils.lua.ast.LuaAstExpression;
import org.yatech.jedis.utils.lua.ast.LuaAstLocal;
import org.yatech.jedis.utils.lua.ast.LuaAstNot;

/**
 * Created by Yinon Avraham on 11/09/2015.
 */
public class LuaConditions {

    public static LuaCondition notNull(LuaValue value) {
        LuaAstExpression expression;
        if (value instanceof LuaLocal) {
            expression = new LuaAstLocal(((LuaLocal) value).getName());
        } else {
            throw new IllegalArgumentException("Unexpected value type: " + value.getClass().getName());
        }
        return new LuaCondition(new LuaAstNot(expression));
    }

}
