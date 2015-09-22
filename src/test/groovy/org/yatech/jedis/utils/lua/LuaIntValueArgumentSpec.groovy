package org.yatech.jedis.utils.lua

import spock.lang.Specification

/**
 * Created by yinona on 22/09/15.
 */
class LuaIntValueArgumentSpec extends Specification {

    def 'constructor with default value'() {
        given:
        def arg = new LuaIntValueArgument('thename')

        expect:
        arg.name == 'thename'
        arg.value == null
        arg.toString() == 'thename=null'
    }

    def 'constructor with explicit value'() {
        given:
        def arg = new LuaIntValueArgument('thename', 7)

        expect:
        arg.name == 'thename'
        arg.value == 7
        arg.toString() == 'thename=7'
    }

    def 'equals and hash code'() {
        given:
        def arg1 = new LuaIntValueArgument('arg1')
        def arg1_ = new LuaIntValueArgument('arg1', 11)
        def arg2 = new LuaIntValueArgument('arg2')

        expect:
        arg1 == arg1_
        arg1 != arg2
        arg2 != arg1_
        arg1.hashCode() == arg1_.hashCode()
    }

}
