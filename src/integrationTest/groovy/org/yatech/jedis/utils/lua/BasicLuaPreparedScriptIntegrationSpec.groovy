package org.yatech.jedis.utils.lua

import static org.yatech.jedis.utils.lua.AbstractLuaScriptBuilder.*
import static org.yatech.jedis.utils.lua.LuaConditions.isNull
import static org.yatech.jedis.utils.lua.LuaScriptBuilder.startScript

/**
 * Created on 22/09/15.
 * @author Yinon Avraham
 */
class BasicLuaPreparedScriptIntegrationSpec extends BaseIntegrationSpec {

    def 'zadd in script with double value'() {
        given:
        def key1 = newKeyArgument('key1')
        def score = newDoubleValueArgument('score')
        def mem1 = newStringValueArgument('mem1')
        def script = startScript().with {
            zadd(key1, score, mem1)
            endPreparedScript()
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
            endPreparedScript()
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

}
