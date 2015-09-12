package org.yatech.jedis.utils.lua;

import redis.clients.jedis.Jedis;

/**
 * Created by Yinon Avraham on 01/09/2015.
 */
public class LuaScript {

    private final String scriptText;

    public LuaScript(String scriptText) {
        this.scriptText = scriptText;
    }

    protected String getScriptText() {
        return scriptText;
    }

    public Object exec(Jedis jedis) {
        return jedis.eval(scriptText);
    }

    @Override
    public String toString() {
        return scriptText;
    }

}
