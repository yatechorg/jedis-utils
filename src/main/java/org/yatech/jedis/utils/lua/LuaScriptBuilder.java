package org.yatech.jedis.utils.lua;

import org.yatech.jedis.utils.lua.ast.LuaAstArg;
import org.yatech.jedis.utils.lua.ast.LuaAstReturnStatement;
import org.yatech.jedis.utils.lua.ast.LuaAstStatement;

import java.util.*;

/**
 * Builder for a lua script
 *
 * <p>
 * Created on 11/09/2015.
 * @author Yinon Avraham
 * @see #startScript()
 * @see #endScript()
 * @see #endPreparedScript()
 */
public class LuaScriptBuilder extends AbstractLuaScriptBuilder<LuaScriptBuilder> {

    private int localIndex;
    private int keysIndex;
    private int argvIndex;
    private final LinkedHashMap<LuaValueArgument, LuaAstArg> valueArg2AstArg;
    private final LinkedHashMap<LuaKeyArgument, LuaAstArg> keyArg2AstArg;

    private LuaScriptBuilder() {
        super();
        localIndex = 0;
        keysIndex = 1;
        argvIndex = 1;
        valueArg2AstArg = new LinkedHashMap<>();
        keyArg2AstArg = new LinkedHashMap<>();
    }

    @Override
    String getNextLocalName() {
        return "local" + (localIndex++);
    }

    @Override
    LuaAstArg getOrCreateArgvArgument(LuaValueArgument<?> valueArgument) {
        LuaAstArg astArg = valueArg2AstArg.get(valueArgument);
        if (astArg == null) {
            astArg = new LuaAstArg(getNextArgvName());
            valueArg2AstArg.put(valueArgument, astArg);
        }
        return astArg;
    }

    @Override
    LuaAstArg getOrCreateKeyArgument(LuaKeyArgument keyArgument) {
        LuaAstArg astArg = keyArg2AstArg.get(keyArgument);
        if (astArg == null) {
            astArg = new LuaAstArg(getNextKeysName());
            keyArg2AstArg.put(keyArgument, astArg);
        }
        return astArg;
    }

    private String getNextKeysName() {
        return "KEYS[" + (keysIndex++) + "]";
    }

    private String getNextArgvName() {
        return "ARGV[" + (argvIndex++) + "]";
    }

    public static LuaScriptBuilder startScript() {
        return new LuaScriptBuilder();
    }

    /**
     * End building the script
     * @param config the configuration for the script to build
     * @return the new {@link LuaScript} instance
     */
    public LuaScript endScript(LuaScriptConfig config) {
        if (!endsWithReturnStatement()) {
            add(new LuaAstReturnStatement());
        }
        String scriptText = buildScriptText();
        return new BasicLuaScript(scriptText, config);
    }

    /**
     * End building the script
     * @return the new {@link LuaScript} instance
     */
    public LuaScript endScript() {
        return endScript(LuaScriptConfig.DEFAULT);
    }

    /**
     * End building the script, adding a return value statement
     * @param config the configuration for the script to build
     * @param value the value to return
     * @return the new {@link LuaScript} instance
     */
    public LuaScript endScriptReturn(LuaValue value, LuaScriptConfig config) {
        add(new LuaAstReturnStatement(argument(value)));
        String scriptText = buildScriptText();
        return new BasicLuaScript(scriptText, config);
    }

    /**
     * End building the script, adding a return value statement
     * @param value the value to return
     * @return the new {@link LuaScript} instance
     */
    public LuaScript endScriptReturn(LuaValue value) {
        return endScriptReturn(value, LuaScriptConfig.DEFAULT);
    }

    private boolean endsWithReturnStatement() {
        List<LuaAstStatement> statements = script.getStatements();
        return !statements.isEmpty() && statements.get(statements.size()-1) instanceof LuaAstReturnStatement;
    }

    /**
     * End building the prepared script
     * @param config the configuration for the script to build
     * @return the new {@link LuaPreparedScript} instance
     */
    public LuaPreparedScript endPreparedScript(LuaScriptConfig config) {
        if (!endsWithReturnStatement()) {
            add(new LuaAstReturnStatement());
        }
        String scriptText = buildScriptText();
        ArrayList<LuaKeyArgument> keyList = new ArrayList<>(keyArg2AstArg.keySet());
        ArrayList<LuaValueArgument> argvList = new ArrayList<>(valueArg2AstArg.keySet());
        if (config.isThreadSafe()) {
            return new ThreadSafeLuaPreparedScript(scriptText, keyList, argvList, config);
        } else {
            return new BasicLuaPreparedScript(scriptText, keyList, argvList, config);
        }
    }

    /**
     * End building the prepared script
     * @return the new {@link LuaPreparedScript} instance
     */
    public LuaPreparedScript endPreparedScript() {
        return endPreparedScript(LuaScriptConfig.DEFAULT);
    }

    /**
     * End building the prepared script, adding a return value statement
     * @param value the value to return
     * @param config the configuration for the script to build
     * @return the new {@link LuaPreparedScript} instance
     */
    public LuaPreparedScript endPreparedScriptReturn(LuaValue value, LuaScriptConfig config) {
        add(new LuaAstReturnStatement(argument(value)));
        return endPreparedScript(config);
    }

    /**
     * End building the prepared script, adding a return value statement
     * @param value the value to return
     * @return the new {@link LuaPreparedScript} instance
     */
    public LuaPreparedScript endPreparedScriptReturn(LuaValue value) {
        return endPreparedScriptReturn(value, LuaScriptConfig.DEFAULT);
    }

    /**
     * Start a new script block
     * @param parentBuilder the {@link LuaScriptBuilder} that the new block belongs to
     * @return a new builder for the script block
     * @see org.yatech.jedis.utils.lua.LuaIfStatementBuilder#then(LuaScriptBlock)
     * @see LuaScriptBlockBuilder#endBlock()
     */
    public static LuaScriptBlockBuilder startBlock(LuaScriptBuilder parentBuilder) {
        return LuaScriptBlockBuilder.startBlock(parentBuilder);
    }

    private String buildScriptText() {
        LuaScriptVisitor scriptVisitor = new LuaScriptVisitor();
        script.accept(scriptVisitor);
        return scriptVisitor.toString();
    }
}
