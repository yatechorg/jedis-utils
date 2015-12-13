package org.yatech.jedis.utils.lua

import spock.lang.Specification

/**
 * Created on 22/09/15.
 * @author Yinon Avraham
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

    def 'clone'() {
        given:
        def arg1 = new LuaIntValueArgument('arg1')
        assert arg1.value == null

        when:
        def arg1_ = arg1.clone()

        then:
        arg1_.name == 'arg1'
        arg1_.value == null

        when:
        arg1.value = 7

        then:
        arg1_.value == null

        when:
        arg1_.value = 8

        then:
        arg1.value == 7
        arg1_.value == 8
    }

}
