package org.yatech.jedis.collections;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import static org.yatech.jedis.collections.Utils.assertNotNull;

/**
 * <p>Created on 14/05/16
 *
 * @author Yinon Avraham
 */
public class JedisCollections {

    private final JedisPool jedisPool;

    public JedisCollections(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
        assertNotNull(jedisPool, "jedisPool");
    }

    public static JedisMap getMap(Jedis jedis, String key) {
        return new JedisMap(jedis, key);
    }

    public JedisMap getMap(int db, String key) {
        return new JedisMap(jedisPool, db, key);
    }
}
