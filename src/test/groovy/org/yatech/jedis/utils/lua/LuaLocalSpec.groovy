package org.yatech.jedis.utils.lua

import spock.lang.Specification

/**
 * Created on 16/10/15.
 * @author Yinon Avraham
 */
class LuaLocalSpec extends Specification {

    def 'equals'() {
        given:
        def local1a = new LuaLocalValue('local1')
        def local1b = new LuaLocalValue('local1')
        def local2 = new LuaLocalValue('local2')
        def local3 = new LuaLocalArray('local1')

        expect:
        local1a.equals(local1a)
        local1a.equals(local1b)
        local1b.equals(local1a)
        !local1a.equals(local2)
        !local2.equals(local1a)
        local1a.equals(local3)
        local3.equals(local1a)
    }

    def 'hash code'() {
        given:
        def local1a = new LuaLocalValue('local1')
        def local1b = new LuaLocalValue('local1')
        def local2 = new LuaLocalValue('local2')
        def local3 = new LuaLocalArray('local1')

        expect:
        local1a.hashCode() == local1a.hashCode()
        local1a.hashCode() == local1b.hashCode()
        local1a.hashCode() != local2.hashCode()
        local1a.hashCode() == local3.hashCode()
    }
}
