package org.yatech.jedis.utils.lua

import redis.clients.jedis.JedisPool

import java.util.concurrent.atomic.AtomicInteger

import static org.yatech.jedis.utils.lua.AbstractLuaScriptBuilder.*
import static org.yatech.jedis.utils.lua.LuaConditions.isNull
import static org.yatech.jedis.utils.lua.LuaScriptBuilder.startScript

/**
 * Created on 22/09/15.
 * @author Yinon Avraham
 */
class ThreadSafeLuaPreparedScriptIntegrationSpec extends BaseIntegrationSpec {

    def 'zadd in script with double value'() {
        given:
        def key1 = newKeyArgument('key1')
        def score = newDoubleValueArgument('score')
        def mem1 = newStringValueArgument('mem1')
        def script = startScript().with {
            zadd(key1, score, mem1)
            endPreparedScript(LuaScriptConfig.newConfig().threadSafe(true).build())
        }

        when:
        script.setKeyArgument('key1', 'key1')
        script.setValueArgument('score', 1.23)
        script.setValueArgument('mem1', 'mem1')
        script.exec(jedis)

        then:
        jedis.zscore('key1', 'mem1') == 1.23

        when:
        script.setKeyArgument('key1', 'key2')
        script.setValueArgument('score', 3.1415)
        script.setValueArgument('mem1', 'mem2')
        script.exec(jedis)

        then:
        jedis.zscore('key2', 'mem2') == 3.1415
    }

    def 'Build prepared script 1'() {
        def key1 = newKeyArgument('key1')
        def key2 = newKeyArgument('key2')
        def member1 = newStringValueArgument('member')
        def score = newDoubleValueArgument('score')
        def db0 = newIntValueArgument('db0')
        def db1 = newIntValueArgument('db1')

        def script = startScript().with {
            select(db0)
            def payload = hgetAll(key1)
            select(db1)
            def existingScore = zscore(key2, member1)
            ifCondition(isNull(existingScore)).then(startBlock(it).with {
                zadd(key2, score, member1)
                endBlock()
            }).endIf()
            hmset(key1, payload)
            select(db0)
            del(key1)
            endPreparedScript(LuaScriptConfig.newConfig().threadSafe(true).build())
        }

        when:
        jedis.select(3)
        jedis.hmset('key1', [k1: 'v1', k2: 'v2'])
        script.setKeyArgument('key1', 'key1')
        script.setKeyArgument('key2', 'key2')
        script.setValueArgument('member', 'member_a')
        script.setValueArgument('score', 1.23)
        script.setValueArgument('db0', 3)
        script.setValueArgument('db1', 4)
        script.exec(jedis)

        then:
        jedis.select(4)
        jedis.zscore('key2', 'member_a') == 1.23
        jedis.hgetAll('key1') == [k1: 'v1', k2: 'v2']
        jedis.select(3)
        !jedis.exists('key1')

        when:
        jedis.select(3)
        jedis.hmset('key1', [k1: 'v1a', k2: 'v2a'])
        script.setValueArgument('score', 3.1415)
        script.exec(jedis)

        then:
        jedis.select(4)
        jedis.zscore('key2', 'member_a') == 1.23
        jedis.hgetAll('key1') == [k1: 'v1a', k2: 'v2a']
        jedis.select(3)
        !jedis.exists('key1')
    }

    def 'concurrent use of the thread safe script'() {
        //This is a stochastic test. I assume that if there is an issue it should be detected (statistically...)
        given:
        def key1 = newKeyArgument('key1')
        def key2 = newKeyArgument('key2')
        def key3 = newKeyArgument('key3')
        def val1 = newStringValueArgument('val1')
        def script = startScript().with {
            set(key1, val1)
            def val2 = get(key1)
            set(key2, val2)
            def val3 = get(key2)
            set(key3, val3)
            def result = get(key3)
            endPreparedScriptReturn(result, LuaScriptConfig.newConfig().threadSafe(true).build())
        }
        def threads = []
        def errorCount = new AtomicInteger(0)
        final int THREAD_N = 20
        final int ITERATION_N = 10000
        final int MAX_TIMEOUT = 30000
        JedisPool jedisPool = createJedisPool(THREAD_N)
        (1..THREAD_N).each { i ->
            def t = new Thread({ ->
                (1..ITERATION_N).each {
                    def base = "$i${System.currentTimeMillis()}"
                    def val = "${base}val"
                    script.setKeyArgument('key1', "${base}1")
                    script.setKeyArgument('key2', "${base}2")
                    script.setKeyArgument('key3', "${base}3")
                    script.setValueArgument('val1', val)
                    def jedis = jedisPool.getResource()
                    def res
                    try {
                        jedis.select(0)
                        res = script.exec(jedis)
                    } finally {
                        jedis.close()
                    }
                    if (res != val) errorCount.incrementAndGet()
                }
            } as Runnable)
            t.setDaemon(true)
            threads << t
        }

        when:
        threads.each { Thread t ->
            t.start()
        }
        threads.eachWithIndex { Thread t, int i ->
            t.join((int)Math.ceil(MAX_TIMEOUT / (i+1))+1)
        }

        then:
        errorCount.get() == 0

        cleanup:
        jedisPool.destroy()
    }

}
