package org.yatech.jedis.utils.lua

import spock.lang.Specification

/**
 * Created by yinona on 22/09/15.
 */
class LuaStringValueArgumentSpec extends Specification {

    def 'constructor with default value'() {
        given:
        def arg = new LuaStringValueArgument('thename')

        expect:
        arg.name == 'thename'
        arg.value == null
        arg.toString() == 'thename=null'
    }

    def 'constructor with explicit value'() {
        given:
        def arg = new LuaStringValueArgument('thename', 'thevalue')

        expect:
        arg.name == 'thename'
        arg.value == 'thevalue'
        arg.toString() == 'thename=thevalue'
    }

    def 'equals and hash code'() {
        given:
        def arg1 = new LuaStringValueArgument('arg1')
        def arg1_ = new LuaStringValueArgument('arg1', 'thevalue')
        def arg2 = new LuaStringValueArgument('arg2')

        expect:
        arg1 == arg1_
        arg1 != arg2
        arg2 != arg1_
        arg1.hashCode() == arg1_.hashCode()
    }

}
