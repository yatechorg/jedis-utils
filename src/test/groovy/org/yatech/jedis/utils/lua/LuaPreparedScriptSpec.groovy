package org.yatech.jedis.utils.lua

import redis.clients.jedis.Jedis
import spock.lang.Specification

/**
 * Created by yinona on 22/09/15.
 */
class LuaPreparedScriptSpec extends Specification {

    def 'exec'() {
        given:
        def jedis = Mock(Jedis)
        def keys = [new LuaKeyArgument('key1')]
        def argv = [new LuaStringValueArgument('str1'),
                    new LuaIntValueArgument('int1'),
                    new LuaIntValueArgument('long1'),
                    new LuaDoubleValueArgument('dbl1')]
        def prepScript = new LuaPreparedScript('script-text', keys, argv)

        when:
        prepScript.exec(jedis)

        then:
        1 * jedis.eval('script-text', _ as List, _ as List) >> { s, k, v ->
            assert k == ['null']
            assert v == ['null', 'null', 'null', 'null']
        }

        when:
        prepScript.setKeyArgument('key1', 'thekey1')
        prepScript.setValueArgument('str1', 'thevalue')
        prepScript.setValueArgument('int1', 31)
        prepScript.setValueArgument('long1', 17L)
        prepScript.setValueArgument('dbl1', 1.618)
        prepScript.exec(jedis)

        then:
        1 * jedis.eval('script-text', _ as List, _ as List) >> { s, k, v ->
            assert k == ['thekey1']
            assert v == ['thevalue', '31', '17', '1.618']
        }
    }

}
