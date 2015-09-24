package org.yatech.jedis.utils.lua;

import org.yatech.jedis.utils.lua.ast.LuaAstArg;
import org.yatech.jedis.utils.lua.ast.LuaAstReturnStatement;
import org.yatech.jedis.utils.lua.ast.LuaAstStatement;

import java.util.*;

/**
 * Builder for a lua script
 *
 * Created by Yinon Avraham on 11/09/2015.
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
     * @return the new {@link LuaScript} instance
     */
    public LuaScript endScript() {
        if (!endsWithReturnStatement()) {
            add(new LuaAstReturnStatement());
        }
        String scriptText = buildScriptText();
        return new LuaScript(scriptText);
    }

    /**
     * End building the script, adding a return value statement
     * @param value the value to return
     * @return the new {@link LuaScript} instance
     */
    public LuaScript endScriptReturn(LuaValue value) {
        add(new LuaAstReturnStatement(argument(value)));
        String scriptText = buildScriptText();
        return new LuaScript(scriptText);
    }

    private boolean endsWithReturnStatement() {
        List<LuaAstStatement> statements = script.getStatements();
        return !statements.isEmpty() && statements.get(statements.size()-1) instanceof LuaAstReturnStatement;
    }

    /**
     * End building the prepared script
     * @return the new {@link LuaPreparedScript} instance
     */
    public LuaPreparedScript endPreparedScript() {
        if (!endsWithReturnStatement()) {
            add(new LuaAstReturnStatement());
        }
        String scriptText = buildScriptText();
        return new LuaPreparedScript(scriptText, new ArrayList<>(keyArg2AstArg.keySet()), new ArrayList<>(valueArg2AstArg.keySet()));
    }

    /**
     * End building the prepared script, adding a return value statement
     * @param value the value to return
     * @return the new {@link LuaPreparedScript} instance
     */
    public LuaPreparedScript endPreparedScriptReturn(LuaValue value) {
        add(new LuaAstReturnStatement(argument(value)));
        String scriptText = buildScriptText();
        return new LuaPreparedScript(scriptText, new ArrayList<>(keyArg2AstArg.keySet()), new ArrayList<>(valueArg2AstArg.keySet()));
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
