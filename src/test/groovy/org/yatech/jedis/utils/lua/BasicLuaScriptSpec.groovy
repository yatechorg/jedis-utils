package org.yatech.jedis.utils.lua

import org.yatech.jedis.utils.lua.ast.LuaScriptConfig
import redis.clients.jedis.Jedis
import spock.lang.Specification

/**
 * Created on 22/09/15.
 * @author Yinon Avraham
 */
class BasicLuaScriptSpec extends Specification {

    def 'exec - no script caching'() {
        given:
        def script = new BasicLuaScript('script-text', LuaScriptConfig.newConfig().useScriptCaching(false).build())
        def jedis = Mock(Jedis)

        when:
        script.exec(jedis)

        then:
        1 * jedis.eval('script-text', [], []) >> null
    }

    def 'exec - with script caching'() {
        given:
        def script = new BasicLuaScript('script-text', LuaScriptConfig.newConfig().useScriptCaching(true).build())
        def jedis = Mock(Jedis)

        when:
        script.exec(jedis)

        then:
        1 * jedis.scriptLoad('script-text') >> 'the-sha1'
        1 * jedis.evalsha('the-sha1', [], []) >> null

        when:
        script.exec(jedis)

        then:
        0 * jedis.scriptLoad('script-text') >> 'the-sha1'
        1 * jedis.evalsha('the-sha1', [], []) >> null
    }
}
