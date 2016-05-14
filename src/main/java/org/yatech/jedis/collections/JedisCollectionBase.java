package org.yatech.jedis.collections;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import static org.yatech.jedis.collections.Utils.assertNotNull;
import static org.yatech.jedis.collections.Utils.assertTrue;

/**
 * <p>Created on 14/05/16
 *
 * @author Yinon Avraham
 */
public abstract class JedisCollectionBase {

    private static final int DB_NA = -1;

    private final JedisPool jedisPool;
    private final Jedis jedis;
    private final String key;
    private final int db;

    JedisCollectionBase(JedisPool jedisPool, int db, String key) {
        this.jedisPool = jedisPool;
        this.jedis = null;
        this.db = db;
        this.key = key;
        assertNotNull(jedisPool, "jedisPool");
        assertNotNull(key, "key");
        assertTrue(db >= 0, "db", "must be non-negative");
    }

    JedisCollectionBase(Jedis jedis, String key) {
        this.jedisPool = null;
        this.jedis = jedis;
        this.db = DB_NA;
        this.key = key;
        assertNotNull(jedis, "jedis");
        assertNotNull(key, "key");
    }

    protected String getKey() {
        return key;
    }

    <T> T doWithJedis(JedisCallable<T> callable) {
        if (jedis != null) {
            return callable.call(jedis);
        } else {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.select(db);
                return callable.call(jedis);
            }
        }
    }
}
