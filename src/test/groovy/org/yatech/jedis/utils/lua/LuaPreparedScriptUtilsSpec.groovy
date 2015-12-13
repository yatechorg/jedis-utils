package org.yatech.jedis.utils.lua

import spock.lang.Specification

/**
 * Created on 13/12/15
 * @author Yinon Avraham
 */
class LuaPreparedScriptUtilsSpec extends Specification {

    def 'clone arguments map'() {
        given:
        def source = [
                arg1: new LuaKeyArgument('arg1', 'thekey'),
                arg2: new LuaStringValueArgument('arg2', 'thevalue'),
                arg3: new LuaIntValueArgument('arg3', 1),
                arg4: new LuaLongValueArgument('arg4', 2L),
                arg5: new LuaDoubleValueArgument('arg5', 1.1)
        ]

        when:
        def clone = LuaPreparedScriptUtils.cloneArgMap(source)

        then:
        clone instanceof LinkedHashMap
        clone.size() == source.size()
        clone.keySet().each { key ->
            clone[(key)] == source[(key)]
        }

        when:
        source.arg1.value = 'anotherkey'
        source.arg2.value = 'anothervalue'
        source.arg3.value = 2
        source.arg4.value = 3L
        source.arg5.value = 2.2

        then:
        clone.keySet().each { key ->
            clone[(key)] != source[(key)]
        }
    }

    def 'to argument map'() {
        given:
        def expectedSize = argList?.size() ?: 0

        when:
        def map = LuaPreparedScriptUtils.toArgMap(argList)

        then:
        map.size() == expectedSize
        map.entrySet().eachWithIndex { Map.Entry<String, LuaArgument> entry, int i ->
            assert entry.key == argList[i].name;
            assert entry.value.name == argList[i].name;
            assert entry.value.value == argList[i].value;
            assert !entry.value.is(argList[i]);
        } ?: true

        where:
        argList << [
                null,
                [],
                [new LuaKeyArgument('arg1'), new LuaIntValueArgument('arg2'), new LuaStringValueArgument('arg3')]
        ]
    }

    def 'to execution values'() {
        given:
        def args = [
                arg1: new LuaKeyArgument('arg1', 'thekey'),
                arg2: new LuaStringValueArgument('arg2', 'thevalue'),
                arg3: new LuaIntValueArgument('arg3', 7),
                arg4: new LuaLongValueArgument('arg4', 77L),
                arg5: new LuaDoubleValueArgument('arg5', 7.7)
        ]

        when:
        def values = LuaPreparedScriptUtils.toExecValues(args)

        then:
        values == ['thekey', 'thevalue', '7', '77', '7.7']
    }

}
