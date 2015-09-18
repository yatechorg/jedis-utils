package org.yatech.jedis.utils.lua

import spock.lang.Specification
import static org.yatech.jedis.utils.lua.LuaScriptBuilder.*
import static org.yatech.jedis.utils.lua.LuaConditions.*

/**
 * Created by Yinon Avraham on 01/09/2015.
 */
class LuaScriptBuilderSpec extends Specification {

    def 'Build script 1'() {
        def builder = startScript();
        builder.select(0)
        def payload = builder.hgetAll('key1')
        builder.select(1)
        def existingScore = builder.zscore('key2', 'member1')
        builder.ifCondition(isNull(existingScore)).then(
                startBlock(builder)
                .zadd('key2', 1.2, 'member1')
                .endBlock())
            .endIf()
        .hmset('key1', payload)
        .select(0)
        .del('key1')
        LuaScript script = builder.endScript()

        when:
        String scriptText = script.toString()

        then:
        scriptText == 'redis.call("SELECT",0)\n' +
                'local local0 = redis.call("HGETALL","key1")\n' +
                'redis.call("SELECT",1)\n' +
                'local local1 = redis.call("ZSCORE","key2","member1")\n' +
                'if (not local1) then\n' +
                '  redis.call("ZADD","key2",1.2,"member1")\n' +
                'end\n' +
                'redis.call("HMSET","key1",unpack(local0))\n' +
                'redis.call("SELECT",0)\n' +
                'redis.call("DEL","key1")\n' +
                'return\n'
    }

    def 'Build prepared script 1'() {
        def key1 = newKeyArgument('key1')
        def key2 = newKeyArgument('key2')
        def member1 = newStringValueArgument('member1')
        def score = newDoubleValueArgument('score')
        def db0 = newIntValueArgument('db0')
        def db1 = newIntValueArgument('db1')

        def builder = startScript()
        builder.select(db0)
        def payload = builder.hgetAll(key1)
        builder.select(db1)
        def existingScore = builder.zscore(key2, member1)
        builder.ifCondition(isNull(existingScore)).then(
                startBlock(builder)
                .zadd(key2, score, member1)
                .endBlock())
            .endIf()
        .hmset(key1, payload)
        .select(db0)
        .del(key1)
        LuaPreparedScript preparedScript = builder.endPreparedScript()

        when:
        String scriptText = preparedScript.toString()

        then:
        scriptText == 'redis.call("SELECT",ARGV[1])\n' +
                'local local0 = redis.call("HGETALL",KEYS[1])\n' +
                'redis.call("SELECT",ARGV[2])\n' +
                'local local1 = redis.call("ZSCORE",KEYS[2],ARGV[3])\n' +
                'if (not local1) then\n' +
                '  redis.call("ZADD",KEYS[2],ARGV[4],ARGV[3])\n' +
                'end\n' +
                'redis.call("HMSET",KEYS[1],unpack(local0))\n' +
                'redis.call("SELECT",ARGV[1])\n' +
                'redis.call("DEL",KEYS[1])\n' +
                'return\n'
    }

    def 'argument compatibility'() {
        given:
        def keyArg = newKeyArgument('keyArg')
        def stringArg = newStringValueArgument('stringArg')
        def doubleArg = newDoubleValueArgument('doubleArg')
        def intArg = newIntValueArgument('intArg')
        def localValue1 = new LuaLocalValue('localValue1')
        def localValue2 = new LuaLocalValue('localValue2')
        def localValue3 = new LuaLocalValue('localValue3')
        def localArray = new LuaLocalArray('localArray')

        def builder = startScript()

        when:
        // --- db ---
        // select
        builder.select(intArg)
        builder.select(localValue1)

        // --- keys ---
        // del
        builder.del(keyArg)
        builder.del(localValue1)

        // --- hash ---
        // hgetAll
        builder.hgetAll(keyArg)
        builder.hgetAll(localValue1)
        // hmset
        builder.hmset(keyArg, localArray)
        builder.hmset(localValue1, localArray)

        // --- zset ---
        // zadd
        builder.zadd(keyArg, doubleArg, stringArg)
        builder.zadd(localValue1, localValue2, localValue3)
        builder.zadd(keyArg, localArray)
        builder.zadd(localValue1, localArray)
        // zscore
        builder.zscore(keyArg, stringArg)
        builder.zscore(localValue1, localValue2)

        def script = builder.endScript()

        then:
        script.toString() == 'redis.call("SELECT",ARGV[1])\n' +
                'redis.call("SELECT",localValue1)\n' +
                'redis.call("DEL",KEYS[1])\n' +
                'redis.call("DEL",localValue1)\n' +
                'local local0 = redis.call("HGETALL",KEYS[1])\n' +
                'local local1 = redis.call("HGETALL",localValue1)\n' +
                'redis.call("HMSET",KEYS[1],unpack(localArray))\n' +
                'redis.call("HMSET",localValue1,unpack(localArray))\n' +
                'redis.call("ZADD",KEYS[1],ARGV[2],ARGV[3])\n' +
                'redis.call("ZADD",localValue1,localValue2,localValue3)\n' +
                'redis.call("ZADD",KEYS[1],unpack(localArray))\n' +
                'redis.call("ZADD",localValue1,unpack(localArray))\n' +
                'local local2 = redis.call("ZSCORE",KEYS[1],ARGV[3])\n' +
                'local local3 = redis.call("ZSCORE",localValue1,localValue2)\n' +
                'return\n'
    }

}
