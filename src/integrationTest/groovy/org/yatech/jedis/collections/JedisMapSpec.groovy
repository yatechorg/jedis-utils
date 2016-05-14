package org.yatech.jedis.collections

import org.yatech.jedis.BaseIntegrationSpec
import redis.clients.jedis.JedisPool
import spock.lang.Shared
import spock.lang.Unroll

/**
 * <p>Created on 14/05/16
 * @author Yinon Avraham
 */
class JedisMapSpec extends BaseIntegrationSpec {

    @Shared
    private JedisCollections jedisCollections

    @Shared
    private JedisPool jedisPool;

    def setupSpec() {
        jedisPool = createJedisPool(4)
        jedisCollections = new JedisCollections(jedisPool)
    }

    def cleanupSpec() {
        if (jedisPool) {
            jedisPool.close()
            jedisPool.destroy()
        }
    }

    @Unroll
    def 'test main map functionality of map #testName'() {
        given:
        def verifyJedis = createJedis()
        verifyJedis.select(db)
        Map<String, String> map = createClosure.call()

        expect:
        map.length() == 0
        map.size() == 0
        map.isEmpty()
        !map.containsKey('field1')
        !map.containsValue('value1')
        map.entrySet().empty
        map.keySet().empty
        map.values().empty
        map.get('field1') == null
        map.remove('field1') == null
        map.remove('field1', 'field2') == 0
        verifyJedis.type('the-map') == 'none'

        when: 'put a field-value pair'
        String oldValue = map.put('field1', 'value1')

        then:
        oldValue == null
        map.get('field1') == 'value1'
        map.length() == 1
        map.size() == 1
        !map.isEmpty()
        map.containsKey('field1')
        map.containsValue('value1')
        assertEntrySetEquals(map.entrySet(), [field1:'value1'])
        map.keySet() == ['field1'].toSet()
        map.values().toSet() == ['value1'].toSet()
        verifyJedis.type('the-map') == 'hash'
        verifyJedis.hget('the-map', 'field1') == 'value1'

        when: 'overwrite an existing field with a new value'
        oldValue = map.put('field1', 'value1a')

        then:
        oldValue == 'value1'
        map.get('field1') == 'value1a'
        map.length() == 1
        map.size() == 1
        map.containsKey('field1')
        !map.containsValue('value1')
        map.containsValue('value1a')
        assertEntrySetEquals(map.entrySet(), [field1:'value1a'])
        map.keySet() == ['field1'].toSet()
        map.values().toSet() == ['value1a'].toSet()

        when: 'put 2 additional field-value pairs'
        map.put('field2', 'value2')
        map.put('field3', 'value3')

        then:
        map.get('field2') == 'value2'
        map.get('field3') == 'value3'
        map.length() == 3
        map.size() == 3
        map.containsKey('field2')
        map.containsKey('field3')
        map.containsValue('value2')
        map.containsValue('value3')
        assertEntrySetEquals(map.entrySet(), [field1:'value1a', field2:'value2', field3:'value3'])
        map.keySet() == ['field1','field2','field3'].toSet()
        map.values().toSet() == ['value1a', 'value2', 'value3'].toSet()

        when: 'remove a single field'
        oldValue = map.remove('field1')

        then:
        oldValue == 'value1a'
        !map.containsKey('field1')
        !map.containsValue('value1a')
        map.get('field1') == null

        when: 'remove a non existing field'
        oldValue = map.remove('field1')

        then:
        oldValue == null

        when: 'clear map'
        map.clear()

        then:
        map.length() == 0
        map.size() == 0
        map.isEmpty()
        map.entrySet().empty
        map.keySet().empty
        map.values().empty
        !verifyJedis.exists('the-map')

        cleanup:
        close(verifyJedis)

        where:
        testName                      | db | createClosure
        'created with jedis pool'     | 2  | { jedisCollections.getMap(2, 'the-map') }
        'created with jedis instance' | 7  | { jedis.select(7); JedisCollections.getMap(jedis, 'the-map') }
    }

    @Unroll
    def 'test put all and remove multi for map #testName'() {
        given:
        Map<String, String> map = createClosure.call()

        when:
        map.putAll([f1: '1', f2: '2'])

        then:
        map.size() == 2
        map.f1 == '1'
        map.f2 == '2'

        when:
        map.putAll([f3: '3', f4: '4', f5: '5'])

        then:
        map.size() == 5
        map.f1 == '1'
        map.f2 == '2'
        map.f3 == '3'
        map.f4 == '4'
        map.f5 == '5'

        when:
        def removed = map.remove('f2', 'f3', 'f4', 'f6')

        then:
        map.size() == 2
        map.f1 == '1'
        map.f5 == '5'
        removed == 3l

        where:
        testName                      | db | createClosure
        'created with jedis pool'     | 2  | { jedisCollections.getMap(2, 'the-map') }
        'created with jedis instance' | 7  | { jedis.select(7); JedisCollections.getMap(jedis, 'the-map') }
    }

    @Unroll
    def 'put if missing in a map #testName'() {
        given:
        def map = createClosure.call()

        when:
        def done = map.putIfMissing('field1', 'value1')

        then:
        done
        map.get('field1') == 'value1'

        when:
        done = map.putIfMissing('field1', 'value2')

        then:
        !done
        map.get('field1') == 'value1'

        when:
        map.remove('field1')
        done = map.putIfMissing('field1', 'value2')

        then:
        done
        map.get('field1') == 'value2'

        where:
        testName                      | db | createClosure
        'created with jedis pool'     | 2  | { jedisCollections.getMap(2, 'the-map') }
        'created with jedis instance' | 7  | { jedis.select(7); JedisCollections.getMap(jedis, 'the-map') }
    }

    @Unroll
    def 'constructor(jedisPool, db, key)'() {
        when:
        new JedisMap(jp, db, key)

        then:
        thrown(IllegalArgumentException)

        where:
        jp        | db | key
        null      | 0  | 'key'
        jedisPool | -1 | 'key'
        jedisPool | 1  | null
    }

    @Unroll
    def 'constructor(jedis, key)'() {
        when:
        new JedisMap(j, key)

        then:
        thrown(IllegalArgumentException)

        where:
        j     | key
        null  | 'key'
        jedis | null
    }

    private static boolean assertEntrySetEquals(Set<Map.Entry<String, String>> actual, Map<String, String> expected) {
        assert actual.size() == expected.size()
        actual.each { entry ->
            assert expected[(entry.key)] == entry.value
        }
        true
    }
}
