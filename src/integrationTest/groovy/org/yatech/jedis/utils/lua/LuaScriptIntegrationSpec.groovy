package org.yatech.jedis.utils.lua

import org.yatech.jedis.BaseIntegrationSpec
import redis.clients.jedis.exceptions.JedisDataException

import static org.yatech.jedis.utils.lua.LuaConditions.isNull
import static org.yatech.jedis.utils.lua.LuaScriptBuilder.*

/**
 * Created on 22/09/15.
 * @author Yinon Avraham
 */
class LuaScriptIntegrationSpec extends BaseIntegrationSpec {

    def 'test if statement 1'() {
        given:
        def script = startScript().with {
            def local0 = hgetAll('key0')
            def local1 = get('key1')
            ifCondition(isNull(local1)).then(startBlock(it).with {
                del('key2')
                hmset('key2', local0)
                endBlock()
            }).endIf()
            endScriptReturn(local1)
        }
        jedis.hmset('key0', [k0: 'v0'])
        jedis.set('key1', 'v1')
        jedis.hmset('key2', [k2: 'v2'])

        when:
        def res = script.exec(jedis)
        then:
        jedis.hgetAll('key2') == [k2: 'v2']
        res == 'v1'

        when:
        jedis.del('key1')
        res = script.exec(jedis)
        then:
        jedis.hgetAll('key2') == [k0: 'v0']
        res == null
    }

    def 'test if statement with return'() {
        given:
        def script = startScript().with {
            def local0 = get('key0')
            ifCondition(isNull(local0)).then(startBlock(it).with {
                endBlockReturn(get('key1'))
            }).endIf()
            endScriptReturn(get('key2'))
        }

        jedis.set('key1', 'v1')
        jedis.set('key2', 'v2')

        when:
        def res = script.exec(jedis)
        then:
        res == 'v1'

        when:
        jedis.set('key0', 'v0')
        res = script.exec(jedis)
        then:
        res == 'v2'
    }

    def 'zadd in script with double value'() {
        given:
        def script = startScript().with {
            zadd('key1', 1.23, 'mem1')
            endScript()
        }

        when:
        script.exec(jedis)

        then:
        jedis.zscore('key1', 'mem1') == 1.23d
    }

    def 'zadd and return zscore in script with double value'() {
        given:
        def script = startScript().with {
            zadd('key1', 1.23, 'mem1')
            endScriptReturn(zscore('key1', 'mem1'))
        }

        when:
        def res = script.exec(jedis)

        then:
        res == '1.23'
    }

    def 'use scripts cache, NOSCRIPT exception is handled'() {
        given:
        def key = newKeyArgument('key')
        def script = startScript().with {
            endPreparedScriptReturn(get(key))
        }
        jedis.set('k1', 'v1')
        jedis.set('k2', 'v2')

        when:
        script.setKeyArgument('key', 'k1')
        def res = script.exec(jedis)
        then:
        res == 'v1'

        when:
        jedis.scriptFlush()
        script.setKeyArgument('key', 'k2')
        res = script.exec(jedis)
        then:
        res == 'v2'
    }

    def 'use scripts cache, non NOSCRIPT exception is thrown'() {
        given:
        def key = newKeyArgument('key')
        def script = startScript().with {
            endPreparedScriptReturn(hgetAll(key))
        }
        jedis.set('k1', 'v1')
        jedis.hmset('k2', [hk2:'v2'])

        when:
        script.setKeyArgument('key', 'k2')
        def res = script.exec(jedis)
        then:
        res == ['hk2', 'v2']

        when:
        script.setKeyArgument('key', 'k1')
        script.exec(jedis)
        then:
        def e = thrown(JedisDataException)
        e.message.contains('WRONGTYPE')
    }

}
