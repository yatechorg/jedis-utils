package org.yatech.jedis.utils.lua

import spock.lang.Specification

/**
 * Created by yinona on 22/09/15.
 */
class LuaKeyArgumentSpec extends Specification {

    def 'constructor with default value'() {
        given:
        def arg = new LuaKeyArgument('thename')

        expect:
        arg.name == 'thename'
        arg.value == null
        arg.toString() == 'thename=null'
    }

    def 'constructor with explicit value'() {
        given:
        def arg = new LuaKeyArgument('thename', 'thevalue')

        expect:
        arg.name == 'thename'
        arg.value == 'thevalue'
        arg.toString() == 'thename=thevalue'
    }

    def 'equals and hash code'() {
        given:
        def arg1 = new LuaKeyArgument('arg1')
        def arg1_ = new LuaKeyArgument('arg1', 'thevalue')
        def arg2 = new LuaKeyArgument('arg2')

        expect:
        arg1 == arg1_
        arg1 != arg2
        arg2 != arg1_
        arg1.hashCode() == arg1_.hashCode()
    }

}
