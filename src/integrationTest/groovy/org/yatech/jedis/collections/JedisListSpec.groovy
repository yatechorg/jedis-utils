package org.yatech.jedis.collections

import spock.lang.Unroll

/**
 * <p>Created on 15/05/16
 * @author Yinon Avraham
 */
class JedisListSpec extends BaseCollectionIntegrationSpec {

    @Unroll
    def 'test main map functionality of list #testName'() {
        given:
        def verifyJedis = createJedis()
        verifyJedis.select(db)
        JedisList list = createClosure.call()

        expect:
        !verifyJedis.exists('the-list')
        list.empty
        list.size() == 0
        list.length() == 0
        !list.contains('a')
        !list.containsAll(['b', 'c'])
        list.indexOf('a') < 0
        !list.iterator().hasNext()
        !list.remove('a')
        Arrays.equals(list.toArray(), new Object[0])
        Arrays.equals(list.toArray(new String[0]), new String[0])
        !list.removeAll(['a','b'])
        !list.retainAll(['a','b'])
        list.toList().empty

        when:
        def added = list.add('a')

        then:
        added
        verifyJedis.exists('the-list')
        verifyJedis.type('the-list') == 'list'
        !list.empty
        list.size() == 1
        list.length() == 1
        list.contains('a')
        list.containsAll(['a'])
        list.indexOf('a') == 0
        list.iterator().hasNext()
        list.iterator().next() == 'a'
        Arrays.equals(list.toArray(), ['a'].toArray())
        Arrays.equals(list.toArray(new String[0]), ['a'].toArray(new String[0]))
        !list.retainAll(['a','b'])
        list.toList() == ['a']

        when:
        added = list.addAll(['b', 'c'])

        then:
        added
        list.size() == 3
        list.length() == 3
        list.contains('a')
        list.contains('b')
        list.contains('c')
        list.containsAll(['a','c'])
        list.indexOf('a') == 0
        list.indexOf('b') == 1
        list.indexOf('c') == 2
        list.iterator().toList() == ['a','b','c']
        Arrays.equals(list.toArray(), ['a','b','c'].toArray())
        Arrays.equals(list.toArray(new String[0]), ['a','b','c'].toArray(new String[0]))
        list.toList() == ['a','b','c']

        when:
        def removed = list.remove('b')

        then:
        removed
        list.toList() == ['a','c']

        when:
        added = list.addAll(['b','d','a'])

        then:
        added
        list.toList() == ['a','c','b','d','a']

        expect:
        list.subList(0, 4) == ['a','c','b','d','a']
        list.subList(0, 0) == ['a']
        list.subList(0, 1) == ['a','c']
        list.subList(4, 4) == ['a']
        list.subList(3, 4) == ['d','a']
        list.subList(1, 2) == ['c','b']

        when:
        removed = list.retainAll(['a','b'])

        then:
        removed
        list.toList() == ['a','b','a']

        when:
        removed = list.removeAll(['a','c'])

        then:
        removed
        list.toList() == ['b']

        when:
        list.clear()

        then:
        !verifyJedis.exists('the-list')
        list.empty
        list.size() == 0

        cleanup:
        close(verifyJedis)

        where:
        testName                      | db | createClosure
        'created with jedis pool'     | 2  | { jedisCollections.getList(2, 'the-list') }
        'created with jedis instance' | 7  | { jedis.select(7); JedisCollections.getList(jedis, 'the-list') }
    }

    @Unroll
    def 'constructor(jedisPool, db, key)'() {
        when:
        new JedisList(jp, db, key)

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
        new JedisList(j, key)

        then:
        thrown(IllegalArgumentException)

        where:
        j     | key
        null  | 'key'
        jedis | null
    }
}
