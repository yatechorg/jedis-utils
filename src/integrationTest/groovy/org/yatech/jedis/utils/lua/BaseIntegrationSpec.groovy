package org.yatech.jedis.utils.lua

import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import redis.embedded.RedisServer
import spock.lang.Shared
import spock.lang.Specification

/**
 * Created on 22/09/15.
 * @author Yinon Avraham
 */
abstract class BaseIntegrationSpec extends Specification {

    private static final int PORT = 6399

    @Shared
    private RedisServer redisServer

    protected Jedis jedis

    def setupSpec() {
        println 'Starting embedded redis server'
        redisServer = new RedisServer(PORT)
        redisServer.start()
    }

    def cleanupSpec() {
        println 'Stopping embedded redis server'
        redisServer.stop()
    }

    def setup() {
        jedis = createJedis()
    }

    def cleanup() {
        if (jedis) {
            jedis.flushAll()
        }
        close(jedis)
    }

    protected Jedis createJedis() {
        return new Jedis('localhost', PORT)
    }

    protected JedisPool createJedisPool(Integer poolSize = null) {
        def config = new JedisPoolConfig()
        if (poolSize && poolSize > 0) config.maxTotal = poolSize
        return new JedisPool(config, 'localhost', PORT)
    }

    protected void close(Jedis jedis) {
        if (jedis) {
            jedis.close()
            jedis.disconnect()
        }
    }
}
