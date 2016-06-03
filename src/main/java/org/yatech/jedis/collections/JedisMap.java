package org.yatech.jedis.collections;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * A {@link Map} implementation giving an abstraction over Redis' hash value type.
 * <p>Created on 14/05/16
 *
 * @author Yinon Avraham
 */
public class JedisMap extends JedisCollectionBase implements Map<String, String> {

    JedisMap(JedisPool jedisPool, int db, String key) {
        super(jedisPool, db, key);
    }

    JedisMap(Jedis jedis, String key) {
        super(jedis, key);
    }

    @Override
    public int size() {
        long length = length();
        return Integer.valueOf(""+length);
    }

    /**
     * Get the length of the map (i.e. the number of keys).<br>
     * This is similar to {@link #size()} but is preferred since redis' capacity supports long typed values
     * @return the number of keys
     */
    public long length() {
        return doWithJedis(new JedisCallable<Long>() {
            @Override
            public Long call(Jedis jedis) {
                return jedis.exists(getKey()) ? jedis.hlen(getKey()) : 0;
            }
        });
    }

    @Override
    public boolean isEmpty() {
        return length() == 0;
    }

    @Override
    public boolean containsKey(final Object field) {
        return doWithJedis(new JedisCallable<Boolean>() {
            @Override
            public Boolean call(Jedis jedis) {
                return jedis.exists(getKey()) ? jedis.hexists(getKey(), field.toString()) : false;
            }
        });
    }

    @Override
    public boolean containsValue(Object value) {
        return values().contains(value.toString());
    }

    @Override
    public String get(final Object field) {
        return doWithJedis(new JedisCallable<String>() {
            @Override
            public String call(Jedis jedis) {
                return jedis.exists(getKey()) ? jedis.hget(getKey(), field.toString()) : null;
            }
        });
    }

    @Override
    public String put(final String field, final String value) {
        return doWithJedis(new JedisCallable<String>() {
            @Override
            public String call(Jedis jedis) {
                String oldValue = jedis.exists(getKey()) ? jedis.hget(getKey(), field) : null;
                jedis.hset(getKey(), field, value);
                return oldValue;
            }
        });
    }

    /**
     * Put <code>value</code> associated to <code>field</code>, only if field does not yet exist.
     * @param field the field
     * @param value the value
     * @return <code>true</code> if the value was set, <code>false</code> otherwise
     */
    public boolean putIfMissing(final String field, final String value) {
        return doWithJedis(new JedisCallable<Boolean>() {
            @Override
            public Boolean call(Jedis jedis) {
                return jedis.hsetnx(getKey(), field, value) != 0;
            }
        });
    }

    @Override
    public String remove(final Object field) {
        return doWithJedis(new JedisCallable<String>() {
            @Override
            public String call(Jedis jedis) {
                String oldValue = jedis.exists(getKey()) ? jedis.hget(getKey(), field.toString()) : null;
                jedis.hdel(getKey(), field.toString());
                return oldValue;
            }
        });
    }

    /**
     * Remove multiple fields from the map
     * @param fields the fields to remove
     * @return the number of fields removed
     */
    public long remove(final String... fields) {
        return doWithJedis(new JedisCallable<Long>() {
            @Override
            public Long call(Jedis jedis) {
                return jedis.hdel(getKey(), fields);
            }
        });
    }

    @Override
    public void putAll(final Map<? extends String, ? extends String> hash) {
        doWithJedis(new JedisCallable<Void>() {
            @Override
            public Void call(Jedis jedis) {
                jedis.hmset(getKey(), (Map<String,String>)hash);
                return null;
            }
        });
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

    @Override
    public Set<String> keySet() {
        return doWithJedis(new JedisCallable<Set<String>>() {
            @Override
            public Set<String> call(Jedis jedis) {
                return jedis.exists(getKey()) ? jedis.hkeys(getKey()) : Collections.<String>emptySet();
            }
        });
    }

    @Override
    public Collection<String> values() {
        return doWithJedis(new JedisCallable<Collection<String>>() {
            @Override
            public Collection<String> call(Jedis jedis) {
                return jedis.exists(getKey()) ? jedis.hvals(getKey()) : Collections.<String>emptyList();
            }
        });
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        return doWithJedis(new JedisCallable<Set<Entry<String, String>>>() {
            @Override
            public Set<Entry<String, String>> call(Jedis jedis) {
                return jedis.exists(getKey()) ? jedis.hgetAll(getKey()).entrySet() : Collections.<Entry<String, String>>emptySet();
            }
        });
    }
}
