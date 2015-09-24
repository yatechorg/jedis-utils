package org.yatech.jedis.utils.lua.ast;

/**
 * Created by Yinon Avraham on 11/09/2015.
 */
public interface LuaAstVisitor {

    void visit(LuaAstScript script);

    void visit(LuaAstFunctionCallStatement statement);
    void visit(LuaAstAssignmentStatement statement);
    void visit(LuaAstIfStatement statement);
    void visit(LuaAstReturnStatement statement);

    void visit(LuaAstRedisCall redisCall);
    void visit(LuaAstUnpack unpack);

    void visit(LuaAstStringValue value);
    void visit(LuaAstIntValue value);
    void visit(LuaAstLongValue value);
    void visit(LuaAstDoubleValue value);

    void visit(LuaAstArg arg);

    void visit(LuaAstLocalDeclaration declaration);
    void visit(LuaAstLocal local);

    void visit(LuaAstNot not);
}
