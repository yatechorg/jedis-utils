package org.yatech.jedis.collections

import redis.clients.jedis.ScanParams
import spock.lang.Unroll

/**
 * @author Yinon Avraham.
 */
@Unroll
class JedisKeysScannerIntegSpec extends BaseCollectionIntegrationSpec {

    def "scan no keys"() {
        given:
        jedis.select(0)
        assert jedis.keys('*').size() == 0
        def scanner = new JedisKeysScanner(jedis)

        expect:
        !scanner.hasNext()

        when:
        scanner.next()
        then:
        thrown(NoSuchElementException)

        when:
        scanner = new JedisKeysScanner(jedisPool, 0)
        then:
        !scanner.hasNext()

        when:
        scanner.next()
        then:
        thrown(NoSuchElementException)
    }

    def 'scan keys (jedis): #keys'() {
        given:
        jedis.select(0)
        def expectedKeys = generateKeyList(keys)
        expectedKeys.each { jedis.set(it, it) }
        assert jedis.keys('*').size() == keys

        when:
        def scanner = new JedisKeysScanner(jedis)
        def result = readAll(scanner)

        then:
        result.toSet() == expectedKeys.toSet()

        where:
        keys << [ 0, 1, 2, 3, 4, 10, 20, 50, 100, 200, 500, 1000 ]
    }

    def 'scan keys (jedisPool): #keys'() {
        given:
        jedis.select(2)
        def expectedKeys = generateKeyList(keys)
        expectedKeys.each { jedis.set(it, it) }
        assert jedis.keys('*').size() == keys

        when:
        def scanner = new JedisKeysScanner(jedisPool, 2)
        def result = readAll(scanner)

        then:
        result.toSet() == expectedKeys.toSet()

        where:
        keys << [ 0, 1, 2, 3, 4, 10, 20, 50, 100, 200, 500, 1000 ]
    }

    def 'scan keys with pattern'() {
        given:
        jedis.select(1)
        (1..5).each { jedis.set("key_$it", 'key') }
        (1..10).each { jedis.set("foo_$it", 'foo') }
        (1..15).each { jedis.set("bar_$it", 'bar') }

        when:
        def scanner = new JedisKeysScanner(jedis, new ScanParams().match('key*'))
        def result = readAll(scanner)
        then:
        result.size() == 5
        result.every { it.startsWith('key') }

        when:
        scanner = new JedisKeysScanner(jedis, new ScanParams().match('foo*'))
        result = readAll(scanner)
        then:
        result.size() == 10
        result.every { it.startsWith('foo') }

        when:
        scanner = new JedisKeysScanner(jedis, new ScanParams().match('bar*'))
        result = readAll(scanner)
        then:
        result.size() == 15
        result.every { it.startsWith('bar') }

        when:
        scanner = new JedisKeysScanner(jedisPool, 1, new ScanParams().match('key*'))
        result = readAll(scanner)
        then:
        result.size() == 5
        result.every { it.startsWith('key') }

        when:
        scanner = new JedisKeysScanner(jedisPool, 1, new ScanParams().match('foo*'))
        result = readAll(scanner)
        then:
        result.size() == 10
        result.every { it.startsWith('foo') }

        when:
        scanner = new JedisKeysScanner(jedisPool, 1, new ScanParams().match('bar*'))
        result = readAll(scanner)
        then:
        result.size() == 15
        result.every { it.startsWith('bar') }
    }

    private static List<String> generateKeyList(int size) {
        def result = []
        if (size > 0) {
            (1..size).each { result << "key_$it" }
        }
        result
    }

    private List readAll(JedisKeysScanner scanner) {
        def result = []
        while (scanner.hasNext()) {
            result << scanner.next()
        }
        result
    }
}
