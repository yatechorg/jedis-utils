package org.yatech.jedis.collections

import org.yatech.jedis.BaseIntegrationSpec
import redis.clients.jedis.JedisPool
import spock.lang.Shared

/**
 * <p>Created on 15/05/16
 *
 * @author Yinon Avraham
 */
public abstract class BaseCollectionIntegrationSpec extends BaseIntegrationSpec {

    @Shared
    protected JedisCollections jedisCollections

    @Shared
    protected JedisPool jedisPool;

    def setupSpec() {
        jedisPool = createJedisPool(4)
        jedisCollections = JedisCollections.getInstance(jedisPool)
    }

    def cleanupSpec() {
        if (jedisPool) {
            jedisPool.close()
            jedisPool.destroy()
        }
    }

}
