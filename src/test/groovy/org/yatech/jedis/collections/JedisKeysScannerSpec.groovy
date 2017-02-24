package org.yatech.jedis.collections

import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.ScanParams
import redis.clients.jedis.ScanResult
import spock.lang.Specification
import spock.lang.Unroll

/**
 * @author Yinon Avraham.
 */
@Unroll
class JedisKeysScannerSpec extends Specification {

    def 'test required constructor arguments: jedis'() {
        when:
        new JedisKeysScanner(null)

        then:
        thrown(IllegalArgumentException)
    }

    def 'test required constructor arguments: jedis, scanParams'() {
        when:
        new JedisKeysScanner((Jedis)jedis, scanParams)

        then:
        thrown(IllegalArgumentException)

        where:
        jedis       | scanParams
        null        | new ScanParams()
        Mock(Jedis) | null
        null        | null
    }

    def 'test required constructor arguments: jedisPool, db'() {
        when:
        new JedisKeysScanner((JedisPool)jedisPool, db)

        then:
        thrown(IllegalArgumentException)

        where:
        jedisPool       | db
        null            | 0
        null            | -1
        Mock(JedisPool) | -1
    }

    def 'test required constructor arguments: jedisPool, db, scanParams'() {
        when:
        new JedisKeysScanner((JedisPool)jedisPool, db, scanParams)

        then:
        thrown(IllegalArgumentException)

        where:
        jedisPool       | db | scanParams
        null            | 0  | new ScanParams()
        null            | 0  | null
        null            | -1 | new ScanParams()
        null            | -1 | null
        Mock(JedisPool) | 0  | null
        Mock(JedisPool) | -1 | new ScanParams()
        Mock(JedisPool) | -1 | null
    }

    def 'iterate'() {
        given:
        def jedis = Mock(Jedis)
        def keys = []
        def cursorCount = ['0':0, '1':0, '2':0, '3':0]

        when:
        def scanner = new JedisKeysScanner(jedis)
        while (scanner.hasNext()) {
            keys << scanner.next()
        }

        then:
        4 * jedis.scan(_ as String, _ as ScanParams) >> { String cursor, ScanParams scanParams ->
            assert scanParams != null
            assert cursor != null
            cursorCount[(cursor)] += 1
            assert cursorCount[(cursor)] == 1
            switch (cursor) {
                case '0':
                    return mockScanResult('1', ['a','b'])
                case '1':
                    return mockScanResult('2', [])
                case '2':
                    return mockScanResult('3', ['c'])
                case '3':
                    return mockScanResult('0', ['d','e','f'])
                default:
                    throw new Exception("Unexpected cursor: '$cursor")
            }
        }
        keys == ['a', 'b', 'c', 'd', 'e', 'f']
    }

    def 'remove is not supported'() {
        given:given:
        def jedis = Mock(Jedis)
        jedis.scan(_ as String, _ as ScanParams) >> { String cursor, ScanParams scanParams ->
            mockScanResult('1', ['a','b'])
        }

        when:
        def scanner = new JedisKeysScanner(jedis)
        scanner.remove()

        then:
        thrown(UnsupportedOperationException)
    }

    def 'next when reaching the end of the scanner throws exception'() {
        given:
        def jedis = Mock(Jedis)
        jedis.scan(_ as String, _ as ScanParams) >> { String cursor, ScanParams scanParams ->
            mockScanResult('0', ['a','b'])
        }
        def res = []

        when:
        def scanner = new JedisKeysScanner(jedis)
        while (scanner.hasNext()) {
            res << scanner.next()
        }
        scanner.next()

        then:
        thrown(NoSuchElementException)
        res == ['a', 'b']
    }

    def mockScanResult(String cursor, List<String> result) {
        Mock(ScanResult) {
            getStringCursor() >> cursor
            getResult() >> result
        }
    }

}
