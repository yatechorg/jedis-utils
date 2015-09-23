package org.yatech.jedis.utils.lua

import spock.lang.Specification

import static org.yatech.jedis.utils.lua.LuaConditions.isNull
import static org.yatech.jedis.utils.lua.LuaScriptBuilder.startBlock
import static org.yatech.jedis.utils.lua.LuaScriptBuilder.startScript

/**
 * Created by yinona on 22/09/15.
 */
class LuaScriptIntegrationSpec extends BaseIntegrationSpec {

    def 'script with a single zadd command'() {
        given:
        def script = startScript().with {
            zadd('key1', 1.23, 'mem1')
            endScript()
        }

        when:
        script.exec(jedis)

        then:
        jedis.zscore('key1', 'mem1') == 1.23
    }

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

}
