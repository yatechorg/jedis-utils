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
        return unpack(local(hash.getName()));
    }

    private <T> LuaAstExpression argument(LuaValue<T> value) {
        if (value instanceof LuaLocal) {
            return local(((LuaLocal) value).getName());
        } else if (value instanceof LuaKeyArgument) {
            return getOrCreateKeyArgument((LuaKeyArgument)value);
        } else if (value instanceof LuaValueArgument) {
            return getOrCreateArgvArgument((LuaValueArgument) value);
        } else {
            throw new IllegalArgumentException(
                    String.format("LuaValue of specific type $s is not expected.",
                            value.getClass().getName()));
        }
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
     *     builder.ifCondition(isNull(local1))
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
    public BuilderType select(LuaValue<Integer> db) {
        add(redisCallStatement("SELECT", arguments(argument(db))));
        return thisBuilder();
    }

    @Override
    public BuilderType del(String key) {
        add(redisCallStatement("DEL", arguments(stringValue(key))));
        return thisBuilder();
    }

    @Override
    public BuilderType del(LuaValue<String> key) {
        add(redisCallStatement("DEL", arguments(argument(key))));
        return thisBuilder();
    }

    @Override
    public LuaLocalArray hgetAll(String key) {
        LuaAstLocalDeclaration local = declareNewLocal();
        add(assignment(local, redisCall("HGETALL", arguments(stringValue(key)))));
        return new LuaLocalArray(local.getName());
    }

    @Override
    public LuaLocalArray hgetAll(LuaValue<String> key) {
        LuaAstLocalDeclaration local = declareNewLocal();
        add(assignment(local, redisCall("HGETALL", arguments(argument(key)))));
        return new LuaLocalArray(local.getName());
    }

    @Override
    public BuilderType hmset(String key, Map<String, String> hash) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public BuilderType hmset(LuaValue<String> key, Map<String, String> hash) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public BuilderType hmset(String key, LuaLocalArray hash) {
        add(redisCallStatement("HMSET", arguments(stringValue(key), unpackArray(hash))));
        return thisBuilder();
    }

    @Override
    public BuilderType hmset(LuaValue<String> key, LuaLocalArray hash) {
        add(redisCallStatement("HMSET", arguments(argument(key), unpackArray(hash))));
        return thisBuilder();
    }

    @Override
    public BuilderType zadd(String key, double score, String member) {
        add(redisCallStatement("ZADD", arguments(stringValue(key), doubleValue(score), stringValue(member))));
        return thisBuilder();
    }

    @Override
    public BuilderType zadd(String key, double score, LuaValue<String> member) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public BuilderType zadd(String key, LuaValue<Double> score, String member) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public BuilderType zadd(String key, LuaValue<Double> score, LuaValue<String> member) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public BuilderType zadd(LuaValue<String> key, double score, String member) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public BuilderType zadd(LuaValue<String> key, double score, LuaValue<String> member) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public BuilderType zadd(LuaValue<String> key, LuaValue<Double> score, String member) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public BuilderType zadd(LuaValue<String> key, LuaValue<Double> score, LuaValue<String> member) {
        add(redisCallStatement("ZADD", arguments(argument(key), argument(score), argument(member))));
        return thisBuilder();
    }

    @Override
    public BuilderType zadd(String key, Map<String, Double> scoreMembers) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public BuilderType zadd(LuaValue<String> key, Map<String, Double> scoreMembers) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public BuilderType zadd(String key, LuaLocalArray scoreMembers) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public BuilderType zadd(LuaValue<String> key, LuaLocalArray scoreMembers) {
        add(redisCallStatement("ZADD", arguments(argument(key), unpackArray(scoreMembers))));
        return thisBuilder();
    }

    @Override
    public LuaLocalValue zscore(String key, String member) {
        LuaAstLocalDeclaration local = declareNewLocal();
        add(assignment(local, redisCall("ZSCORE", arguments(stringValue(key), stringValue(member)))));
        return new LuaLocalValue(local.getName());
    }

    @Override
    public LuaLocalValue zscore(LuaValue<String> key, String member) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public LuaLocalValue zscore(String key, LuaValue<String> member) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public LuaLocalValue zscore(LuaValue<String> key, LuaValue<String> member) {
        LuaAstLocalDeclaration local = declareNewLocal();
        add(assignment(local, redisCall("ZSCORE", arguments(argument(key), argument(member)))));
        return new LuaLocalValue(local.getName());
    }
}
