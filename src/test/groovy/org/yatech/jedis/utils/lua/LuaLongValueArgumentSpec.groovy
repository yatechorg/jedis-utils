package org.yatech.jedis.utils.lua

import spock.lang.Specification

/**
 * Created by yinona on 22/09/15.
 */
class LuaLongValueArgumentSpec extends Specification {

    def 'constructor with default value'() {
        given:
        def arg = new LuaLongValueArgument('thename')

        expect:
        arg.name == 'thename'
        arg.value == null
        arg.toString() == 'thename=null'
    }

    def 'constructor with explicit value'() {
        given:
        def arg = new LuaLongValueArgument('thename', 17L)

        expect:
        arg.name == 'thename'
        arg.value == 17L
        arg.toString() == 'thename=17'
    }

    def 'equals and hash code'() {
        given:
        def arg1 = new LuaLongValueArgument('arg1')
        def arg1_ = new LuaLongValueArgument('arg1', 11L)
        def arg2 = new LuaLongValueArgument('arg2')

        expect:
        arg1 == arg1_
        arg1 != arg2
        arg2 != arg1_
        arg1.hashCode() == arg1_.hashCode()
    }

}
