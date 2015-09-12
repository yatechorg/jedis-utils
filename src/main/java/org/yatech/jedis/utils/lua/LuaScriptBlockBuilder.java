package org.yatech.jedis.utils.lua;

import org.yatech.jedis.utils.lua.ast.LuaAstArg;

/**
 * Created by Yinon Avraham on 11/09/2015.
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

    public LuaScriptBlock endBlock() {
        return new LuaScriptBlock(script);
    }

}
