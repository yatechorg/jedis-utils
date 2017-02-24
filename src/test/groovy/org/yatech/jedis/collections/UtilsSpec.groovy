package org.yatech.jedis.collections

import spock.lang.Specification

import static org.yatech.jedis.collections.Utils.*

/**
 * <p>Created on 14/05/16
 * @author Yinon Avraham
 */
class UtilsSpec extends Specification {

    def 'private empty constructor'() {
        given:
        def constructor = Utils.getDeclaredConstructor()

        when:
        constructor.newInstance()

        then:
        thrown(IllegalAccessException)
    }

    def 'check true'() {
        when:
        checkTrue(true, 'arg1', 'the error message')

        then:
        noExceptionThrown()

        when:
        checkTrue(false, 'arg1', 'the error message')

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "arg1: the error message"
    }

    def 'check not null'() {
        when:
        def val = checkNotNull(1, 'arg1')
        then:
        noExceptionThrown()
        val == 1

        when:
        checkNotNull(null, 'arg1')
        then:
        def e = thrown(IllegalArgumentException)
        e.message == "arg1: must not be null"
    }

    def 'check not negative'() {
        when:
        def val = checkNotNegative(0, 'arg1')
        then:
        noExceptionThrown()
        val == 0

        when:
        val = checkNotNegative(1, 'arg1')
        then:
        noExceptionThrown()
        val == 1

        when:
        checkNotNegative(-1, 'arg1')
        then:
        def e = thrown(IllegalArgumentException)
        e.message == "arg1: must be non-negative"
    }

    def 'collection to string array'() {
        expect:
        toStringArray(collection) == array

        where:
        collection       | array
        null             | null
        []               | new String[0]
        [1, 2, 3]        | ["1", "2", "3"] as String[]
        ["a", "b", "c"]  | ["a", "b", "c"] as String[]
        ["a", null, "c"] | ["a", null, "c"] as String[]
    }
}
