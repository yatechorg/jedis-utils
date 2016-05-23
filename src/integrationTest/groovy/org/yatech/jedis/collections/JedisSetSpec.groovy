package org.yatech.jedis.collections

import redis.clients.jedis.exceptions.JedisDataException
import spock.lang.Unroll

/**
 * <p>Created on 23/05/16
 * @author Yinon Avraham
 */
class JedisSetSpec extends BaseCollectionIntegrationSpec {

    @Unroll
    def 'test main map functionality of list #testName'() {
        given:
        def verifyJedis = createJedis()
        verifyJedis.select(db)
        JedisSet set = createClosure.call()

        expect:
        !verifyJedis.exists('the-set')
        set.empty
        set.size() == 0
        set.cardinality() == 0
        !set.contains('a')
        !set.containsAll(['b', 'c'])
        !set.iterator().hasNext()
        !set.remove('a')
        Arrays.equals(set.toArray(), new Object[0])
        Arrays.equals(set.toArray(new String[0]), new String[0])
        !set.removeAll(['a','b'])
        !set.retainAll(['a','b'])
        set.toSet().empty

        when:
        def added = set.add('a')

        then:
        added
        verifyJedis.exists('the-set')
        verifyJedis.type('the-set') == 'set'
        !set.empty
        set.size() == 1
        set.cardinality() == 1
        set.contains('a')
        set.containsAll(['a'])
        set.iterator().hasNext()
        set.iterator().next() == 'a'
        Arrays.equals(set.toArray(), ['a'].toArray())
        Arrays.equals(set.toArray(new String[0]), ['a'].toArray(new String[0]))
        !set.retainAll(['a','b'])
        set.toSet() == ['a'].toSet()

        when:
        added = set.addAll(['b', 'c'])

        then:
        added
        set.size() == 3
        set.cardinality() == 3
        set.contains('a')
        set.contains('b')
        set.contains('c')
        set.containsAll(['a','c'])
        set.iterator().toSet() == ['a','b','c'].toSet()
        set.toArray() as Set == ['a','b','c'].toArray() as Set
        set.toArray(new String[0]) as Set == ['a','b','c'].toArray(new String[0]) as Set
        set.toSet() == ['a','b','c'].toSet()

        when:
        def removed = set.remove('b')

        then:
        removed
        set.toSet() == ['a','c'].toSet()

        when:
        added = set.addAll(['b','d','a'])

        then:
        added
        set.toSet() == ['a','c','b','d'].toSet()

        when:
        removed = set.retainAll(['a','b'])

        then:
        removed
        set.toSet() == ['a','b'].toSet()

        when:
        removed = set.removeAll(['a','c'])

        then:
        removed
        set.toSet() == ['b'].toSet()

        when:
        set.clear()

        then:
        !verifyJedis.exists('the-set')
        set.empty
        set.size() == 0

        cleanup:
        close(verifyJedis)

        where:
        testName                      | db | createClosure
        'created with jedis pool'     | 2  | { jedisCollections.getSet(2, 'the-set') }
        'created with jedis instance' | 7  | { jedis.select(7); JedisCollections.getSet(jedis, 'the-set') }
    }

    @Unroll
    def 'constructor(jedisPool, db, key)'() {
        when:
        new JedisSet(jp, db, key)

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
        new JedisSet(j, key)

        then:
        thrown(IllegalArgumentException)

        where:
        j     | key
        null  | 'key'
        jedis | null
    }

    def 'pop single'() {
        given:
        def set = jedisCollections.getSet(2, 'the-set')
        set.clear()

        expect:
        set.pop() == null

        when:
        set.addAll('a','b','c')
        def e1 = set.pop()

        then:
        set.toSet() == ['a','b','c'].toSet() - e1

        when:
        def e2 = set.pop()

        then:
        set.toSet() == ['a','b','c'].toSet() - [e1,e2].toSet()

        when:
        def e3 = set.pop()

        then:
        [e3].toSet() == ['a','b','c'].toSet() - [e1,e2].toSet()
        set.empty

        expect:
        set.pop() == null

        cleanup:
        set.clear()
    }

    def 'pop multi is not supported on Redis 2.8'() {
        given:
        def set = jedisCollections.getSet(2, 'the-set')
        set.clear()
        set.addAll('a','b','c')

        when:
        set.pop(1)

        then:
        def e = thrown(JedisDataException)
        e.message.contains("ERR wrong number of arguments for 'spop' command")
    }

    //TODO uncomment once the embedded redis has a version that supports spop with the count argument
//    def 'pop multi'() {
//        given:
//        def set = jedisCollections.getSet(2, 'the-set')
//        def all = ['a','b','c','d','e'].toSet()
//        set.clear()
//
//        expect:
//        set.pop(1) == [].toSet()
//
//        when:
//        set.addAll(all)
//        def s1 = set.pop(1)
//
//        then:
//        s1.size() == 1
//        set.toSet() == all - s1
//
//        when:
//        def s2 = set.pop(2)
//
//        then:
//        s2.size() == 2
//        set.toSet() == all - (s1+s2)
//
//        when:
//        def s3 = set.pop(4)
//
//        then:
//        s3.size() == 2
//        s3 == all - (s1+s2)
//        set.empty
//
//        expect:
//        set.pop(3) == [].toSet()
//
//        cleanup:
//        set.clear()
//    }

}
