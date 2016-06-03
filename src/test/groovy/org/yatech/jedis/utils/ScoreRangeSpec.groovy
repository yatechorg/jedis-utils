package org.yatech.jedis.utils

import spock.lang.Specification

/**
 * <p>Created on 03/06/16
 * @author Yinon Avraham
 */
class ScoreRangeSpec extends Specification {

    def 'infinity'() {
        given:
        def scoreRange = ScoreRange.fromInfinity().toInfinity().build()

        expect:
        scoreRange.from() == '-inf'
        scoreRange.to() == '+inf'
        scoreRange.fromReverse() == '+inf'
        scoreRange.toReverse() == '-inf'
        !scoreRange.hasLimit()
        scoreRange.offset() < 0
        scoreRange.count() < 0
    }

    def 'inclusive'() {
        given:
        def scoreRange = ScoreRange.from(2,true).to(7,true).limit(1,7).build()

        expect:
        scoreRange.from() == '2.0'
        scoreRange.to() == '7.0'
        scoreRange.fromReverse() == '2.0'
        scoreRange.toReverse() == '7.0'
        scoreRange.hasLimit()
        scoreRange.offset() == 1
        scoreRange.count() == 7
    }

    def 'exclusive'() {
        given:
        def scoreRange = ScoreRange.from(2,false).to(7,false).limit(0,2).build()

        expect:
        scoreRange.from() == '(2.0'
        scoreRange.to() == '(7.0'
        scoreRange.fromReverse() == '(2.0'
        scoreRange.toReverse() == '(7.0'
        scoreRange.hasLimit()
        scoreRange.offset() == 0
        scoreRange.count() == 2
    }

    def 'limit offset and count are entangled'() {
        given:
        def scoreRangeBuilder = ScoreRange.builder().limit(offset, count)

        when:
        scoreRangeBuilder.build()

        then:
        thrown(IllegalArgumentException)

        where:
        offset | count
        -1     | 1
        1      | -1
    }
}
