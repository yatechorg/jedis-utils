package org.yatech.jedis.utils.lua;

import org.yatech.jedis.utils.lua.ast.LuaAstBooleanExpression;
import org.yatech.jedis.utils.lua.ast.LuaAstIfStatement;
import org.yatech.jedis.utils.lua.ast.LuaAstScript;

/**
 * Builder for an <code>if</code> statement
 *
 * Created by Yinon Avraham on 11/09/2015.
 */
public class LuaIfStatementBuilder<ParentBuilderType extends AbstractLuaScriptBuilder> {

    private final AbstractLuaScriptBuilder<ParentBuilderType> parentBuilder;
    private final LuaAstBooleanExpression condition;
    private LuaAstScript scriptBlock;

    LuaIfStatementBuilder(AbstractLuaScriptBuilder<ParentBuilderType> parentBuilder, LuaCondition condition) {
        this.parentBuilder = parentBuilder;
        this.condition = condition.getExpression();
    }

    /**
     * The <code>then</code> part of the <code>if</code> statement
     * @param block the script block to do in the <code>then</code> part
     * @return this builder
     * @see org.yatech.jedis.utils.lua.LuaScriptBuilder#startBlock(LuaScriptBuilder)
     * @see #endIf()
     */
    public LuaIfStatementBuilder<ParentBuilderType> then(LuaScriptBlock block) {
        this.scriptBlock = block.getScript();
        return this;
    }

    /**
     * End the <code>if</code> statement and return to the parent script builder
     * @return the parent builder from which this if statement started
     */
    public ParentBuilderType endIf() {
        parentBuilder.add(new LuaAstIfStatement(condition, scriptBlock));
        return (ParentBuilderType)parentBuilder;
    }
}
