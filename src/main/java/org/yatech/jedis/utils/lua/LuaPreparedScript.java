package org.yatech.jedis.utils.lua;

/**
 * A prepared script which can have a fixed script with argument placeholders.
 * The placeholders can be set with concrete values and then the script can be executed without the need to rebuild it.
 * <p>
 * Created on 13/12/15
 * @author Yinon Avraham
 */
public interface LuaPreparedScript extends LuaScript {

    /**
     * Set the concrete key name for a key argument
     * @param name the name of the argument (placeholder)
     * @param key the concrete key name
     */
    public void setKeyArgument(String name, String key);

    /**
     * Set the concrete value for a value argument
     * @param name the name of the argument (placeholder)
     * @param value the concrete value
     */
    public void setValueArgument(String name, String value);

    /**
     * Set the concrete value for a value argument
     * @param name the name of the argument (placeholder)
     * @param value the concrete value
     */
    public void setValueArgument(String name, int value);

    /**
     * Set the concrete value for a value argument
     * @param name the name of the argument (placeholder)
     * @param value the concrete value
     */
    public void setValueArgument(String name, long value);

    /**
     * Set the concrete value for a value argument
     * @param name the name of the argument (placeholder)
     * @param value the concrete value
     */
    public void setValueArgument(String name, double value);
}
