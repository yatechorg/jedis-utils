package org.yatech.jedis.utils.lua;

import redis.clients.jedis.Jedis;

/**
 * Lua script holder. This is a fixed script which can be executed using a Jedis connection.
 *
 * Created by Yinon Avraham on 01/09/2015.
 * @see LuaScriptBuilder
 * @see LuaScriptBuilder#endScript()
 */
public class LuaScript {

    private final String scriptText;

    LuaScript(String scriptText) {
        this.scriptText = scriptText;
    }

    protected String getScriptText() {
        return scriptText;
    }

    /**
     * Execute this script using the given Jedis connection
     * @param jedis the Jedis connection to use
     * @return the result object from the executed script
     * @see Jedis#eval(String)
     */
    public Object exec(Jedis jedis) {
        return jedis.eval(scriptText);
    }

    @Override
    public String toString() {
        return scriptText;
    }

}
