package org.yatech.jedis.collections;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import static org.yatech.jedis.collections.Utils.toStringArray;

/**
 * A {@link Set} implementation giving an abstraction over Redis' set value type.
 * <p>Created on 23/05/16
 *
 * @author Yinon Avraham
 */
public class JedisSet extends JedisCollectionBase implements Set<String> {

    JedisSet(JedisPool jedisPool, int db, String key) {
        super(jedisPool, db, key);
    }

    JedisSet(Jedis jedis, String key) {
        super(jedis, key);
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
                return jedis.scard(getKey());
            }
        });
    }

    @Override
    public boolean isEmpty() {
        return cardinality() == 0;
    }


    @Override
    public boolean contains(final Object o) {
        return doWithJedis(new JedisCallable<Boolean>() {
            @Override
            public Boolean call(Jedis jedis) {
                return jedis.sismember(getKey(), o.toString());
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
                return jedis.smembers(getKey());
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
        return addAll(member) > 0;
    }

    /**
     * Adds to this set all of the elements in the specified members array
     * @param members the members to add
     * @return the number of members actually added
     */
    public long addAll(final String... members) {
        return doWithJedis(new JedisCallable<Long>() {
            @Override
            public Long call(Jedis jedis) {
                return jedis.sadd(getKey(), members);
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
                return jedis.srem(getKey(), members);
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
     * Removes and returns a random element from the set.
     * @return the removed element, or <code>null</code> when the key does not exist.
     */
    public String pop() {
        return doWithJedis(new JedisCallable<String>() {
            @Override
            public String call(Jedis jedis) {
                return jedis.spop(getKey());
            }
        });
    }

    /**
     * Removes and returns multiple random elements from the set.<br>
     * <em>Note-</em> This method is not supported in Redis versions 2.6, 2.8, 3.0 .
     * @param count the number of elements to remove and return
     * @return the removed elements, or <code>null</code> when the key does not exist.
     */
    public Set<String> pop(final long count) {
        return doWithJedis(new JedisCallable<Set<String>>() {
            @Override
            public Set<String> call(Jedis jedis) {
                return jedis.spop(getKey(), count);
            }
        });
    }
}
