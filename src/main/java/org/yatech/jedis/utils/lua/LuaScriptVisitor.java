package org.yatech.jedis.utils.lua;

import org.yatech.jedis.utils.lua.ast.*;

/**
 * Created by Yinon Avraham on 11/09/2015.
 */
class LuaScriptVisitor implements LuaAstVisitor {

    private final StringBuilder sb;
    private int depth;

    public LuaScriptVisitor() {
        this.sb = new StringBuilder();
        this.depth = 0;
    }

    @Override
    public String toString() {
        return sb.toString();
    }

    @Override
    public void visit(LuaAstScript script) {
        for (LuaAstStatement statement : script.getStatements()) {
            statement.accept(this);
        }
    }

    @Override
    public void visit(LuaAstFunctionCallStatement statement) {
        appendIndentation();
        statement.getFunction().accept(this);
        appendNewLine();
    }

    @Override
    public void visit(LuaAstAssignmentStatement statement) {
        appendIndentation();
        statement.getLocal().accept(this);
        append(" = ");
        statement.getExpression().accept(this);
        appendNewLine();
    }

    @Override
    public void visit(LuaAstIfStatement statement) {
        append("if (");
        statement.getCondition().accept(this);
        append(") then");
        appendNewLine();
        depth += 1;
        statement.getScriptBlock().accept(this);
        depth -= 1;
        append("end");
        appendNewLine();
    }

    @Override
    public void visit(LuaAstReturnStatement statement) {
        appendIndentation();
        append("return");
        if (statement.getExpression() != null) {
            append(" ");
            statement.getExpression().accept(this);
        }
        appendNewLine();
    }

    @Override
    public void visit(LuaAstRedisCall redisCall) {
        append("redis.call(\"");
        append(redisCall.getMethodName());
        append("\"");
        for (LuaAstExpression arg : redisCall.getArguments()) {
            append(",");
            arg.accept(this);
        }
        append(")");
    }

    @Override
    public void visit(LuaAstUnpack unpack) {
        append("unpack(");
        unpack.getLocal().accept(this);
        append(")");
    }

    @Override
    public void visit(LuaAstStringValue value) {
        append("\"");
        append(value.getValue());
        append("\"");
    }

    @Override
    public void visit(LuaAstIntValue value) {
        append(String.valueOf(value.getValue()));
    }

    @Override
    public void visit(LuaAstDoubleValue value) {
        append(String.valueOf(value.getValue()));
    }

    @Override
    public void visit(LuaAstArg arg) {
        append(arg.getName());
    }

    @Override
    public void visit(LuaAstLocalDeclaration declaration) {
        append("local ");
        append(declaration.getName());
    }

    @Override
    public void visit(LuaAstLocal local) {
        append(local.getName());
    }

    @Override
    public void visit(LuaAstNot not) {
        append("not ");
        not.getExpression().accept(this);
    }

    private void append(String s) {
        sb.append(s);
    }

    private void appendIndentation() {
        for (int i = 0; i < depth*2; i++) {
            sb.append(" ");
        }
    }

    private void appendNewLine() {
        sb.append("\n");
    }
}
