package org.yatech.jedis.collections

import org.yatech.jedis.utils.LexRange
import org.yatech.jedis.utils.ScoreRange
import spock.lang.Unroll

/**
 * <p>Created on 24/05/16
 * @author Yinon Avraham
 */
class JedisSortedSetSpec extends BaseCollectionIntegrationSpec {

    @Unroll
    def 'test main set functionality of sorted set #testName'() {
        given:
        def verifyJedis = createJedis()
        verifyJedis.select(db)
        JedisSortedSet set = createClosure.call()

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
        verifyJedis.type('the-set') == 'zset'
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
        'created with jedis pool'     | 2  | { jedisCollections.getSortedSet(2, 'the-set') }
        'created with jedis instance' | 7  | { jedis.select(7); JedisCollections.getSortedSet(jedis, 'the-set') }
    }

    @Unroll
    def 'constructor(jedisPool, db, key)'() {
        when:
        new JedisSortedSet(jp, db, key)

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
        new JedisSortedSet(j, key)

        then:
        thrown(IllegalArgumentException)

        where:
        j     | key
        null  | 'key'
        jedis | null
    }

    def 'add members with scores'() {
        given:
        def set = jedisCollections.getSortedSet(1,'the-set')
        set.clear()

        expect:
        set.empty

        when:
        set.add('a',1)

        then:
        set.contains('a')
        set.score('a') == 1d

        when:
        set.addAll([a:2d,b:3d,c:4d])

        then:
        set.cardinality() == 3
        set.score('a') == 2d
        set.score('b') == 3d
        set.score('c') == 4d
        set.toList() == ['a','b','c']

        when:
        set.addAll([a:5d,b:3d,c:1d])

        then:
        set.cardinality() == 3
        set.score('a') == 5d
        set.score('b') == 3d
        set.score('c') == 1d
        set.toList() == ['c','b','a']
    }

    def 'fixed zero score provider'() {
        given:
        def set = jedisCollections.getSortedSet(1,'the-set',JedisSortedSet.FIXED_ZERO_SCORE_PROVIDER)
        set.clear()

        expect:
        set.empty

        when:
        set.add('a')
        set.add('b')
        set.add('c')
        set.addAll('d', 'e', 'f')
        set.addAll(Arrays.asList('g', 'h', 'i'))

        then:
        set.score('a') == 0d
        set.score('b') == 0d
        set.score('c') == 0d
        set.score('d') == 0d
        set.score('e') == 0d
        set.score('f') == 0d
        set.score('g') == 0d
        set.score('h') == 0d
        set.score('i') == 0d
    }

    def 'current timestamp score provider'() {
        given:
        def set = jedisCollections.getSortedSet(1,'the-set',JedisSortedSet.CURRENT_TIME_SCORE_PROVIDER)
        set.clear()

        expect:
        set.empty

        when:
        set.add('a')
        sleep(5)
        set.add('b')
        sleep(5)
        set.add('c')
        sleep(5)
        set.addAll('d', 'e', 'f')
        sleep(5)
        set.addAll(Arrays.asList('g', 'h', 'i'))

        then:
        set.score('b') > set.score('a')
        set.score('c') > set.score('b')
        set.score('d') > set.score('c')
        set.score('e') >= set.score('d')
        set.score('f') >= set.score('e')
        set.score('g') > set.score('f')
        set.score('h') >= set.score('g')
        set.score('i') >= set.score('h')
    }

    def 'custom score provider'() {
        given:
        def set = JedisCollections.getSortedSet(jedis,'the-set',{it.codePointAt(0) as Double} as JedisSortedSet.ScoreProvider)
        set.clear()

        expect:
        set.empty

        when:
        set.add('c')
        set.add('b')
        set.add('a')
        set.addAll(Arrays.asList('g', 'h', 'i'))
        set.addAll('d', 'e', 'f')

        then:
        set.score('b') > set.score('a')
        set.score('c') > set.score('b')
        set.score('d') > set.score('c')
        set.score('e') > set.score('d')
        set.score('f') > set.score('e')
        set.score('g') > set.score('f')
        set.score('h') > set.score('g')
        set.score('i') > set.score('h')
    }

    def 'ranks'() {
        given:
        def set = jedisCollections.getSortedSet(1,'the-set')
        set.clear()
        assert set.empty
        set.add('a', 1)
        set.add('b', 2)
        set.add('c', 3)
        set.add('d', 4)

        expect: 'rank'
        set.rank('a') == 0L
        set.rank('b') == 1L
        set.rank('c') == 2L
        set.rank('d') == 3L
        set.rank('e') == null

        and: 'reversed rank'
        set.rankReverse('a') == 3L
        set.rankReverse('b') == 2L
        set.rankReverse('c') == 1L
        set.rankReverse('d') == 0L
        set.rankReverse('e') == null
    }

    def 'range by rank'() {
        given:
        def set = jedisCollections.getSortedSet(1,'the-set')
        set.clear()
        assert set.empty
        set.add('d',10)
        set.add('e',21)
        set.add('a',33)
        set.add('b',47)
        set.add('f',53)
        set.add('c',67)

        expect: 'range by rank'
        set.rangeByRank(0, -1) == set.toSet()
        set.rangeByRank(1, 4) == ['e','a','b','f'].toSet()

        and: 'range by rank reversed'
        set.rangeByRankReverse(0, -1) == set.toSet()
        set.rangeByRankReverse(1, 3) == ['f','b','a'].toSet()

        when: 'remove some by lex'
        def removed = set.removeRangeByRank(3, 4)
        then:
        removed == 2L
        set.toSet() == ['d','c','e','a'].toSet()

        when: 'remove all by lex'
        set.removeRangeByRank(0, -1)
        then:
        set.empty
    }

    def 'range by lex'() {
        given:
        def set = jedisCollections.getSortedSet(1,'the-set')
        set.clear()
        assert set.empty
        set.addAll('c','a','e','b','f','d')

        expect: 'count by lex'
        set.countByLex(LexRange.fromInfinity().toInfinity().build()) == set.cardinality()
        set.countByLex(LexRange.from('a', true).to('f', true).build()) == set.cardinality()
        set.countByLex(LexRange.from('a', false).to('f', false).build()) == 4

        and: 'range by lex'
        set.rangeByLex(LexRange.fromInfinity().toInfinity().build()) == set.toSet()
        set.rangeByLex(LexRange.from('a', true).to('f', true).build()) == set.toSet()
        set.rangeByLex(LexRange.from('a', false).to('f', false).build()) == ['b','c','d','e'].toSet()

        and: 'range by lex with limit'
        set.rangeByLex(LexRange.fromInfinity().toInfinity().limit(0,2).build()) == ['a','b'].toSet()
        set.rangeByLex(LexRange.from('a', true).to('f', true).limit(1,2).build()) == ['b','c'].toSet()
        set.rangeByLex(LexRange.from('a', false).to('f', false).limit(1,1).build()) == ['c'].toSet()

        and: 'range by lex reversed'
        set.rangeByLexReverse(LexRange.fromInfinity().toInfinity().build()) == set.toSet()
        set.rangeByLexReverse(LexRange.from('f', true).to('a', true).build()) == set.toSet()
        set.rangeByLexReverse(LexRange.from('f', false).to('a', false).build()) == ['b','c','d','e'].toSet()

        and: 'range by lex with limit reversed'
        set.rangeByLexReverse(LexRange.fromInfinity().toInfinity().limit(0,2).build()) == ['f','e'].toSet()
        set.rangeByLexReverse(LexRange.from('f', true).to('a', true).limit(1,2).build()) == ['e','d'].toSet()
        set.rangeByLexReverse(LexRange.from('f', false).to('a', false).limit(1,1).build()) == ['d'].toSet()

        when: 'remove some by lex'
        def removed = set.removeRangeByLex(LexRange.from('c', false).to('d', true).build())
        then:
        removed == 1L
        set.toSet() == ['a','b','c','e','f'].toSet()

        when: 'remove all by lex'
        set.removeRangeByLex(LexRange.fromInfinity().toInfinity().build())
        then:
        set.empty
    }

    def 'range by score'() {
        given:
        def set = jedisCollections.getSortedSet(1,'the-set')
        set.clear()
        assert set.empty
        set.add('d',1)
        set.add('e',2)
        set.add('a',3)
        set.add('b',4)
        set.add('f',5)
        set.add('c',6)

        expect: 'range by score'
        set.rangeByScore(ScoreRange.fromInfinity().toInfinity().build()) == set.toSet()
        set.rangeByScore(ScoreRange.from(1, true).to(6, true).build()) == set.toSet()
        set.rangeByScore(ScoreRange.from(1, false).to(6, false).build()) == ['e','a','b','f'].toSet()

        and: 'range by lex with limit'
        set.rangeByScore(ScoreRange.fromInfinity().toInfinity().limit(0,2).build()) == ['d','e'].toSet()
        set.rangeByScore(ScoreRange.from(1, true).to(6, true).limit(1,2).build()) == ['e','a'].toSet()
        set.rangeByScore(ScoreRange.from(1, false).to(6, false).limit(1,1).build()) == ['a'].toSet()

        and: 'range by lex reversed'
        set.rangeByScoreReverse(ScoreRange.fromInfinity().toInfinity().build()) == set.toSet()
        set.rangeByScoreReverse(ScoreRange.from(6, true).to(1, true).build()) == set.toSet()
        set.rangeByScoreReverse(ScoreRange.from(6, false).to(1, false).build()) == ['f','b','a','e'].toSet()

        and: 'range by lex with limit reversed'
        set.rangeByScoreReverse(ScoreRange.fromInfinity().toInfinity().limit(0,2).build()) == ['c','f'].toSet()
        set.rangeByScoreReverse(ScoreRange.from(6, true).to(1, true).limit(1,2).build()) == ['f','b'].toSet()
        set.rangeByScoreReverse(ScoreRange.from(6, false).to(1, false).limit(1,1).build()) == ['b'].toSet()

        when: 'remove some by lex'
        def removed = set.removeRangeByScore(ScoreRange.from(3, false).to(4, true).build())
        then:
        removed == 1L
        set.toSet() == ['a','d','c','e','f'].toSet()

        when: 'remove all by lex'
        set.removeRangeByScore(ScoreRange.fromInfinity().toInfinity().build())
        then:
        set.empty
    }
}
