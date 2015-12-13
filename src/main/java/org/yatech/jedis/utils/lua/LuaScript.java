package org.yatech.jedis.utils.lua;

import redis.clients.jedis.Jedis;

/**
 * Lua script holder. This is a fixed script which can be executed using a Jedis connection.
 * <p>
 * Created on 13/12/15
 * @author Yinon Avraham
 */
public interface LuaScript {

    /**
     * Execute this script using the given Jedis connection
     * @param jedis the Jedis connection to use
     * @return the result object from the executed script
     * @see Jedis#eval(String)
     */
    Object exec(Jedis jedis);
}
