package org.yatech.jedis.utils.lua.ast;

import org.yatech.jedis.utils.lua.LuaLocalArray;

import java.util.Arrays;
import java.util.List;

/**
 * Created by yinona on 15/09/15.
 */
public class LuaAstHelper {

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

    public static LuaAstStringValue stringValue(String key) {
        return new LuaAstStringValue(key);
    }

    public static LuaAstDoubleValue doubleValue(double score) {
        return new LuaAstDoubleValue(score);
    }

    public static LuaAstLocalDeclaration declareLocal(String name) {
        return new LuaAstLocalDeclaration(name);
    }

    public static LuaAstUnpack unpack(String localName) {
        return new LuaAstUnpack(new LuaAstLocal(localName));
    }


}
