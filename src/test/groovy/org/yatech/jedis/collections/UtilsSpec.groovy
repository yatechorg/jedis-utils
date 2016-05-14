package org.yatech.jedis.collections

import spock.lang.Specification

import static org.yatech.jedis.collections.Utils.assertNotNull
import static org.yatech.jedis.collections.Utils.assertTrue

/**
 * <p>Created on 14/05/16
 * @author Yinon Avraham
 */
class UtilsSpec extends Specification {

    def 'assert true'() {
        when:
        assertTrue(true, 'arg1', 'the error message')

        then:
        noExceptionThrown()

        when:
        assertTrue(false, 'arg1', 'the error message')

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "arg1: the error message"
    }

    def 'assert not null'() {
        when:
        assertNotNull(1, 'arg1')

        then:
        noExceptionThrown()

        when:
        assertNotNull(null, 'arg1')

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "arg1: must not be null"
    }
}
