package org.yatech.jedis.utils.lua

import spock.lang.Specification

/**
 * Created by yinona on 22/09/15.
 */
class LuaDoubleValueArgumentSpec extends Specification {

    def 'constructor with default value'() {
        given:
        def arg = new LuaDoubleValueArgument('thename')

        expect:
        arg.name == 'thename'
        arg.value == null
        arg.toString() == 'thename=null'
    }

    def 'constructor with explicit value'() {
        given:
        def arg = new LuaDoubleValueArgument('thename', 3.1415)

        expect:
        arg.name == 'thename'
        arg.value == 3.1415
        arg.toString() == 'thename=3.1415'
    }

    def 'equals and hash code'() {
        given:
        def arg1 = new LuaDoubleValueArgument('arg1')
        def arg1_ = new LuaDoubleValueArgument('arg1', 0.618)
        def arg2 = new LuaDoubleValueArgument('arg2')

        expect:
        arg1 == arg1_
        arg1 != arg2
        arg2 != arg1_
        arg1.hashCode() == arg1_.hashCode()
    }

}
