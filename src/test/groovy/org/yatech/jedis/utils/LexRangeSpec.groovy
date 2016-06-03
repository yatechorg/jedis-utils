package org.yatech.jedis.utils

import spock.lang.Specification

/**
 * <p>Created on 03/06/16
 * @author Yinon Avraham
 */
class LexRangeSpec extends Specification {

    def 'infinity'() {
        given:
        def lexRange = LexRange.fromInfinity().toInfinity().build()

        expect:
        lexRange.from() == '-'
        lexRange.to() == '+'
        lexRange.fromReverse() == '+'
        lexRange.toReverse() == '-'
        !lexRange.hasLimit()
        lexRange.offset() < 0
        lexRange.count() < 0
    }

    def 'inclusive'() {
        given:
        def lexRange = LexRange.from('a',true).to('zzz',true).limit(1,7).build()

        expect:
        lexRange.from() == '[a'
        lexRange.to() == '[zzz'
        lexRange.fromReverse() == '[a'
        lexRange.toReverse() == '[zzz'
        lexRange.hasLimit()
        lexRange.offset() == 1
        lexRange.count() == 7
    }

    def 'exclusive'() {
        given:
        def lexRange = LexRange.from('a',false).to('zzz',false).limit(0,2).build()

        expect:
        lexRange.from() == '(a'
        lexRange.to() == '(zzz'
        lexRange.fromReverse() == '(a'
        lexRange.toReverse() == '(zzz'
        lexRange.hasLimit()
        lexRange.offset() == 0
        lexRange.count() == 2
    }

    def 'limit offset and count are entangled'() {
        given:
        def lexRangeBuilder = LexRange.builder().limit(offset, count)

        when:
        lexRangeBuilder.build()

        then:
        thrown(IllegalArgumentException)

        where:
        offset | count
        -1     | 1
        1      | -1
    }
}
