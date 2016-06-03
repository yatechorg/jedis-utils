package org.yatech.jedis.collections;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * A {@link Collection} ({@link List} like) implementation giving an abstraction over Redis' list value type.
 * <p>Created on 14/05/16
 *
 * @author Yinon Avraham
 */
public class JedisList extends JedisCollectionBase implements Collection<String> {

    JedisList(JedisPool jedisPool, int db, String key) {
        super(jedisPool, db, key);
    }

    JedisList(Jedis jedis, String key) {
        super(jedis, key);
    }

    @Override
    public int size() {
        return Integer.valueOf("" + length());
    }

    /**
     * Get the length of the list (i.e. the number of elements).<br>
     * This is similar to {@link #size()} but is preferred since redis' capacity supports long typed values
     * @return the number of elements in the list
     */
    public long length() {
        return doWithJedis(new JedisCallable<Long>() {
            @Override
            public Long call(Jedis jedis) {
                return jedis.llen(getKey());
            }
        });
    }

    @Override
    public boolean isEmpty() {
        return length() == 0;
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o.toString()) >= 0;
    }

    @Override
    public Iterator<String> iterator() {
        //TODO Improve memory consumption using lindex & llen
        return toList().iterator();
    }

    /**
     * Get all the abstracted list value elements as {@link List}
     * @return a new {@link List} instance
     */
    public List<String> toList() {
        return doWithJedis(new JedisCallable<List<String>>() {
            @Override
            public List<String> call(Jedis jedis) {
                return jedis.lrange(getKey(), 0, jedis.llen(getKey())-1);
            }
        });
    }

    @Override
    public Object[] toArray() {
        return toList().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return toList().toArray(a);
    }

    @Override
    public boolean add(final String s) {
        return doWithJedis(new JedisCallable<Boolean>() {
            @Override
            public Boolean call(Jedis jedis) {
                jedis.rpush(getKey(), s);
                return true;
            }
        });
    }

    @Override
    public boolean remove(final Object o) {
        return doWithJedis(new JedisCallable<Boolean>() {
            @Override
            public Boolean call(Jedis jedis) {
                long removed = jedis.lrem(getKey(), 1, o.toString());
                return removed > 0;
            }
        });
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        return doWithJedis(new JedisCallable<Boolean>() {
            @Override
            public Boolean call(Jedis jedis) {
                for (Object o : c) {
                    if (doIndexOf(jedis, o.toString()) < 0) {
                        return false;
                    }
                }
                return true;
            }
        });
    }

    @Override
    public boolean addAll(final Collection<? extends String> c) {
        return doWithJedis(new JedisCallable<Boolean>() {
            @Override
            public Boolean call(Jedis jedis) {
                long added = jedis.rpush(getKey(), c.toArray(new String[c.size()]));
                return added > 0;
            }
        });
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        return doWithJedis(new JedisCallable<Boolean>() {
            @Override
            public Boolean call(Jedis jedis) {
                long removed = 0;
                for (Object o : c) {
                    removed += jedis.lrem(getKey(), jedis.llen(getKey()), o.toString());
                }
                return removed > 0;
            }
        });
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        return doWithJedis(new JedisCallable<Boolean>() {
            @Override
            public Boolean call(Jedis jedis) {
                long removed = 0;
                long fromIndex = 0;
                while ((fromIndex = indexOfElementNotInCollection(jedis, fromIndex, c)) >= 0) {
                    String element = jedis.lindex(getKey(), fromIndex);
                    removed += jedis.lrem(getKey(), jedis.llen(getKey()), element);
                }
                return removed > 0;
            }
        });
    }

    private long indexOfElementNotInCollection(Jedis jedis, long fromIndex, Collection<?> c) {
        long len = jedis.llen(getKey());
        for (long i = fromIndex; i < len; i++) {
            String element = jedis.lindex(getKey(), i);
            if (!c.contains(element)) {
                return i;
            }
        }
        return -1L;
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
     * Find the index of the first matching element in the list
     * @param element the element value to find
     * @return the index of the first matching element, or <code>-1</code> if none found
     */
    public long indexOf(final String element) {
        return doWithJedis(new JedisCallable<Long>() {
            @Override
            public Long call(Jedis jedis) {
                return doIndexOf(jedis, element);
            }
        });
    }

    private Long doIndexOf(Jedis jedis, String element) {
        long length = jedis.llen(getKey());
        for (long i = 0; i < length; i++) {
            if (element.equals(jedis.lindex(getKey(), i))) {
                return i;
            }
        }
        return -1L;
    }

    /**
     * Get the element value in the list by index
     * @param index the position in the list from which to get the element
     * @return the element value
     */
    public String get(final long index) {
        return doWithJedis(new JedisCallable<String>() {
            @Override
            public String call(Jedis jedis) {
                return jedis.lindex(getKey(), index);
            }
        });
    }

    /**
     * Get a sub-list of this list
     * @param fromIndex index of the first element in the sub-list (inclusive)
     * @param toIndex index of the last element in the sub-list (inclusive)
     * @return the sub-list
     */
    public List<String> subList(final long fromIndex, final long toIndex) {
        return doWithJedis(new JedisCallable<List<String>>() {
            @Override
            public List<String> call(Jedis jedis) {
                return jedis.lrange(getKey(), fromIndex, toIndex);
            }
        });
    }
}
