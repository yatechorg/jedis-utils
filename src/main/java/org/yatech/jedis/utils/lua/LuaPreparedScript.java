package org.yatech.jedis.utils.lua;

import redis.clients.jedis.Jedis;

import java.util.*;

/**
 * Created by Yinon Avraham on 11/09/2015.
 */
public class LuaPreparedScript extends LuaScript {

    private final LinkedHashMap<String, LuaKeyArgument> name2keyArguments;
    private final LinkedHashMap<String, LuaValueArgument> name2valueArguments;

    LuaPreparedScript(String scriptText, List<LuaKeyArgument> keyArguments, List<LuaValueArgument> valueArguments) {
        super(scriptText);
        this.name2keyArguments = toArgMap(keyArguments);
        this.name2valueArguments = toArgMap(valueArguments);
    }

    private <T extends LuaArgument> LinkedHashMap<String, T> toArgMap(List<T> args) {
        LinkedHashMap<String, T> map = new LinkedHashMap<>();
        if (args != null) {
            for (T arg : args) {
                map.put(arg.getName(), arg);
            }
        }
        return map;
    }

    public void setKeyArgument(String name, String key) {
        name2keyArguments.get(name).setValue(key);
    }

    public void setValueArgument(String name, String value) {
        name2valueArguments.get(name).setValue(value);
    }

    public void setValueArgument(String name, int value) {
        name2valueArguments.get(name).setValue(value);
    }

    public void setValueArgument(String name, double value) {
        name2valueArguments.get(name).setValue(value);
    }

    @Override
    public Object exec(Jedis jedis) {
        return jedis.eval(getScriptText(), toValues(name2keyArguments), toValues(name2valueArguments));
    }

    private List<String> toValues(Map<String, ? extends LuaArgument> name2arguments) {
        List<String> values = new ArrayList<>(name2arguments.size());
        for (Map.Entry<String, ? extends LuaArgument> entry : name2arguments.entrySet()) {
            values.add(String.valueOf(entry.getValue().getValue()));
        }
        return values;
    }

}
