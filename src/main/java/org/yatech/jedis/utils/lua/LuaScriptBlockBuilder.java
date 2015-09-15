package org.yatech.jedis.utils.lua;

import org.yatech.jedis.utils.lua.ast.LuaAstArg;

/**
 * Builder for a script block
 *
 * Created by Yinon Avraham on 11/09/2015.
 * @see org.yatech.jedis.utils.lua.LuaScriptBuilder#startBlock(LuaScriptBuilder)
 * @see #endBlock()
 */
public class LuaScriptBlockBuilder extends AbstractLuaScriptBuilder<LuaScriptBlockBuilder> {

    private final AbstractLuaScriptBuilder parentBuilder;

    private LuaScriptBlockBuilder(AbstractLuaScriptBuilder parentBuilder) {
        super();
        this.parentBuilder = parentBuilder;
    }

    @Override
    String getNextLocalName() {
        return parentBuilder.getNextLocalName();
    }

    @Override
    LuaAstArg getOrCreateArgvArgument(LuaValueArgument<?> valueArgument) {
        return parentBuilder.getOrCreateArgvArgument(valueArgument);
    }

    @Override
    LuaAstArg getOrCreateKeyArgument(LuaKeyArgument keyArgument) {
        return parentBuilder.getOrCreateKeyArgument(keyArgument);
    }

    static LuaScriptBlockBuilder startBlock(AbstractLuaScriptBuilder parentBuilder) {
        return new LuaScriptBlockBuilder(parentBuilder);
    }

    /**
     * End the script block
     * @return the new {@link LuaScriptBlock} instance
     */
    public LuaScriptBlock endBlock() {
        return new LuaScriptBlock(script);
    }

}
