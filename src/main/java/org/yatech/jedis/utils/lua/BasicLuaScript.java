package org.yatech.jedis.utils.lua;

import org.yatech.jedis.utils.lua.ast.LuaScriptConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisDataException;

import java.util.Collections;
import java.util.List;

/**
 * Lua script holder. This is a fixed script which can be executed using a Jedis connection.
 * This class is thread-safe.
 * <p>
 * Created by Yinon Avraham on 01/09/2015.
 * @see LuaScriptBuilder
 * @see LuaScriptBuilder#endScript()
 */
class BasicLuaScript implements LuaScript {

    private final String scriptText;
    private final LuaScriptConfig config;
    private final Object sha1Lock = new Object();
    private String scriptSha1;

    BasicLuaScript(String scriptText, LuaScriptConfig config) {
        this.config = config == null ? LuaScriptConfig.DEFAULT : config;
        this.scriptText = scriptText;
    }

    @Override
    public Object exec(Jedis jedis) {
        return exec(jedis, Collections.<String>emptyList(), Collections.<String>emptyList());
    }

    Object exec(Jedis jedis, List<String> keys, List<String> argv) {
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
            synchronized (sha1Lock) {
                //This is the only place where the script's sha1 is set, this is the only state of this class
                scriptSha1 = jedis.scriptLoad(scriptText);
            }
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
