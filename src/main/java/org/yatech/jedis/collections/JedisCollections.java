package org.yatech.jedis.collections;

import org.yatech.jedis.collections.JedisSortedSet.ScoreProvider;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import static org.yatech.jedis.collections.Utils.assertNotNull;

/**
 * A facade for creating wrappers for Redis values (hash, list, set, etc.)
 * <p>Created on 14/05/16
 *
 * @author Yinon Avraham
 */
public class JedisCollections {

    private final JedisPool jedisPool;

    private JedisCollections(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
        assertNotNull(jedisPool, "jedisPool");
    }

    /**
     * Get a {@link JedisCollections} facade instance. Reuses {@link Jedis} connections from a given {@link JedisPool}.
     * @param jedisPool the pool of {@link Jedis} connections from which to get a connections.
     */
    public static JedisCollections getInstance(JedisPool jedisPool) {
        return new JedisCollections(jedisPool);
    }

    /**
     * Get a {@link java.util.Map} abstraction of a hash redis value in the specified key, using the given {@link Jedis}.
     * @param jedis the {@link Jedis} connection to use for the map operations.
     * @param key the key of the hash value in redis
     * @return the {@link JedisMap} instance
     */
    public static JedisMap getMap(Jedis jedis, String key) {
        return new JedisMap(jedis, key);
    }

    /**
     * Get a {@link java.util.Map} abstraction of a hash redis value in the specified database index and key.
     * @param db the database index where the key with the hash value is / should be stored.
     * @param key the key of the hash value in redis
     * @return the {@link JedisMap} instance
     */
    public JedisMap getMap(int db, String key) {
        return new JedisMap(jedisPool, db, key);
    }

    /**
     * Get a list abstraction of a list redis value in the specified key, using the given {@link Jedis}.
     * @param jedis the {@link Jedis} connection to use for the list operations.
     * @param key the key of the list value in redis
     * @return the {@link JedisList} instance
     */
    public static JedisList getList(Jedis jedis, String key) {
        return new JedisList(jedis, key);
    }

    /**
     * Get a list abstraction of a list redis value in the specified database index and key.
     * @param db the database index where the key with the list value is / should be stored.
     * @param key the key of the list value in redis
     * @return the {@link JedisList} instance
     */
    public JedisList getList(int db, String key) {
        return new JedisList(jedisPool, db, key);
    }

    /**
     * Get a {@link java.util.Set} abstraction of a set redis value in the specified key, using the given {@link Jedis}.
     * @param jedis the {@link Jedis} connection to use for the set operations.
     * @param key the key of the set value in redis
     * @return the {@link JedisSet} instance
     */
    public static JedisSet getSet(Jedis jedis, String key) {
        return new JedisSet(jedis, key);
    }

    /**
     * Get a {@link java.util.Set} abstraction of a set redis value in the specified database index and key.
     * @param db the database index where the key with the set value is / should be stored.
     * @param key the key of the set value in redis
     * @return the {@link JedisSet} instance
     */
    public JedisSet getSet(int db, String key) {
        return new JedisSet(jedisPool, db, key);
    }

    /**
     * Get a {@link java.util.Set} abstraction of a sorted set redis value in the specified key, using the given {@link Jedis}.
     * Uses the default score provider (time based).
     * @param jedis the {@link Jedis} connection to use for the sorted set operations.
     * @param key the key of the sorted set value in redis
     * @return the {@link JedisSortedSet} instance
     */
    public static JedisSortedSet getSortedSet(Jedis jedis, String key) {
        return new JedisSortedSet(jedis, key);
    }

    /**
     * Get a {@link java.util.Set} abstraction of a sorted set redis value in the specified key, using the given {@link Jedis}.
     * @param jedis the {@link Jedis} connection to use for the sorted set operations.
     * @param key the key of the sorted set value in redis
     * @param scoreProvider the provider to use for assigning scores when none is given explicitly.
     * @return the {@link JedisSortedSet} instance
     */
    public static JedisSortedSet getSortedSet(Jedis jedis, String key, ScoreProvider scoreProvider) {
        return new JedisSortedSet(jedis, key, scoreProvider);
    }

    /**
     * Get a {@link java.util.Set} abstraction of a sorted set redis value in the specified database index and key.
     * Uses the default score provider (time based).
     * @param db the database index where the key with the sorted set value is / should be stored.
     * @param key the key of the sorted set value in redis
     * @return the {@link JedisSortedSet} instance
     */
    public JedisSortedSet getSortedSet(int db, String key) {
        return new JedisSortedSet(jedisPool, db, key);
    }

    /**
     * Get a {@link java.util.Set} abstraction of a sorted set redis value in the specified database index and key.
     * @param db the database index where the key with the sorted set value is / should be stored.
     * @param key the key of the sorted set value in redis
     * @param scoreProvider the provider to use for assigning scores when none is given explicitly.
     * @return the {@link JedisSortedSet} instance
     */
    public JedisSortedSet getSortedSet(int db, String key, ScoreProvider scoreProvider) {
        return new JedisSortedSet(jedisPool, db, key, scoreProvider);
    }
}
