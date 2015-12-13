package org.yatech.jedis.utils.lua.ast;

import java.util.Arrays;
import java.util.List;

/**
 * Created on 15/09/15.
 * @author Yinon Avraham
 */
public class LuaAstHelper {

    private LuaAstHelper() {}

    public static LuaAstFunctionCallStatement redisCallStatement(String methodName, List<LuaAstExpression> arguments) {
        return new LuaAstFunctionCallStatement(redisCall(methodName, arguments));
    }

    public static LuaAstRedisCall redisCall(String methodName, List<LuaAstExpression> arguments) {
        return new LuaAstRedisCall(methodName, arguments);
    }

    public static List<LuaAstExpression> arguments(LuaAstExpression... args) {
        return Arrays.asList(args);
    }

    public static LuaAstAssignmentStatement assignment(LuaAstLocalDeclaration local, LuaAstExpression expression) {
        return new LuaAstAssignmentStatement(local, expression);
    }

    public static LuaAstAssignmentStatement assignment(LuaAstLocal local, LuaAstExpression expression) {
        return new LuaAstAssignmentStatement(local, expression);
    }

    public static LuaAstStringValue stringValue(String value) {
        return new LuaAstStringValue(value);
    }

    public static LuaAstIntValue intValue(int value) {
        return new LuaAstIntValue(value);
    }

    public static LuaAstLongValue longValue(long value) {
        return new LuaAstLongValue(value);
    }

    public static LuaAstDoubleValue doubleValue(double value) {
        return new LuaAstDoubleValue(value);
    }

    public static LuaAstLocalDeclaration declareLocal(String name) {
        return new LuaAstLocalDeclaration(name);
    }

    public static LuaAstUnpack unpack(LuaAstLocal local) {
        return new LuaAstUnpack(local);
    }

    public static LuaAstLocal local(String localName) {
        return new LuaAstLocal(localName);
    }


}
