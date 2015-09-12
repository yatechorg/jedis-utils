package org.yatech.jedis.utils.lua;

import org.yatech.jedis.utils.lua.ast.LuaAstBooleanExpression;
import org.yatech.jedis.utils.lua.ast.LuaAstIfStatement;
import org.yatech.jedis.utils.lua.ast.LuaAstScript;

/**
 * Created by Yinon Avraham on 11/09/2015.
 */
public class LuaIfConditionBuilder<ParentBuilderType extends AbstractLuaScriptBuilder> {

    private final AbstractLuaScriptBuilder<ParentBuilderType> parentBuilder;
    private final LuaAstBooleanExpression condition;
    private LuaAstScript scriptBlock;

    LuaIfConditionBuilder(AbstractLuaScriptBuilder<ParentBuilderType> parentBuilder, LuaCondition condition) {
        this.parentBuilder = parentBuilder;
        this.condition = condition.getExpression();
    }

    public LuaIfConditionBuilder<ParentBuilderType> then(LuaScriptBlock block) {
        this.scriptBlock = block.getScript();
        return this;
    }

    public ParentBuilderType endIf() {
        parentBuilder.add(new LuaAstIfStatement(condition, scriptBlock));
        return (ParentBuilderType)parentBuilder;
    }
}
