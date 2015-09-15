package org.yatech.jedis.utils.lua;

import org.yatech.jedis.utils.lua.ast.*;
import static org.yatech.jedis.utils.lua.ast.LuaAstHelper.*;

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

    // *** Ast helper methods

    private LuaAstLocalDeclaration declareNewLocal() {
        String name = getNextLocalName();
        return declareLocal(name);
    }

    private LuaAstUnpack unpackArray(LuaLocalArray hash) {
        return unpack(hash.getName());
    }

    // *** Arguments ***

    /**
     * Create a new key argument which is a place holder for keys in a {@link org.yatech.jedis.utils.lua.LuaPreparedScript}.
     * @param argName the name of the argument
     * @return the new argument instance
     * @see org.yatech.jedis.utils.lua.LuaPreparedScript#setKeyArgument(String, String)
     */
    public static LuaKeyArgument newKeyArgument(String argName) {
        return new LuaKeyArgument(argName);
    }

    /**
     * Create a new string value argument which is a place holder for string values in a {@link org.yatech.jedis.utils.lua.LuaPreparedScript}.
     * @param argName the name of the argument
     * @return the new argument instance
     * @see org.yatech.jedis.utils.lua.LuaPreparedScript#setValueArgument(String, String)
     */
    public static LuaStringValueArgument newStringValueArgument(String argName) {
        return new LuaStringValueArgument(argName);
    }

    /**
     * Create a new int value argument which is a place holder for int values in a {@link org.yatech.jedis.utils.lua.LuaPreparedScript}.
     * @param argName the name of the argument
     * @return the new argument instance
     * @see org.yatech.jedis.utils.lua.LuaPreparedScript#setValueArgument(String, int)
     */
    public static LuaIntValueArgument newIntValueArgument(String argName) {
        return new LuaIntValueArgument(argName);
    }

    /**
     * Create a new double value argument which is a place holder for double values in a {@link org.yatech.jedis.utils.lua.LuaPreparedScript}.
     * @param argName the name of the argument
     * @return the new argument instance
     * @see org.yatech.jedis.utils.lua.LuaPreparedScript#setValueArgument(String, double)
     */
    public static LuaDoubleValueArgument newDoubleValueArgument(String argName) {
        return new LuaDoubleValueArgument(argName);
    }

    // *** If condition ***

    /**
     * Start an <code>if</code> statement. For example:
     * <pre><code>
     *     builder.ifCondition(notNull(local1))
     *         .then(
     *             startBlock(builder).del("key1").endBlock()
     *         .endIf()
     * </code></pre>
     * @param condition the condition for the statement
     * @return a builder for the statement
     * @see org.yatech.jedis.utils.lua.LuaConditions
     * @see LuaIfStatementBuilder#then(LuaScriptBlock)
     * @see LuaIfStatementBuilder#endIf()
     */
    public LuaIfStatementBuilder<BuilderType> ifCondition(LuaCondition condition) {
        return new LuaIfStatementBuilder<BuilderType>(this, condition);
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
        LuaAstLocalDeclaration local = declareNewLocal();
        add(assignment(local, redisCall("HGETALL", arguments(stringValue(key)))));
        return new LuaLocalArray(local.getName());
    }

    @Override
    public LuaLocalArray hgetAll(LuaLocalValue key) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public LuaLocalArray hgetAll(LuaKeyArgument key) {
        LuaAstLocalDeclaration local = declareNewLocal();
        LuaAstArg keyArg = getOrCreateKeyArgument(key);
        add(assignment(local, redisCall("HGETALL", arguments(keyArg))));
        return new LuaLocalArray(local.getName());
    }

    @Override
    public LuaLocalValue zscore(String key, String member) {
        LuaAstLocalDeclaration local = declareNewLocal();
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
        LuaAstLocalDeclaration local = declareNewLocal();
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
        add(redisCallStatement("HMSET", arguments(stringValue(key), unpackArray(hash))));
        return thisBuilder();
    }

    @Override
    public BuilderType hmset(LuaLocalValue key, LuaLocalArray hash) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public BuilderType hmset(LuaKeyArgument key, LuaLocalArray hash) {
        LuaAstArg keyArg = getOrCreateKeyArgument(key);
        add(redisCallStatement("HMSET", arguments(keyArg, unpackArray(hash))));
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
