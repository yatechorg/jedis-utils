package org.yatech.jedis.utils.lua

import org.yatech.jedis.utils.lua.ast.LuaAstArg
import org.yatech.jedis.utils.lua.ast.LuaAstAssignmentStatement
import org.yatech.jedis.utils.lua.ast.LuaAstDoubleValue
import org.yatech.jedis.utils.lua.ast.LuaAstFunctionCallStatement
import org.yatech.jedis.utils.lua.ast.LuaAstIfStatement
import org.yatech.jedis.utils.lua.ast.LuaAstIntValue
import org.yatech.jedis.utils.lua.ast.LuaAstLocal
import org.yatech.jedis.utils.lua.ast.LuaAstLocalDeclaration
import org.yatech.jedis.utils.lua.ast.LuaAstNot
import org.yatech.jedis.utils.lua.ast.LuaAstRedisCall
import org.yatech.jedis.utils.lua.ast.LuaAstReturnStatement
import org.yatech.jedis.utils.lua.ast.LuaAstScript
import org.yatech.jedis.utils.lua.ast.LuaAstStringValue
import org.yatech.jedis.utils.lua.ast.LuaAstUnpack
import spock.lang.Specification

/**
 * Created by Yinon Avraham on 11/09/2015.
 */
class LuaScriptVisitorSpec extends Specification {

    def 'Visit script 1'() {
        given:
        def script = new LuaAstScript()
        script.statements << new LuaAstFunctionCallStatement(new LuaAstRedisCall('SELECT', [new LuaAstIntValue(0)]))
        script.statements << new LuaAstAssignmentStatement(new LuaAstLocalDeclaration('local0'), new LuaAstRedisCall('HGETALL', [new LuaAstStringValue('key1')]))
        script.statements << new LuaAstFunctionCallStatement(new LuaAstRedisCall('SELECT', [new LuaAstIntValue(1)]))
        script.statements << new LuaAstAssignmentStatement(new LuaAstLocalDeclaration('local1'), new LuaAstRedisCall('ZSCORE', [new LuaAstStringValue('key2'), new LuaAstStringValue('member1')]))
        def ifBlock = new LuaAstScript()
        ifBlock.statements << new LuaAstFunctionCallStatement(new LuaAstRedisCall('ZADD', [new LuaAstStringValue('key2'), new LuaAstDoubleValue(1.2), new LuaAstStringValue('member1')]))
        script.statements << new LuaAstIfStatement(new LuaAstNot(new LuaAstLocal('local1')), ifBlock)
        script.statements << new LuaAstFunctionCallStatement(new LuaAstRedisCall('HMSET', [new LuaAstStringValue('key1'), new LuaAstUnpack(new LuaAstLocal('local0'))]))
        script.statements << new LuaAstFunctionCallStatement(new LuaAstRedisCall('SELECT', [new LuaAstIntValue(0)]))
        script.statements << new LuaAstFunctionCallStatement(new LuaAstRedisCall('DEL', [new LuaAstStringValue('key1')]))
        script.statements << new LuaAstReturnStatement()

        def scriptVisitor = new LuaScriptVisitor()

        when:
        script.accept(scriptVisitor)
        def scriptText = scriptVisitor.toString()

        then:
        scriptText == 'redis.call("SELECT",0)\n' +
                'local local0 = redis.call("HGETALL","key1")\n' +
                'redis.call("SELECT",1)\n' +
                'local local1 = redis.call("ZSCORE","key2","member1")\n' +
                'if (not local1) then\n' +
                '  redis.call("ZADD","key2",1.2,"member1")\n' +
                'end\n' +
                'redis.call("HMSET","key1",unpack(local0))\n' +
                'redis.call("SELECT",0)\n' +
                'redis.call("DEL","key1")\n' +
                'return\n'
    }

    def 'Visit script 1 - with args'() {
        given:
        def script = new LuaAstScript()
        script.statements << new LuaAstFunctionCallStatement(new LuaAstRedisCall('SELECT', [new LuaAstArg('ARGV[1]')]))
        script.statements << new LuaAstAssignmentStatement(new LuaAstLocalDeclaration('local0'), new LuaAstRedisCall('HGETALL', [new LuaAstArg('KEYS[1]')]))
        script.statements << new LuaAstFunctionCallStatement(new LuaAstRedisCall('SELECT', [new LuaAstArg('ARGV[2]')]))
        script.statements << new LuaAstAssignmentStatement(new LuaAstLocalDeclaration('local1'), new LuaAstRedisCall('ZSCORE', [new LuaAstArg('KEYS[2]'), new LuaAstArg('ARGV[3]')]))
        def ifBlock = new LuaAstScript()
        ifBlock.statements << new LuaAstFunctionCallStatement(new LuaAstRedisCall('ZADD', [new LuaAstArg('KEYS[2]'), new LuaAstArg('ARGV[4]'), new LuaAstArg('ARGV[3]')]))
        script.statements << new LuaAstIfStatement(new LuaAstNot(new LuaAstLocal('local1')), ifBlock)
        script.statements << new LuaAstFunctionCallStatement(new LuaAstRedisCall('HMSET', [new LuaAstArg('KEYS[1]'), new LuaAstUnpack(new LuaAstLocal('local0'))]))
        script.statements << new LuaAstFunctionCallStatement(new LuaAstRedisCall('SELECT', [new LuaAstArg('ARGV[1]')]))
        script.statements << new LuaAstFunctionCallStatement(new LuaAstRedisCall('DEL', [new LuaAstArg('KEYS[1]')]))
        script.statements << new LuaAstReturnStatement()

        def scriptVisitor = new LuaScriptVisitor()

        when:
        script.accept(scriptVisitor)
        def scriptText = scriptVisitor.toString()

        then:
        scriptText == 'redis.call("SELECT",ARGV[1])\n' +
                'local local0 = redis.call("HGETALL",KEYS[1])\n' +
                'redis.call("SELECT",ARGV[2])\n' +
                'local local1 = redis.call("ZSCORE",KEYS[2],ARGV[3])\n' +
                'if (not local1) then\n' +
                '  redis.call("ZADD",KEYS[2],ARGV[4],ARGV[3])\n' +
                'end\n' +
                'redis.call("HMSET",KEYS[1],unpack(local0))\n' +
                'redis.call("SELECT",ARGV[1])\n' +
                'redis.call("DEL",KEYS[1])\n' +
                'return\n'
    }
}
