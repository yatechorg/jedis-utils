package org.yatech.jedis.utils.lua;

import java.util.Map;

/**
 * Created by Yinon Avraham on 01/09/2015.
 */
public interface JedisCommands<VoidReturnType> {

    // SELECT
    VoidReturnType select(int db);
    VoidReturnType select(LuaIntValueArgument db);

    // HGETALL
    LuaLocalArray hgetAll(String key);
    LuaLocalArray hgetAll(LuaLocalValue key);
    LuaLocalArray hgetAll(LuaKeyArgument key);

    // ZSCORE
    LuaLocalValue zscore(String key, String member);
    LuaLocalValue zscore(LuaLocalValue key, String member);
    LuaLocalValue zscore(LuaKeyArgument key, String member);
    LuaLocalValue zscore(String key, LuaLocalValue member);
    LuaLocalValue zscore(String key, LuaStringValueArgument member);
    LuaLocalValue zscore(LuaLocalValue key, LuaLocalValue member);
    LuaLocalValue zscore(LuaLocalValue key, LuaStringValueArgument member);
    LuaLocalValue zscore(LuaKeyArgument key, LuaLocalValue member);
    LuaLocalValue zscore(LuaKeyArgument key, LuaStringValueArgument member);

    // ZADD
    VoidReturnType zadd(String key, double score, String member);
    VoidReturnType zadd(String key, double score, LuaLocalValue member);
    VoidReturnType zadd(String key, double score, LuaStringValueArgument member);
    VoidReturnType zadd(String key, LuaLocalValue score, String member);
    VoidReturnType zadd(String key, LuaLocalValue score, LuaLocalValue member);
    VoidReturnType zadd(String key, LuaLocalValue score, LuaStringValueArgument member);
    VoidReturnType zadd(String key, LuaDoubleValueArgument score, String member);
    VoidReturnType zadd(String key, LuaDoubleValueArgument score, LuaLocalValue member);
    VoidReturnType zadd(String key, LuaDoubleValueArgument score, LuaStringValueArgument member);
    VoidReturnType zadd(LuaLocalValue key, double score, String member);
    VoidReturnType zadd(LuaLocalValue key, double score, LuaLocalValue member);
    VoidReturnType zadd(LuaLocalValue key, double score, LuaStringValueArgument member);
    VoidReturnType zadd(LuaLocalValue key, LuaLocalValue score, String member);
    VoidReturnType zadd(LuaLocalValue key, LuaLocalValue score, LuaLocalValue member);
    VoidReturnType zadd(LuaLocalValue key, LuaLocalValue score, LuaStringValueArgument member);
    VoidReturnType zadd(LuaLocalValue key, LuaDoubleValueArgument score, String member);
    VoidReturnType zadd(LuaLocalValue key, LuaDoubleValueArgument score, LuaLocalValue member);
    VoidReturnType zadd(LuaLocalValue key, LuaDoubleValueArgument score, LuaStringValueArgument member);
    VoidReturnType zadd(LuaKeyArgument key, double score, String member);
    VoidReturnType zadd(LuaKeyArgument key, double score, LuaLocalValue member);
    VoidReturnType zadd(LuaKeyArgument key, double score, LuaStringValueArgument member);
    VoidReturnType zadd(LuaKeyArgument key, LuaLocalValue score, String member);
    VoidReturnType zadd(LuaKeyArgument key, LuaLocalValue score, LuaLocalValue member);
    VoidReturnType zadd(LuaKeyArgument key, LuaLocalValue score, LuaStringValueArgument member);
    VoidReturnType zadd(LuaKeyArgument key, LuaDoubleValueArgument score, String member);
    VoidReturnType zadd(LuaKeyArgument key, LuaDoubleValueArgument score, LuaLocalValue member);
    VoidReturnType zadd(LuaKeyArgument key, LuaDoubleValueArgument score, LuaStringValueArgument member);

    VoidReturnType zadd(String key, Map<String, Double> scoreMembers);
    VoidReturnType zadd(LuaLocalValue key, Map<String, Double> scoreMembers);
    VoidReturnType zadd(LuaKeyArgument key, Map<String, Double> scoreMembers);
    VoidReturnType zadd(String key, LuaLocalArray scoreMembers);
    VoidReturnType zadd(LuaLocalValue key, LuaLocalArray scoreMembers);
    VoidReturnType zadd(LuaKeyArgument key, LuaLocalArray scoreMembers);

    // HMSET
    VoidReturnType hmset(String key, Map<String, String> hash);
    VoidReturnType hmset(LuaLocalValue key, Map<String, String> hash);
    VoidReturnType hmset(LuaKeyArgument key, Map<String, String> hash);
    VoidReturnType hmset(String key, LuaLocalArray hash);
    VoidReturnType hmset(LuaLocalValue key, LuaLocalArray hash);
    VoidReturnType hmset(LuaKeyArgument key, LuaLocalArray hash);

    // DEL
    VoidReturnType del(String key);
    VoidReturnType del(LuaLocalValue key);
    VoidReturnType del(LuaKeyArgument key);

}
