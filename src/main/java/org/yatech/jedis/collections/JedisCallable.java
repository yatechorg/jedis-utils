package org.yatech.jedis.collections;

import redis.clients.jedis.Jedis;

/**
 * <p>Created on 14/05/16
 *
 * @author Yinon Avraham
 */
interface JedisCallable<T> {

    T call(Jedis jedis);

}
