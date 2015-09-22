package org.yatech.jedis.utils.lua

import redis.clients.jedis.Jedis
import spock.lang.Specification

/**
 * Created by yinona on 22/09/15.
 */
class LuaScriptSpec extends Specification {

    def 'exec'() {
        given:
        def script = new LuaScript('script-text')
        def jedis = Mock(Jedis)

        when:
        script.exec(jedis)

        then:
        1 * jedis.eval('script-text') >> null
    }
}
