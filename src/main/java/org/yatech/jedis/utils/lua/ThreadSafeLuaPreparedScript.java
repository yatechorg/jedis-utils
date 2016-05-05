package org.yatech.jedis.utils.lua;

import redis.clients.jedis.Jedis;

import java.util.LinkedHashMap;
import java.util.List;

import static org.yatech.jedis.utils.lua.LuaPreparedScriptUtils.cloneArgMap;
import static org.yatech.jedis.utils.lua.LuaPreparedScriptUtils.toArgMap;
import static org.yatech.jedis.utils.lua.LuaPreparedScriptUtils.toExecValues;

/**
 * A thread safe prepared script which can have a fixed script with argument placeholders.
 * The placeholders can be set with concrete values and then the script can be executed without the need to rebuild it.
 *
 * <br/>
 * Created on 13/12/15
 * @author Yinon Avraham
 */
class ThreadSafeLuaPreparedScript extends BasicLuaScript implements LuaPreparedScript {

    private final ThreadLocal<ArgumentsHolder> localArguments;

    ThreadSafeLuaPreparedScript(String scriptText, final List<LuaKeyArgument> keyArguments,
                                final List<LuaValueArgument> valueArguments, LuaScriptConfig config) {
        super(scriptText, config);
        localArguments = new ThreadLocal<ArgumentsHolder>() {
            private final ArgumentsHolder template = new ArgumentsHolder(toArgMap(keyArguments), toArgMap(valueArguments));

            @Override
            protected ArgumentsHolder initialValue() {
                return new ArgumentsHolder(cloneArgMap(template.name2keyArguments), cloneArgMap(template.name2valueArguments));
            }
        };
    }

    @Override
    public void setKeyArgument(String name, String key) {
        localArguments.get().name2keyArguments.get(name).setValue(key);
    }

    @Override
    public void setValueArgument(String name, String value) {
        localArguments.get().name2valueArguments.get(name).setValue(value);
    }

    @Override
    public void setValueArgument(String name, int value) {
        localArguments.get().name2valueArguments.get(name).setValue(value);
    }

    @Override
    public void setValueArgument(String name, long value) {
        localArguments.get().name2valueArguments.get(name).setValue(value);
    }

    @Override
    public void setValueArgument(String name, double value) {
        localArguments.get().name2valueArguments.get(name).setValue(value);
    }

    @Override
    public Object exec(Jedis jedis) {
        ArgumentsHolder holder = localArguments.get();
        return exec(jedis, toExecValues(holder.name2keyArguments), toExecValues(holder.name2valueArguments));
    }

    private static class ArgumentsHolder {
        final LinkedHashMap<String, LuaKeyArgument> name2keyArguments;
        final LinkedHashMap<String, LuaValueArgument> name2valueArguments;

        ArgumentsHolder(LinkedHashMap<String, LuaKeyArgument> name2keyArguments,
                        LinkedHashMap<String, LuaValueArgument> name2valueArguments) {
            this.name2keyArguments = name2keyArguments;
            this.name2valueArguments = name2valueArguments;
        }
    }
}
