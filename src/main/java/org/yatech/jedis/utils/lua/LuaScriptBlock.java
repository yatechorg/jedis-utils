package org.yatech.jedis.utils.lua;

import org.yatech.jedis.utils.lua.ast.LuaAstScript;

/**
 * A lua script block holder
 *
 * Created by Yinon Avraham on 01/09/2015.
 * @see org.yatech.jedis.utils.lua.LuaScriptBuilder#startBlock(LuaScriptBuilder)
 * @see org.yatech.jedis.utils.lua.LuaScriptBlockBuilder
 */
public class LuaScriptBlock {

    private final LuaAstScript script;

    LuaScriptBlock(LuaAstScript script) {
        this.script = script;
    }

    LuaAstScript getScript() {
        return script;
    }

}
