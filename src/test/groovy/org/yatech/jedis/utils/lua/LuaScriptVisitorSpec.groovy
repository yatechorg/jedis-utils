package org.yatech.jedis.utils.lua

import org.yatech.jedis.utils.lua.ast.LuaAstArg
import org.yatech.jedis.utils.lua.ast.LuaAstAssignmentStatement
import org.yatech.jedis.utils.lua.ast.LuaAstDoubleValue
import org.yatech.jedis.utils.lua.ast.LuaAstFunction
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

    def 'visit function call statement'() {
        given:
        def function = Mock(LuaAstFunction)
        def functionCallStatement = new LuaAstFunctionCallStatement(function)
        def visitor = new LuaScriptVisitor()

        when:
        visitor.visit(functionCallStatement)

        then:
        1 * function.accept(visitor)
    }

    def 'visit assignment statement'() {
        given:
        def expression = new LuaAstStringValue('the-value')
        def assignmentStatement = new LuaAstAssignmentStatement(local, expression)
        def visitor = new LuaScriptVisitor()

        when:
        visitor.visit(assignmentStatement)

        then:
        visitor.toString() == expected

        where:
        local                                  | expected
        new LuaAstLocal('thelocal')            | 'thelocal = "the-value"\n'
        new LuaAstLocalDeclaration('thelocal') | 'local thelocal = "the-value"\n'
    }

    def 'visit if statement'() {
        given:
        def condition = new LuaAstNot(new LuaAstLocal('thelocal'))
        def block = new LuaAstScript()
        block.statements.add(new LuaAstReturnStatement())
        def ifStatement = new LuaAstIfStatement(condition, block)
        def visitor = new LuaScriptVisitor()

        when:
        visitor.visit(ifStatement)

        then:
        visitor.toString() == 'if (not thelocal) then\n  return\nend\n'
    }

    def 'visit return statement - void'() {
        given:
        def statement = new LuaAstReturnStatement()
        def visitor = new LuaScriptVisitor()

        when:
        visitor.visit(statement)

        then:
        visitor.toString() == 'return\n'
    }

    def 'visit return statement - local'() {
        given:
        def statement = new LuaAstReturnStatement(new LuaAstLocal('thelocal'))
        def visitor = new LuaScriptVisitor()

        when:
        visitor.visit(statement)

        then:
        visitor.toString() == 'return thelocal\n'
    }

    def 'visit redis call'() {
        given:
        def redisCall = new LuaAstRedisCall('THEMETHOD', [new LuaAstLocal('thelocal'), new LuaAstStringValue('value')])
        def visitor = new LuaScriptVisitor()

        when:
        visitor.visit(redisCall)

        then:
        visitor.toString() == 'redis.call("THEMETHOD",thelocal,"value")'
    }

    def 'visit unpack'() {
        given:
        def unpack = new LuaAstUnpack(new LuaAstLocal('thelocal'))
        def visitor = new LuaScriptVisitor()

        when:
        visitor.visit(unpack)

        then:
        visitor.toString() == 'unpack(thelocal)'
    }

    def 'visit string value'() {
        given:
        def value = new LuaAstStringValue('value')
        def visitor = new LuaScriptVisitor()

        when:
        visitor.visit(value)

        then:
        visitor.toString() == '"value"'
    }

    def 'visit int value'() {
        given:
        def value = new LuaAstIntValue(17)
        def visitor = new LuaScriptVisitor()

        when:
        visitor.visit(value)

        then:
        visitor.toString() == '17'
    }

    def 'visit double value'() {
        given:
        def value = new LuaAstDoubleValue(3.1415)
        def visitor = new LuaScriptVisitor()

        when:
        visitor.visit(value)

        then:
        visitor.toString() == '3.1415'
    }

    def 'visit argument'() {
        given:
        def arg = new LuaAstArg('thearg')
        def visitor = new LuaScriptVisitor()

        when:
        visitor.visit(arg)

        then:
        visitor.toString() == 'thearg'
    }

    def 'visit local declaration'() {
        given:
        def local = new LuaAstLocalDeclaration('thelocal')
        def visitor = new LuaScriptVisitor()

        when:
        visitor.visit(local)

        then:
        visitor.toString() == 'local thelocal'
    }

    def 'visit local'() {
        given:
        def local = new LuaAstLocal('thelocal')
        def visitor = new LuaScriptVisitor()

        when:
        visitor.visit(local)

        then:
        visitor.toString() == 'thelocal'
    }

    def 'visit not'() {
        given:
        def not = new LuaAstNot(new LuaAstLocal('thelocal'))
        def visitor = new LuaScriptVisitor()

        when:
        visitor.visit(not)

        then:
        visitor.toString() == 'not thelocal'
    }
}
