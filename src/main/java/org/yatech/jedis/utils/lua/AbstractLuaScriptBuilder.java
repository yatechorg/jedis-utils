package org.yatech.jedis.utils.lua;

import org.yatech.jedis.utils.lua.ast.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Yinon Avraham on 01/09/2015.
 */
public abstract class AbstractLuaScriptBuilder<BuilderType extends AbstractLuaScriptBuilder> implements JedisCommands<BuilderType> {

    protected final LuaAstScript script;

    protected AbstractLuaScriptBuilder() {
        this.script = new LuaAstScript();
    }

    private BuilderType thisBuilder() {
        return (BuilderType)this;
    }

    abstract String getNextLocalName();
    abstract LuaAstArg getOrCreateArgvArgument(LuaValueArgument<?> valueArgument);
    abstract LuaAstArg getOrCreateKeyArgument(LuaKeyArgument keyArgument);

    void add(LuaAstStatement statement) {
        script.getStatements().add(statement);
    }

    private LuaAstFunctionCallStatement redisCallStatement(String methodName, List<LuaAstExpression> arguments) {
        return new LuaAstFunctionCallStatement(redisCall(methodName, arguments));
    }

    private LuaAstRedisCall redisCall(String methodName, List<LuaAstExpression> arguments) {
        return new LuaAstRedisCall(methodName, arguments);
    }

    private List<LuaAstExpression> arguments(LuaAstExpression... args) {
        return Arrays.asList(args);
    }

    private LuaAstAssignmentStatement assignment(LuaAstLocalDeclaration local, LuaAstExpression expression) {
        return new LuaAstAssignmentStatement(local, expression);
    }

    private LuaAstStringValue stringValue(String key) {
        return new LuaAstStringValue(key);
    }

    private LuaAstDoubleValue doubleValue(double score) {
        return new LuaAstDoubleValue(score);
    }

    private LuaAstLocalDeclaration localDeclaration() {
        String name = getNextLocalName();
        return new LuaAstLocalDeclaration(name);
    }

    private LuaAstUnpack unpack(LuaLocalArray hash) {
        return new LuaAstUnpack(new LuaAstLocal(hash.getName()));
    }

    // *** Arguments ***

    public static LuaKeyArgument newKeyArgument(String argName) {
        return new LuaKeyArgument(argName);
    }

    public static LuaStringValueArgument newStringValueArgument(String argName) {
        return new LuaStringValueArgument(argName);
    }

    public static LuaIntValueArgument newIntValueArgument(String argName) {
        return new LuaIntValueArgument(argName);
    }

    public static LuaDoubleValueArgument newDoubleValueArgument(String argName) {
        return new LuaDoubleValueArgument(argName);
    }

    // *** If condition ***

    public LuaIfConditionBuilder<BuilderType> ifCondition(LuaCondition condition) {
        return new LuaIfConditionBuilder<BuilderType>(this, condition);
    }

    // *** Jedis Commands ***

    @Override
    public BuilderType select(int db) {
        add(redisCallStatement("SELECT", arguments(new LuaAstIntValue(db))));
        return thisBuilder();
    }

    @Override
    public BuilderType select(LuaIntValueArgument db) {
        LuaAstArg dbArg = getOrCreateArgvArgument(db);
        add(redisCallStatement("SELECT", arguments(dbArg)));
        return thisBuilder();
    }

    @Override
    public LuaLocalArray hgetAll(String key) {
        LuaAstLocalDeclaration local = localDeclaration();
        add(assignment(local, redisCall("HGETALL", arguments(stringValue(key)))));
        return new LuaLocalArray(local.getName());
    }

    @Override
    public LuaLocalArray hgetAll(LuaLocalValue key) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public LuaLocalArray hgetAll(LuaKeyArgument key) {
        LuaAstLocalDeclaration local = localDeclaration();
        LuaAstArg keyArg = getOrCreateKeyArgument(key);
        add(assignment(local, redisCall("HGETALL", arguments(keyArg))));
        return new LuaLocalArray(local.getName());
    }

    @Override
    public LuaLocalValue zscore(String key, String member) {
        LuaAstLocalDeclaration local = localDeclaration();
        add(assignment(local, redisCall("ZSCORE", arguments(stringValue(key), stringValue(member)))));
        return new LuaLocalValue(local.getName());
    }

    @Override
    public LuaLocalValue zscore(LuaLocalValue key, String member) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public LuaLocalValue zscore(LuaKeyArgument key, String member) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public LuaLocalValue zscore(String key, LuaLocalValue member) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public LuaLocalValue zscore(String key, LuaStringValueArgument member) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public LuaLocalValue zscore(LuaLocalValue key, LuaLocalValue member) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public LuaLocalValue zscore(LuaLocalValue key, LuaStringValueArgument member) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public LuaLocalValue zscore(LuaKeyArgument key, LuaLocalValue member) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public LuaLocalValue zscore(LuaKeyArgument key, LuaStringValueArgument member) {
        LuaAstLocalDeclaration local = localDeclaration();
        LuaAstArg keyArg = getOrCreateKeyArgument(key);
        LuaAstArg memberArg = getOrCreateArgvArgument(member);
        add(assignment(local, redisCall("ZSCORE", arguments(keyArg, memberArg))));
        return new LuaLocalValue(local.getName());
    }

    @Override
    public BuilderType zadd(String key, double score, String member) {
        add(redisCallStatement("ZADD", arguments(stringValue(key), doubleValue(score), stringValue(member))));
        return thisBuilder();
    }

    @Override
    public BuilderType zadd(LuaLocalValue key, double score, String member) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public BuilderType zadd(String key, LuaLocalValue score, String member) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public BuilderType zadd(String key, double score, LuaLocalValue member) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public BuilderType zadd(String key, double score, LuaStringValueArgument member) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public BuilderType zadd(LuaLocalValue key, LuaLocalValue score, String member) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public BuilderType zadd(LuaLocalValue key, double score, LuaLocalValue member) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public BuilderType zadd(LuaLocalValue key, double score, LuaStringValueArgument member) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public BuilderType zadd(String key, LuaLocalValue score, LuaLocalValue member) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public BuilderType zadd(String key, LuaLocalValue score, LuaStringValueArgument member) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public BuilderType zadd(String key, LuaDoubleValueArgument score, String member) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public BuilderType zadd(String key, LuaDoubleValueArgument score, LuaLocalValue member) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public BuilderType zadd(String key, LuaDoubleValueArgument score, LuaStringValueArgument member) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public BuilderType zadd(LuaLocalValue key, LuaLocalValue score, LuaLocalValue member) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public BuilderType zadd(LuaLocalValue key, LuaLocalValue score, LuaStringValueArgument member) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public BuilderType zadd(LuaLocalValue key, LuaDoubleValueArgument score, String member) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public BuilderType zadd(LuaLocalValue key, LuaDoubleValueArgument score, LuaLocalValue member) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public BuilderType zadd(LuaLocalValue key, LuaDoubleValueArgument score, LuaStringValueArgument member) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public BuilderType zadd(LuaKeyArgument key, double score, String member) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public BuilderType zadd(LuaKeyArgument key, double score, LuaLocalValue member) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public BuilderType zadd(LuaKeyArgument key, double score, LuaStringValueArgument member) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public BuilderType zadd(LuaKeyArgument key, LuaLocalValue score, String member) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public BuilderType zadd(LuaKeyArgument key, LuaLocalValue score, LuaLocalValue member) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public BuilderType zadd(LuaKeyArgument key, LuaLocalValue score, LuaStringValueArgument member) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public BuilderType zadd(LuaKeyArgument key, LuaDoubleValueArgument score, String member) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public BuilderType zadd(LuaKeyArgument key, LuaDoubleValueArgument score, LuaLocalValue member) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public BuilderType zadd(LuaKeyArgument key, LuaDoubleValueArgument score, LuaStringValueArgument member) {
        LuaAstArg keyArg = getOrCreateKeyArgument(key);
        LuaAstArg scoreArg = getOrCreateArgvArgument(score);
        LuaAstArg memberArg = getOrCreateArgvArgument(member);
        add(redisCallStatement("ZADD", arguments(keyArg, scoreArg, memberArg)));
        return thisBuilder();
    }

    @Override
    public BuilderType zadd(String key, Map<String, Double> scoreMembers) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public BuilderType zadd(LuaLocalValue key, Map<String, Double> scoreMembers) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public BuilderType zadd(LuaKeyArgument key, Map<String, Double> scoreMembers) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public BuilderType zadd(String key, LuaLocalArray scoreMembers) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public BuilderType zadd(LuaLocalValue key, LuaLocalArray scoreMembers) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public BuilderType zadd(LuaKeyArgument key, LuaLocalArray scoreMembers) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public BuilderType hmset(String key, Map<String, String> hash) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public BuilderType hmset(LuaLocalValue key, Map<String, String> hash) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public BuilderType hmset(LuaKeyArgument key, Map<String, String> hash) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public BuilderType hmset(String key, LuaLocalArray hash) {
        add(redisCallStatement("HMSET", arguments(stringValue(key), unpack(hash))));
        return thisBuilder();
    }

    @Override
    public BuilderType hmset(LuaLocalValue key, LuaLocalArray hash) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public BuilderType hmset(LuaKeyArgument key, LuaLocalArray hash) {
        LuaAstArg keyArg = getOrCreateKeyArgument(key);
        add(redisCallStatement("HMSET", arguments(keyArg, unpack(hash))));
        return thisBuilder();
    }

    @Override
    public BuilderType del(String key) {
        add(redisCallStatement("DEL", arguments(stringValue(key))));
        return thisBuilder();
    }

    @Override
    public BuilderType del(LuaLocalValue key) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public BuilderType del(LuaKeyArgument key) {
        LuaAstArg keyArg = getOrCreateKeyArgument(key);
        add(redisCallStatement("DEL", arguments(keyArg)));
        return thisBuilder();
    }
}
