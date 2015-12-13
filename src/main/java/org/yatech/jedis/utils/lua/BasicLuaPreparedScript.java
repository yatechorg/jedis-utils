package org.yatech.jedis.utils.lua;

import org.yatech.jedis.utils.lua.ast.LuaScriptConfig;
import redis.clients.jedis.Jedis;

import java.util.*;

import static org.yatech.jedis.utils.lua.LuaPreparedScriptUtils.toArgMap;
import static org.yatech.jedis.utils.lua.LuaPreparedScriptUtils.toExecValues;

/**
 * A prepared script which can have a fixed script with argument placeholders.
 * The placeholders can be set with concrete values and then the script can be executed without the need to rebuild it.
 * This class is <b>NOT</b> thread-safe.
 *
 * Created by Yinon Avraham on 11/09/2015.
 * @see LuaScriptBuilder#endPreparedScript()
 */
class BasicLuaPreparedScript extends BasicLuaScript implements LuaPreparedScript {

    private final LinkedHashMap<String, LuaKeyArgument> name2keyArguments;
    private final LinkedHashMap<String, LuaValueArgument> name2valueArguments;

    BasicLuaPreparedScript(String scriptText, List<LuaKeyArgument> keyArguments, List<LuaValueArgument> valueArguments, LuaScriptConfig config) {
        super(scriptText, config);
        this.name2keyArguments = toArgMap(keyArguments);
        this.name2valueArguments = toArgMap(valueArguments);
    }

    @Override
    public void setKeyArgument(String name, String key) {
        name2keyArguments.get(name).setValue(key);
    }

    @Override
    public void setValueArgument(String name, String value) {
        name2valueArguments.get(name).setValue(value);
    }

    @Override
    public void setValueArgument(String name, int value) {
        name2valueArguments.get(name).setValue(value);
    }

    @Override
    public void setValueArgument(String name, long value) {
        name2valueArguments.get(name).setValue(value);
    }

    @Override
    public void setValueArgument(String name, double value) {
        name2valueArguments.get(name).setValue(value);
    }

    @Override
    public Object exec(Jedis jedis) {
        return exec(jedis, toExecValues(name2keyArguments), toExecValues(name2valueArguments));
    }
}
