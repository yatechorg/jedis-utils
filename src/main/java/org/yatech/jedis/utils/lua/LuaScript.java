package org.yatech.jedis.utils.lua;

import org.yatech.jedis.utils.lua.ast.LuaScriptConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisDataException;

import java.util.Collections;
import java.util.List;

/**
 * Lua script holder. This is a fixed script which can be executed using a Jedis connection.
 *
 * Created by Yinon Avraham on 01/09/2015.
 * @see LuaScriptBuilder
 * @see LuaScriptBuilder#endScript()
 */
public class LuaScript {

    private final String scriptText;
    private final LuaScriptConfig config;
    private String scriptSha1;

    LuaScript(String scriptText, LuaScriptConfig config) {
        this.config = config == null ? LuaScriptConfig.DEFAULT : config;
        this.scriptText = scriptText;
    }

    /**
     * Execute this script using the given Jedis connection
     * @param jedis the Jedis connection to use
     * @return the result object from the executed script
     * @see Jedis#eval(String)
     */
    public Object exec(Jedis jedis) {
        return exec(jedis, Collections.<String>emptyList(), Collections.<String>emptyList());
    }

    protected Object exec(Jedis jedis, List<String> keys, List<String> argv) {
        if (!config.isUseScriptCaching()) {
            return jedis.eval(scriptText, keys, argv);
        } else {
            if (scriptSha1 != null) {
                try {
                    return jedis.evalsha(scriptSha1, keys, argv);
                } catch (RuntimeException e) {
                    if (!isNoScriptException(e)) {
                        throw e;
                    }
                }
            }
            scriptSha1 = jedis.scriptLoad(scriptText);
            return jedis.evalsha(scriptSha1, keys, argv);
        }
    }

    private boolean isNoScriptException(Exception e) {
        return (e instanceof JedisDataException && e.getMessage().contains("NOSCRIPT"));
    }

    @Override
    public String toString() {
        return scriptText;
    }

}
