package org.yatech.jedis.utils.lua;

import org.yatech.jedis.utils.lua.ast.LuaAstExpression;
import org.yatech.jedis.utils.lua.ast.LuaAstLocal;
import org.yatech.jedis.utils.lua.ast.LuaAstNot;

/**
 * Helper class for creating lua conditions (predicates...)
 *
 * Created by Yinon Avraham on 11/09/2015.
 * @see org.yatech.jedis.utils.lua.LuaScriptBuilder#ifCondition(LuaCondition)
 */
public class LuaConditions {

    /**
     * IS NULL predicate
     * @param value the value for which to check
     * @return a {@link LuaCondition} instance
     */
    public static LuaCondition isNull(LuaValue value) {
        LuaAstExpression expression;
        if (value instanceof LuaLocal) {
            expression = new LuaAstLocal(((LuaLocal) value).getName());
        } else {
            throw new IllegalArgumentException("Unexpected value type: " + value.getClass().getName());
        }
        return new LuaCondition(new LuaAstNot(expression));
    }

}
