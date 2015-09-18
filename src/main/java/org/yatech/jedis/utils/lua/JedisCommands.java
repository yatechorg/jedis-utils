package org.yatech.jedis.utils.lua;

import java.util.Map;

/**
 * Created by Yinon Avraham on 01/09/2015.
 */
public interface JedisCommands<VoidReturnType> {

    // +----+
    // | DB |
    // +----+

    // SELECT
    VoidReturnType select(int db);
    VoidReturnType select(LuaValue<Integer> db);

    // +------+
    // | KEYS |
    // +------+

    // DEL
    VoidReturnType del(String key);
    VoidReturnType del(LuaValue<String> key);

    // +------+
    // | HASH |
    // +------+

    // HGETALL
    LuaLocalArray hgetAll(String key);
    LuaLocalArray hgetAll(LuaValue<String> key);

    // HMSET
    VoidReturnType hmset(String key, Map<String, String> hash);
    VoidReturnType hmset(LuaValue<String> key, Map<String, String> hash);
    VoidReturnType hmset(String key, LuaLocalArray hash);
    VoidReturnType hmset(LuaValue<String> key, LuaLocalArray hash);

    // +------+
    // | ZSET |
    // +------+

    // ZADD
    VoidReturnType zadd(String key, double score, String member);
    VoidReturnType zadd(String key, double score, LuaValue<String> member);
    VoidReturnType zadd(String key, LuaValue<Double> score, String member);
    VoidReturnType zadd(String key, LuaValue<Double> score, LuaValue<String> member);
    VoidReturnType zadd(LuaValue<String> key, double score, String member);
    VoidReturnType zadd(LuaValue<String> key, double score, LuaValue<String> member);
    VoidReturnType zadd(LuaValue<String> key, LuaValue<Double> score, String member);
    VoidReturnType zadd(LuaValue<String> key, LuaValue<Double> score, LuaValue<String> member);

    VoidReturnType zadd(String key, Map<String, Double> scoreMembers);
    VoidReturnType zadd(LuaValue<String> key, Map<String, Double> scoreMembers);
    VoidReturnType zadd(String key, LuaLocalArray scoreMembers);
    VoidReturnType zadd(LuaValue<String> key, LuaLocalArray scoreMembers);

    // ZSCORE
    LuaLocalValue zscore(String key, String member);
    LuaLocalValue zscore(LuaValue<String> key, String member);
    LuaLocalValue zscore(String key, LuaValue<String> member);
    LuaLocalValue zscore(LuaValue<String> key, LuaValue<String> member);

}
