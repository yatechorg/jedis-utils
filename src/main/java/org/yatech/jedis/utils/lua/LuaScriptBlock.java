package org.yatech.jedis.utils.lua;

import org.yatech.jedis.utils.lua.ast.LuaAstScript;

/**
 * Created by Yinon Avraham on 01/09/2015.
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
