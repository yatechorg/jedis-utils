package org.yatech.jedis.utils.lua;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 13/12/15
 *
 * @author Yinon Avraham
 */
final class LuaPreparedScriptUtils {

    private LuaPreparedScriptUtils() {}

    static <T extends LuaArgument> LinkedHashMap<String, T> toArgMap(List<T> args) {
        LinkedHashMap<String, T> map = new LinkedHashMap<>();
        if (args != null) {
            for (T arg : args) {
                map.put(arg.getName(), (T) arg.clone());
            }
        }
        return map;
    }

    static <T extends LuaArgument> List<String> toExecValues(Map<String, T> name2arguments) {
        List<String> values = new ArrayList<>(name2arguments.size());
        for (Map.Entry<String, T> entry : name2arguments.entrySet()) {
            values.add(String.valueOf(entry.getValue().getValue()));
        }
        return values;
    }

    static  <T extends LuaArgument> LinkedHashMap<String, T> cloneArgMap(LinkedHashMap<String, T> source) {
        LinkedHashMap<String, T> target = new LinkedHashMap<>(source.size());
        for (Map.Entry<String, T> sourceEntry : source.entrySet()) {
            target.put(sourceEntry.getKey(), (T) sourceEntry.getValue().clone());
        }
        return target;
    }

}
