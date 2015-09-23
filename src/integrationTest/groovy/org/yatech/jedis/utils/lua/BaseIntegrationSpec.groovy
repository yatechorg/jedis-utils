package org.yatech.jedis.utils.lua

import redis.clients.jedis.Jedis
import redis.embedded.RedisServer
import spock.lang.Shared
import spock.lang.Specification

/**
 * Created by yinona on 22/09/15.
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

    protected void close(Jedis jedis) {
        if (jedis) {
            jedis.close()
            jedis.disconnect()
        }
    }
}
