package org.yatech.jedis.utils.lua

import org.yatech.jedis.utils.lua.ast.LuaAstArg
import spock.lang.Specification
import static org.yatech.jedis.utils.lua.AbstractLuaScriptBuilder.*

/**
 * Created by yinona on 19/09/15.
 */
class AbstractLuaScriptBuilderSpec extends Specification {

    def "NewKeyArgument"() {
        when:
        def arg = newKeyArgument('keyArg')

        then:
        arg instanceof LuaKeyArgument
        arg.name == 'keyArg'
        arg.value == null
    }

    def "NewStringValueArgument"() {
        when:
        def arg = newStringValueArgument('strArg')

        then:
        arg instanceof LuaStringValueArgument
        arg.name == 'strArg'
        arg.value == null
    }

    def "NewIntValueArgument"() {
        when:
        def arg = newIntValueArgument('intArg')

        then:
        arg instanceof LuaIntValueArgument
        arg.name == 'intArg'
        arg.value == null
    }

    def "NewDoubleValueArgument"() {
        when:
        def arg = newDoubleValueArgument('dblArg')

        then:
        arg instanceof LuaDoubleValueArgument
        arg.name == 'dblArg'
        arg.value == null
    }

    def "select"() {
        given:
        def arg = newIntValueArgument('arg')
        def local = new LuaLocalValue('theLocal')

        when:
        def script = build { AbstractLuaScriptBuilder builder ->
            builder.select(1)
            builder.select(arg)
            builder.select(local)
        }

        then:
        script == 'redis.call("SELECT",1)\n' +
                'redis.call("SELECT",ARGV[1])\n' +
                'redis.call("SELECT",theLocal)\n'
    }

    def "set"() {
        given:
        def arg1 = newKeyArgument('arg1')
        def arg2 = newStringValueArgument('arg2')
        def local1 = new LuaLocalValue('theLocal1')
        def local2 = new LuaLocalValue('theLocal2')

        when:
        def script = build { AbstractLuaScriptBuilder builder ->
            builder.set('thekey', 'thevalue')
            builder.set('thekey', arg2)
            builder.set('thekey', local1)
            builder.set(arg1, 'thevalue')
            builder.set(arg1, arg2)
            builder.set(arg1, local1)
            builder.set(local1, 'thevalue')
            builder.set(local1, arg2)
            builder.set(local1, local2)
        }

        then:
        script == 'redis.call("SET","thekey","thevalue")\n' +
                'redis.call("SET","thekey",ARGV[1])\n' +
                'redis.call("SET","thekey",theLocal1)\n' +
                'redis.call("SET",KEYS[1],"thevalue")\n' +
                'redis.call("SET",KEYS[2],ARGV[2])\n' +
                'redis.call("SET",KEYS[3],theLocal1)\n' +
                'redis.call("SET",theLocal1,"thevalue")\n' +
                'redis.call("SET",theLocal1,ARGV[3])\n' +
                'redis.call("SET",theLocal1,theLocal2)\n'
    }

    def "get"() {
        given:
        def arg = newKeyArgument('arg')
        def local = new LuaLocalValue('theLocal')

        when:
        def script = build { AbstractLuaScriptBuilder builder ->
            builder.get('thekey')
            builder.get(arg)
            builder.get(local)
        }

        then:
        script == 'local dummyLocal = redis.call("GET","thekey")\n' +
                'local dummyLocal = redis.call("GET",KEYS[1])\n' +
                'local dummyLocal = redis.call("GET",theLocal)\n'
    }

    def "del"() {
        given:
        def arg = newKeyArgument('arg')
        def local = new LuaLocalValue('theLocal')

        when:
        def script = build { AbstractLuaScriptBuilder builder ->
            builder.del('thekey')
            builder.del(arg)
            builder.del(local)
        }

        then:
        script == 'redis.call("DEL","thekey")\n' +
                'redis.call("DEL",KEYS[1])\n' +
                'redis.call("DEL",theLocal)\n'
    }

    def "expire"() {
        given:
        def keyArg = newKeyArgument('keyArg')
        def secondsArg = newIntValueArgument('secondsArg')
        def local = new LuaLocalValue('theLocal')

        when:
        def script = build { AbstractLuaScriptBuilder builder -> builder.with {
            expire('thekey', 7)
            expire('thekey', secondsArg)
            expire('thekey', local)
            expire(keyArg, 7)
            expire(keyArg, secondsArg)
            expire(keyArg, local)
            expire(local, 7)
            expire(local, secondsArg)
            expire(local, local)
        }}

        then:
        script == 'redis.call("EXPIRE","thekey",7)\n' +
                'redis.call("EXPIRE","thekey",ARGV[1])\n' +
                'redis.call("EXPIRE","thekey",theLocal)\n' +
                'redis.call("EXPIRE",KEYS[1],7)\n' +
                'redis.call("EXPIRE",KEYS[2],ARGV[2])\n' +
                'redis.call("EXPIRE",KEYS[3],theLocal)\n' +
                'redis.call("EXPIRE",theLocal,7)\n' +
                'redis.call("EXPIRE",theLocal,ARGV[3])\n' +
                'redis.call("EXPIRE",theLocal,theLocal)\n'
    }

    def "expireAt"() {
        given:
        def keyArg = newKeyArgument('keyArg')
        def tsArg = newLongValueArgument('tsArg')
        def local = new LuaLocalValue('theLocal')

        when:
        def script = build { AbstractLuaScriptBuilder builder -> builder.with {
            expireAt('thekey', 7L)
            expireAt('thekey', tsArg)
            expireAt('thekey', local)
            expireAt(keyArg, 7L)
            expireAt(keyArg, tsArg)
            expireAt(keyArg, local)
            expireAt(local, 7L)
            expireAt(local, tsArg)
            expireAt(local, local)
        }}

        then:
        script == 'redis.call("EXPIREAT","thekey",7)\n' +
                'redis.call("EXPIREAT","thekey",ARGV[1])\n' +
                'redis.call("EXPIREAT","thekey",theLocal)\n' +
                'redis.call("EXPIREAT",KEYS[1],7)\n' +
                'redis.call("EXPIREAT",KEYS[2],ARGV[2])\n' +
                'redis.call("EXPIREAT",KEYS[3],theLocal)\n' +
                'redis.call("EXPIREAT",theLocal,7)\n' +
                'redis.call("EXPIREAT",theLocal,ARGV[3])\n' +
                'redis.call("EXPIREAT",theLocal,theLocal)\n'
    }

    def "keys"() {
        given:
        def arg = newStringValueArgument('arg')
        def local = new LuaLocalValue('theLocal')

        when:
        def res = []
        def script = build { AbstractLuaScriptBuilder builder -> builder.with {
            res << keys('pattern')
            res << keys(arg)
            res << keys(local)
        }}

        then:
        script == 'local dummyLocal = redis.call("KEYS","pattern")\n' +
                'local dummyLocal = redis.call("KEYS",ARGV[1])\n' +
                'local dummyLocal = redis.call("KEYS",theLocal)\n'
        res.each {
            assert it instanceof LuaLocalArray
            assert it.name == 'dummyLocal'
        }
    }

    def "move"() {
        given:
        def keyArg = newKeyArgument('keyArg')
        def dbArg = newIntValueArgument('dbArg')
        def local = new LuaLocalValue('theLocal')

        when:
        def script = build { AbstractLuaScriptBuilder builder -> builder.with {
            move('thekey', 1)
            move('thekey', dbArg)
            move('thekey', local)
            move(keyArg, 2)
            move(keyArg, dbArg)
            move(keyArg, local)
            move(local, 3)
            move(local, dbArg)
            move(local, local)
        }}

        then:
        script == 'redis.call("MOVE","thekey",1)\n' +
                'redis.call("MOVE","thekey",ARGV[1])\n' +
                'redis.call("MOVE","thekey",theLocal)\n' +
                'redis.call("MOVE",KEYS[1],2)\n' +
                'redis.call("MOVE",KEYS[2],ARGV[2])\n' +
                'redis.call("MOVE",KEYS[3],theLocal)\n' +
                'redis.call("MOVE",theLocal,3)\n' +
                'redis.call("MOVE",theLocal,ARGV[3])\n' +
                'redis.call("MOVE",theLocal,theLocal)\n'
    }

    def "persist"() {
        given:
        def arg = newKeyArgument('arg')
        def local = new LuaLocalValue('theLocal')

        when:
        def script = build { AbstractLuaScriptBuilder builder -> builder.with {
            persist('thekey')
            persist(arg)
            persist(local)
        }}

        then:
        script == 'redis.call("PERSIST","thekey")\n' +
                'redis.call("PERSIST",KEYS[1])\n' +
                'redis.call("PERSIST",theLocal)\n'
    }

    def "pexpire"() {
        given:
        def keyArg = newKeyArgument('keyArg')
        def msArg = newLongValueArgument('msArg')
        def local = new LuaLocalValue('theLocal')

        when:
        def script = build { AbstractLuaScriptBuilder builder -> builder.with {
            pexpire('thekey', 7L)
            pexpire('thekey', msArg)
            pexpire('thekey', local)
            pexpire(keyArg, 7L)
            pexpire(keyArg, msArg)
            pexpire(keyArg, local)
            pexpire(local, 7L)
            pexpire(local, msArg)
            pexpire(local, local)
        }}

        then:
        script == 'redis.call("PEXPIRE","thekey",7)\n' +
                'redis.call("PEXPIRE","thekey",ARGV[1])\n' +
                'redis.call("PEXPIRE","thekey",theLocal)\n' +
                'redis.call("PEXPIRE",KEYS[1],7)\n' +
                'redis.call("PEXPIRE",KEYS[2],ARGV[2])\n' +
                'redis.call("PEXPIRE",KEYS[3],theLocal)\n' +
                'redis.call("PEXPIRE",theLocal,7)\n' +
                'redis.call("PEXPIRE",theLocal,ARGV[3])\n' +
                'redis.call("PEXPIRE",theLocal,theLocal)\n'
    }

    def "pexpireAt"() {
        given:
        def keyArg = newKeyArgument('keyArg')
        def tsArg = newLongValueArgument('tsArg')
        def local = new LuaLocalValue('theLocal')

        when:
        def script = build { AbstractLuaScriptBuilder builder -> builder.with {
            pexpireAt('thekey', 7L)
            pexpireAt('thekey', tsArg)
            pexpireAt('thekey', local)
            pexpireAt(keyArg, 7L)
            pexpireAt(keyArg, tsArg)
            pexpireAt(keyArg, local)
            pexpireAt(local, 7L)
            pexpireAt(local, tsArg)
            pexpireAt(local, local)
        }}

        then:
        script == 'redis.call("PEXPIREAT","thekey",7)\n' +
                'redis.call("PEXPIREAT","thekey",ARGV[1])\n' +
                'redis.call("PEXPIREAT","thekey",theLocal)\n' +
                'redis.call("PEXPIREAT",KEYS[1],7)\n' +
                'redis.call("PEXPIREAT",KEYS[2],ARGV[2])\n' +
                'redis.call("PEXPIREAT",KEYS[3],theLocal)\n' +
                'redis.call("PEXPIREAT",theLocal,7)\n' +
                'redis.call("PEXPIREAT",theLocal,ARGV[3])\n' +
                'redis.call("PEXPIREAT",theLocal,theLocal)\n'
    }

    def "pttl"() {
        given:
        def arg = newKeyArgument('arg')
        def local = new LuaLocalValue('theLocal')
        def ret = []

        when:
        def script = build { AbstractLuaScriptBuilder builder -> builder.with {
            ret << pttl('thekey')
            ret << pttl(arg)
            ret << pttl(local)
        }}

        then:
        script == 'local dummyLocal = redis.call("PTTL","thekey")\n' +
                'local dummyLocal = redis.call("PTTL",KEYS[1])\n' +
                'local dummyLocal = redis.call("PTTL",theLocal)\n'
        ret.each {
            assert it instanceof LuaLocalValue
            assert it.name == 'dummyLocal'
        }
    }

    def "randomKey"() {
        given:
        def res = []

        when:
        def script = build { AbstractLuaScriptBuilder builder -> builder.with {
            res << randomKey()
        }}

        then:
        script == 'local dummyLocal = redis.call("RANDOMKEY")\n'
        res.each {
            assert it instanceof LuaLocalValue
            assert it.name == 'dummyLocal'
        }
    }

    def "rename"() {
        given:
        def keyArg = newKeyArgument('keyArg')
        def newKeyArg = newKeyArgument('newKeyArg')
        def local = new LuaLocalValue('theLocal')

        when:
        def script = build { AbstractLuaScriptBuilder builder -> builder.with {
            rename('thekey', 'thenewkey')
            rename('thekey', newKeyArg)
            rename('thekey', local)
            rename(keyArg, 'thenewkey')
            rename(keyArg, newKeyArg)
            rename(keyArg, local)
            rename(local, 'thenewkey')
            rename(local, newKeyArg)
            rename(local, local)
        }}

        then:
        script == 'redis.call("RENAME","thekey","thenewkey")\n' +
                'redis.call("RENAME","thekey",KEYS[1])\n' +
                'redis.call("RENAME","thekey",theLocal)\n' +
                'redis.call("RENAME",KEYS[2],"thenewkey")\n' +
                'redis.call("RENAME",KEYS[3],KEYS[4])\n' +
                'redis.call("RENAME",KEYS[5],theLocal)\n' +
                'redis.call("RENAME",theLocal,"thenewkey")\n' +
                'redis.call("RENAME",theLocal,KEYS[6])\n' +
                'redis.call("RENAME",theLocal,theLocal)\n'
    }

    def "renamenx"() {
        given:
        def keyArg = newKeyArgument('keyArg')
        def newKeyArg = newKeyArgument('newKeyArg')
        def local = new LuaLocalValue('theLocal')

        when:
        def script = build { AbstractLuaScriptBuilder builder -> builder.with {
            renamenx('thekey', 'thenewkey')
            renamenx('thekey', newKeyArg)
            renamenx('thekey', local)
            renamenx(keyArg, 'thenewkey')
            renamenx(keyArg, newKeyArg)
            renamenx(keyArg, local)
            renamenx(local, 'thenewkey')
            renamenx(local, newKeyArg)
            renamenx(local, local)
        }}

        then:
        script == 'redis.call("RENAMENX","thekey","thenewkey")\n' +
                'redis.call("RENAMENX","thekey",KEYS[1])\n' +
                'redis.call("RENAMENX","thekey",theLocal)\n' +
                'redis.call("RENAMENX",KEYS[2],"thenewkey")\n' +
                'redis.call("RENAMENX",KEYS[3],KEYS[4])\n' +
                'redis.call("RENAMENX",KEYS[5],theLocal)\n' +
                'redis.call("RENAMENX",theLocal,"thenewkey")\n' +
                'redis.call("RENAMENX",theLocal,KEYS[6])\n' +
                'redis.call("RENAMENX",theLocal,theLocal)\n'
    }

    def "hgetAll"() {
        given:
        def arg = newKeyArgument('arg')
        def local = new LuaLocalValue('theLocal')
        def ret = []

        when:
        def script = build { AbstractLuaScriptBuilder builder ->
            ret << builder.hgetAll('thekey')
            ret << builder.hgetAll(arg)
            ret << builder.hgetAll(local)
        }

        then:
        script == 'local dummyLocal = redis.call("HGETALL","thekey")\n' +
                'local dummyLocal = redis.call("HGETALL",KEYS[1])\n' +
                'local dummyLocal = redis.call("HGETALL",theLocal)\n'
        ret.each {
            assert it instanceof LuaLocalArray
            assert it.name == 'dummyLocal'
        }
    }

    def "hmset"() {
        given:
        def arg = newKeyArgument('arg')
        def local = new LuaLocalValue('theLocal')
        def hash = new LuaLocalArray('theHash')

        when:
        def script = build { AbstractLuaScriptBuilder builder ->
            builder.hmset('thekey', hash)
                    .hmset(arg, hash)
                    .hmset(local, hash)
        }

        then:
        script == 'redis.call("HMSET","thekey",unpack(theHash))\n' +
                'redis.call("HMSET",KEYS[1],unpack(theHash))\n' +
                'redis.call("HMSET",theLocal,unpack(theHash))\n'
    }

    def "hmset with Map not implemented"() {
        given:
        def arg = newStringValueArgument('arg')
        def local = new LuaLocalValue('theLocal')
        def hash = ['k1':'v1']

        when:
        build { AbstractLuaScriptBuilder builder ->
            dohmset.call(builder, arg, local, hash)
        }

        then:
        thrown(UnsupportedOperationException)

        where:
        dohmset << [ { b, l, a, h -> b.hmset('thekey', h) },
                     { b, l, a, h -> b.hmset(a, h) },
                     { b, l, a, h -> b.hmset(l, h) } ]
    }

    def "zadd"() {
        given:
        def keyArg = newKeyArgument('keyArg')
        def keyLocal = new LuaLocalValue('keyLocal')
        def scoreArg = newDoubleValueArgument('scoreArg')
        def scoreLocal = new LuaLocalValue('scoreLocal')
        def memberArg = newStringValueArgument('memberArg')
        def memberLocal = new LuaLocalValue('memberLocal')
        def scoresForMembersLocal = new LuaLocalArray('scoresForMembersLocal')

        when:
        def script = build { AbstractLuaScriptBuilder builder ->
            builder.zadd('thekey', 1.23, 'member1')
                    .zadd('thekey', 1.23, memberArg)
                    .zadd('thekey', 1.23, memberLocal)
                    .zadd('thekey', scoreArg, 'member1')
                    .zadd('thekey', scoreArg, memberArg)
                    .zadd('thekey', scoreArg, memberLocal)
                    .zadd('thekey', scoreLocal, 'member1')
                    .zadd('thekey', scoreLocal, memberArg)
                    .zadd('thekey', scoreLocal, memberLocal)
                    .zadd(keyArg, 1.23, 'member1')
                    .zadd(keyArg, 1.23, memberArg)
                    .zadd(keyArg, 1.23, memberLocal)
                    .zadd(keyArg, scoreArg, 'member1')
                    .zadd(keyArg, scoreArg, memberArg)
                    .zadd(keyArg, scoreArg, memberLocal)
                    .zadd(keyArg, scoreLocal, 'member1')
                    .zadd(keyArg, scoreLocal, memberArg)
                    .zadd(keyArg, scoreLocal, memberLocal)
                    .zadd(keyLocal, 1.23, 'member1')
                    .zadd(keyLocal, 1.23, memberArg)
                    .zadd(keyLocal, 1.23, memberLocal)
                    .zadd(keyLocal, scoreArg, 'member1')
                    .zadd(keyLocal, scoreArg, memberArg)
                    .zadd(keyLocal, scoreArg, memberLocal)
                    .zadd(keyLocal, scoreLocal, 'member1')
                    .zadd(keyLocal, scoreLocal, memberArg)
                    .zadd(keyLocal, scoreLocal, memberLocal)
                    .zadd('thekey', scoresForMembersLocal)
                    .zadd(keyArg, scoresForMembersLocal)
                    .zadd(keyLocal, scoresForMembersLocal)
        }

        then:
        script == 'redis.call("ZADD","thekey",1.23,"member1")\n' +
                'redis.call("ZADD","thekey",1.23,ARGV[1])\n' +
                'redis.call("ZADD","thekey",1.23,memberLocal)\n' +
                'redis.call("ZADD","thekey",ARGV[2],"member1")\n' +
                'redis.call("ZADD","thekey",ARGV[3],ARGV[4])\n' +
                'redis.call("ZADD","thekey",ARGV[5],memberLocal)\n' +
                'redis.call("ZADD","thekey",scoreLocal,"member1")\n' +
                'redis.call("ZADD","thekey",scoreLocal,ARGV[6])\n' +
                'redis.call("ZADD","thekey",scoreLocal,memberLocal)\n' +
                'redis.call("ZADD",KEYS[1],1.23,"member1")\n' +
                'redis.call("ZADD",KEYS[2],1.23,ARGV[7])\n' +
                'redis.call("ZADD",KEYS[3],1.23,memberLocal)\n' +
                'redis.call("ZADD",KEYS[4],ARGV[8],"member1")\n' +
                'redis.call("ZADD",KEYS[5],ARGV[9],ARGV[10])\n' +
                'redis.call("ZADD",KEYS[6],ARGV[11],memberLocal)\n' +
                'redis.call("ZADD",KEYS[7],scoreLocal,"member1")\n' +
                'redis.call("ZADD",KEYS[8],scoreLocal,ARGV[12])\n' +
                'redis.call("ZADD",KEYS[9],scoreLocal,memberLocal)\n' +
                'redis.call("ZADD",keyLocal,1.23,"member1")\n' +
                'redis.call("ZADD",keyLocal,1.23,ARGV[13])\n' +
                'redis.call("ZADD",keyLocal,1.23,memberLocal)\n' +
                'redis.call("ZADD",keyLocal,ARGV[14],"member1")\n' +
                'redis.call("ZADD",keyLocal,ARGV[15],ARGV[16])\n' +
                'redis.call("ZADD",keyLocal,ARGV[17],memberLocal)\n' +
                'redis.call("ZADD",keyLocal,scoreLocal,"member1")\n' +
                'redis.call("ZADD",keyLocal,scoreLocal,ARGV[18])\n' +
                'redis.call("ZADD",keyLocal,scoreLocal,memberLocal)\n' +
                'redis.call("ZADD","thekey",unpack(scoresForMembersLocal))\n' +
                'redis.call("ZADD",KEYS[10],unpack(scoresForMembersLocal))\n' +
                'redis.call("ZADD",keyLocal,unpack(scoresForMembersLocal))\n'
    }

    def "zadd with Map not implemented"() {
        given:
        def keyArg = newKeyArgument('keyArg')
        def keyLocal = new LuaLocalValue('keyLocal')
        def membersScores = ['m1':1.23]

        when:
        build { AbstractLuaScriptBuilder builder ->
            dozadd.call(builder, keyLocal, keyArg, membersScores)
        }

        then:
        thrown(UnsupportedOperationException)

        where:
        dozadd << [ { b, l, a, ms -> b.zadd('thekey', ms) },
                     { b, l, a, ms -> b.zadd(a, ms) },
                     { b, l, a, ms -> b.zadd(l, ms) } ]
    }

    def "zscore"() {
        given:
        def keyArg = newKeyArgument('keyArg')
        def keyLocal = new LuaLocalValue('keyLocal')
        def memberArg = newStringValueArgument('memberArg')
        def memberLocal = new LuaLocalValue('memberLocal')
        def ret = []

        when:
        def script = build { AbstractLuaScriptBuilder builder ->
            ret << builder.zscore('thekey', 'member1')
            ret << builder.zscore('thekey', memberArg)
            ret << builder.zscore('thekey', memberLocal)
            ret << builder.zscore(keyArg, 'member1')
            ret << builder.zscore(keyArg, memberArg)
            ret << builder.zscore(keyArg, memberLocal)
            ret << builder.zscore(keyLocal, 'member1')
            ret << builder.zscore(keyLocal, memberArg)
            ret << builder.zscore(keyLocal, memberLocal)
        }

        then:
        script == 'local dummyLocal = redis.call("ZSCORE","thekey","member1")\n' +
                'local dummyLocal = redis.call("ZSCORE","thekey",ARGV[1])\n' +
                'local dummyLocal = redis.call("ZSCORE","thekey",memberLocal)\n' +
                'local dummyLocal = redis.call("ZSCORE",KEYS[1],"member1")\n' +
                'local dummyLocal = redis.call("ZSCORE",KEYS[2],ARGV[2])\n' +
                'local dummyLocal = redis.call("ZSCORE",KEYS[3],memberLocal)\n' +
                'local dummyLocal = redis.call("ZSCORE",keyLocal,"member1")\n' +
                'local dummyLocal = redis.call("ZSCORE",keyLocal,ARGV[3])\n' +
                'local dummyLocal = redis.call("ZSCORE",keyLocal,memberLocal)\n'
        ret.each {
            assert it instanceof LuaLocalValue
            assert it.name == 'dummyLocal'
        }
    }

    private String build(Closure testBlock) {
        def builder = new AbstractLuaScriptBuilder() {
            private int argvIndex = 1
            private int keysIndex = 1
            @Override
            String getNextLocalName() {
                'dummyLocal'
            }
            @Override
            LuaAstArg getOrCreateArgvArgument(LuaValueArgument valueArgument) {
                new LuaAstArg("ARGV[${argvIndex++}]")
            }
            @Override
            LuaAstArg getOrCreateKeyArgument(LuaKeyArgument keyArgument) {
                new LuaAstArg("KEYS[${keysIndex++}]")
            }
        }
        testBlock.call(builder)
        def scriptVisitor = new LuaScriptVisitor()
        builder.script.accept(scriptVisitor)
        scriptVisitor.toString()
    }
}
