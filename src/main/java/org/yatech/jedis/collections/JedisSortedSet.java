package org.yatech.jedis.collections;

import org.yatech.jedis.utils.LexRange;
import org.yatech.jedis.utils.ScoreRange;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static org.yatech.jedis.collections.Utils.toStringArray;

/**
 * A {@link Set} implementation giving an abstraction over Redis' sorted set value type.
 * <p>Created on 24/05/16
 *
 * @author Yinon Avraham
 */
public class JedisSortedSet extends JedisCollectionBase implements Set<String> {

    /**
     * A score provider strategy which assigns the current time
     */
    public static final ScoreProvider CURRENT_TIME_SCORE_PROVIDER = new ScoreProvider() {
        @Override
        public double getScore(String member) {
            return System.currentTimeMillis();
        }
    };
    /**
     * A score provider strategy which always assigns zero
     */
    public static final ScoreProvider FIXED_ZERO_SCORE_PROVIDER = new ScoreProvider() {
        @Override
        public double getScore(String member) {
            return 0;
        }
    };

    private static final ScoreProvider DEFAULT_SCORE_PROVIDER = CURRENT_TIME_SCORE_PROVIDER;

    private final ScoreProvider scoreProvider;

    JedisSortedSet(JedisPool jedisPool, int db, String key) {
        this(jedisPool, db, key, DEFAULT_SCORE_PROVIDER);
    }

    JedisSortedSet(JedisPool jedisPool, int db, String key, ScoreProvider scoreProvider) {
        super(jedisPool, db, key);
        this.scoreProvider = scoreProvider == null ? DEFAULT_SCORE_PROVIDER : scoreProvider;
    }

    JedisSortedSet(Jedis jedis, String key) {
        this(jedis, key, DEFAULT_SCORE_PROVIDER);
    }

    JedisSortedSet(Jedis jedis, String key, ScoreProvider scoreProvider) {
        super(jedis, key);
        this.scoreProvider = scoreProvider == null ? DEFAULT_SCORE_PROVIDER : scoreProvider;
    }

    @Override
    public int size() {
        return Integer.valueOf("" + cardinality());
    }

    /**
     * Get the cardinality of the set (i.e. the number of elements).<br>
     * This is similar to {@link #size()} but is preferred since redis' capacity supports long typed values
     * @return the number of elements in the set
     */
    public long cardinality() {
        return doWithJedis(new JedisCallable<Long>() {
            @Override
            public Long call(Jedis jedis) {
                return jedis.zcard(getKey());
            }
        });
    }

    @Override
    public boolean isEmpty() {
        return cardinality() == 0;
    }


    @Override
    public boolean contains(final Object member) {
        return score(member.toString()) != null;
    }

    /**
     * Return the score of the specified element of the sorted set at key.
     * @param member
     * @return The score value or <code>null</code> if the element does not exist in the set.
     */
    public Double score(final String member) {
        return doWithJedis(new JedisCallable<Double>() {
            @Override
            public Double call(Jedis jedis) {
                return jedis.zscore(getKey(), member);
            }
        });
    }

    @Override
    public Iterator<String> iterator() {
        //TODO Improve memory consumption using sscan
        return toSet().iterator();
    }

    public Set<String> toSet() {
        return doWithJedis(new JedisCallable<Set<String>>() {
            @Override
            public Set<String> call(Jedis jedis) {
                return jedis.zrange(getKey(), 0, -1);
            }
        });
    }

    @Override
    public Object[] toArray() {
        return toSet().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return toSet().toArray(a);
    }

    @Override
    public boolean add(final String member) {
        double score = scoreProvider.getScore(member);
        return add(member, score);
    }

    /**
     * Add an element assigned with its score
     * @param member the member to add
     * @param score the score to assign
     * @return <code>true</code> if the set has been changed
     */
    public boolean add(final String member, final double score) {
        return doWithJedis(new JedisCallable<Boolean>() {
            @Override
            public Boolean call(Jedis jedis) {
                return jedis.zadd(getKey(), score, member) > 0;
            }
        });
    }

    /**
     * Adds to this set all of the elements in the specified members array
     * (Uses the configured {@link ScoreProvider} for assigning scores)
     * @param members the members to add
     * @return the number of members actually added
     */
    public long addAll(final String... members) {
        Map<String, Double> scoredMembers = new HashMap<>(members.length);
        for (String member : members) {
            scoredMembers.put(member, scoreProvider.getScore(member));
        }
        return addAll(scoredMembers);
    }

    /**
     * Adds to this set all of the elements in the specified map of members and their score.
     * @param scoredMember the members to add together with their scores
     * @return the number of members actually added
     */
    public long addAll(final Map<String, Double> scoredMember) {
        return doWithJedis(new JedisCallable<Long>() {
            @Override
            public Long call(Jedis jedis) {
                return jedis.zadd(getKey(), scoredMember);
            }
        });
    }

    @Override
    public boolean remove(Object member) {
        return removeAll(member.toString()) > 0;
    }

    /**
     * Removes from this set all of its elements that are contained in the specified members array
     * @param members the members to remove
     * @return the number of members actually removed
     */
    public long removeAll(final String... members) {
        return doWithJedis(new JedisCallable<Long>() {
            @Override
            public Long call(Jedis jedis) {
                return jedis.zrem(getKey(), members);
            }
        });
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends String> members) {
        return addAll(members.toArray(new String[members.size()])) > 0;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean changed = false;
        for (String member : toSet()) {
            if (!c.contains(member)) {
                changed |= remove(member);
            }
        }
        return changed;
    }

    @Override
    public boolean removeAll(Collection<?> members) {
        return removeAll(toStringArray(members)) > 0;
    }

    @Override
    public void clear() {
        doWithJedis(new JedisCallable<Void>() {
            @Override
            public Void call(Jedis jedis) {
                jedis.del(getKey());
                return null;
            }
        });
    }

    /**
     * Returns the rank of member in the sorted set, with the scores ordered from low to high.
     * The rank (or index) is 0-based, which means that the member with the lowest score has rank 0.
     * @param member
     * @return the rank of member, or <code>null</code> if the member does not exist
     */
    public Long rank(final String member) {
        return doWithJedis(new JedisCallable<Long>() {
            @Override
            public Long call(Jedis jedis) {
                return jedis.zrank(getKey(), member);
            }
        });
    }

    /**
     * Returns the rank of member in the sorted set, with the scores ordered from high to low.
     * The rank (or index) is 0-based, which means that the member with the highest score has rank 0.
     * @param member
     * @return the reversed rank of member, or <code>null</code> if the member does not exist
     */
    public Long rankReverse(final String member) {
        return doWithJedis(new JedisCallable<Long>() {
            @Override
            public Long call(Jedis jedis) {
                return jedis.zrevrank(getKey(), member);
            }
        });
    }

    /**
     * Returns the specified range of elements in the sorted set.
     * The elements are considered to be ordered from the lowest to the highest score.
     * Lexicographical order is used for elements with equal score.
     * Both start and stop are zero-based inclusive indexes. They can also be negative numbers indicating offsets from
     * the end of the sorted set, with -1 being the last element of the sorted set.
     * @param start
     * @param end
     * @return the range of elements
     */
    public Set<String> rangeByRank(final long start, final long end) {
        return doWithJedis(new JedisCallable<Set<String>>() {
            @Override
            public Set<String> call(Jedis jedis) {
                return jedis.zrange(getKey(), start, end);
            }
        });
    }

    /**
     * Returns the specified range of elements in the sorted set.
     * The elements are considered to be ordered from the highest to the lowest score.
     * Descending lexicographical order is used for elements with equal score.
     * Both start and stop are zero-based inclusive indexes. They can also be negative numbers indicating offsets from
     * the end of the sorted set, with -1 being the last element of the sorted set.
     * @param start
     * @param end
     * @return the range of elements
     */
    public Set<String> rangeByRankReverse(final long start, final long end) {
        return doWithJedis(new JedisCallable<Set<String>>() {
            @Override
            public Set<String> call(Jedis jedis) {
                return jedis.zrevrange(getKey(), start, end);
            }
        });
    }

    /**
     * Removes all elements in the sorted set with rank between start and stop.
     * Both start and stop are 0 -based indexes with 0 being the element with the lowest score.
     * These indexes can be negative numbers, where they indicate offsets starting at the element with the highest score.
     * For example: -1 is the element with the highest score, -2 the element with the second highest score and so forth.
     * @param start
     * @param end
     * @return the number of elements removed.
     */
    public long removeRangeByRank(final long start, final long end) {
        return doWithJedis(new JedisCallable<Long>() {
            @Override
            public Long call(Jedis jedis) {
                return jedis.zremrangeByRank(getKey(), start, end);
            }
        });
    }

    /**
     * When all the elements in a sorted set are inserted with the same score, in order to force lexicographical
     * ordering, this command returns the number of elements in the sorted set with a value in the given range.
     * @param lexRange
     * @return the number of elements in the specified range.
     */
    public long countByLex(final LexRange lexRange) {
        return doWithJedis(new JedisCallable<Long>() {
            @Override
            public Long call(Jedis jedis) {
                return jedis.zlexcount(getKey(), lexRange.from(), lexRange.to());
            }
        });
    }

    /**
     * When all the elements in a sorted set are inserted with the same score, in order to force lexicographical
     * ordering, this command returns all the elements in the sorted set with a value in the given range.
     * If the elements in the sorted set have different scores, the returned elements are unspecified.
     * @param lexRange
     * @return the range of elements
     */
    public Set<String> rangeByLex(final LexRange lexRange) {
        return doWithJedis(new JedisCallable<Set<String>>() {
            @Override
            public Set<String> call(Jedis jedis) {
                if (lexRange.hasLimit()) {
                    return jedis.zrangeByLex(getKey(), lexRange.from(), lexRange.to(), lexRange.offset(), lexRange.count());
                } else {
                    return jedis.zrangeByLex(getKey(), lexRange.from(), lexRange.to());
                }
            }
        });
    }

    /**
     * When all the elements in a sorted set are inserted with the same score, in order to force lexicographical
     * ordering, this command returns all the elements in the sorted set with a value in the given range.
     * @param lexRange
     * @return the range of elements
     */
    public Set<String> rangeByLexReverse(final LexRange lexRange) {
        return doWithJedis(new JedisCallable<Set<String>>() {
            @Override
            public Set<String> call(Jedis jedis) {
                if (lexRange.hasLimit()) {
                    return jedis.zrevrangeByLex(getKey(), lexRange.fromReverse(), lexRange.toReverse(), lexRange.offset(), lexRange.count());
                } else {
                    return jedis.zrevrangeByLex(getKey(), lexRange.fromReverse(), lexRange.toReverse());
                }
            }
        });
    }

    /**
     * When all the elements in a sorted set are inserted with the same score, in order to force lexicographical
     * ordering, this command removes all elements in the sorted set between the lexicographical range specified.
     * @param lexRange
     * @return the number of elements removed.
     */
    public long removeRangeByLex(final LexRange lexRange) {
        return doWithJedis(new JedisCallable<Long>() {
            @Override
            public Long call(Jedis jedis) {
                return jedis.zremrangeByLex(getKey(), lexRange.from(), lexRange.to());
            }
        });
    }

    /**
     * Returns all the elements in the sorted set with a score in the given range.
     * The elements are considered to be ordered from low to high scores.
     * The elements having the same score are returned in lexicographical order (this follows from a property of the
     * sorted set implementation in Redis and does not involve further computation).
     * @param scoreRange
     * @return elements in the specified score range
     */
    public Set<String> rangeByScore(final ScoreRange scoreRange) {
        return doWithJedis(new JedisCallable<Set<String>>() {
            @Override
            public Set<String> call(Jedis jedis) {
                if (scoreRange.hasLimit()) {
                    return jedis.zrangeByScore(getKey(), scoreRange.from(), scoreRange.to(), scoreRange.offset(), scoreRange.count());
                } else {
                    return jedis.zrangeByScore(getKey(), scoreRange.from(), scoreRange.to());
                }
            }
        });
    }

    /**
     * Returns all the elements in the sorted set with a score in the given range.
     * In contrary to the default ordering of sorted sets, for this command the elements are considered to be ordered
     * from high to low scores.
     * The elements having the same score are returned in reverse lexicographical order.
     * @param scoreRange
     * @return elements in the specified score range
     */
    public Set<String> rangeByScoreReverse(final ScoreRange scoreRange) {
        return doWithJedis(new JedisCallable<Set<String>>() {
            @Override
            public Set<String> call(Jedis jedis) {
                if (scoreRange.hasLimit()) {
                    return jedis.zrevrangeByScore(getKey(), scoreRange.fromReverse(), scoreRange.toReverse(), scoreRange.offset(), scoreRange.count());
                } else {
                    return jedis.zrevrangeByScore(getKey(), scoreRange.fromReverse(), scoreRange.toReverse());
                }
            }
        });
    }

    /**
     * Removes all elements in the sorted set with a score in the given range.
     * @param scoreRange
     * @return the number of elements removed.
     */
    public long removeRangeByScore(final ScoreRange scoreRange) {
        return doWithJedis(new JedisCallable<Long>() {
            @Override
            public Long call(Jedis jedis) {
                return jedis.zremrangeByScore(getKey(), scoreRange.from(), scoreRange.to());
            }
        });
    }

    /**
     * A strategy for implicitly providing scores to members in case no such score is provided explicitly.
     */
    public interface ScoreProvider {
        double getScore(String member);
    }
}
